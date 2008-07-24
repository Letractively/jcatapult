[#ftl/]
[#if attributes['errors']]
  [#assign errors = action_errors/]
[#else]
  [#assign errors = action_messages/]
[/#if]
[#if errors?size > 0]
<ul class="action-errors">
  [#list errors as error]
  <li class="action-error">${error}</li>
  [/#list]
</ul>
[/#if]
