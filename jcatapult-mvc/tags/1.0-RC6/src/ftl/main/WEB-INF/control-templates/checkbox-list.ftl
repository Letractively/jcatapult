[#ftl/]
[#include "dynamic-attributes.ftl"/]
[#include "label.ftl"/]
[#list options?keys as key]
<div class="input">
<div class="control-container">
  <input type="checkbox"[#if options[key].selected] checked="checked"[/#if] value="${key}"${append_attributes(attributes, ['id'])}/><span class="checkbox-text">${options[key].text}</span>
</div>
</div>
[/#list]
