({
  "success" : ${success?string},
  <#if success>
    // Add your success JSON here
  <#else>
    // Add your error JSON here
  </#if>
})