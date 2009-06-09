<html>
<body>
<p>
  Someone requested a password reset email be sent to this email address from
  ${url}. If you requested that your password be reset, please click the link below to
  be taken to the reset password form.
</p>
<p>
  <a href="${url}?guid=${user.guid?html}">${url}?guid=${user.guid?html}</a>
</p>
<p>
  If you did not request this, you can ignore this email and your current password will not be changed.
</p>
<p>
  Thank you.
</p>
</body>
</html>