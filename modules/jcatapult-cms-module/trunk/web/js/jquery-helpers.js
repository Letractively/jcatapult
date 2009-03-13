/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
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


