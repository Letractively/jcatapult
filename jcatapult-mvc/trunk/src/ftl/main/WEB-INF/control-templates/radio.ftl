[#ftl/]
[#include "parameter-attributes.ftl"/]
<div class="input">
[#include "label.ftl"/]
<div class="control-container"><input type="radio"${append_attributes(attributes)}/><input type="hidden" name="__jc_rb_${attributes['name']}" value=""/></div>
</div>