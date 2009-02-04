[#ftl/]
[#if totalCount % searchCriteria.numberPerPage > 0]
  [#assign extra = 1]
[#else]
  [#assign extra = 0]
[/#if]
[#assign totalPages = (totalCount / searchCriteria.numberPerPage) + extra]
[#if totalPages?int > 1 && !searchCriteria.showAll]
  <div id="pagination-controls" class="${attributes['name']}-pagination-controls jcatapult-module-pagination-controls">
    [#if searchCriteria.page > 1]
      <a href="?searchCriteria.page=${searchCriteria.page - 1}&searchCriteria.numberPerPage=${searchCriteria.numberPerPage}">[@jc.message key="prev" default="Prev"/]</a> |
    [/#if]
    [#if searchCriteria.page == 1]
      [@jc.message key="prev" default="Prev"/] |
    [/#if]

    [#if searchCriteria.page - 5 < 1]
      [#assign start = 1]
    [#else]
      [#assign start = searchCriteria.page - 5]
    [/#if]

    [#assign end = searchCriteria.page - 1]
    [#if (end >= start)]
      [#list start..end as i]
        <a href="?searchCriteria.page=${i}&searchCriteria.numberPerPage=${searchCriteria.numberPerPage}">${i}</a> |
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
        <a href="?searchCriteria.page=${i}&searchCriteria.numberPerPage=${searchCriteria.numberPerPage}">${i}</a> |
      [/#list]
    [/#if]

    [#if totalCount > (searchCriteria.page * searchCriteria.numberPerPage)]
      <a href="?searchCriteria.page=${searchCriteria.page + 1}&searchCriteria.numberPerPage=${searchCriteria.numberPerPage}">[@jc.message key="next" default="Next"/]</a>
    [/#if]
    [#if totalCount <= (searchCriteria.page * searchCriteria.numberPerPage)]
      [@jc.message key="next" default="Next"/]
    [/#if]
  </div>
[/#if]