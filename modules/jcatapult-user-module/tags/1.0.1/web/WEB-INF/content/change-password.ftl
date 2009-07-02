[#ftl/]
<html>
<head>
  <title>[@jc.message key="title"/]</title>
</head>
<body>
<div id="reset-password" class="user-module jcatapult-module">
  <div class="security-header user-module-header jcatapult-module-header">
    <h1>[@jc.message key="title"/]</h1>
  </div>
  <div class="security-content user-module-content jcatapult-module-content">
    [@jc.actionmessages errors=true/]
    [#if messageStore.actionErrors?size == 0]
      <p>[@jc.message key="notice"/]</p>
      [@jc.form action="change-password" method="POST"]
        [@jc.password name="password" required=true/]
        [@jc.password name="passwordConfirm" required=true/]
        <div id="reset-password-controls" class="user-module-controls jcatapult-module-controls">
          [@jc.submit type="submit" name="submit"/]
        </div>
      [/@jc.form]
    [/#if]
  </div>
</div>
</body>
</html>