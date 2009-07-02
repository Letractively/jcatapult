[#ftl/]
<html>
<head>
  <title>[@jc.message key="title"/]</title>
</head>
<body>
<div id="account-summary" class="user-module jcatapult-module">
  <div class="account-summary-header user-module-header jcatapult-module-header">
    <h1>[@jc.message key="heading"/]</h1>
  </div>
  <div class="account-summary-content user-module-content jcatapult-module-content">
    [#include "../info.ftl"/]

    <div id="account-summary-controls" class="user-module-controls jcatapult-module-controls">
      <a href="/account/edit">EDIT</a>
    </div>
  </div>
</div>
</body>
</html>