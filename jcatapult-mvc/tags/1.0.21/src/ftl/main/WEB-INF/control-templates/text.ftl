[#ftl/]
[#include "class-helper.ftl"/]
[#include "dynamic-attributes.ftl"/]
<div class="[@class attributes, 'text' true/]">
<div class="label-container">[#include "label.ftl"/]</div>
<div class="control-container"><input type="text"${append_attributes(attributes)}/></div>
</div>
