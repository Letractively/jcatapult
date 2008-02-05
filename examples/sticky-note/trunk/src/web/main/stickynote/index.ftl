[#ftl]
<html>
<head><title>StickyNote | Index</title></head>
<body>
[@s.form action="delete" method="POST" theme="simple"]
  <table id="listing">
    <tr>
      <th id="note-header"><a href="index?sortProperty=note">Note</a></th>
      <th id="headline-header"><a href="index?sortProperty=headline">Headline</a></th>
      <th id="delete-header">Delete</th>
    </tr>
    [#list stickyNotes as stickyNote]
      <tr>
        <td class="[#if stickyNote_index % 2 == 0]even[#else]odd[/#if] note-row"><a href="edit?id=${stickyNote.id}">${stickyNote.note}</a></td>
        <td class="[#if stickyNote_index % 2 == 0]even[#else]odd[/#if] headline-row">${stickyNote.headline}</td>
        <td class="[#if stickyNote_index % 2 == 0]even[#else]odd[/#if] delete-row">[@s.checkbox name="ids" fieldValue="${stickyNote.id}"/]</td>
      </tr>
    [/#list]

    [#if stickyNotes?size == 0]
      <tr>
        <td colspan="4" class="empty-row">No stickyNotes on file</td>
      </tr>
    [/#if]
  </table>
  <div id="listing-controls">
    <a href="add"><button>ADD AN STICKYNOTE</button></a>
    [@s.submit type="button" value="DELETE"/]
  </div>
  tc is ${totalCount}
  npp is ${numberPerPage}
  [#if totalCount % numberPerPage > 0]
    [#assign extra = 1]
  [#else]
    [#assign extra = 0]
  [/#if]
  e is ${extra}
  [#assign totalPages = (totalCount / numberPerPage) + extra]
  tp is ${totalPages}
  [#if totalPages?int > 1 && !showAll]
    <div id="pagination-controls">
      [#if page > 1]
        <a href="index?page=${page - 1}&numberPerPage=${numberPerPage}">Prev</a> |
      [/#if]
      [#if page == 1]
        Prev |
      [/#if]

      [#if page - 5 < 1]
        [#assign start = 1]
      [#else]
        [#assign start = page - 5]
      [/#if]

      [#assign end = page - 1]
      [#if end > start]
        [#list start..end as i]
          <a href="index?page=${i}&numberPerPage=${numberPerPage}">${i}</a> |
        [/#list]
      [/#if]
      ${page} |

      [#assign start = page + 1]
      [#if page + 5 > totalPages]
        [#assign end = totalPages]
      [#else]
        [#assign end = page + 5]
      [/#if]
      [#if end > start]
        [#list start..end as i]
          <a href="index?page=${i}&numberPerPage=${numberPerPage}">${i}</a> |
        [/#list]
      [/#if]

      [#if totalCount > (page * numberPerPage)]
        <a href="index?page=${page + 1}&numberPerPage=${numberPerPage}">Next</a>
      [/#if]
      [#if totalCount <= (page * numberPerPage)]
        Next
      [/#if]
    </div>
  [/#if]
  [#if totalCount > 25]
    <div id="number-per-page">
      Number per page
      <a href="index?numberPerPage=25">25</a> |
      <a href="index?numberPerPage=100">100</a> |
      <a href="index?showAll=true">Show all</a><br/>
    </div>
  [/#if]
[/@s.form]
</body>
</html>