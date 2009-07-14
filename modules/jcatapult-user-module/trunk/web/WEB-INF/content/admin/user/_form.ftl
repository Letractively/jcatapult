[#ftl/]
[@jc.form action=formAction method="POST"]
  <div id="user-admin-input" class="user-module-input jcatapult-module-input">
    [@jc.hidden name="user.id"/]
    [#include "../../_user-form-login-fields.ftl"/]
    [#include "../../_user-form-contact-fields.ftl"/]
    [#include "../../_user-form-emailprefs-fields.ftl"/]
    <div id="status-input" class="user-module-input-section jcatapult-module-input-section">
      [@jc.checkbox name="user.passwordExpired" value="true" required=true/]
      [@jc.checkbox name="user.expired" value="true" required=true/]
      [@jc.checkbox name="user.locked" value="true" required=true/]
    </div>
    <div id="role-input" class="user-module-input-section jcatapult-module-input-section">
      [@jc.select items=roles valueExpr="id" l10nExpr="name" name="associations['roles']" required=true/]
    </div>
  </div>
  <div id="user-admin-controls" class="user-module-controls jcatapult-module-controls">
    [@jc.submit name="save"/]
    <div class="input">
      <div class="control-container">
        <a href="/admin/user/">CANCEL</a>
      </div>
    </div>
  </div>
[/@jc.form]