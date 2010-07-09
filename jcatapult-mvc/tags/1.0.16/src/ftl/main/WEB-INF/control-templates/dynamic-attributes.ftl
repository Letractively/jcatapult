[#ftl/]
[#list dynamic_attributes?keys as key]
<input type="hidden" name="${attributes['name']}@${key}" value="${dynamic_attributes[key]?html}"/>
[/#list]