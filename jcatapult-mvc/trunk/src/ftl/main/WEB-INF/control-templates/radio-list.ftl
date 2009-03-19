[#ftl/]
[#include "dynamic-attributes.ftl"/]
<div class="radio-list input control">
[#include "label.ftl"/]
[#list options?keys as key]
<div class="radio-list-item-container input-container control-container">
<input type="radio"[#if options[key].selected] checked="checked"[/#if] value="${key}"${append_attributes(attributes, ['id'])}/><span class="radio-text">${options[key].text}</span>
</div>
[/#list]
</div>
<input type="hidden" name="__jc_rb_${attributes['name']}" value="${uncheckedValue}"/>
