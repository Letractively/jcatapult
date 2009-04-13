[#ftl/]
<div id="listing-controls" class="${attributes['name']}-controls jcatapult-module-controls">
  [#if addable]
    <div id="add-control" class="${attributes['name']}-control jcatapult-module-control">
      <a href="add">[@jc.message key="add" default="Add"/]</a>
    </div>
  [/#if]
  [#if deletable]
    <div id="delete-control" class="${attributes['name']}-control jcatapult-module-control">
      [@jc.submit name="delete"/]
    </div>
  [/#if]
</div>