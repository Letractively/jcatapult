[#ftl/]
<meta${append_attributes(attributes)} name="${name}" content="${content.content}"/>[#t/]
[#if inEditMode]<script type="text/javascript">top.CMS.register_meta_node('${name}', '${content.content?js_string}', '${attributes['description']}', '${attributes['label']}');</script>[/#if][#t/]