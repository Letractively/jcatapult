[#ftl/]
[#include "class-helper.ftl"/]
[#include "dynamic-attributes.ftl"/]
<div class="[@class attributes, 'reset-button' false/]">
<div class="label-container"> </div>
<div class="control-container"><input type="reset"${append_attributes(attributes)}/></div>
</div>
