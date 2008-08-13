[#ftl/]
[#include "dynamic-attributes.ftl"/]
[#include "label.ftl"/]
[#list options?keys as key]
<div class="input">
<div class="control-container">
  <input type="radio"[#if options[key].selected] checked="checked"[/#if] value="${key}"${append_attributes(attributes, ['id'])}/><span class="radio-text">${options[key].text}</span>
</div>
</div>
[/#list]
<input type="hidden" name="__jc_rb_${attributes['name']}" value="${uncheckedValue}"/>
