[#ftl/]
[#assign joda=JspTaglibs['http://www.joda.org/joda/time/tags']/]

[#macro params ignore]
[#if !persistent]
[#list searchCriteria.parameters?keys as key]
[#if key != ignore && searchCriteria.parameters[key]??]
${key}=${searchCriteria.parameters[key]?string?url('UTF-8')}&[#t/]
[/#if]
[/#list]
[/#if]
[/#macro]

<table id="listing" class="${attributes['name']}-listing jcatapult-module-listing">
  <tr>
    [#list attributes['properties'] as prop]
      <th id="${prop?replace('.', '-')}-header">[#rt/]
        <a href="?[@params "searchCriteria.sortProperty"/]searchCriteria.sortProperty=${prop}[#if (searchCriteria.sortProperty!"")?contains(prop + " desc")]+asc[#else]+desc[/#if]">[#t/]
          [@jc.message key=(attributes['name'] + "." + prop)/][#t/]
          [#if (searchCriteria.sortProperty!"")?contains(prop + " desc")]
            <img src="/module/jcatapult-crud/sort-arrow-up-1.0.gif" alt="" border="0"/>[#t/]
          [#elseif (searchCriteria.sortProperty!"")?contains(prop + " asc")]
            <img src="/module/jcatapult-crud/sort-arrow-down-1.0.gif" alt="" border="0"/>[#t/]
          [/#if]
        </a>[#t/]
      </th>[#rt/]
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
        [#assign value=("((result." + prop + ")!'')")?eval/]
        <td class="[#if result_index % 2 == 0]even[#else]odd[/#if] ${prop?replace('.', '-')}-row">[#t/]
          [#if prop_index == 0 && detailable]<a href="details?[@params "none"/]index=${result_index}&id=${result.id?c}">[#t/][/#if]
          [#if value?is_date]
            ${value?string(attributes['dateTimeFormat']!"MM/dd/yyyy")}[#t/]
          [#elseif isjodadate(value)]
            [@joda.format value=value pattern=attributes['dateTimeFormat']!"MM/dd/yyy"/][#t/]
          [#else]
            ${value?string}[#t/]
          [/#if]
          [#if result_index == 0]</a>[#t/][/#if]
        </td>[#t/]
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