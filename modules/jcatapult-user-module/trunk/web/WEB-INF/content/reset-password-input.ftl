[#ftl/]
[#--
  ~ Copyright (c) 2001-2008, Inversoft, All Rights Reserved
  --]
<html>
<head>
  <title>[@jc.message key="title"/]</title>
</head>
<body>
<div id="reset-password" class="user-module inversoft-module">
  <div class="security-header user-module-header inversoft-module-header">
    <h1>[@jc.message key="title"/]</h1>
  </div>
  <div class="security-content user-module-content inversoft-module-content">
    [@jc.fieldmessages errors=true/]
    [@jc.form action="reset-password" method="POST"]
      <h3>[@jc.message key="notice"/]</h3>
      [@jc.text name="login"/]
      <div id="reset-password-controls" class="user-module-controls inversoft-module-controls">
        [@jc.submit name="submit"/]
      </div>
    [/@jc.form]
  </div>
</div>
</body>
</html>