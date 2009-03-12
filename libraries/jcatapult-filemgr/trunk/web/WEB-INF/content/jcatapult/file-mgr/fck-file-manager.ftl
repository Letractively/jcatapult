[#ftl]
<script type="text/javascript">
  [#if connector.error]
    window.parent.OnUploadCompleted(1, "${connector.uploadResult.fileUrl!}", "${connector.uploadResult.modifiedFileName!}", "${connector.error.message}");
  [#else]
    window.parent.OnUploadCompleted(${connector.uploadResult.resultCode}, "${connector.uploadResult.fileUrl!}", "${connector.uploadResult.modifiedFileName!}", "");
  [/#if]
</script>