[#ftl/]
[#include "class-helper.ftl"/]
[#include "dynamic-attributes.ftl"/]
<div class="[@class attributes, 'select' true/]">
<div class="label-container">[#include "label.ftl"/]</div>
<div class="control-container">
<select${append_attributes(attributes)}>
[#list options?keys as key]
<option value="${key}"[#if options[key].selected] selected="selected"[/#if]>${options[key].text}</option>
[/#list]
</select>
</div>
</div>
