[#ftl/]
<html>
<head>
  [@jc.cmstitlenode name="title"]Hello[/@jc.cmstitlenode]
  [@jc.cmsmetanode name="foo" description="The test" label="Test"]Bar[/@jc.cmsmetanode]
</head>
<body>
[@jc.cmscontentnode name="test"]
  Default content.
[/@jc.cmscontentnode]
<a href="/test/other">Other page</a>
</body>
</html>