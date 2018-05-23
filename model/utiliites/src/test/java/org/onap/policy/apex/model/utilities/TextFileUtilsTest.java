/*
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

package org.onap.policy.apex.model.utilities;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;
import org.onap.policy.apex.model.utilities.TextFileUtils;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TextFileUtilsTest {

    @Test
    public void test() throws IOException {
        File tempTextFile = File.createTempFile("Test", "txt");
        
        TextFileUtils.putStringAsTextFile("This is the contents of a text file", tempTextFile.getAbsolutePath());
        
        String textFileString0 = TextFileUtils.getTextFileAsString(tempTextFile.getAbsolutePath());
        assertEquals("This is the contents of a text file", textFileString0);
        
        FileInputStream fis = new FileInputStream(tempTextFile);
        String textFileString1 = TextFileUtils.getStreamAsString(fis);
        assertEquals(textFileString0, textFileString1);
        
    }

}
