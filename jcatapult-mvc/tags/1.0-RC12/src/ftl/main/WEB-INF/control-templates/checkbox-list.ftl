[#ftl/]
[#include "dynamic-attributes.ftl"/]
<div class="checkbox-list input control">
<div class="checkbox-list-label input-label control-label">[#include "label.ftl"/]</div>
[#list options?keys as key]
<div class="checkbox-list-item-container input-container control-container">
<input type="checkbox"[#if options[key].selected] checked="checked"[/#if] value="${key}"${append_attributes(attributes, ['id'])}/><span class="checkbox-text">${options[key].text}</span>
</div>
[/#list]
</div>
