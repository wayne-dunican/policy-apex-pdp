/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.plugins.executor.javascript;

import java.util.Map;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.executor.TaskExecutor;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class JavascriptTaskExecutor is the task executor for task logic written in Javascript It is unlikely that this
 * is thread safe.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JavascriptTaskExecutor extends TaskExecutor {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(JavascriptTaskExecutor.class);

    // Javascript engine
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    private CompiledScript compiled = null;

    /**
     * Prepares the task for processing.
     *
     * @throws StateMachineException thrown when a state machine execution error occurs
     */
    @Override
    public void prepare() throws StateMachineException {
        // Call generic prepare logic
        super.prepare();
        try {
            compiled = ((Compilable) engine).compile(getSubject().getTaskLogic().getLogic());
        } catch (final ScriptException e) {
            LOGGER.error("execute: task logic failed to compile for task  \"" + getSubject().getKey().getID() + "\"");
            throw new StateMachineException(
                    "task logic failed to compile for task  \"" + getSubject().getKey().getID() + "\"", e);
        }
    }

    /**
     * Executes the executor for the task in a sequential manner.
     *
     * @param executionID the execution ID for the current APEX policy execution
     * @param incomingFields the incoming fields
     * @return The outgoing fields
     * @throws StateMachineException on an execution error
     * @throws ContextException on context errors
     */
    @Override
    public Map<String, Object> execute(final long executionID, final Map<String, Object> incomingFields)
            throws StateMachineException, ContextException {
        // Do execution pre work
        executePre(executionID, incomingFields);

        // Set up the Javascript engine
        engine.put("executor", getExecutionContext());

        // Check and execute the Javascript logic
        boolean returnValue = false;
        try {
            if (compiled == null) {
                engine.eval(getSubject().getTaskLogic().getLogic());
            } else {
                compiled.eval(engine.getContext());
            }
        } catch (final ScriptException e) {
            LOGGER.error("execute: task logic failed to run for task  \"" + getSubject().getKey().getID() + "\"");
            throw new StateMachineException(
                    "task logic failed to run for task  \"" + getSubject().getKey().getID() + "\"", e);
        }

        try {
            final Object ret = engine.get("returnValue");
            if (ret == null) {
                LOGGER.error("execute: task logic failed to set a return value for task  \""
                        + getSubject().getKey().getID() + "\"");
                throw new StateMachineException("execute: task logic failed to set a return value for task  \""
                        + getSubject().getKey().getID() + "\"");
            }
            returnValue = (Boolean) ret;
        } catch (NullPointerException | ClassCastException e) {
            LOGGER.error("execute: task selection logic failed to set a correct return value for state  \""
                    + getSubject().getKey().getID() + "\"", e);
            throw new StateMachineException("execute: task selection logic failed to set a return value for state  \""
                    + getSubject().getKey().getID() + "\"", e);
        }

        // Do the execution post work
        executePost(returnValue);

        // Send back the return event
        if (returnValue) {
            return getOutgoing();
        } else {
            return null;
        }
    }

    /**
     * Cleans up the task after processing.
     *
     * @throws StateMachineException thrown when a state machine execution error occurs
     */
    @Override
    public void cleanUp() throws StateMachineException {
        LOGGER.debug("cleanUp:" + getSubject().getKey().getID() + "," + getSubject().getTaskLogic().getLogicFlavour()
                + "," + getSubject().getTaskLogic().getLogic());
        engine = null;
    }
}
