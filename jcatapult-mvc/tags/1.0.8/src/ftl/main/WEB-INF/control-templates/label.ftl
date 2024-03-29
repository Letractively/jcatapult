[#ftl/]
[#macro print_label]
  [#assign has_field_errors = field_errors?? /]
  [#if has_field_errors]
    <span class="error">[#t/]
  [/#if]
  ${label}[#t/]
  [#if has_field_errors]
 ([#rt/]
    [#list field_errors as error]
      ${error}[#if error_has_next], [/#if][#t/]
    [/#list]
)[#rt/]
  [/#if]
  [#if has_field_errors]
    </span>[#t/]
  [/#if]
  [#if required!false]
    <span class="required">*</span>[#t/]
  [/#if]
[/#macro]
<label for="${attributes['id']}" class="label">[@print_label/]</label>[#t/]