/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.protocol.jms;

import static org.junit.Assert.assertEquals;


import org.junit.Test;

public class JmsObjectEventProtocolParametersTest {

    @Test
    public void getIncomingEventVersion() {
        final JmsObjectEventProtocolParameters jmsObjectEventProtocolParameters =
            new JmsObjectEventProtocolParameters();
        final String actual = jmsObjectEventProtocolParameters.getIncomingEventVersion();
        assertEquals("1.0.0", actual);
    }

    @Test
    public void getIncomingEventSource() {
        final JmsObjectEventProtocolParameters jmsObjectEventProtocolParameters =
            new JmsObjectEventProtocolParameters();
        final String actual = jmsObjectEventProtocolParameters.getIncomingEventSource();
        assertEquals("JMS", actual);
    }

    @Test
    public void getIncomingEventTarget() {
        final JmsObjectEventProtocolParameters jmsObjectEventProtocolParameters =
            new JmsObjectEventProtocolParameters();
        final String actual = jmsObjectEventProtocolParameters.getIncomingEventTarget();
        assertEquals("Apex", actual);
    }
}
