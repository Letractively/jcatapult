[#ftl/]
[#include "dynamic-attributes.ftl"/]
<input type="hidden" name="__jc_a_${attributes['name']}" value="${actionURI!''}"/>
<div class="submit-button button control">
<div class="submit-button-label button-label control-label"> </div>
<div class="submit-button-container button-container control-container"><input type="submit"${append_attributes(attributes)}/></div>
</div>
