[#ftl/]
<html>
<head>
  <title>[@jc.message key="title"/]</title>
</head>
<body>
<div id="account-update" class="user-module">
  <div class="account-update-header user-module-header inversoft-module-header">
    <h1>[@jc.message key="heading"/]</h1>
  </div>
  <div class="account-update-content user-module-content inversoft-module-content">
    [#global formAction="edit"/]
    [#include "../user_form.ftl"/]
  </div>
</div>
</body>
</html>