[#ftl/]
[#include "class-helper.ftl"/]
[#include "dynamic-attributes.ftl"/]
<div class="[@class attributes, 'password' true/]">
<div class="label-container">[#include "label.ftl"/]</div>
<div class="control-container"><input type="password"${append_attributes(attributes)}/></div>
</div>
