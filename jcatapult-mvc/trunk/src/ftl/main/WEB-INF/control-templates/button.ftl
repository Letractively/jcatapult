[#ftl/]
[#include "dynamic-attributes.ftl"/]
<input type="hidden" name="__jc_a_${attributes['name']}" value="${actionURI!''}"/>
<div class="button-button button control">
<div class="button-button-label button-label control-label"> </div>
<div class="button-button-container button-container control-container"><input type="button"${append_attributes(attributes)}/></div>
</div>
