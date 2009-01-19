/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */

var submit = false;

jQuery.fn.serialize_form = function() {
  var data = {};
  this.find(":input").each(function() {
    $this = $(this);
    data[$this.attr("name")] = $this.val();
  });

  return data;
}

jQuery.fn.clear_form = function() {
  submit = true;
  this.find(":input").trigger("focus");
  return this;
}


function input_blur(input, value) {
  if (!submit && input.value.match(/^ *$/)) {
    input.value = value;
    $(input).addClass('blank');
  };
}

function input_focus(input, value) {
  if (input.value == value) {
    input.value = '';
    $(input).removeClass('blank');
  };
}


