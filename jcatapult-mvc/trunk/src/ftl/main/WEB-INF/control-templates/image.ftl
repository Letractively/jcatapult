[#ftl/]
[#include "dynamic-attributes.ftl"/]
<input type="hidden" name="__jc_a_${attributes['name']}" value="${actionURI!''}"/>
<div class="image-button button control">
<div class="image-button-label-container button-label-container no-label-container"> </div>
<div class="image-button-container button-container control-container"><input type="image"${append_attributes(attributes)}/></div>
</div>
