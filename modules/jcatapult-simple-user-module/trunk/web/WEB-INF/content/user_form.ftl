[#ftl/]
[@jc.form action=formAction method="POST"]
  <h3>[@jc.message key="notice"/]</h3>
  [@jc.actionmessages errors=true/]
  <div id="account-input" class="user-module-input jcatapult-module-input">
    <div id="login-input" class="user-module-input-section jcatapult-module-input-section">
      [@jc.text name="user.email" size="30" required=true/]
      [#assign passwordRequired = (formAction != "edit")/]
      [#if formAction == "edit"]
        [@jc.message key="passwordInstructions"/]
      [/#if]
      [@jc.password name="password" size="12" required=passwordRequired/]
      [@jc.password name="passwordConfirm" size="12" required=passwordRequired/]
    </div>
  </div>
  <div id="account-controls" class="user-module-controls jcatapult-module-controls">
    [@jc.submit name="submit"/]
  </div>
[/@jc.form]