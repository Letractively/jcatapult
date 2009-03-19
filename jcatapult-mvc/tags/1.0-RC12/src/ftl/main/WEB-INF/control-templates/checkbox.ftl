[#ftl/]
[#include "dynamic-attributes.ftl"/]
<div class="checkbox input control">
<div class="checkbox-label input-label control-label">[#include "label.ftl"/]</div>
<div class="checkbox-container input-container control-container"><input type="checkbox"${append_attributes(attributes)}/><input type="hidden" name="__jc_cb_${attributes['name']}" value="${uncheckedValue}"/></div>
</div>
