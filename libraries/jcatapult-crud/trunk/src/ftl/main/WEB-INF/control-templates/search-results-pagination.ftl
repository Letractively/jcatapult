[#ftl/]
[#macro params ignore]
[#if !persistent]
[#list searchCriteria.parameters?keys as key]
[#if key != ignore && searchCriteria.parameters[key]??]
${key}=${searchCriteria.parameters[key]?string?url('UTF-8')}&[#t/]
[/#if]
[/#list]
[/#if]
[/#macro]
[#if numberOfPages > 1 && !searchCriteria.showAll]
  <div id="pagination-controls" class="${attributes['name']}-pagination-controls jcatapult-module-pagination-controls">
    [#if searchCriteria.page > 1]
      <a href="?[@params "searchCriteria.page"/]searchCriteria.page=1">[@jc.message key="beginning" default="&lt;&lt;"/]</a>
      <a href="?[@params "searchCriteria.page"/]searchCriteria.page=${searchCriteria.page - 1}">[@jc.message key="prev" default="&lt;"/]</a>
    [#else]
      [@jc.message key="prev" default="<"/]
    [/#if]

    [#list startPage..endPage as i]
      [#if i != searchCriteria.page]
        <a href="?[@params "searchCriteria.page"/]searchCriteria.page=${i}">${i}</a>
      [#else]
        ${searchCriteria.page}
      [/#if]
    [/#list]

    [#if totalCount > (searchCriteria.page * searchCriteria.numberPerPage)]
      <a href="?[@params "searchCriteria.page"/]searchCriteria.page=${searchCriteria.page + 1}">[@jc.message key="next" default="&gt;"/]</a>
      <a href="?[@params "searchCriteria.page"/]searchCriteria.page=${numberOfPages}">[@jc.message key="end" default="&gt;&gt;"/]</a>
    [#else]
      [@jc.message key="next" default=">"/]
    [/#if]
  </div>
[/#if]