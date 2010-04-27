<#-- @ftlvariable name="messageStore" type="org.jcatapult.mvc.message.MessageStore" -->
<#-- @ftlvariable name="result" type="org.jcatapult.module.cms.service.CreateResult" -->
<#-- @ftlvariable name="locale" type="java.util.Locale" -->
({
  <#if (messageStore.fieldErrors?keys?size > 0)>
  "success": false,
  "errors": {
    <#list messageStore.fieldErrors?keys as key>
      "${key}": [
      <#list messageStore.fieldErrors[key] as error>
        "${error}"<#if error_has_next>, </#if>
      </#list>
      ]
    </#list>
  <#else>
  "success": true,
  "name": '${name}',
  "created": ${result.created?string},
  "pending": ${result.pending?string}
  </#if>
})