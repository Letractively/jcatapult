[#ftl/]
[#include "parameter-attributes.ftl"/]
<div class="input">
[#include "label.ftl"/]
<div class="control-container"><textarea${append_attributes(attributes, ['value'])}>${attributes['value']!''}</textarea></div>
</div>