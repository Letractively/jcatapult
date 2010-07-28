[#ftl/]
[#macro class attributes name input]
[#if attributes['class']??]${attributes['class']}-${name} ${attributes['class']}-[#if input]input[#else]button[/#if] ${attributes['class']}-control [/#if]${name} [#if input]input[#else]button[/#if] control[#t/]
[/#macro]