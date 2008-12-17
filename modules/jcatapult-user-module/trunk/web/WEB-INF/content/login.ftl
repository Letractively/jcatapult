[#ftl/]
[#assign js=JspTaglibs["http://www.jcatapult.org/jcatapult-security/tags"]/]
[#--
  ~ Copyright (c) 2001-2008, Inversoft, All Rights Reserved
  --]
<html>
<head>
  <title>[@jc.message key="title"/]</title>
</head>
<body>
<div id="login" class="user-module inversoft-module">
  <div class="security-header user-module-header inversoft-module-header">
    <h1>[@jc.message key="title"/]</h1>
  </div>
  <div class="security-content user-module-content inversoft-module-content">
    <form action="/jcatapult-security-check" method="POST">
      <h3>[@jc.message key="notice"/]</h3>
      <ul class="field-errors">
        [@js.loginException expired=true locked=true]
          <li>[@jc.message key="expired"/]</li>
        [/@js.loginException]
        [@js.loginException password=true username=true]
          <li>[@jc.message key="failure"/]</li>
        [/@js.loginException]
      </ul>

      [@jc.text name="j_username" id="j_username" size="30" required=true /]
      [@jc.password name="j_password" id="j_password" size="30" required=true/]
      <div id="login-controls" class="user-module-controls inversoft-module-controls">
        [@jc.submit name="login"/]
      </div>

      <div id="security-links">
        <a href="register">[@jc.message key="register"/]</a>
        <br/>
        <a href="reset-password">[@jc.message key="forgot-password"/]</a>
      </div>
    </form>
  </div>
</div>
</body>
</html>