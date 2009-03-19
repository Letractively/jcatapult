[#ftl/]
[#include "dynamic-attributes.ftl"/]
<div class="select input control">
[#include "label.ftl"/]
<div class="select-container input-container control-container">
<select${append_attributes(attributes)}>
[#list options?keys as key]
<option value="${key}"[#if options[key].selected] selected="selected"[/#if]>${options[key].text}</option>
[/#list]
</select>
</div>
</div>
