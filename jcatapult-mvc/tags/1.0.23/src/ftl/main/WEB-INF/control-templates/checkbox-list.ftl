[#ftl/]
[#include "class-helper.ftl"/]
[#include "dynamic-attributes.ftl"/]
<div class="[@class attributes, 'checkbox-list' true/]">
<div class="label-container">[#include "label.ftl"/]</div>
<div class="control-container">
[#list options?keys as key]
<div class="control-item-container">
<input type="checkbox"[#if options[key].selected] checked="checked"[/#if] value="${key}"${append_attributes(attributes, ['id'])}/><span class="checkbox-text">${options[key].text}</span>
</div>
[/#list]
</div>
</div>
