<!--
  ~ Copyright (c) 2008, Your Corporation. All Rights Reserved.
  -->

<#-- Output the hidden fields for all additional attributes -->
<#assign keys = dynamicAttrs?keys/>

<#list keys as key>
<input type="hidden" name="${name?default("")?html}@${key}" value="${dynamicAttrs[key]}"/>
</#list>

<#--
	Only show message if errors are available.
	This will be done if ActionSupport is used.
-->
<#assign hasFieldErrors = name?exists && fieldErrors?exists && fieldErrors[name]?exists/>

<#if fileURI == "">

  <#--
    if the label position is top, output the label in a div.
  -->
  <#if label?exists && dynamicAttrs.labelposition?default("top") == 'top'>
  <div class="label" <#rt/>
  <#if dynamicAttrs.id?exists>id="${dynamicAttrs.id}-label"</#if><#rt/>>
  </#if>

  <#--
    Handle the label
  -->
  <#if label?exists>
  <label <#t/>
  <#if dynamicAttrs.id?exists>
    for="${dynamicAttrs.id?html}" <#t/>
  </#if>
  <#if hasFieldErrors>
    class="error-label"<#t/>
  <#else>
    class="label"<#t/>
  </#if>
  ><#t/>
  <#if dynamicAttrs.required?default('false') == 'true' && dynamicAttrs.requiredposition?default("right") != 'right'>
  <span class="required">*</span><#t/>
  </#if>
  ${label?html}<#t/>
  <#if dynamicAttrs.required?default('false') == 'true' && dynamicAttrs.requiredposition?default("right") == 'right'>
  <span class="required">*</span><#t/>
  </#if>
  <#if hasFieldErrors>
    (${fieldErrors[dynamicAttrs.name][0]?html})
  </#if>
  </label><#t/>
  </#if>
  <#lt/>
  <#if label?exists && dynamicAttrs.labelposition?default("top") == 'top'>
  </div>
  </#if>
  <#if label?exists && dynamicAttrs.labelposition?default("top") == 'top'>
  <div class="field" <#rt/>
  <#else>
  <span class="field" <#rt/>
  </#if>
  <#if dynamicAttrs.id?exists>id="${dynamicAttrs.id}-control"</#if><#rt/>>

  <input type="file"<#rt/>
   name="${name?default("")?html}"<#rt/>
  <#if value?exists>
   id="${value?html}"<#rt/>
  </#if>
  <#if cssId?exists>
   id="${cssId?html}"<#rt/>
  </#if>
  <#if cssClass?exists>
   class="${cssClass?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.size?exists>
   size="${dynamicAttrs.size?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.disabled?default(false)>
   disabled="disabled"<#rt/>
  </#if>
  <#if dynamicAttrs.accept?exists>
   accept="${dynamicAttrs.accept?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.tabindex?exists>
   tabindex="${dynamicAttrs.tabindex?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.cssStyle?exists>
   style="${dynamicAttrs.cssStyle?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.title?exists>
   title="${dynamicAttrs.title?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.onclick?exists>
   onclick="${dynamicAttrs.onclick?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.ondblclick?exists>
   ondblclick="${dynamicAttrs.ondblclick?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.onmousedown?exists>
   onmousedown="${dynamicAttrs.onmousedown?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.onmouseup?exists>
   onmouseup="${dynamicAttrs.onmouseup?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.onmouseover?exists>
   onmouseover="${dynamicAttrs.onmouseover?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.onmousemove?exists>
   onmousemove="${dynamicAttrs.onmousemove?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.onmouseout?exists>
   onmouseout="${dynamicAttrs.onmouseout?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.onfocus?exists>
   onfocus="${dynamicAttrs.onfocus?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.onblur?exists>
   onblur="${dynamicAttrs.onblur?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.onkeypress?exists>
   onkeypress="${dynamicAttrs.onkeypress?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.onkeydown?exists>
   onkeydown="${dynamicAttrs.onkeydown?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.onkeyup?exists>
   onkeyup="${dynamicAttrs.onkeyup?html}"<#rt/>
  </#if>
  <#if onselect?exists>
   onselect="${dynamicAttrs.onselect?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.onchange?exists>
   onchange="${dynamicAttrs.onchange?html}"<#rt/>
  </#if>
  <#if dynamicAttrs.accesskey?exists>
   accesskey="${dynamicAttrs.accesskey?html}"
  </#if>/>
<#else>
  <a href="${fileURI}">${label}</a> | <a href="${deleteURI}">DELETE</a>
</#if>