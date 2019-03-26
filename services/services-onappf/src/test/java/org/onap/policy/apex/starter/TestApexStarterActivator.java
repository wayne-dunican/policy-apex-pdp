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

package org.onap.policy.apex.starter;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.starter.exception.ApexStarterException;
import org.onap.policy.apex.starter.parameters.ApexStarterParameterGroup;
import org.onap.policy.apex.starter.parameters.ApexStarterParameterHandler;
import org.onap.policy.apex.starter.parameters.CommonTestData;



/**
 * Class to perform unit test of {@link ApexStarterActivator}}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class TestApexStarterActivator {

    private ApexStarterActivator activator;

    /**
     * Initializes an activator.
     *
     * @throws Exception if an error occurs
     */
    @Before
    public void setUp() throws Exception {
        final String[] apexStarterConfigParameters = { "-c", "src/test/resources/ApexStarterConfigParameters.json",
            "-p", "src/test/resources/topic.properties" };
        final ApexStarterCommandLineArguments arguments =
                new ApexStarterCommandLineArguments(apexStarterConfigParameters);
        final ApexStarterParameterGroup parGroup = new ApexStarterParameterHandler().getParameters(arguments);

        final Properties props = new Properties();
        final String propFile = arguments.getFullPropertyFilePath();
        try (FileInputStream stream = new FileInputStream(propFile)) {
            props.load(stream);
        }

        activator = new ApexStarterActivator(parGroup, props);
    }

    /**
     * Method for cleanup after each test.
     *
     * @throws Exception if an error occurs
     */
    @After
    public void teardown() throws Exception {
        if (activator != null && activator.isAlive()) {
            activator.terminate();
        }
    }

    @Test
    public void testApexStarterActivator() throws ApexStarterException {
        assertFalse(activator.isAlive());
        activator.initialize();
        assertTrue(activator.isAlive());
        assertTrue(activator.getParameterGroup().isValid());
        assertEquals(CommonTestData.APEX_STARTER_GROUP_NAME, activator.getParameterGroup().getName());

        // repeat - should throw an exception
        assertThatIllegalStateException().isThrownBy(() -> activator.initialize());
        assertTrue(activator.isAlive());
        assertTrue(activator.getParameterGroup().isValid());
    }

    @Test
    public void testGetCurrent_testSetCurrent() {
        assertSame(activator, ApexStarterActivator.getCurrent());
    }

    @Test
    public void testTerminate() throws Exception {
        activator.initialize();
        activator.terminate();
        assertFalse(activator.isAlive());

        // repeat - should throw an exception
        assertThatIllegalStateException().isThrownBy(() -> activator.terminate());
        assertFalse(activator.isAlive());
    }
}
