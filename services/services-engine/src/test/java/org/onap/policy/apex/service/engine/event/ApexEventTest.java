/*
 *  ============LICENSE_START=======================================================
 *  Copyright (C) 2021. Nordix Foundation.
 *  Modifications Copyright (C) 2022 Bell Canada. All rights reserved.
 *  ================================================================================
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

package org.onap.policy.apex.service.engine.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Properties;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class ApexEventTest {
    private final Random random = new Random();
    private ApexEvent apexEvent;

    /**
     * Prepares tests.
     *
     * @throws Exception when object is created
     */
    @Before
    public void setUp() throws Exception {
        apexEvent =
            new ApexEvent("name", "version", "namespace", "source", "target", "");

    }

    @Test
    public void invalidEventName() {
        final String name = "++" + RandomStringUtils.randomAlphabetic(5);
        Assertions.assertThatCode(() ->
            apexEvent = new ApexEvent(name, "version", "namespace", "source", "target", ""))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void invalidEventVersion() {
        final String version = "++" + RandomStringUtils.randomAlphabetic(5);
        Assertions.assertThatCode(() ->
            apexEvent = new ApexEvent("name", version, "namespace", "source", "target", ""))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void invalidEventNamespace() {
        final String namespace = "++" + RandomStringUtils.randomAlphabetic(5);
        Assertions.assertThatCode(() ->
            apexEvent = new ApexEvent("name", "version", namespace, "source", "target", ""))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void invalidEventSource() {
        final String source = "++" + RandomStringUtils.randomAlphabetic(5);
        Assertions.assertThatCode(() ->
            apexEvent = new ApexEvent("name", "version", "namespace", source, "target", ""))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void invalidEventTarget() {
        final String target = "++" + RandomStringUtils.randomAlphabetic(5);
        Assertions.assertThatCode(() ->
            apexEvent = new ApexEvent("name", "version", "namespace", "source", target, ""))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void invalidEventStatus() {
        final String toscaPolicyState = "INVALID_STATUS";
        Assertions.assertThatCode(() ->
                        apexEvent = new ApexEvent("name", "version", "namespace", "source", "target", toscaPolicyState))
                .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void setExecutionId() {
        final int executionId = random.nextInt();
        apexEvent.setExecutionId(executionId);
        final long actual = apexEvent.getExecutionId();
        assertThat(actual).isEqualTo(executionId);
    }

    @Test
    public void setExecutionProperties() {
        final Properties properties = new Properties();
        apexEvent.setExecutionProperties(properties);
        final Properties actual = apexEvent.getExecutionProperties();
        assertThat(actual).isSameAs(properties);
    }

    @Test
    public void setExceptionMessage() {
        final String message = RandomStringUtils.randomAlphabetic(25);
        apexEvent.setExceptionMessage(message);
        final String actual = apexEvent.getExceptionMessage();
        assertThat(actual).isEqualTo(message);
    }

    @Test
    public void put() {
        final String key = RandomStringUtils.randomAlphabetic(5);
        final Object event = new Object();
        apexEvent.put(key, event);
        final Object actual = apexEvent.get(key);
        assertThat(actual).isEqualTo(event);
    }

    @Test
    public void put2() {
        final String key = "_#+@" + RandomStringUtils.randomAlphabetic(5);
        final Object event = new Object();
        apexEvent.put(key, event);
        final Object actual = apexEvent.get(key);
        assertThat(actual).isNull();
    }

    @Test
    public void putAll() {
        final String key1 = RandomStringUtils.randomAlphabetic(5);
        final String key2 = RandomStringUtils.randomAlphabetic(6);
        final Object event1 = new Object();
        final Object event2 = new Object();
        final Map<String, Object> events = Map.of(key1, event1, key2, event2);
        apexEvent.putAll(events);
        final Object actual1 = apexEvent.get(key1);
        final Object actual3 = apexEvent.get(key2);
        assertThat(actual1).isEqualTo(event1);
        assertThat(actual3).isEqualTo(event2);
    }

    @Test
    public void putAllOneInvalidKey() {
        final String key1 = RandomStringUtils.randomAlphabetic(5);
        final String key2 = "_#+@" + RandomStringUtils.randomAlphabetic(6);
        final String key3 = RandomStringUtils.randomAlphabetic(7);
        final Object event1 = new Object();
        final Object event2 = new Object();
        final Object event3 = new Object();
        final Map<String, Object> events = Map.of(key1, event1, key2, event2, key3, event3);

        apexEvent.putAll(events);

        final Object actual1 = apexEvent.get(key1);
        final Object actual2 = apexEvent.get(key2);
        final Object actual3 = apexEvent.get(key3);
        assertThat(actual1).isNull();
        assertThat(actual2).isNull();
        assertThat(actual3).isNull();
    }
}