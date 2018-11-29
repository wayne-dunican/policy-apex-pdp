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

package org.onap.policy.apex.testsuites.integration.common.model.java;

import org.onap.policy.apex.core.engine.executor.context.TaskExecutionContext;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;

/**
 * The Class EvalTask_Logic is default evaluation task logic in Java.
 */
public class EvalTaskLogic {

    /**
     * Gets the event.
     *
     * @param executor the executor
     * @return the event
     */
    public boolean getEvent(final TaskExecutionContext executor) {
        String idString = executor.subject.getId();
        executor.logger.debug(idString);
        
        String inFieldString = executor.inFields.toString();
        executor.logger.debug(inFieldString);
        
        executor.outFields.putAll(executor.inFields);

        executor.outFields.put("State3Timestamp", java.lang.System.nanoTime());
        
        String outFieldString = executor.outFields.toString();
        executor.logger.debug(outFieldString);
        return true;
    }
}
