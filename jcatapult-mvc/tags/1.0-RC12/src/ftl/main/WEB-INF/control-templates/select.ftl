[#ftl/]
[#include "dynamic-attributes.ftl"/]
<div class="select input control">
<div class="select-label input-label control-label">[#include "label.ftl"/]</div>
<div class="select-container input-container control-container">
<select${append_attributes(attributes)}>
[#list options?keys as key]
<option value="${key}"[#if options[key].selected] selected="selected"[/#if]>${options[key].text}</option>
[/#list]
</select>
</div>
</div>
