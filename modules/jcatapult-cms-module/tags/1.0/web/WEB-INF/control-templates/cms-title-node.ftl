[#ftl/]
<title>${content.content}</title>[#t/]
[#if inEditMode]<script type="text/javascript">top.CMS.register_meta_node('title', '${content.content?js_string}', 'The page title', 'Title');</script>[/#if][#t/]