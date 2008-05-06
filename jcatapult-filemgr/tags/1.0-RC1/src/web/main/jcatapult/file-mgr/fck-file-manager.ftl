[#flt/]
[#if connector.error]
  <script type="text/javascript">
  window.parent.frames['frmUpload'].OnUploadCompleted(1, '', '', '${connector.error.message}');
  </script>
[#else]
  <script type="text/javascript">
  window.parent.frames['frmUpload'].OnUploadCompleted(${connector.uploadResult.resultCode}, '${result.modifiedFileName!}');
  </script>
[#/if]