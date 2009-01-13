[#ftl/]
[#include "dynamic-attributes.ftl"/]
<input type="hidden" name="__jc_a_${attributes['name']}" value="${actionURI!''}"/>
<div class="input">
<div class="control-container"><input type="button"${append_attributes(attributes)}/></div>
</div>
