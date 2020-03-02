/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

package org.onap.policy.apex.context.test.concepts;

import java.io.Serializable;

import lombok.Data;

/**
 * The Class TestContextBooleanItem.
 */
@Data
public class TestContextBooleanItem implements Serializable {
    private static final long serialVersionUID = 7241008665286367796L;

    private boolean flag = false;

    /**
     * The Constructor.
     */
    public TestContextBooleanItem() {
        // Default constructor
    }

    /**
     * The Constructor.
     *
     * @param flag the flag
     */
    public TestContextBooleanItem(final Boolean flag) {
        this.flag = flag;
    }
}
