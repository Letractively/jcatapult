[#ftl]
<h2>[#if actionType == 'update']Updating[#else]Adding[/#if] a StickyNote</h2>
[@s.form action="${actionType}" method="POST" theme="semantic"]
  <div id="form-notice">
    Notice here.
  </div>
  <!-- Save off the information for updates and deletes -->
  [@s.hidden name="ids" value="%{stickyNote.id}"/]
  [@s.hidden name="stickyNote.id"/]

  [@s.textfield key="stickyNote.note" required="true"/]
  [@s.textfield key="stickyNote.uri" required="true"/]
  [@s.textfield key="stickyNote.headline" required="true"/]

  [@s.action name="prepare" id="prepare"/]

  <div id="form-controls">
    [@s.submit value="Save"/]
    [#if actionType =="update"]
      [@s.submit action="delete" value="Delete"/]
    [/#if]
    [@s.submit name="redirect:index" value="Cancel"/]
  </div>
[/@s.form]