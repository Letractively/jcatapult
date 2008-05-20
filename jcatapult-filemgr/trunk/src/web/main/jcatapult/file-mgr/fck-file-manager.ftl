[#ftl/]

[#if connector.error]
    window.parent.OnUploadCompleted(1, "fileUrl", "fileName", "${connector.error.message}" );
[#else]
    window.parent.OnUploadCompleted(${connector.uploadResult.resultCode}, "fileUrl", "${result.modifiedFileName!}", "" );
[#/if]
