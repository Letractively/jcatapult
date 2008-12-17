[#ftl/]
[#if json]
({
  "success": ${r"$"}{success?string},
  <#if success>
    // Add your success JSON here
  <#else>
    // Add your error JSON here
  </#if>
})
[#else]
<html>
<head>
  <title></title>
</head>
<body>

</body>
</html>
[/#if]