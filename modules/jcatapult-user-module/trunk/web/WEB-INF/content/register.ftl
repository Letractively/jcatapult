[#ftl/]
[#--
  ~ Copyright (c) 2001-2008, Inversoft, All Rights Reserved
  --]
<html>
<head>
  <title>[@jc.message key="title"/]</title>
</head>
<body>
<div id="register" class="user-module inversoft-module">
  <div class="security-header user-module-header inversoft-module-header">
    <h1>[@jc.message key="title"/]</h1>
  </div>
  <div class="security-content user-module-content inversoft-module-content">
    [#global formAction="register"/]
    [#include "user_form.ftl"/]
  </div>
</div>
</body>
</html>