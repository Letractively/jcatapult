/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
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
package org.jcatapult.struts.convert;

import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.jcatapult.servlet.ServletObjectsHolder;
import org.jcatapult.test.servlet.MockServletRequest;
import org.joda.time.DateTime;
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This class tests the date time converter.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DateTimeTypeConverterTest {
    @Test
    public void testFormatInValues() {
        ServletRequest request = EasyMock.createStrictMock(ServletRequest.class);
        EasyMock.expect(request.getAttribute("foo.bar#attributes")).andReturn(mapNV("dateTimeFormat", "MM/dd/yyyy hh:mm aa"));
        request.setAttribute(EasyMock.eq("foo.bar#attributes"), EasyMock.eq(mapNV("dateTimeFormat", "MM/dd/yyyy hh:mm aa")));
        EasyMock.replay(request);
        ServletObjectsHolder.setServletRequest((HttpServletRequest) request);

        String[] values = {"10/20/2006 10:30 PM"};
        DateTimeTypeConverter converter = new DateTimeTypeConverter();
        Map context = mapNV("conversion.property.fullName", "foo.bar");
        DateTime dt = (DateTime) converter.convertValue(context, null, null, null, values, DateTime.class);
        assertNotNull(dt);
        assertEquals(20, dt.getDayOfMonth());
        assertEquals(2006, dt.getYear());
        assertEquals(30, dt.getMinuteOfHour());
    }

    @Test
    public void testFormatStylePattern() {
        ServletRequest request = new MockServletRequest();
        ServletObjectsHolder.setServletRequest((HttpServletRequest) request);

        String values = "10-20-2006 10:30:00 PM";
        DateTimeTypeConverter converter = new DateTimeTypeConverter();
        Map<String, Object> context = mapNV("conversion.property.fullName", "foo.bar");
        DateTime dt = (DateTime) converter.convertValue(context, null, null, null, values, DateTime.class);
        assertNotNull(dt);
        assertEquals(20, dt.getDayOfMonth());
        assertEquals(2006, dt.getYear());
        assertEquals(30, dt.getMinuteOfHour());
        assertEquals("MM-dd-yy hh:mm:ss aa", ((Map<String, Object>) request.getAttribute("foo.bar#attributes")).get("dateTimeFormat"));
    }

    @Test
    public void testFormatStylePatternPartial() {
        ServletRequest request = new MockServletRequest();
        ServletObjectsHolder.setServletRequest((HttpServletRequest) request);

        String values = "10-20-06";
        DateTimeTypeConverter converter = new DateTimeTypeConverter();
        Map<String, Object> context = mapNV("conversion.property.fullName", "foo.bar");
        DateTime dt = (DateTime) converter.convertValue(context, null, null, null, values, DateTime.class);
        assertNotNull(dt);
        assertEquals(20, dt.getDayOfMonth());
        assertEquals(2006, dt.getYear());
        assertEquals("MM-dd-yy", ((Map<String, Object>) request.getAttribute("foo.bar#attributes")).get("dateTimeFormat"));
    }

    @Test
    public void testOutput() {
        ServletRequest request = new MockServletRequest();
        request.setAttribute("foo.bar#attributes", mapNV("dateTimeFormat", "MM/dd/yyyy hh:mm aa"));
        ServletObjectsHolder.setServletRequest((HttpServletRequest) request);

        DateTime now = new DateTime(2006, 10, 20, 22, 30, 0, 0);
        DateTimeTypeConverter converter = new DateTimeTypeConverter();
        Map<String, Object> context = mapNV("conversion.property.fullName", "foo.bar");
        String value = (String) converter.convertValue(context, null, null, null, now, String.class);
        assertNotNull(value);
        assertEquals("10/20/2006 10:30 PM", value);
    }
}