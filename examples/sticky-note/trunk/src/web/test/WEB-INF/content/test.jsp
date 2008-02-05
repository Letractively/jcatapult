<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
  <title>Test</title>
  <link rel="stylesheet" href="/component/sticky-note/1.0-A1/css/sticky-note.css"/>
  <script type="text/javascript" src="/js/jquery-1.2.2.pack.js"/>
</head>
<body>
  <s:action namespace="/stickynote" name="fetch" executeResult="true" flush="true"/>
</body>
</html>