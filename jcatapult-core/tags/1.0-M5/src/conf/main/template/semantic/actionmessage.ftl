<#if (actionMessages?exists && actionMessages?size > 0)>
	<ul id="action-messages">
		<#list actionMessages as message>
			<li><span class="action-message">${message}</span></li>
		</#list>
	</ul>
</#if>
