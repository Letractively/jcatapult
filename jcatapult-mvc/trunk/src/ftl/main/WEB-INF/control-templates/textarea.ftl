[#ftl/]
[#include "dynamic-attributes.ftl"/]
<div class="textarea input control">
[#include "label.ftl"/]
<div class="textarea-container input-container control-container"><textarea${append_attributes(attributes, ['value'])}>${attributes['value']!''}</textarea></div>
</div>
