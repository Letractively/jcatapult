<#macro importFields>
  <#list type.allFields as field>
    <#if field.hasAnnotation("javax.persistence.ManyToOne")>
import ${field.mainType.fullName};
    <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name != "java.util.Map">
import ${field.genericTypes[0].fullName};
    <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name == "java.util.Map">
import ${field.genericTypes[1].fullName};
    </#if>
  </#list>
</#macro>

<#macro idVariables>
  <#list type.allFields as field>
    <#if field.hasAnnotation("javax.persistence.ManyToOne")>
    private Integer ${field.name}Id;
    </#if>
    <#if field.hasAnnotation("javax.persistence.ManyToMany")>
    private Integer[] ${field.name}Ids;
    </#if>
  </#list>
</#macro>

<#macro idProperties setters>
  <#list type.allFields as field>
    <#if field.hasAnnotation("javax.persistence.ManyToOne")>
    public Integer get${field.methodName}Id() {
        return ${field.name}Id;
    }
    <#if setters>

    public void set${field.methodName}Id(Integer id) {
        this.${field.name}Id = id;
    }
    </#if>

    </#if>
    <#if field.hasAnnotation("javax.persistence.ManyToMany")>
    public Integer[] get${field.methodName}Ids() {
        return ${field.name}Ids;
    }
    <#if setters>

    public void set${field.methodName}Ids(Integer[] ids) {
        this.${field.name}Ids = ids;
    }
    </#if>

    </#if>
  </#list>
</#macro>

<#macro idParams><#list type.allFields as field><#if field.hasAnnotation("javax.persistence.ManyToOne")>, ${field.name}Id</#if><#if field.hasAnnotation("javax.persistence.ManyToMany")>, ${field.name}Ids</#if></#list></#macro>

<#macro idParamsList><#list type.allFields as field><#if field.hasAnnotation("javax.persistence.ManyToOne")>, Integer ${field.name}Id</#if><#if field.hasAnnotation("javax.persistence.ManyToMany")>, Integer[] ${field.name}Ids</#if></#list></#macro>

<#macro idValues><#list type.allFields as field><#if field.hasAnnotation("javax.persistence.ManyToOne")>, 1</#if><#if field.hasAnnotation("javax.persistence.ManyToMany")>, ${field.name}Ids</#if></#list></#macro>

<#function firstPropertyName field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
    <#list field.mainType.allFields as f>
      <#if f.mainType.fullName == "java.lang.String"><#return f.name/></#if>
    </#list>
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.fullName != "java.util.Map">
    <#list field.genericTypes[0].allFields as f>
      <#if f.mainType.fullName == "java.lang.String"><#return f.name/></#if>
    </#list>
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.fullName == "java.util.Map">
    <#list field.genericTypes[1].allFields as f>
      <#if f.mainType.fullName == "java.lang.String"><#return f.name/></#if>
    </#list>
  </#if>
  <#return ""/>
</#function>

<#function jspEL value><#return '$' + '{' + value + '}'/></#function>

<#function required field>
  <#assign column = field.getAnnotation("javax.persistence.Column")!""/>
  <#if (column == "" && field.mainType.primitive && field.mainType.fullName != "boolean") ||
          (column != "" && !column.parameters['nullable']!true)>
    <#return true/>
  </#if>
  <#return false/>
</#function>
