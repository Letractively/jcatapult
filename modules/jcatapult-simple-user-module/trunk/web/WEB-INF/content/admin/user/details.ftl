[#ftl/]
<html>
<head><title>[@jc.message key="title"/]</title></head>
<body>
<div id="user-details" class="user-module jcatapult-module">
  [#include "../../info.ftl"/]

  <div id="user-status-summary" class="user-module-section jcatapult-module-section">
    <h3>[@jc.message key="status-header"/]</h3>
    <div id="user-status-details" class="user-module-details jcatapult-module-details">
      [@jc.message key="user.passwordExpired"/]: ${user.passwordExpired?string}<br/>
      [@jc.message key="user.expired"/]: ${user.expired?string}<br/>
      [@jc.message key="user.locked"/]: ${user.locked?string}<br/>
    </div>
  </div>
  <div id="user-controls" class="user-module-controls jcatapult-module-controls">
    <a href="edit?id=${user.id}">[@jc.message key="edit"/]</a><br/>
    <a href="/admin/user/">[@jc.message key="back"/]</a>
  </div>
</div>
</body>
</html>