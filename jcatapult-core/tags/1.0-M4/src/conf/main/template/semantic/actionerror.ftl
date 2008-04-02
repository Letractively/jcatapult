<#if (actionErrors?exists && actionErrors?size > 0)>
	<ul id="action-errors">
	<#list actionErrors as error>
		<li><span class="error-message">${error}</span></li>
	</#list>
	</ul>
</#if>
