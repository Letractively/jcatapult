[#ftl/]
[#list parameter_attributes?keys as key]
<input type="hidden" name="${attributes['name']}@${key}" value="${parameter_attributes[key]?html}"/>
[/#list]
<input type="text"${append_attributes(attributes)}/>