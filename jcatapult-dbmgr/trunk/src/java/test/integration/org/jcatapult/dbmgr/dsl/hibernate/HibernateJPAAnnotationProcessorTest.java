package org.jcatapult.dbmgr.dsl.hibernate;

import org.jcatapult.dbmgr.dsl.domain.Table;
import org.jcatapult.dbmgr.domain.TestEntity;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jhumphrey
 */
public class HibernateJPAAnnotationProcessorTest {

    @Test
    public void testGetMappings() {
        HibernateJPAAnnotationMapper p = new HibernateJPAAnnotationMapper();
        Table table = p.getMapping(TestEntity.class);

        Assert.assertNotNull(table);

        Assert.assertEquals("test_entity", table.name);
    }
}
