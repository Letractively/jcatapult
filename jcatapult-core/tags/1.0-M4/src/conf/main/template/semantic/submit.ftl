<div class="control">
<#if parameters.type?exists && parameters.type=="button">
<button type="submit"<#rt/>
<#elseif parameters.type?exists && parameters.type=="image">
<@s.property value="parameters.body"/>
<input type="image"<#rt/>
<#if parameters.label?exists>
 alt="${parameters.label?html}"<#rt/>
</#if>
<#if parameters.src?exists>
 src="${parameters.src?html}"<#rt/>
</#if>
<#else>
<input type="submit"<#rt/>
</#if>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.name?exists>
 name="${parameters.name?html}"<#rt/>
</#if>
<#if parameters.nameValue?exists>
 value="<@s.property value="parameters.nameValue"/>"<#rt/>
</#if>
<#if parameters.title?exists>
 title="${parameters.title?html}"<#rt/>
</#if>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.cssStyle?exists>
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/semantic/scripting-events.ftl" />
<#include "/${parameters.templateDir}/semantic/common-attributes.ftl" />
<#if parameters.type?exists && parameters.type=="button">
<#if parameters.body?length gt 0>
><@s.property value="parameters.body"/></button><#rt/>
<#elseif parameters.label?exists>
><@s.property value="parameters.label"/></button><#rt/>
</#if>
<#else>
/><#rt/>
</#if>
</div>