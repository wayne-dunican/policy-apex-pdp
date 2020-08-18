/*
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

package org.onap.policy.apex.model.basicmodel.concepts;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey.Compatibility;

public class AxKeyTest {

    @Test
    public void testArtifactKey() {
        assertThatThrownBy(() -> new AxArtifactKey("some bad key id"))
            .hasMessage("parameter \"id\": value \"some bad key id\", "
                            + "does not match regular expression \"[A-Za-z0-9\\-_\\.]+:[0-9].[0-9].[0-9]\"");
        AxArtifactKey someKey0 = new AxArtifactKey();
        assertEquals(AxArtifactKey.getNullKey(), someKey0);

        AxArtifactKey someKey1 = new AxArtifactKey("name", "0.0.1");
        AxArtifactKey someKey2 = new AxArtifactKey(someKey1);
        AxArtifactKey someKey3 = new AxArtifactKey(someKey1.getId());
        assertEquals(someKey1, someKey2);
        assertEquals(someKey1, someKey3);

        assertEquals(someKey2, someKey1.getKey());
        assertEquals(1, someKey1.getKeys().size());

        someKey0.setName("zero");
        someKey0.setVersion("0.0.2");

        someKey3.setVersion("0.0.2");

        AxArtifactKey someKey4 = new AxArtifactKey(someKey1);
        someKey4.setVersion("0.1.2");

        AxArtifactKey someKey5 = new AxArtifactKey(someKey1);
        someKey5.setVersion("1.2.2");

        AxArtifactKey someKey6 = new AxArtifactKey(someKey1);
        someKey6.setVersion("3");

        assertEquals(Compatibility.DIFFERENT, someKey0.getCompatibility(new AxReferenceKey()));
        assertEquals(Compatibility.DIFFERENT, someKey0.getCompatibility(someKey1));
        assertEquals(Compatibility.IDENTICAL, someKey2.getCompatibility(someKey1));
        assertEquals(Compatibility.PATCH, someKey3.getCompatibility(someKey1));
        assertEquals(Compatibility.MINOR, someKey4.getCompatibility(someKey1));
        assertEquals(Compatibility.MAJOR, someKey5.getCompatibility(someKey1));
        assertEquals(Compatibility.MAJOR, someKey6.getCompatibility(someKey1));

        assertTrue(someKey1.isCompatible(someKey2));
        assertTrue(someKey1.isCompatible(someKey3));
        assertTrue(someKey1.isCompatible(someKey4));
        assertFalse(someKey1.isCompatible(someKey0));
        assertFalse(someKey1.isCompatible(someKey5));
        assertFalse(someKey1.isCompatible(new AxReferenceKey()));

        assertEquals(AxValidationResult.ValidationResult.VALID,
                        someKey0.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID,
                        someKey1.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID,
                        someKey2.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID,
                        someKey3.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID,
                        someKey4.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID,
                        someKey5.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID,
                        someKey6.validate(new AxValidationResult()).getValidationResult());

        someKey0.clean();
        assertNotNull(someKey0.toString());

        AxArtifactKey someKey7 = new AxArtifactKey(someKey1);
        assertEquals(150332875, someKey7.hashCode());
        assertEquals(0, someKey7.compareTo(someKey1));
        assertEquals(-12, someKey7.compareTo(someKey0));

        assertThatThrownBy(() -> someKey0.compareTo(null))
            .hasMessage("comparison object may not be null");
        assertEquals(0, someKey0.compareTo(someKey0));
        assertEquals(353602977, someKey0.compareTo(new AxReferenceKey()));

        assertNotNull(someKey0);
        // disabling sonar because this code tests the equals() method
        assertEquals(someKey0, someKey0); // NOSONAR
        assertNotEquals(someKey0, (Object) new AxReferenceKey());

        AxArtifactKey nullKey0 = AxArtifactKey.getNullKey();
        assertTrue(nullKey0.isNullKey());
        AxArtifactKey nullKey1 = new AxArtifactKey();
        assertTrue(nullKey1.isNullKey());
        AxArtifactKey nullKey2 = new AxArtifactKey(AxKey.NULL_KEY_NAME, AxKey.NULL_KEY_VERSION);
        assertTrue(nullKey2.isNullKey());
        AxArtifactKey notnullKey = new AxArtifactKey("Blah", AxKey.NULL_KEY_VERSION);
        assertFalse(notnullKey.isNullKey());

    }


    @Test
    public void testValidation() throws IllegalArgumentException, IllegalAccessException,
        NoSuchFieldException, SecurityException {
        AxArtifactKey testKey = new AxArtifactKey("TheKey", "0.0.1");
        assertEquals("TheKey:0.0.1", testKey.getId());

        Field nameField = testKey.getClass().getDeclaredField("name");
        nameField.setAccessible(true);
        nameField.set(testKey, "Key Name");
        AxValidationResult validationResult = new AxValidationResult();
        testKey.validate(validationResult);
        nameField.set(testKey, "TheKey");
        nameField.setAccessible(false);
        assertEquals(
            "name invalid-parameter name with value Key Name "
                + "does not match regular expression [A-Za-z0-9\\-_\\.]+",
            validationResult.getMessageList().get(0).getMessage());

        Field versionField = testKey.getClass().getDeclaredField("version");
        versionField.setAccessible(true);
        versionField.set(testKey, "Key Version");
        AxValidationResult validationResultV = new AxValidationResult();
        testKey.validate(validationResultV);
        versionField.set(testKey, "0.0.1");
        versionField.setAccessible(false);
        assertEquals(
            "version invalid-parameter version with value Key Version "
                + "does not match regular expression [A-Za-z0-9.]+",
            validationResultV.getMessageList().get(0).getMessage());
    }
}
