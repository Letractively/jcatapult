[#ftl/]
<html>
<head>
  <title>[@jc.message key="title"/]</title>
</head>
<body>
<div id="credit-card-edit" class="user-module jcatapult-module">
  <div class="credit-card-edit-header user-module-header jcatapult-module-header">
    <h1>[@jc.message key="heading"/]</h1>
  </div>
  <div class="credit-card-edit-content user-module-content jcatapult-module-content">
    [@jc.form action="edit" method="POST"]
      <h3>[@jc.message key="notice"/]</h3>
      [@jc.actionmessages errors=true/]
      <div id="credit-card-fields" class="user-module-fields jcatapult-module-fields">
        [@jc.hidden name="creditCard.id"/]
        [@jc.text name="creditCard.number" required=true size="20"/]
        [@jc.text name="creditCard.svn" required=true size="4"/]

        [@jc.monthsselect name="creditCard.expirationMonth" required=true/]
        [@jc.yearsselect name="creditCard.expirationYear" required=true/]

        [@jc.text name="creditCard.firstName" required=true/]
        [@jc.text name="creditCard.lastName" required=true/]
        [@jc.text name="creditCard.address.street" required=true size="30"/]
        [@jc.text name="creditCard.address.city" required=true/]
        [@jc.text name="creditCard.address.state" required=false/]
        [@jc.text name="creditCard.address.postalCode" required=false/]

        [@jc.countriesselect name="creditCard.address.country" preferredCodes="US" includeBlank=true required=true/]
      </div>
      <div id="credit-card-controls" class="user-module-controls jcatapult-module-controls">
        [@jc.submit name="submit"/]
        <a href="/account/summary">[@jc.message key="cancel"/]</a>
      </div>
    [/@jc.form]
  </div>
</div>
</body>
</html>