<#--
	Only show message if errors are available.
	This will be done if ActionSupport is used.
-->
<#assign hasFieldErrors = parameters.name?exists && fieldErrors?exists && fieldErrors[parameters.name]?exists/>

<#--
	if the label position is top, output the label in a div.
-->
<#if parameters.label?exists && parameters.labelposition?default("") == 'top'>
<div class="label" <#rt/>
<#if parameters.id?exists>id="${parameters.id}_label"</#if><#rt/>>
</#if>

<#--
  Handle the label
-->
<#if parameters.label?exists>
  <label <#t/>
<#if parameters.id?exists>
  for="${parameters.id?html}" <#t/>
</#if>
<#if hasFieldErrors>
  class="errorLabel"<#t/>
<#else>
  class="label"<#t/>
</#if>
><#t/>
<#if parameters.required?default(false) && parameters.requiredposition?default("right") != 'right'>
  <span class="required">*</span><#t/>
</#if>
${parameters.label?html}<#t/>
<#if parameters.required?default(false) && parameters.requiredposition?default("right") == 'right'>
 <span class="required">*</span><#t/>
</#if>
<#if hasFieldErrors>
  (${fieldErrors[parameters.name][0]?html})
</#if>
<#--<#include "/${parameters.templateDir}/xhtml/tooltip.ftl" />-->
</label><#t/>
</#if>
<#lt/>
<#if parameters.label?exists && parameters.labelposition?default("") == 'top'>
</div>
</#if>
<div class="control" <#rt/>
<#if parameters.id?exists>id="${parameters.id}_control"</#if><#rt/>>
