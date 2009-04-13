[#ftl/]
[#if settings['captcha'] && formAction != 'edit']
  <div id="captcha-image" class="user-module-input-section jcatapult-module-input-section">
    <img src="/captcha.png" width="200" height="50"/>
  </div>
  <div id="captcha-input" class="user-module-input-section jcatapult-module-input-section">
    [@jc.text name="captcha" required=true/]
  </div>
[/#if]
