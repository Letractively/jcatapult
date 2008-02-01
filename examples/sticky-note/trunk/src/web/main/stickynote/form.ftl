[#ftl]
<h2>StickyNotes</h2>
[@s.form action="${actionType}" method="POST" theme="semantic"]
  <h3>StickyNote</h3>
  <div class="notice">
    Notice here.
  </div>
  <!-- Save off the information for updates and deletes -->
  [@s.hidden name="ids" value="%{stickyNote.id}"/]
  [@s.hidden name="stickyNote.id"/]

  [@s.textfield key="stickyNote.headline" required="false"/]
  [@s.textfield key="stickyNote.note" required="false"/]

  [@s.action name="prepare" id="prepare"/]

  [@s.submit value="Save"/]
  [#if actionType =="update"]
    [@s.submit action="delete" value="Delete"/]
  [/#if]
  [@s.submit name="redirectAction:index" value="Cancel"/]
[/@s.form]