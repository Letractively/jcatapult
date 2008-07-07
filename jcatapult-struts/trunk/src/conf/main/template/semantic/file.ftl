[#ftl]
[#assign fileURI = parameters.dynamicAttributes['fileURI']?default("")/]
[#assign deleteURI = parameters.dynamicAttributes['deleteURI']?default("")/]

[#if fileURI == '']
  [#assign fileURI = parameters.fileURI?default("")/]
[/#if]

[#if deleteURI == '']
  [#assign deleteURI = parameters.deleteURI?default("")/]
[/#if]

[#include "/${parameters.templateDir}/semantic/fieldheader.ftl" /]

[#if fileURI == '']
<input type="file"[#rt/]
 name="${parameters.name?default("")?html}"[#rt/]
[#if parameters.get("size")?exists]
 size="${parameters.get("size")?html}"[#rt/]
[/#if]
[#if parameters.nameValue?exists]
 value="[@s.property value="parameters.nameValue"/]"[#rt/]
[/#if]
[#if parameters.disabled?default(false)]
 disabled="disabled"[#rt/]
[/#if]
[#if parameters.accept?exists]
 accept="${parameters.accept?html}"[#rt/]
[/#if]
[#if parameters.tabindex?exists]
 tabindex="${parameters.tabindex?html}"[#rt/]
[/#if]
[#if parameters.id?exists]
 id="${parameters.id?html}"[#rt/]
[/#if]
[#if parameters.cssClass?exists]
 class="${parameters.cssClass?html}"[#rt/]
[/#if]
[#if parameters.cssStyle?exists]
 style="${parameters.cssStyle?html}"[#rt/]
[/#if]
[#if parameters.title?exists]
 title="${parameters.title?html}"[#rt/]
[/#if]
[#include "/${parameters.templateDir}/semantic/scripting-events.ftl" /]
[#include "/${parameters.templateDir}/semantic/common-attributes.ftl" /]
/>
[#include "/${parameters.templateDir}/semantic/fieldfooter.ftl" /]
[#else]
  <script type="text/javascript">
    function confirmDelete(deleteURI, fileURI) {
      var deleteJob = confirm("Are you sure you want to delete file '" + fileURI + "'?");
      if (deleteJob) {
        window.location = deleteURI;
      }
    }
  </script>
  <a href="${fileURI}">Click to View File</a> | <a href="javascript:confirmDelete('${deleteURI}', '${fileURI}')">DELETE</a>
[/#if]