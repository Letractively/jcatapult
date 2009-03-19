[#ftl/]
[#include "dynamic-attributes.ftl"/]
<div class="textarea input control">
<div class="textarea-label input-label control-label">[#include "label.ftl"/]</div>
<div class="textarea-container input-container control-container"><textarea${append_attributes(attributes, ['value'])}>${attributes['value']!''}</textarea></div>
</div>
