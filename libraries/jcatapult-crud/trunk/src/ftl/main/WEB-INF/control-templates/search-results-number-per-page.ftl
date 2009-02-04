[#ftl/]
[#if totalCount > 25]
  <div id="number-per-page-controls" class="${attributes['name']}-number-per-page-controls jcatapult-module-number-per-page-controls">
    Number per page
    <a href="?searchCriteria.numberPerPage=25&searchCriteria.showAll=false">25</a> |
    <a href="?searchCriteria.numberPerPage=100&searchCriteria.showAll=false">100</a> |
    <a href="?searchCriteria.showAll=true">[@jc.message key="show-all" default="Show all"/]</a>
  </div>
[/#if]