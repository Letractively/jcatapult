<#-- @ftlvariable name="results" type="java.util.List<org.jcatapult.module.cms.action.cms.content.NodeResult>" -->
({
<#list results as result>
  <#if result.node??>
    "${result.node.localName}": {
      "visible": ${result.node.visible?string},
      <#if result.node.visible>
        "content": "${result.content.content?js_string}"
      </#if>
    }
  </#if>
</#list>
})
