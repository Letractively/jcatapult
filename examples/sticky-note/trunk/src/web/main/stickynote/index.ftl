[#ftl]
<html>
<head><title>StickyNote | Index</title></head>
<body>
[@s.form action="delete" method="POST" theme="simple"]
  <table>
    <tr>
      <th><a href="index?sortProperty=headline">Headline</a></th>
      <th><a href="index?sortProperty=note">Note</a></th>
      <th>Delete</th>
    </tr>
    [#list stickyNotes as stickyNote]
      <tr>
        <td><a href="edit?id=${stickyNote.id}">${stickyNote.headline}</a></td>
        <td>${stickyNote.note}</td>
        <td>[@s.checkbox name="ids" fieldValue="${stickyNote.id}"/]</td>
      </tr>
    [/#list]

    [#if stickyNotes?size == 0]
      <tr>
        <td colspan="4">No stickyNotes on file</td>
      </tr>
    [/#if]
    <tr>
      <td colspan="3"><a href="add"><button>ADD AN STICKYNOTE</button></a></td>
      <td>[@s.submit type="button" value="DELETE"/]</td>
    </tr>
  </table>

  [#if totalCount % numberPerPage > 0]
    [#assign extra = 1]
  [#else]
    [#assign extra = 0]
  [/#if]
  [#assign totalPages = (totalCount / numberPerPage) + extra]
  [#if totalPages > 1 && !showAll]
    <div id="pagination_controls">
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
  <div id="number_per_page">
    Number per page
    <a href="index?numberPerPage=25">25</a> |
    <a href="index?numberPerPage=100">100</a> |
    <a href="index?showAll=true">Show all</a><br/>
  </div>
[/@s.form]
</body>
</html>