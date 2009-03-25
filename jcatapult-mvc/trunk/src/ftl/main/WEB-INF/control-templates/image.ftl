[#ftl/]
[#include "class-helper.ftl"/]
[#include "dynamic-attributes.ftl"/]
<input type="hidden" name="__jc_a_${attributes['name']}" value="${actionURI!''}"/>
<div class="[@class attributes, 'image-button' false/]">
<div class="label-container"> </div>
<div class="control-container"><input type="image"${append_attributes(attributes)}/></div>
</div>
