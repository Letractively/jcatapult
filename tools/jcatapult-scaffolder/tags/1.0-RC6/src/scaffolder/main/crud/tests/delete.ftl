package ${actionPackage};

import static junit.framework.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

import ${servicePackage}.${type.name}Service;

/**
 * <p>
 * This class tests the delete action.
 * </p>
 *
 * @author  Scaffolder
 */
public class DeleteTest {
    /**
     * Tests the nothing is deleted if no ids are selected.
     */
    @Test
    public void testNoDelete() {
        ${type.name}Service service = EasyMock.createStrictMock(${type.name}Service.class);
        EasyMock.replay(service);

        Delete delete = new Delete(service);
        String result = delete.execute();
        assertEquals("success", result);
        EasyMock.verify(service);
    }

    /**
     * Tests that all the selected ids are deleted.
     */
    @Test
    public void testDelete() {
        int[] ids = new int[]{1, 2, 3};
        ${type.name}Service service = EasyMock.createStrictMock(${type.name}Service.class);
        service.deleteMany(ids);
        EasyMock.replay(service);

        Delete delete = new Delete(service);
        delete.setIds(ids);
        String result = delete.execute();
        assertEquals("success", result);
        EasyMock.verify(service);
    }
}