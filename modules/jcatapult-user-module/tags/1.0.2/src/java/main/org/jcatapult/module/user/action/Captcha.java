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
package org.jcatapult.module.user.action;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

import org.jcatapult.module.user.service.CaptchaResult;
import org.jcatapult.module.user.service.CaptchaService;
import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Stream;
import org.jcatapult.mvc.scope.annotation.Session;

import com.google.inject.Inject;

/**
 * <p>
 * This class provides the Captcha image. It uses a service interface to
 * abstract out the captcha generation and by default uses Simple Captcha.
 * This action uses the JCatapult MVC extension support to determine what
 * the resulting image format is. It currently supports PNGs only because
 * simple captcha doesn't support anything else. Therefore, it must be
 * used like this:
 * </p>
 *
 * <pre>
 * &lt;img src="/captcha.png"/>
 * </pre>
 *
 * @author  Brian Pontarelli
 */
@Action
@Stream()
public class Captcha {
    private final CaptchaService captchaService;

    @Session
    public String captchaText;

    public InputStream stream;
    public String type;
    public long length;
    public final String name = null; // No disposition

    @Inject
    public Captcha(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    public String gif() {
        render("gif");
        return "success";
    }

    public String jpg() {
        render("jpg");
        return "success";
    }

    public String png() {
        render("png");
        return "success";
    }

    private void render(String format) {
        CaptchaResult result = captchaService.createImage(format);
        RenderedImage image = result.image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, format, baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        captchaText = result.text;
        stream = new ByteArrayInputStream(baos.toByteArray());
        type = "image/" + type;
        length = baos.size();
    }
}
