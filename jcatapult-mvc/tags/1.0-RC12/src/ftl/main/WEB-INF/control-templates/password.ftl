[#ftl/]
[#include "dynamic-attributes.ftl"/]
<div class="password input control">
<div class="password-label input-label control-label">[#include "label.ftl"/]</div>
<div class="password-container input-container control-container"><input type="password"${append_attributes(attributes)}/></div>
</div>
