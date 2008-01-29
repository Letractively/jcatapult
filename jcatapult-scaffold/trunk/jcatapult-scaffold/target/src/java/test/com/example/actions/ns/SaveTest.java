package com.example.actions.ns.bean;

import static junit.framework.Assert.*;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.junit.Test;

import org.jcatapult.scaffold.Bean;
import com.example.services.BeanService;

/**
 * <p>
 * This class tests the save action.
 * </p>
 *
 * @author  Scaffolder
 */
public class SaveTest {
    /**
     * Tests save.
     */
    @Test
    public void testSave() {
        Integer[] categoriesIds = new Integer[]{1, 2, 3};
        Bean bean = new Bean();
        BeanService service = EasyMock.createStrictMock(BeanService.class);
        service.persist(bean, 1, categoriesIds);
        EasyMock.replay(service);

        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.replay(configuration);

        Save save = new Save(service, configuration);
        save.setBean(bean);
        save.setEmailId(1);
        save.setCategoriesIds(categoriesIds);
        String result = save.execute();
        assertEquals("success", result);
        EasyMock.verify(service, configuration);
    }
}