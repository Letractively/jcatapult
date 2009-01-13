<#import "/global/macros.ftl" as global>
package ${servicePackage};

import java.util.List;

import com.google.inject.ImplementedBy;

import ${type.fullName};
<@global.importFields />

/**
 * <p>
 * This is the service for dealing with {@link ${type.name}}s
 * </p>
 *
 * @author  Scaffolder
 */
@ImplementedBy(${type.name}ServiceImpl.class)
public interface ${type.name}Service {
    /**
     * Gets all of the ${type.pluralName}.
     *
     * @return  The List of ${type.pluralName}.
     */
    List<${type.name}> find();

    /**
     * Locates the ${type.name} with the given id.
     *
     * @param   id The ID of the ${type.name}.
     * @return  The ${type.name} or null if it doesn't exist or has been deleted.
     */
    ${type.name} findById(Integer id);

    /**
     * @return  The total number of ${type.pluralName}.
     */
    int getNumberOf${type.pluralName}();

    /**
     * Saves or updates the given ${type.name}.
     *
     * @param   ${type.fieldName} The ${type.name} to save or update.
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
     * @param   ${field.name}ID The id of the ${field.name} of the ${type.name}.
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany")>
     * @param   ${field.name}IDs The list of IDs for the ${field.name} of the ${type.name}.
  </#if>
</#list>
     */
    void persist(${type.name} ${type.fieldName}<@global.idParamsList />);

    /**
     * Deletes the ${type.name} with the given ID.
     *
     * @param   id The ID of the ${type.name} to delete.
     */
    void delete(int id);

    /**
     * Deletes the ${type.pluralName} with the given IDs.
     *
     * @param   ids The IDs of the ${type.pluralName} to delete.
     */
    void deleteMany(int[] ids);

<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
    /**
     * @return  A list of all the ${field.pluralName} for ${type.pluralName}.
     */
    List<${field.mainType.name}> get${field.mainType.pluralName}();

  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name != "java.util.Map">
    /**
     * @return  A list of all the ${field.pluralName} for ${type.pluralName}.
     */
    List<${field.genericTypes[0].name}> get${field.genericTypes[0].pluralName}();

  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name == "java.util.Map">
    /**
     * @return  A list of all the ${field.pluralName} for ${type.pluralName}.
     */
    List<${field.genericTypes[1].name}> get${field.genericTypes[1].pluralName}();

  </#if>
</#list>
}