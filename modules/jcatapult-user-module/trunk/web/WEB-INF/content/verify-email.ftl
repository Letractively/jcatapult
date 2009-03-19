[#ftl/]
<html>
<head>
  <title>[@jc.message key="title"/]</title>
</head>
<body>
<div id="verify-email" class="user-module jcatapult-module">
  <div class="verify-email-header user-module-header jcatapult-module-header">
    <h1>[@jc.message key="title"/]</h1>
  </div>
  <div class="verify-email-content user-module-content jcatapult-module-content">
    [@jc.actionmessages errors=true/]
    [@jc.form action="verify-email" method="POST"]
      [@jc.text name="username" required=true/]
      <div id="verify-email-controls" class="user-module-controls jcatapult-module-controls">
        [@jc.submit type="submit" name="submit"/]
      </div>
    [/@jc.form]
  </div>
</div>
</body>
</html>