[#ftl/]
[#macro params ignore]
[#if !persistent]
[#list searchCriteria.parameters?keys as key]
[#if !ignore?seq_contains(key) && searchCriteria.parameters[key]??]
${key}=${searchCriteria.parameters[key]?string?url('UTF-8')}&[#t/]
[/#if]
[/#list]
[/#if]
[/#macro]
[#if totalCount > 25]
  <div id="number-per-page-controls" class="${attributes['name']}-number-per-page-controls jcatapult-module-number-per-page-controls">
    [@jc.message key="numberPerPage" default="Number per page"/]
    [#if persistent]
      <a href="?searchCriteria.numberPerPage=25&searchCriteria.showAll=false">25</a> |
      <a href="?searchCriteria.numberPerPage=100&searchCriteria.showAll=false">100</a> |
      <a href="?searchCriteria.showAll=true">[@jc.message key="show-all" default="Show all"/]</a>
    [#else]
      <a href="?[@params ["searchCriteria.numberPerPage", "searchCriteria.showAll"]/]searchCriteria.numberPerPage=25&searchCriteria.showAll=false">25</a> |
      <a href="?[@params ["searchCriteria.numberPerPage", "searchCriteria.showAll"]/]searchCriteria.numberPerPage=100&searchCriteria.showAll=false">100</a> |
      <a href="?[@params ["searchCriteria.numberPerPage", "searchCriteria.showAll"]/]searchCriteria.showAll=true">[@jc.message key="show-all" default="Show all"/]</a>
    [/#if]
  </div>
[/#if]