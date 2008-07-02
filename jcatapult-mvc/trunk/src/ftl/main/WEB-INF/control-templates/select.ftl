[#ftl/]
[#include "dynamic-attributes.ftl"/]
<div class="input">
[#include "label.ftl"/]
<div class="control-container">
  <select${append_attributes(attributes)}>
    [#list options?keys as key]
    <option value="${key}"[#if options[key].selected] selected="selected"[/#if]>${options[key].text}</option>
    [/#list]
  </select>
</div>
</div>
