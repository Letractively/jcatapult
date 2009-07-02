/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
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
package org.jcatapult.module.user.service;

import org.jcatapult.config.Configuration;

import com.google.inject.Inject;
import nl.captcha.Captcha;

/**
 * <p>
 * This class uses the Simple Captcha library to create captcha images. It
 * provides a number of different configuration parameters:
 * </p>
 *
 * <table border="1">
 * <tr><th>Name</th><th>Description</th><th>Default</th></tr>
 * <tr><td>jcatapult.user.captcha.width</td><td>Sets the width of the image (int).</td><td>200</td></tr>
 * <tr><td>jcatapult.user.captcha.height</td><td>Sets the height of the image (int).</td><td>50</td></tr>
 * <tr><td>jcatapult.user.captcha.noise</td><td>Determines if the image has noise (boolean).</td><td>true</td></tr>
 * <tr><td>jcatapult.user.captcha.gimp</td><td>Determines if the image is gimped (boolean).</td><td>true</td></tr>
 * <tr><td>jcatapult.user.captcha.border</td><td>Determines if the image has a border (boolean).</td><td>true</td></tr>
 * </table>
 *
 * @author  Brian Pontarelli
 */
public class SimpleCaptchaService implements CaptchaService {
    private final int width;
    private final int height;
    private final boolean noise;
    private final boolean gimp;
    private final boolean border;

    @Inject
    public SimpleCaptchaService(Configuration config) {
        width = config.getInt("jcatapult.user.captcha.width", 200);
        height = config.getInt("jcatapult.user.captcha.height", 50);
        noise = config.getBoolean("jcatapult.user.captcha.noise", true);
        gimp = config.getBoolean("jcatapult.user.captcha.gimp", true);
        border = config.getBoolean("jcatapult.user.captcha.border", true);
    }

    /**
     * {@inheritDoc}
     * @param imageFormat
     */
    public CaptchaResult createImage(String imageFormat) {
        Captcha.Builder builder= new Captcha.Builder(width, height);
        if (noise) {
            builder.addNoise();
        }
        if (gimp) {
            builder.gimp();
        }
        if (border) {
            builder.addBorder();
        }

        Captcha captcha = builder.addText().build();
        return new CaptchaResult(captcha.getImage(), captcha.getAnswer());
    }
}
