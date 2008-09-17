<#import "/global/macros.ftl" as global>
package ${servicePackage};

import java.util.ArrayList;
import java.util.List;

import org.jcatapult.persistence.service.PersistenceService;

import com.google.inject.Inject;
import ${type.fullName};
<@global.importFields />

/**
 * <p>
 * This is the implementation of the ${type.name}Service.
 * </p>
 *
 * @author  Scaffolder
 */
public class ${type.name}ServiceImpl implements ${type.name}Service {
    private PersistenceService persistenceService;

    @Inject
    public ${type.name}ServiceImpl(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    /**
     * {@inheritDoc}
     */
    public List<${type.name}> find() {
        return persistenceService.queryAll(${type.name}.class, "select obj from ${type.name} obj <#if type.isA("org.jcatapult.domain.SoftDeletable")>where obj.deleted = false</#if>");
    }

    /**
     * {@inheritDoc}
     */
    public ${type.name} findById(Integer id) {
        ${type.name} ${type.fieldName} = persistenceService.findById(${type.name}.class, id);
    <#if type.isA("org.jcatapult.domain.SoftDeletable")>
        if (${type.fieldName} != null && ${type.fieldName}.isDeleted()) {
            return null;
        }

    </#if>
        return ${type.fieldName};
    }

    /**
     * @return  The default sort property.
     */
    protected String getDefaultSortProperty() {
<#list type.allFields as field>
  <#if !field.static && !field.final && !field.hasAnnotation("javax.persistence.Transient") && field.mainType.fullName == "java.lang.String">
        return "${field.name}";
    <#break/>
  </#if>
</#list>
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOf${type.pluralName}() {
        return (int) persistenceService.count(${type.name}.class<#if type.isA("org.jcatapult.domain.SoftDeletable")>, false</#if>);
    }

    /**
     * {@inheritDoc}
     */
    public void persist(${type.name} ${type.fieldName}<@global.idParamsList />) {
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
        ${field.mainType.name} ${field.name} = persistenceService.findById(${field.mainType.name}.class, ${field.name}Id);
        ${type.fieldName}.set${field.methodName}(${field.name});

  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name != "java.util.Map">
        List<${field.genericTypes[0].name}> ${field.pluralName} = new ArrayList<${field.genericTypes[0].name}>();
        for (Integer id : ${field.name}IDs) {
            ${field.pluralName}.add(persistenceService.findById(${field.genericTypes[0].name}.class, id));
        }
        ${type.fieldName}.set${field.methodName}(${field.pluralName});

  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name == "java.util.Map">
        List<${field.genericTypes[1].name}> ${field.pluralName} = new ArrayList<${field.genericTypes[1].name}>();
        for (Integer id : ${field.name}IDs) {
            ${field.pluralName}.add(persistenceService.findById(${field.genericTypes[1].name}.class, id));
        }
        ${type.fieldName}.set${field.methodName}(${field.pluralName});

  </#if>
</#list>
        persistenceService.persist(${type.fieldName});
    }

    /**
     * {@inheritDoc}
     */
    public void delete(int id) {
        persistenceService.delete(${type.name}.class, id);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteMany(int[] ids) {
        for (int id : ids) {
            delete(id);
        }
    }

<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
    /**
     * {@inheritDoc}
     */
    public List<${field.mainType.name}> get${field.mainType.pluralName}() {
        return persistenceService.findAllByType(${field.mainType.name}.class<#if field.mainType.isA("org.jcatapult.domain.SoftDeletable")>, false</#if>);
    }

  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name != "java.util.Map">
    /**
     * {@inheritDoc}
     */
    public List<${field.genericTypes[0].name}> get${field.genericTypes[0].pluralName}() {
        return persistenceService.findAllByType(${field.genericTypes[0].name}.class<#if field.genericTypes[0].isA("org.jcatapult.domain.SoftDeletable")>, false</#if>);
    }

  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name == "java.util.Map">
    /**
     * {@inheritDoc}
     */
    public List<${field.genericTypes[1].name}> get${field.genericTypes[1].pluralName}() {
        return persistenceService.findAllByType(${field.genericTypes[1].name}.class<#if field.genericTypes[1].isA("org.jcatapult.domain.SoftDeletable")>, false</#if>);
    }

  </#if>
</#list>
}
