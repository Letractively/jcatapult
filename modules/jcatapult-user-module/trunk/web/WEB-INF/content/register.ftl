[#ftl/]
<html>
<head>
  <title>[@jc.message key="title"/]</title>
</head>
<body>
<div id="register" class="user-module jcatapult-module">
  <div class="security-header user-module-header jcatapult-module-header">
    <h1>[@jc.message key="title"/]</h1>
  </div>
  <div class="security-content user-module-content jcatapult-module-content">
    [#global formAction="register"/]
    [#include "user_form.ftl"/]
  </div>
</div>
</body>
</html>