[#ftl/]
[#include "class-helper.ftl"/]
[#include "dynamic-attributes.ftl"/]
<input type="hidden" name="__jc_a_${attributes['name']}" value="${actionURI!''}"/>
<div class="[@class attributes, 'submit-button' false/]">
<div class="label-container"> </div>
<div class="control-container"><input type="submit"${append_attributes(attributes)}/></div>
</div>
