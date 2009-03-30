[#ftl/]
<table id="listing" class="${attributes['name']}-listing jcatapult-module-listing">
  <tr>
    [#list attributes['properties'] as prop]
    <th id="${prop?replace('.', '-')}-header"><a href="?searchCriteria.sortProperty=${prop}[#if searchCriteria.sortProperty! == prop + ' desc']+asc[#else]+desc[/#if]">[@jc.message key=(attributes['name'] + "." + prop)/][#if searchCriteria.sortProperty! == prop + " desc"]<img src="/module/jcatapult-crud/sort-arrow-up-1.0.gif" alt="" border="0"/>[#elseif searchCriteria.sortProperty! == prop + " asc"]<img src="/module/jcatapult-crud/sort-arrow-down-1.0.gif" alt="" border="0"/>[/#if]</a></th>
    [/#list]
    [#if editable]
      <th id="edit-header">[@jc.message key="edit-header" default="Edit"/]</th>
    [/#if]
    [#if deletable]
      <th id="delete-header">[@jc.message key="delete-header" default="Delete"/]</th>
    [/#if]
  </tr>
  [#list results as result]
    <tr>
      [#list attributes['properties'] as prop]
      <td class="[#if result_index % 2 == 0]even[#else]odd[/#if] ${prop?replace('.', '-')}-row">[#if prop_index == 0 && detailable]<a href="details?id=${result.id?c}">[/#if]${("((result." + prop + ")!'')?string")?eval}[#if result_index == 0]</a>[/#if]</td>
      [/#list]
      [#if editable]
        <td class="[#if result_index % 2 == 0]even[#else]odd[/#if] edit-row"><a href="edit/${result.id?c}"><img src="/module/jcatapult-crud/edit-1.0.png" alt="" border=""/></a></td>
      [/#if]
      [#if deletable]
        <td class="[#if result_index % 2 == 0]even[#else]odd[/#if] delete-row"><input type="checkbox" name="ids" value="${result.id?c}"/></td>
      [/#if]
    </tr>
  [/#list]
  [#if results?size == 0]
    [#assign columns = attributes['properties']?size]
    [#if deletable][#assign columns = columns + 1/][/#if]
    [#if editable][#assign columns = columns + 1/][/#if]
    <tr>
      <td colspan="${columns}">[@jc.message key="no-results" default="No results"/]</td>
    </tr>
  [/#if]
</table>