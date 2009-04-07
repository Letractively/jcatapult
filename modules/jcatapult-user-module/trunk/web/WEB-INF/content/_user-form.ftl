[#ftl/]
[@jc.form action=formAction method="POST"]
  <h3>[@jc.message key="notice"/]</h3>
  [@jc.actionmessages errors=true/]
  <div id="account-input" class="user-module-input jcatapult-module-input">
    [#include "_user-form-login-fields.ftl"/]
    [#include "_user-form-contact-fields.ftl"/]
    [#include "_user-form-emailprefs-fields.ftl"/]
    [#include "_user-form-captcha-fields.ftl"/]
  </div>
  <div id="account-controls" class="user-module-controls jcatapult-module-controls">
    [@jc.submit name="submit"/]
  </div>
[/@jc.form]