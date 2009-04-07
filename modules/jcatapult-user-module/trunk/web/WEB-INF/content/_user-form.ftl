[#ftl/]
[@jc.form action=formAction method="POST"]
  <h3>[@jc.message key="notice"/]</h3>
  [@jc.actionmessages errors=true/]
  [#include "_user-form-fields.ftl"/]
[/@jc.form]