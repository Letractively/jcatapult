[#ftl]
<script type="text/javascript">
  (function()
  {
    var d = document.domain ;

    while (true)
    {
      // Test if we can access a parent property.
      try
      {
        var test = window.top.opener.document.domain ;
        break;
      }
      catch(e) {
      }

		// Remove a domain part: www.mytest.example.com => mytest.example.com => example.com ...
      d = d.replace(/.*?(?:\.|$)/, '');

      if (d.length == 0)
        break;		// It was not able to detect the domain.

      try
      {
        document.domain = d;
      }
      catch (e)
      {
        break;
      }
    }
  })();

  [#if connector.error]
    window.parent.OnUploadCompleted(1, "${connector.uploadResult.fileUrl!}", "${connector.uploadResult.modifiedFileName!}", "${connector.error.message}");
  [#else]
    window.parent.OnUploadCompleted(${connector.uploadResult.resultCode}, "${connector.uploadResult.fileUrl!}", "${connector.uploadResult.modifiedFileName!}", "");
  [/#if]

</script>