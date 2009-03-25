[#ftl/]
[#include "class-helper.ftl"/]
[#include "dynamic-attributes.ftl"/]
<div class="[@class attributes, 'textarea' true/]">
<div class="label-container">[#include "label.ftl"/]</div>
<div class="control-container"><textarea${append_attributes(attributes, ['value'])}>${attributes['value']!''}</textarea></div>
</div>
