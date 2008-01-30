<#import "/global/macros.ftl" as global>
package ${servicePackage};

import java.util.ArrayList;
import java.util.List;

import com.texturemedia.catapult.service.CatapultBeanService;

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
    private CatapultBeanService catapultBeanService;

    @Inject
    public ${type.name}ServiceImpl(CatapultBeanService catapultBeanService) {
        this.catapultBeanService = catapultBeanService;
    }

    /**
     * {@inheritDoc}
     */
    public List<${type.name}> find(int page, int number, String sortProperty) {
        if (sortProperty == null) {
            sortProperty = getDefaultSortProperty();
        }

        page--;

        return catapultBeanService.queryPaginated(${type.name}.class, "select obj from ${type.name} obj " +
<#if type.isA("com.texturemedia.catapult.domain.SoftDeleteCatapultBean")>
            "where obj.deleted = false " +
</#if>
            "order by obj." + sortProperty, page * number, number);
    }

    /**
     * {@inheritDoc}
     */
    public List<${type.name}> find(String sortProperty) {
        if (sortProperty == null) {
            sortProperty = getDefaultSortProperty();
        }

        return catapultBeanService.query(${type.name}.class, "select obj from ${type.name} obj " +
<#if type.isA("com.texturemedia.catapult.domain.SoftDeleteCatapultBean")>
            "where obj.deleted = false " +
</#if>
            "order by obj." + sortProperty);
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
        return (int) catapultBeanService.queryCount(${type.name}.class,
        "select count(obj) from ${type.name} obj " <#if type.isA("com.texturemedia.catapult.domain.SoftDeleteCatapultBean")>
            + "where obj.deleted = false "
</#if> );
    }

    /**
     * {@inheritDoc}
     */
    public void persist(${type.name} ${type.fieldName}<@global.idParamsList />) {
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
        ${field.mainType.name} ${field.name} = catapultBeanService.getById(${field.mainType.name}.class, ${field.name}Id);
        ${type.fieldName}.set${field.methodName}(${field.name});

  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name != "java.util.Map">
        List<${field.genericTypes[0].name}> ${field.pluralName} = new ArrayList<${field.genericTypes[0].name}>();
        for (Integer id : ${field.name}Ids) {
            ${field.pluralName}.add(catapultBeanService.findById(${field.genericTypes[0].name}.class, id));
        }
        ${type.fieldName}.set${field.methodName}(${field.pluralName});

  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name == "java.util.Map">
        List<${field.genericTypes[1].name}> ${field.pluralName} = new ArrayList<${field.genericTypes[1].name}>();
        for (Integer id : ${field.name}Ids) {
            ${field.pluralName}.add(catapultBeanService.findById(${field.genericTypes[1].name}.class, id));
        }
        ${type.fieldName}.set${field.methodName}(${field.pluralName});

  </#if>
</#list>
        catapultBeanService.persist(${type.fieldName});
    }

    /**
     * {@inheritDoc}
     */
    public void delete(int id) {
        catapultBeanService.delete(${type.name}.class, id);
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
        return catapultBeanService.findAllByType(${field.mainType.name}.class<#if field.mainType.isA("com.texturemedia.catapult.domain.SoftDeleteCatapultBean")>, false</#if>);
    }

  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name != "java.util.Map">
    /**
     * {@inheritDoc}
     */
    public List<${field.genericTypes[0].name}> get${field.genericTypes[0].pluralName}() {
        return catapultBeanService.getAll(${field.genericTypes[0].name}.class<#if field.genericTypes[0].isA("com.texturemedia.catapult.domain.SoftDeleteCatapultBean")>, false</#if>);
    }

  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name == "java.util.Map">
    /**
     * {@inheritDoc}
     */
    public List<${field.genericTypes[1].name}> get${field.genericTypes[1].pluralName}() {
        return catapultBeanService.findAllByType(${field.genericTypes[1].name}.class<#if field.genericTypes[1].isA("com.texturemedia.catapult.domain.SoftDeleteCatapultBean")>, false</#if>);
    }

  </#if>
</#list>
    /**
     * {@inheritDoc}
     */
    public ${type.name} getById(Integer id) {
        ${type.name} ${type.fieldName} = catapultBeanService.getById(${type.name}.class, id);
<#if type.isA("com.texturemedia.catapult.domain.SoftDeleteCatapultBean")>
        if (${type.fieldName} != null && ${type.fieldName}.isDeleted()) {
            return null;
        }

</#if>
        return ${type.fieldName};
    }
}
