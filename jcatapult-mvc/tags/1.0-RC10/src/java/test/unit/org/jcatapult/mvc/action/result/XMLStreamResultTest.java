package org.jcatapult.mvc.action.result;

import java.io.IOException;
import java.lang.annotation.Annotation;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.jcatapult.mvc.action.DefaultActionInvocation;
import org.jcatapult.mvc.action.result.annotation.XMLStream;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.test.servlet.MockServletOutputStream;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the XML Stream result.
 * </p>
 *
 * @author  jhumphrey
 */
public class XMLStreamResultTest {
    @Test
    public void testExplicit() throws IOException, ServletException {

        String property = "xml";
        String propertyValue = "<xml/>";
        byte[] propertyBytes = propertyValue.getBytes();
        int propertyBytesLen = propertyBytes.length;
        String contentType = "application/xhtml+xml";

        Object action = new Object();
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue(property, action)).andReturn(propertyValue);
        EasyMock.replay(ee);

        MockServletOutputStream sos = new MockServletOutputStream();
        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        response.setContentType(contentType);
        response.setContentLength(propertyBytesLen);
        EasyMock.expect(response.getOutputStream()).andReturn(sos);
        EasyMock.replay(response);

        XMLStream xmlStream = new XMLStreamResultTest.XMLStreamImpl("success", "xml");
        XMLStreamResult streamResult = new XMLStreamResult(ee, response);
        streamResult.execute(xmlStream, new DefaultActionInvocation(action, "/foo", "", null));

        assertEquals("<xml/>", sos.toString());

        EasyMock.verify(ee, response);
    }

    public class XMLStreamImpl implements XMLStream {
        private final String code;
        private final String property;

        public XMLStreamImpl(String code, String property) {
            this.code = code;
            this.property = property;
        }

        public String code() {
            return code;
        }

        public String property() {
            return property;
        }

        public Class<? extends Annotation> annotationType() {
            return XMLStream.class;
        }
    }
}
