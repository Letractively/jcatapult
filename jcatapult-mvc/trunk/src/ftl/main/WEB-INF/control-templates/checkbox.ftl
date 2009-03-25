[#ftl/]
[#include "class-helper.ftl"/]
[#include "dynamic-attributes.ftl"/]
<div class="[@class attributes, 'checkbox' true/]">
<div class="label-container">[#include "label.ftl"/]</div>
<div class="control-container"><input type="checkbox"${append_attributes(attributes)}/><input type="hidden" name="__jc_cb_${attributes['name']}" value="${uncheckedValue}"/></div>
</div>
