[#ftl/]
[#--
  ~ Copyright (c) 2001-2008, Inversoft, All Rights Reserved
  --]
<html>
<head>
  <title>[@jc.message key="title"/]</title>
</head>
<body>
<div id="account-summary" class="user-module inversoft-module">
  <div class="account-summary-header user-module-header inversoft-module-header">
    <h1>[@jc.message key="heading"/]</h1>
  </div>
  <div class="account-summary-content user-module-content inversoft-module-content">
    [#include "../info.ftl"/]

    <div id="account-summary-controls" class="user-module-controls inversoft-module-controls">
      <a href="/account/edit">EDIT</a>
    </div>
  </div>
</div>
</body>
</html>