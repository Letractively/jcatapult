<#import "/global/macros.ftl" as g/>
<#macro fieldValidations localType prefix>
  <#list localType.allFields as field>
    <#if !field.static && !field.final && field.name != "id" && field.mainType.simpleType && g.required(field)>
  <field name="${prefix}${localType.fieldName}.${field.name}">
    <field-validator type="required<#if field.mainType.fullName == "java.lang.String">string</#if>">
      <message key="missing.${prefix}${localType.fieldName}.${field.name}"/>
    </field-validator>
  </field>
    <#elseif !field.static && !field.final && !field.mainType.primitive && field.mainType.hasAnnotation("javax.persistence.Embeddable")>
      <@fieldValidations field.mainType prefix + localType.fieldName + "." />
    </#if>
  </#list>
</#macro>
<!DOCTYPE validators PUBLIC "-//OpenSymphony Group//XWork Validator 1.0.2//EN"
       "http://www.opensymphony.com/xwork/xwork-validator-1.0.2.dtd">
<validators>
  <!-- Validation for ManyToOne and ManyToMany -->
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
  <field name="${field.name}Id">
    <field-validator type="required">
      <message key="missing.${field.name}"/>
    </field-validator>
  </field>
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany")>
  <field name="${field.name}Ids">
    <field-validator type="fieldexpression">
      <param name="expression">${field.name}Ids != null and ${field.name}Ids.length > 0</param>
      <message key="missing.${field.name}"/>
    </field-validator>
  </field>
  </#if>
</#list>

  <!-- Validation for fields -->
  <@fieldValidations type ""/>
</validators>