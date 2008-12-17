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
    public Integer ${field.name}ID;
    </#if>
    <#if field.hasAnnotation("javax.persistence.ManyToMany")>
    public Integer[] ${field.name}IDs;
    </#if>
  </#list>
</#macro>

<#macro idParams><#list type.allFields as field><#if field.hasAnnotation("javax.persistence.ManyToOne")>, ${field.name}ID</#if><#if field.hasAnnotation("javax.persistence.ManyToMany")>, ${field.name}IDs</#if></#list></#macro>

<#macro idParamsList><#list type.allFields as field><#if field.hasAnnotation("javax.persistence.ManyToOne")>, Integer ${field.name}ID</#if><#if field.hasAnnotation("javax.persistence.ManyToMany")>, Integer[] ${field.name}IDs</#if></#list></#macro>

<#macro idValues><#list type.allFields as field><#if field.hasAnnotation("javax.persistence.ManyToOne")>, 1</#if><#if field.hasAnnotation("javax.persistence.ManyToMany")>, ${field.name}IDs</#if></#list></#macro>

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
  <#if field.hasAnnotation("org.jcatapult.mvc.validation.annotation.Required")>
    <#return true/>
  </#if>
  <#return false/>
</#function>
