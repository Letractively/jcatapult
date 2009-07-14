[#ftl/]
<div id="contact-info-input" class="user-module-input-section jcatapult-module-input-section">
  [#if settings['name'] || settings['business']]
    <div id="names-input" class="user-module-input-section jcatapult-module-input-section">
      [#if settings['name']]
        [@jc.text name="user.name.firstName" size="30" required=settings['nameRequired']/]
        [@jc.text name="user.name.lastName" size="30" required=settings['nameRequired']/]
      [/#if]
      [#if settings['business']]
        [@jc.text name="user.companyName" size="30" required=settings['businessRequired']/]
      [/#if]
    </div>
  [/#if]
  [#if settings['homeAddress']]
    <div id="home-address-input" class="user-module-input-section jcatapult-module-input-section">
      <h3>[@jc.message key="user.addresses['home']"/]</h3>
      [@jc.text name="user.addresses['home'].street" size="30" required=settings['homeAddressRequired']/]
      [@jc.text name="user.addresses['home'].street2" size="30"/]
      [@jc.text name="user.addresses['home'].city" size="30" required=settings['homeAddressRequired']/]
      [@jc.statesselect name="user.addresses['home'].state" includeBlank=true/]
      [@jc.countriesselect name="user.addresses['home'].country" preferredCodes="US" includeBlank=true required=settings['homeAddressRequired']/]
      [@jc.text name="user.addresses['home'].postalCode" size="15"/]
    </div>
  [/#if]
  [#if settings['workAddress']]
    <div id="work-address-input" class="user-module-input-section jcatapult-module-input-section">
      <h3>[@jc.message key="user.addresses['work']"/]</h3>
      [@jc.text name="user.addresses['work'].street" size="30" required=settings['workAddressRequired']/]
      [@jc.text name="user.addresses['work'].street2" size="30"/]
      [@jc.text name="user.addresses['work'].city" size="30" required=settings['workAddressRequired']/]
      [@jc.statesselect name="user.addresses['work'].state" includeBlank=true/]
      [@jc.countriesselect name="user.addresses['work'].country" preferredCodes="US" includeBlank=true required=settings['workAddressRequired']/]
      [@jc.text name="user.addresses['work'].postalCode" size="15"/]
    </div>
  [/#if]
  [#if settings['homePhone'] || settings['workPhone'] || settings['cellPhone']]
    <div id="phone-input" class="user-module-input-section jcatapult-module-input-section">
      <h3>[@jc.message key="phoneNumbers"/]</h3>
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
  [/#if]
</div>
