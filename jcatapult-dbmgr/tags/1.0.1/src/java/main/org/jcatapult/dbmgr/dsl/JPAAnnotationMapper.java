package org.jcatapult.dbmgr.dsl;

import java.util.List;

import org.jcatapult.dbmgr.dsl.domain.Table;

/**
 * Interface for processing JPA Annotations from jpa entities
 *
 * @author jhumphrey
 */
public interface JPAAnnotationMapper<T> {

    /**
     * Returns a {@link org.jcatapult.dbmgr.dsl.domain.Table} obtained from
     * processing the annotations within the jpa entity object
     * @param jpaEntity the jpa entity
     * @return return type T
     */
    public T getMapping(Class jpaEntity);
}
