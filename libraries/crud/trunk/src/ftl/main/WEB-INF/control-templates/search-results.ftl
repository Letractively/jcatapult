[#ftl/]
<form action="delete" method="POST">
  <table id="listing" class="${attributes['name']}-listing inversoft-module-listing">
    <tr>
      [#list attributes['properties'] as prop]
      <th id="${prop?replace('.', '-')}-header"><a href="index?searchCriteria.sortProperty=${prop}[#if searchCriteria.sortProperty! == prop + ' desc']+asc[#else]+desc[/#if]">[@jc.message key=(attributes['name'] + "." + prop)/][#if searchCriteria.sortProperty! == prop + " desc"]<img src="/module/inversoft-crud/1.0.1/sort-arrow-down.gif" alt="" border="0"/>[#elseif searchCriteria.sortProperty! == prop + " asc"]<img src="/module/inversoft-crud/1.0.1/sort-arrow-up.gif" alt="" border="0"/>[/#if]</a></th>
      [/#list]
      <th id="edit-header">[@jc.message key="edit-header"/]</th>
      [#if deletable]
        <th id="delete-header">[@jc.message key="delete-header"/]</th>
      [/#if]
    </tr>
    [#list results as result]
      <tr>
        [#list attributes['properties'] as prop]
        <td class="[#if result_index % 2 == 0]even[#else]odd[/#if] ${prop?replace('.', '-')}-row">[#if prop_index == 0]<a href="details?id=${result.id}">[/#if]${("((result." + prop + ")!'')?string")?eval}[#if result_index == 0]</a>[/#if]</td>
        [/#list]
        <td class="[#if result_index % 2 == 0]even[#else]odd[/#if] edit-row"><a href="edit/${result.id}"><img src="/module/inversoft-crud/1.0.1/edit.png" alt="" border=""/></a></td>
        [#if deletable]
          <td class="[#if result_index % 2 == 0]even[#else]odd[/#if] delete-row"><input type="checkbox" name="ids" value="${result.id}"/></td>
        [/#if]
      </tr>
    [/#list]
    [#if results?size == 0]
      <tr>
        <td colspan="[#if deletable]7[#else]6[/#if]">[@jc.message key="no-results"/]</td>
      </tr>
    [/#if]
  </table>
  <div id="listing-controls" class="${attributes['name']}-controls inversoft-module-controls">
    [#if addable]
      <a href="add">[@jc.message key="add"/]</a>
    [/#if]
    [#if deletable]
      [@jc.submit name="delete"/]
    [/#if]
  </div>
  [#if totalCount % searchCriteria.numberPerPage > 0]
    [#assign extra = 1]
  [#else]
    [#assign extra = 0]
  [/#if]
  [#assign totalPages = (totalCount / searchCriteria.numberPerPage) + extra]
  [#if totalPages?int > 1 && !searchCriteria.showAll]
    <div id="pagination-controls" class="${attributes['name']}-pagination-controls inversoft-module-pagination-controls">
      [#if searchCriteria.page > 1]
        <a href="index?searchCriteria.page=${searchCriteria.page - 1}&searchCriteria.numberPerPage=${searchCriteria.numberPerPage}">[@jc.message key="prev"/]</a> |
      [/#if]
      [#if searchCriteria.page == 1]
        [@jc.message key="prev"/] |
      [/#if]

      [#if searchCriteria.page - 5 < 1]
        [#assign start = 1]
      [#else]
        [#assign start = searchCriteria.page - 5]
      [/#if]

      [#assign end = searchCriteria.page - 1]
      [#if (end >= start)]
        [#list start..end as i]
          <a href="index?searchCriteria.page=${i}&searchCriteria.numberPerPage=${searchCriteria.numberPerPage}">${i}</a> |
        [/#list]
      [/#if]
      ${searchCriteria.page} |

      [#assign start = searchCriteria.page + 1]
      [#if searchCriteria.page + 5 > totalPages]
        [#assign end = totalPages]
      [#else]
        [#assign end = searchCriteria.page + 5]
      [/#if]
      [#if (end >= start)]
        [#list start..end as i]
          <a href="index?searchCriteria.page=${i}&searchCriteria.numberPerPage=${searchCriteria.numberPerPage}">${i}</a> |
        [/#list]
      [/#if]

      [#if totalCount > (searchCriteria.page * searchCriteria.numberPerPage)]
        <a href="index?searchCriteria.page=${searchCriteria.page + 1}&searchCriteria.numberPerPage=${searchCriteria.numberPerPage}">[@jc.message key="next"/]</a>
      [/#if]
      [#if totalCount <= (searchCriteria.page * searchCriteria.numberPerPage)]
        [@jc.message key="next"/]
      [/#if]
    </div>
  [/#if]
  [#if totalCount > 25]
    <div id="number-per-searchCriteria.page" class="${attributes['name']}-number-per-searchCriteria.page inversoft-module-number-per-searchCriteria.page">
      Number per page
      <a href="index?searchCriteria.numberPerPage=25&searchCriteria.showAll=false">25</a> |
      <a href="index?searchCriteria.numberPerPage=100&searchCriteria.showAll=false">100</a> |
      <a href="index?searchCriteria.showAll=true">[@jc.message key="show-all"/]</a><br/>
    </div>
  [/#if]
</form>