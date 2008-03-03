<#if fieldErrors?exists><#t/>
<#assign eKeys = fieldErrors.keySet()><#t/>
<#assign eKeysSize = eKeys.size()><#t/>
<#assign doneStartUlTag=false><#t/>
<#assign doneEndUlTag=false><#t/>
<#assign haveMatchedErrorField=false><#t/>
<#if (fieldErrorFieldNames?size > 0) ><#t/>
	<#list fieldErrorFieldNames as fieldErrorFieldName><#t/>
		<#list eKeys as eKey><#t/>
		<#if (eKey = fieldErrorFieldName)><#t/>
			<#assign haveMatchedErrorField=true><#t/>
			<#assign eValue = fieldErrors[fieldErrorFieldName]><#t/>
			<#if (haveMatchedErrorField && (!doneStartUlTag))><#t/>
<ul <#t/>
        <#if parameters.cssClass?exists>
class="${parameters.cssClass?html}"<#rt/>
        <#else>
class="field-errors"<#rt/>
        </#if>
        <#if parameters.cssStyle?exists>
style="${parameters.cssStyle?html}"<#rt/>
        </#if>
        <#include "/${parameters.templateDir}/semantic/scripting-events.ftl" />
        <#include "/${parameters.templateDir}/semantic/common-attributes.ftl" />
        <#assign doneStartUlTag=true><#t/>>
			</#if><#t/>
			<#list eValue as eEachValue><#t/>
<li <#rt/>
        <#if parameters.cssErrorClass?exists>
class="${parameters.cssErrorClass?html}"<#rt/>
        <#else>
class="field-error"<#rt/>
        </#if>
        <#if parameters.cssErrorStyle?exists>
style="${parameters.cssErrorStyle?html}"<#rt/>
        </#if>
>${eEachValue}</li><#rt/>
			</#list><#t/>
		</#if><#t/>
		</#list><#t/>
	</#list><#t/>
	<#if (haveMatchedErrorField && (!doneEndUlTag))><#t/>
		</ul><#lt/>
		<#assign doneEndUlTag=true><#t/>
	</#if><#t/>
<#else><#t/>
	<#if (eKeysSize > 0)><#t/>
<ul <#t/>
        <#if parameters.cssClass?exists>
class="${parameters.cssClass?html}"<#rt/>
        <#else>
class="field-errors"<#rt/>
        </#if>
        <#if parameters.cssStyle?exists>
style="${parameters.cssStyle?html}"<#rt/>
        </#if>
			<#list eKeys as eKey><#t/>
				<#assign eValue = fieldErrors[eKey]><#t/>
				<#list eValue as eEachValue><#t/>
<li <#rt/>
        <#if parameters.cssErrorClass?exists>
class="${parameters.cssErrorClass?html}"<#rt/>
        <#else>
class="field-error"<#rt/>
        </#if>
        <#if parameters.cssErrorStyle?exists>
style="${parameters.cssErrorStyle?html}"<#rt/>
        </#if>
>${eEachValue}</li><#rt/>
				</#list><#t/>
			</#list><#t/>
		</ul><#lt/>
	</#if><#t/>
</#if><#t/>
</#if><#t/>
