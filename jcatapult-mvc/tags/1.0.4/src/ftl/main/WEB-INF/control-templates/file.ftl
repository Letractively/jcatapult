[#ftl/]
[#include "class-helper.ftl"/]
[#include "dynamic-attributes.ftl"/]
<div class="[@class attributes, 'file' true/]">
<div class="label-container">[#include "label.ftl"/]</div>
<div class="control-container"><input type="file"${append_attributes(attributes)}/></div>
</div>
