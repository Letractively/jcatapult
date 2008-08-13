[#ftl/]
[#include "dynamic-attributes.ftl"/]
<div class="input">
[#include "label.ftl"/]
<div class="control-container"><input type="checkbox"${append_attributes(attributes)}/><input type="hidden" name="__jc_cb_${attributes['name']}" value="${uncheckedValue}"/></div>
</div>
