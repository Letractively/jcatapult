[#ftl/]
<html id="cms-html">
<head>
  <title>Site editor</title>
  <script type="text/javascript" src="/module/jcatapult-cms-module/cms-1.0.js"></script>
  <script type="text/javascript" src="${richTextEditorURL}"></script>
  <script type="text/javascript">
    CMS.set_options({
      rich_text_editor: "${richTextEditor}"
    });
    $(function() {
      CMS.initialize_cms();
    });
  </script>
</head>
<body id="cms-body">
<div id="cms-toolbar">
  <div id="cms-promo">
    [@jc.message key="promo" default="The JCatapult CMS"/]
  </div>
  <a id="cms-publish" href="/admin/cms/content/publish" onclick="CMS.publish(); return false;">Publish</a>
  <a id="cms-revert" href="/admin/cms/content/revert" onclick="CMS.revert(); return false;">Revert</a>
  <a id="cms-meta" href="/admin/cms/content/meta" onclick="CMS.edit_meta_nodes(); return false;">Edit page details</a>
  <a id="cms-exit" href="/admin/cms/exit">Exit</a>
</div>
<div id="cms-content-editor-container">
  <div id="cms-content-editor" class="flora">
    [@jc.form action="/admin/cms/content/store" method="POST"]
      [@jc.textarea id="cms-content-editor-textarea" name="content" cols="70" rows="15"/]
      [@jc.submit name="preview" onclick="CMS.preview_content_node(); return false;"/]
      [@jc.submit name="cancel" onclick="CMS.cancel_edit_content_node(); return false;"/]
    [/@jc.form]
  </div>
</div>
<div id="cms-meta-editor-container">
  <div id="cms-meta-editor" class="flora">
    <form id="cms-meta-editor-form" action="" method="POST">
      [@jc.submit name="preview" onclick="CMS.preview_meta_nodes(); return false;"/]
      [@jc.submit name="cancel" onclick="CMS.cancel_edit_meta_nodes(); return false;"/]
    </form>
  </div>
</div>
<iframe id="cms-view" name="cms-view" src="${startURI}">

</iframe>
</body>
</html>