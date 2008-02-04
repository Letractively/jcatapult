<#if (actionErrors?exists && actionErrors?size > 0)>
	<ul id="errors">
	<#list actionErrors as error>
		<li><span class="errorMessage">${error}</span></li>
	</#list>
	</ul>
</#if>
