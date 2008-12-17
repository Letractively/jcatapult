${r"[#ftl/]"}
<#import "/global/macros.ftl" as g/>
<html>
<head><title>${type.name} | Details</title></head>
<body>
<h2>${type.name} Details</h2>
<#list type.allFields as field>
  <#if !field.static && !field.final && field.name != "id" && field.mainType.simpleType &&
          field.mainType.fullName != "boolean" && field.mainType.fullName != "java.lang.Boolean">
<div class="details-container">
  <div class="details-label">
    ${field.name}
  </div>
  <div class="details-value">
    ${g.jspEL(type.fieldName + "." + field.name + "?html")}
  </div>
</div>
  <#elseif !field.static && !field.final && field.name != "id" && (field.mainType.fullName == "boolean" || field.mainType.fullName == "java.lang.Boolean")>
<div class="details-container">
  <div class="details-label">
    ${field.name}
  </div>
  <div class="details-value">
    ${g.jspEL(type.fieldName + "." + field.name + "?string")}
  </div>
</div>
  </#if>
</#list>
<div id="details-controls">
  <a href="${uri}/edit?id=${g.jspEL(type.fieldName + ".id")}">Cancel</a>
</div>
</body>
</html>
