/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2020 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.context.parameters.LockManagerParameters;
import org.onap.policy.apex.context.parameters.PersistorParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the JavaTaskSelectExecutor class.
 *
 */
public class JavascriptTaskSelectExecutorTest {
    /**
     * Initiate Parameters.
     */
    @Before
    public void initiateParameters() {
        ParameterService.register(new DistributorParameters());
        ParameterService.register(new LockManagerParameters());
        ParameterService.register(new PersistorParameters());
    }

    /**
     * Clear Parameters.
     */
    @After
    public void clearParameters() {
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
    }

    @Test
    public void testJavascriptTaskSelectExecutor() throws Exception {
        JavascriptTaskSelectExecutor jtse = new JavascriptTaskSelectExecutor();
        assertNotNull(jtse);

        assertThatThrownBy(() -> {
            jtse.prepare();
            fail("test should throw an exception here");
        }).isInstanceOf(NullPointerException.class);

        AxState state = new AxState();
        ApexInternalContext internalContext = new ApexInternalContext(new AxPolicyModel());
        jtse.setContext(null, state, internalContext);
        jtse.prepare();

        AxEvent axEvent1 = new AxEvent(new AxArtifactKey("Event", "0.0.1"));
        EnEvent event1 = new EnEvent(axEvent1);

        assertThatThrownBy(() -> {
            jtse.execute(-1, new Properties(), event1);
        }).hasMessage("execute: logic failed to set a return value for \"NULL:0.0.0:NULL:NULL\"");

        state.getTaskSelectionLogic().setLogic("java.lang.String");
        jtse.prepare();

        assertThatThrownBy(() -> {
            jtse.execute(-1, new Properties(), null);
        }).isInstanceOf(NullPointerException.class);

        AxEvent axEvent = new AxEvent(new AxArtifactKey("Event", "0.0.1"));
        EnEvent event = new EnEvent(axEvent);

        assertThatThrownBy(() -> {
            jtse.execute(-1, new Properties(), event);
        }).hasMessage("execute: logic failed to set a return value for \"NULL:0.0.0:NULL:NULL\"");

        state.getTaskSelectionLogic().setLogic("var returnValueType = Java.type(\"java.lang.Boolean\");\r\n"
                + "var returnValue = new returnValueType(false); ");

        assertThatThrownBy(() -> {
            jtse.prepare();
            jtse.execute(-1, new Properties(), event);
        }).hasMessage("execute-post: task selection logic failed on state \"NULL:0.0.0:NULL:NULL\"");

        state.getTaskSelectionLogic().setLogic("var returnValueType = Java.type(\"java.lang.Boolean\");\r\n"
                + "var returnValue = new returnValueType(true); ");

        jtse.prepare();
        AxArtifactKey taskKey = jtse.execute(0, new Properties(), event);
        assertEquals("NULL:0.0.0", taskKey.getId());
        jtse.cleanUp();
    }
}
