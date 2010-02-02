package org.jcatapult.dbmgr.dsl.hibernate;

import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;

import org.jcatapult.dbmgr.dsl.JPAAnnotationMapper;
import org.jcatapult.dbmgr.dsl.domain.Table;
import org.jcatapult.dbmgr.dsl.domain.Column;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.mapping.RootClass;

/**
 * @author jhumphrey
 */
public class HibernateJPAAnnotationMapper implements JPAAnnotationMapper<Table> {

    /**
     * {@inheritDoc}
     */
    public Table getMapping(Class jpaEntity) {
        AnnotationConfiguration config = new AnnotationConfiguration();
        config.addAnnotatedClass(jpaEntity);

        config.buildMappings();
        Iterator classMappings = config.getClassMappings();

        Table table = new Table();

        while (classMappings.hasNext()) {
            RootClass entity = (RootClass) classMappings.next();

            table.name = entity.getTable().getName();
            table.id = entity.getIdentifierProperty().getName();

            Iterator columns = entity.getTable().getColumnIterator();
            while (columns.hasNext()) {
                org.hibernate.mapping.Column columnMapping = (org.hibernate.mapping.Column) columns.next();
                if (!columnMapping.getName().equals(table.id)) {
                    Column dslColumn = new Column();
                    dslColumn.name = columnMapping.getName();
                    Column.DataType dataType = new Column.DataType();



                    dataType.type = columnMapping.getValue().getType().getName();
                    if (columnMapping.getLength() > 0) {
                        dataType.length = columnMapping.getLength();
                    }
                    dslColumn.type = dataType;
                    table.columns.add(dslColumn);
                }
            }
        }

        return table;
    }
}
