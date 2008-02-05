[#ftl/]
[#list stickyNotes as stickyNote]
<div class="sticky-note">
  <h3>${stickyNote.headline}</h3>
  <p>${stickyNote.note}</p>
</div>
[/#list]

<!--<a href="/stickynote/add-ajax" onclick="$('#sticky-note-editor').load('/stickynote/add-ajax'); return false">Add a sticky note</a>-->
<!--<div id="sticky-note-editor"></div>-->