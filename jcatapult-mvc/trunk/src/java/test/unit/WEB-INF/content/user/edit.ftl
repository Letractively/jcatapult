[#ftl/]
<html>
<head><title>Edit a user</title></head>
<body>
[@jc.form action="/user/edit" method="POST"]
  [@jc.text name="user.name"/]
  [@jc.text name="user.age"/]
  [@jc.text name="user.address['home'].street"/]
  [@jc.text name="user.address['home'].city"/]
  [@jc.text name="user.address['home'].state"/]
  [@jc.text name="user.address['home'].zipcode"/]
  [@jc.text name="user.address['home'].country"/]
  [@jc.text name="user.address['work'].street"/]
  [@jc.text name="user.address['work'].city"/]
  [@jc.text name="user.address['work'].state"/]
  [@jc.text name="user.address['work'].zipcode"/]
  [@jc.text name="user.address['work'].country"/]
[/@jc.form]
</body>
</html>