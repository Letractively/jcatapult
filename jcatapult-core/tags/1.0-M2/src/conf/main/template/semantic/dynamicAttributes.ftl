<#-- Output the hidden fields for all additional attributes -->
<#assign keys = parameters.dynamicAttributes?keys/>
<#list keys as key>
<input type="hidden" name="${parameters.name?default("")?html}@${key}" value="${parameters.dynamicAttributes[key]}"/>
</#list>