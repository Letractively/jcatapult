[#ftl/]
[#include "dynamic-attributes.ftl"/]
<div class="checkbox input control">
[#include "label.ftl"/]
<div class="checkbox-container input-container control-container"><input type="checkbox"${append_attributes(attributes)}/><input type="hidden" name="__jc_cb_${attributes['name']}" value="${uncheckedValue}"/></div>
</div>
