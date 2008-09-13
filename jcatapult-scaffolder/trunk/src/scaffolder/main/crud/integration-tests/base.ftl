<#import "/global/macros.ftl" as global>
package ${actionPackage};

import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.persistence.test.JPABaseTest;

import com.google.inject.Inject;

import ${type.fullName};

/**
 * <p>
 * This class is the base integration test.
 * </p>
 *
 * @author  Scaffolder
 */
public abstract class BaseIntegrationTest extends JPABaseTest {
    @Inject PersistenceService persistenceService;

    /**
     * Creates an ${type.name}. This assumes that all relationship objects have seed values in the database.
     * If this isn't true, they should be created here.
     *
     * @return  The ${type.name}.
     */
    protected ${type.name} make${type.name}() {
        ${type.name} ${type.fieldName} = new ${type.name}();
<#list type.allFields as field>
<#if field.mainType.fullName == "java.lang.String" && !field.hasAnnotation("javax.persistence.Transient")>
        ${type.fieldName}.set${field.methodName}("test ${field.name}");
</#if>
</#list>

        persistenceService.persist(${type.fieldName});
        return ${type.fieldName};
    }
}