[#ftl/]
<html>
<head>
  <title>[@jc.message key="title"/]</title>
</head>
<body>
<div id="credit-card-delete-confirm" class="user-module jcatapult-module">
  <div class="credit-card-delete-confirm-header user-module-header jcatapult-module-header">
    <h1>[@jc.message key="heading"/]</h1>
  </div>
  <div class="credit-card-delete-confirm-content user-module-content jcatapult-module-content">
    [@jc.actionmessages errors=true/]
    ${creditCard.partialNumber} ${creditCard.expirationMonth}/${creditCard.expirationYear?c}<br/>
    [@jc.message key="message"/]<br/>
    <div id="credit-card-delete-confirm-controls" class="user-module-controls jcatapult-module-controls">
      <a href="/account/summary">[@jc.message key="cancel"/]</a>
      <a href="/account/credit-card/delete?id=${creditCard.id}">YES</a>
    </div>
  </div>
</div>
</body>
</html>