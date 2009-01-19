[#ftl/]
<html id="cms-html">
<head>
  <title>Site editor</title>
  <link rel="stylesheet" href="http://dev.jquery.com/view/tags/ui/latest/themes/flora/flora.all.css"/>
  <script type="text/javascript" src="/js/jquery-ui-personalized-1.6b.min.js"></script>
  <script type="text/javascript" src="/module/cms/cms-1.0.js"></script>
  <script type="text/javascript" src="http://js.nicedit.com/nicEdit-latest.js"></script>
</head>
<body id="cms-body">
<div id="cms-toolbar">
  <div id="cms-promo">
    The JCatapult CMS
  </div>
  <a id="cms-publish" href="/cms/content/publish" onclick="CMS.publish(); return false;">Publish</a>
  <a id="cms-revert" href="/cms/content/revert" onclick="CMS.revert(); return false;">Revert</a>
  <a id="cms-meta" href="/cms/content/meta" onclick="CMS.edit_meta_nodes(); return false;">Edit page details</a>
  <a id="cms-exit" href="/cms/exit">Exit</a>
</div>
<div id="cms-content-editor-container">
  <div id="cms-content-editor" class="flora">
    [@jc.form action="/cms/content/store" method="POST"]
      [@jc.textarea id="cms-content-editor-textarea" name="content" cols="70" rows="10"/]
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