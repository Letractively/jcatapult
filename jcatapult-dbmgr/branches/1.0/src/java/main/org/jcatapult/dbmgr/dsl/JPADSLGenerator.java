package org.jcatapult.dbmgr.dsl;

/**
 * Interface for generating groovy DSL script from a JPA Entity
 *
 * @author jhumphrey
 */
public interface JPADSLGenerator {

    /**
     * Generates Groovy DSL script
     *
     * @param processor {@link JPAAnnotationMapper} the annotation processor
     * @return the groovy script
     */
    public String generate(JPAAnnotationMapper processor);
}
