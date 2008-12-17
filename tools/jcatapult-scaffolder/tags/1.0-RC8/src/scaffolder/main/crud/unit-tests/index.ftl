package ${actionPackage};

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

import ${type.fullName};
import ${servicePackage}.${type.name}Service;

/**
 * <p>
 * This class tests the index action.
 * </p>
 *
 * @author  Scaffolder
 */
public class IndexTest {
    /**
     * Tests index.
     */
    @Test
    public void testIndex() {
        List<${type.name}> findResult = new ArrayList<${type.name}>();
        ${type.name}Service service = EasyMock.createStrictMock(${type.name}Service.class);
        EasyMock.expect(service.find()).andReturn(findResult);
        EasyMock.replay(service);

        Index index = new Index(service);
        String result = index.get();
        assertEquals("success", result);
        assertSame(findResult, index.${type.pluralFieldName});
        EasyMock.verify(service);
    }
}