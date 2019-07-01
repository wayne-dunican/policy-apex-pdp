/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.services.onappf.parameters;

import lombok.Getter;
import org.onap.policy.common.endpoints.parameters.RestServerParameters;
import org.onap.policy.common.endpoints.parameters.TopicParameterGroup;
import org.onap.policy.common.parameters.ParameterGroupImpl;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;

/**
 * Class to hold all parameters needed for apex starter component.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
@NotNull
@NotBlank
@Getter
public class ApexStarterParameterGroup extends ParameterGroupImpl {
    private RestServerParameters restServerParameters;
    private PdpStatusParameters pdpStatusParameters;
    private TopicParameterGroup topicParameterGroup;
    /**
     * Create the apex starter parameter group.
     *
     * @param name the parameter group name
     */
    public ApexStarterParameterGroup(final String name) {
        super(name);
    }
}
