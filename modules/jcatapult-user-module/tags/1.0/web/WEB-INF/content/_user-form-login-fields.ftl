[#ftl/]
<div id="login-input" class="user-module-input-section jcatapult-module-input-section">
  [#--
    If the email and username are the same, this will render the email. Otherwise, it will
    render both.
  --]
  <div id="username-email-input"  class="user-module-input-section jcatapult-module-input-section">
    [#if settings['jcatapult.user.username-is-email']]
      [@jc.text name="user.email" size="30" required=true/]
    [#else]
      [@jc.text name="user.email" size="30" required=true/]
      [@jc.text name="user.username" size="30" required=true/]
    [/#if]
  </div>
  <div id="password-input" class="user-module-input-section jcatapult-module-input-section">
    [#assign passwordRequired = (formAction != "edit")/]
    [#if formAction == "edit"]
      [@jc.message key="passwordInstructions"/]
    [/#if]
    [@jc.password name="password" size="12" required=passwordRequired/]
    [@jc.password name="passwordConfirm" size="12" required=passwordRequired/]
  </div>
</div>
