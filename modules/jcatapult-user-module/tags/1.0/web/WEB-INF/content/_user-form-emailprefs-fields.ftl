[#ftl/]
[#if settings['emailOptions']]
  <div id="email-options-input" class="user-module-input-section jcatapult-module-input-section">
    [@jc.hidden name="user.properties.map['email_internal'].name" value="email_internal"/]
    [@jc.checkbox name="user.properties.map['email_internal'].value" value="true" required=true defaultChecked=true uncheckedValue="false"/]
    [@jc.hidden name="user.properties.map['email_partners'].name" value="email_partners"/]
    [@jc.checkbox name="user.properties.map['email_partners'].value" value="true" required=true defaultChecked=true uncheckedValue="false"/]
  </div>
[/#if]
