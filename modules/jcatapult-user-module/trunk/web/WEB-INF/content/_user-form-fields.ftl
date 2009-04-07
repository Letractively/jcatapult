[#ftl/]
<div id="account-input" class="user-module-input jcatapult-module-input">
  <div id="login-input" class="user-module-input-section jcatapult-module-input-section">
    [#--
      If the email and username are the same, this will render the email. Otherwise, it will
      render both.
    --]
    [#if settings['jcatapult.user.username-is-email']]
      [@jc.text name="user.email" size="30" required=true/]
    [#else]
      [@jc.text name="user.email" size="30" required=true/]
      [@jc.text name="user.username" size="30" required=true/]
    [/#if]

    [#assign passwordRequired = (formAction != "edit")/]
    [#if formAction == "edit"]
      [@jc.message key="passwordInstructions"/]
    [/#if]
    [@jc.password name="password" size="12" required=passwordRequired/]
    [@jc.password name="passwordConfirm" size="12" required=passwordRequired/]
  </div>
  <div id="contact-info-input" class="user-module-input-section jcatapult-module-input-section">
    [#if settings['name']]
      [@jc.text name="user.name.firstName" size="30" required=settings['nameRequired']/]
      [@jc.text name="user.name.lastName" size="30" required=settings['nameRequired']/]
    [/#if]
    [#if settings['business']]
      [@jc.text name="user.companyName" size="30" required=settings['businessRequired']/]
    [/#if]
    [#if settings['homeAddress']]
      <h3>[@jc.message key="user.addresses['home']"/]</h3>
      <div id="home-address-input" class="user-module-input-section jcatapult-module-input-section">
        [@jc.text name="user.addresses['home'].street" size="30" required=settings['homeAddressRequired']/]
        [@jc.text name="user.addresses['home'].city" size="30" required=settings['homeAddressRequired']/]
        [@jc.text name="user.addresses['home'].state" size="30"/]
        [@jc.countriesselect name="user.addresses['home'].country" preferredCodes="US" includeBlank=true required=settings['homeAddressRequired']/]
        [@jc.text name="user.addresses['home'].postalCode" size="15"/]
      </div>
    [/#if]
    [#if settings['workAddress']]
      <h3>[@jc.message key="user.addresses['work']"/]</h3>
      <div id="work-address-input" class="user-module-input-section jcatapult-module-input-section">
        [@jc.text name="user.addresses['work'].street" size="30" required=settings['workAddressRequired']/]
        [@jc.text name="user.addresses['work'].city" size="30" required=settings['workAddressRequired']/]
        [@jc.text name="user.addresses['work'].state" size="30"/]
        [@jc.countriesselect name="user.addresses['work'].country" preferredCodes="US" includeBlank=true required=settings['workAddressRequired']/]
        [@jc.text name="user.addresses['work'].postalCode" size="15"/]
      </div>
    [/#if]
    [#if settings['homePhone']]
      <div id="home-phone-input" class="user-module-input-section jcatapult-module-input-section">
        [@jc.text name="user.phoneNumbers['home'].number" size="30" required=settings['homePhoneRequired']/]
      </div>
    [/#if]
    [#if settings['workPhone']]
      <div id="work-phone-input" class="user-module-input-section jcatapult-module-input-section">
        [@jc.text name="user.phoneNumbers['work'].number" size="30" required=settings['workPhoneRequired']/]
      </div>
    [/#if]
    [#if settings['cellPhone']]
      <div id="cell-phone-input" class="user-module-input-section jcatapult-module-input-section">
        [@jc.text name="user.phoneNumbers['cell'].number" size="30" required=settings['cellPhoneRequired']/]
      </div>
    [/#if]
  </div>
  [#if settings['emailOptions']]
    <div id="email-options-input" class="user-module-input-section jcatapult-module-input-section">
      [@jc.hidden name="user.properties.map['email_internal'].name" value="email_internal"/]
      [@jc.checkbox name="user.properties.map['email_internal'].value" value="true" required=true defaultChecked=true uncheckedValue="false"/]
      [@jc.hidden name="user.properties.map['email_partners'].name" value="email_partners"/]
      [@jc.checkbox name="user.properties.map['email_partners'].value" value="true" required=true defaultChecked=true uncheckedValue="false"/]
    </div>
  [/#if]
  [#if settings['captcha'] && formAction == 'register']
    <div id="captcha-image" class="user-module-input-section jcatapult-module-input-section">
      <img src="/captcha.png" width="200" height="50"/>
    </div>
    <div id="captcha-input" class="user-module-input-section jcatapult-module-input-section">
      [@jc.text name="captcha" required=true/]
    </div>
  [/#if]
</div>
<div id="account-controls" class="user-module-controls jcatapult-module-controls">
  [@jc.submit name="submit"/]
</div>
