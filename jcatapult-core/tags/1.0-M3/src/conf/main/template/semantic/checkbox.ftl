<#include "/${parameters.templateDir}/semantic/fieldheader.ftl" />
<input type="checkbox" name="${parameters.name?html}" value="${parameters.fieldValue?html}"<#rt/>
<#if parameters.nameValue?exists && parameters.nameValue || parameters.defaultChecked?exists && parameters.defaultChecked>
 checked="checked"<#rt/>
</#if>
<#if parameters.disabled?default(false)>
 disabled="disabled"<#rt/>
</#if>
<#if parameters.readonly?default(false)>
 readonly="readonly"<#rt/>
</#if>
<#if parameters.tabindex?exists>
 tabindex="${parameters.tabindex?html}"<#rt/>
</#if>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.cssStyle?exists>
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
<#if parameters.title?exists>
 title="${parameters.title?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/semantic/scripting-events.ftl" />
<#include "/${parameters.templateDir}/semantic/common-attributes.ftl" />
/>
<input type="hidden" name="__checkbox_${parameters.name?html}" value="${parameters.fieldValue?html}"/>
<#include "/${parameters.templateDir}/semantic/fieldfooter.ftl" />