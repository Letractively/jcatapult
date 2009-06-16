[#ftl/]
[@jc.actionmessages errors=true /]
<div id="login-summary" class="user-module-section jcatapult-module-section">
  <div id="login-details" class="user-module-details jcatapult-module-details">
    <h3>[@jc.message key="login-header"/]</h3>
    [@jc.message key="user.username"/]: ${user.username}
    [#if user.username != user.email]
      <br/>
      [@jc.message key="user.email"/]: ${user.email}
    [/#if]
  </div>
</div>
[#if user.addresses?size > 0 && user.phoneNumbers?size > 0]
<div id="contact-info-summary" class="user-module-section jcatapult-module-section">
  <div id="contact-info-details" class="user-module-details jcatapult-module-details">
    <h3>[@jc.message key="contact-header"/]</h3>
    [@jc.message key="user.name.firstName"/]: ${user.name.firstName!''}<br/>
    [@jc.message key="user.name.lastName"/]: ${user.name.lastName!''}<br/>
  </div>

  [#if user.addresses['home']??]
  <div id="home-address-details" class="user-module-details jcatapult-module-details">
    <h3>[@jc.message key="user.addresses['home']"/]</h3>
    [@jc.message key="user.addresses['home'].street"/]: ${user.addresses['home'].street}<br/>
    [@jc.message key="user.addresses['home'].city"/]: ${user.addresses['home'].city}<br/>
    [@jc.message key="user.addresses['home'].state"/]: ${user.addresses['home'].state!''}<br/>
    [@jc.message key="user.addresses['home'].country"/]: ${user.addresses['home'].country}<br/>
    [@jc.message key="user.addresses['home'].postalCode"/]: ${user.addresses['home'].postalCode!''}<br/>
  </div>
  [/#if]
  [#if user.addresses['work']??]
  <div id="work-address-details" class="user-module-details jcatapult-module-details">
    <h3>[@jc.message key="user.addresses['work']"/]</h3>
    [@jc.message key="user.addresses['work'].street"/]: ${user.addresses['work'].street}<br/>
    [@jc.message key="user.addresses['work'].city"/]: ${user.addresses['work'].city}<br/>
    [@jc.message key="user.addresses['work'].state"/]: ${user.addresses['work'].state!''}<br/>
    [@jc.message key="user.addresses['work'].country"/]: ${user.addresses['work'].country}<br/>
    [@jc.message key="user.addresses['work'].postalCode"/]: ${user.addresses['work'].postalCode!''}<br/>
  </div>
  [/#if]
  [#if user.phoneNumbers['home']??]
  <div id="home-phone-details" class="user-module-details jcatapult-module-details">
    <h3>[@jc.message key="user.phoneNumbers['home'].number"/]</h3>
    ${user.phoneNumbers['home'].number}
  </div>
  [/#if]
  [#if user.phoneNumbers['work']??]
  <div id="work-phone-details" class="user-module-details jcatapult-module-details">
    <h3>[@jc.message key="user.phoneNumbers['work'].number"/]</h3>
    ${user.phoneNumbers['work'].number}
  </div>
  [/#if]
  [#if user.phoneNumbers['cell']??]
  <div id="cell-phone-details" class="user-module-details jcatapult-module-details">
    <h3>[@jc.message key="user.phoneNumbers['cell'].number"/]</h3>
    ${user.phoneNumbers['cell'].number}
  </div>
  [/#if]
</div>
[/#if]

[#if user.creditCards?size > 0]
<div id="credit-card-summary" class="user-module-section jcatapult-module-section">
  <div id="credit-card-details" class="user-module-details jcatapult-module-details">
    <h3>[@jc.message key="creditCard-header"/]</h3>
    [#list user.creditCards as cc]
      ${cc.partialNumber} ${cc.expirationMonth}/${cc.expirationYear?c}
      [#if creditCardsEditable!true]<a href="/account/credit-card/edit?id=${cc.id}">[@jc.message key="edit"/]</a>[/#if]
      [#if creditCardsEditable!true]<a href="/account/credit-card/confirm-delete?id=${cc.id}">[@jc.message key="delete"/]</a>[/#if]<br/>
    [/#list]
  </div>
</div>
[/#if]

[#if user.properties?? && user.properties.map?size > 0]
<div id="user-property-summary" class="user-module-section jcatapult-module-section">
  <div id="user-property-details" class="user-module-details jcatapult-module-details">
    <h3>[@jc.message key="property-header"/]</h3>
    [#list user.properties.map?keys as key]
    [@jc.message key=user.properties.map[key].name/]: ${user.properties.map[key].value}<br/>
  [/#list]
  </div>
</div>
[/#if]
