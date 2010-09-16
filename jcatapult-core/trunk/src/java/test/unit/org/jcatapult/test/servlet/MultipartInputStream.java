/*
 * Copyright (c) 2001-2010, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.jcatapult.test.servlet;

import javax.servlet.ServletInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import net.java.io.FileTools;
import org.jcatapult.servlet.multipart.FileInfo;

/**
 * <p>
 * This class is a servlet input stream of multipart handling.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MultipartInputStream extends ServletInputStream {
    private final byte[] bytes;
    private int index = 0;

    public MultipartInputStream(Map<String, List<String>> parameters, Map<String, FileInfo> files) throws IOException {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(boas));
        for (String key : parameters.keySet()) {
            List<String> values = parameters.get(key);
            for (String value : values) {
                writer.append("--AaB03x\r\nContent-Disposition: form-data; name=\"").
                    append(key).
                    append("\"\r\n\r\n").
                    append(value).
                    append("\r\n");
            }
        }

        for (String key : files.keySet()) {
            FileInfo file = files.get(key);
            writer.append("--AaB03x\r\nContent-Disposition: form-data; name=\"").
                append(key).
                append("\"; filename=\"").
                append(file.file.getName()).
                append("\"\r\nContent-Type: ").
                append(file.contentType).
                append("\r\nContent-Transfer-Encoding: binary\r\n\r\n");
            writer.flush();

            byte[] ba = FileTools.getBytes(file.file);
            boas.write(ba);
            writer.append("\r\n");
        }

        writer.append("--AaB03x--");
        writer.flush();
        boas.flush();
        bytes = boas.toByteArray();
    }

    @Override
    public int available() throws IOException {
        return bytes.length;
    }

    public int read() throws IOException {
        if (index == bytes.length) {
            return -1;
        }
        return bytes[index++];
    }
}
