/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
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
 *
 */


/**
 * This is a Node class that stores an node that has been edited.
 *
 * @param   span_id The ID of the span within the IFrame that has been edited.
 * @param   name The name of the content node.
 * @param   uri The URI of the content node.
 * @param   global A flag that determines if the content node is global or not.
 * @param   content The initial content of the node.
 * @param   content_type The content type.
 * @param   description The description of the node.
 * @param   label The label used when editing the node.
 */
function Node(span_id, name, uri, global, content, content_type, description, label) {
  this.span_id = span_id;
  this.name = name;
  this.uri= uri;
  this.global = global;
  this.edited_content = content;
  this.original_content = content;
  this.content_type = content_type;
  this.description = description;
  this.label = label;
}

function CMS_class() {
  var current_node;
  var edited_nodes = {};
  var current_editor;
  var options = {};

  /**
   * Sets the options for the CMS class. The options are:
   *
   * (function)_(pre, post): Callback functions before and after the specified function has been
   * called. If the function is the edit_content_node, then you can setup a pre callback like this
   * {edit_content_node_pre: function() {}}. The parameters to the functions are:
   *
   *  1 - The current node being edited if the CMS editor is editing a content node
   *  2 - The hash of edited nodes
   *  3 - Any result from an AJAX call (post methods only)
   *
   * @param   opts The options.
   */
  this.set_options = function(opts) {
    options = opts;
  };

  /**
   * Registers a content node so that it can be edited.
   *
   * @param   div_id The ID of the div the content is stored in.
   * @param   name The name of the content node.
   * @param   global True if the node is global.
   * @param   default_content The default content of the node.
   */
  this.register_content_node = function(div_id, name, global, default_content) {
    var uri = frames["cms-view"].location.pathname;
    edited_nodes[name] = new Node(div_id, name, uri, global, default_content, "HTML", "Content node", "Content");
  };

  /**
   * Registers a meta node so that it can be edited.
   *
   * @param   name The name of the meta node.
   * @param   default_content The default content of the node.
   */
  this.register_meta_node = function(name, default_content, description, label) {
    var uri = frames["cms-view"].location.pathname;
    edited_nodes[name] = new Node("", name, uri, false, default_content, "META", description, label);

    // Build the form by hand
    var html = "<div class='input'><label for='" + edited_nodes[name].name + "'>" + edited_nodes[name].label + "</label><br/>" +
               "<input type='text' name='" + edited_nodes[name].name + "' value='" + edited_nodes[name].edited_content + "'/></div>";
    $("#cms-meta-editor-form").prepend(html);
  };

  /**
   * Opens the CMS editor for editing a content node.
   *
   * @param   name The name of the content node.
   */
  this.edit_content_node = function(name) {
    CMS.invoke(options, "edit_content_node_pre");
    if (edited_nodes[name]) {
      current_node = edited_nodes[name];
      CMS.open_content_editor(current_node.edited_content);
    } else {
      alert("The JCatapult CMS is experiencing issues. Please contact the JCatapult team with the error code 1.");
    }
    CMS.invoke(options, "edit_content_node_post");
  };

  /**
   * Opens the CMS editor for editing all the meta nodes.
   */
  this.edit_meta_nodes = function() {
    CMS.invoke(options, "edit_meta_nodes_pre");
    CMS.open_meta_editor();
    CMS.invoke(options, "edit_meta_nodes_post");
  };

  /**
   * Cancels an edit.
   */
  this.cancel_edit_content_node = function() {
    CMS.invoke(options, "cancel_edit_content_node_pre");
    CMS.close_content_editor();
    CMS.invoke(options, "cancel_edit_content_node_post");
  };

  /**
   * Cancels an edit.
   */
  this.cancel_edit_meta_nodes = function() {
    CMS.invoke(options, "cancel_edit_meta_nodes_pre");
    CMS.close_meta_editor();
    CMS.invoke(options, "cancel_edit_meta_nodes_post");
  };

  /**
   * Previews the edit.
   */
  this.preview_content_node = function() {
    CMS.invoke(options, "preview_content_node_pre");

    CMS.close_rich_text_editor();
    current_node.edited_content = $("#cms-content-editor-textarea").val();
    $("#cms-view").contents().find("#" + current_node.span_id).html(current_node.edited_content);
    $("#cms-publish").show();
    $("#cms-revert").show();
    CMS.close_content_editor();

    // Clear out the current edit information
    current_node = undefined;
    current_editor = undefined;
    CMS.invoke(options, "preview_content_node_post");
  };

  /**
   * Previews the edit.
   */
  this.preview_meta_nodes = function() {
    CMS.invoke(options, "preview_meta_nodes_pre");

    // Update the edited nodes
    $("#cms-meta-editor-form").find(":text").each(function() {
      edited_nodes[$(this).attr("name")].edited_content = $(this).val();
    });

    $("#cms-publish").show();
    $("#cms-revert").show();
    CMS.close_meta_editor();
    CMS.invoke(options, "preview_meta_nodes_post");
  };

  /**
   * Reverts all edited nodes on the page.
   */
  this.revert = function() {
    CMS.invoke(options, "revert_pre");

    $.each(edited_nodes, function() {
      var span_id = this.span_id;
      var original_content = this.original_content;
      $("#cms-view").contents().find("#" + span_id).html(original_content);
    });

    edited_nodes = {};

    $("#cms-publish").hide();
    $("#cms-revert").hide();
    CMS.close_content_editor();
    CMS.invoke(options, "revert_post");
  };

  /**
   * Sends all of the edited nodes to the server to be published.
   */
  this.publish = function() {
    for (var key in edited_nodes) {
      var node = edited_nodes[key];
      if (!node.edited_content || node.edited_content.length == 0 || node.edited_content == node.original_content) {
        continue;
      }

      CMS.invoke(options, "publish_pre", node);
      var postData = {dynamic: false, global: node.global, name: node.name, uri: node.uri, locale: "en_US", content: node.edited_content, contentType: node.content_type};
      $.ajax({
        async: false,
        data: postData,
        dataType: "json",
        success: function(data) {
          if (data.success) {
            alert("Content named [" + data.name + "] saved.");
            node.original_content = node.edited_content;
          } else {
            alert("Error saving content named [" + data.name + "]");
          }

          CMS.invoke(options, "publish_post", data);
        },
        type: "POST",
        url: "/cms/content/store.ajaxjson"
      });
    }

    $("#cms-publish").hide();
    $("#cms-revert").hide();
    CMS.close_content_editor();
  };

  /**
   * Opens the content editor dialog and puts the given content in the editor.
   *
   * @param   content The content.
   */
  this.open_content_editor = function(content) {
    CMS.invoke(options, "open_content_editor_pre");

    $("#cms-content-editor-textarea").val(content);
    $("#cms-content-editor").dialog({height: 400, modal: true, overlay: {opacity: 0.5, background: "black"}, width: 600});
    current_editor = CMS.create_rich_text_editor();

    CMS.invoke(options, "open_content_editor_post");
  };

  /**
   * Closes the content editor dialog.
   */
  this.close_content_editor = function() {
    CMS.invoke(options, "close_content_editor_pre");
    $("#cms-content-editor").dialog("close");
    CMS.invoke(options, "close_content_editor_post");
  };

  /**
   * Opens the meta editor dialog and puts the given content in the editor.
   */
  this.open_meta_editor = function() {
    CMS.invoke(options, "open_meta_editor_pre");
    $("#cms-meta-editor").dialog({height: 400, modal: true, overlay: {opacity: 0.5, background: "black"}, width: 600});
    CMS.invoke(options, "open_meta_editor_post");
  };

  /**
   * Closes the meta editor dialog.
   */
  this.close_meta_editor = function() {
    CMS.invoke(options, "close_meta_editor_pre");
    $("#cms-meta-editor").dialog("close");
    CMS.invoke(options, "close_meta_editor_post");
  };

  this.create_rich_text_editor = function() {
    if (options['rich_text_editor'] == 'nic') {
      return new nicEditor({fullPanel: true}).panelInstance('cms-content-editor-textarea');
    } else if (options['rich_text_editor'] == 'fck') {
      var editor = new FCKeditor('cms-content-editor-textarea');
      editor.BasePath = "/module/fckeditor/2.6.4/";
      editor.Config["CustomConfigurationsPath"] = "/module/jcatapult-cms-module/fckeditor-config-1.0.js";
      editor.ReplaceTextarea();
      return editor;
    } else {
      return undefined; // Just use text areas
    }
  };

  this.close_rich_text_editor = function() {
    if (options['rich_text_editor'] == 'nic') {
      current_editor.removeInstance("cms-content-editor-textarea");
    } else if (options['rich_text_editor'] == 'fck') {
      current_editor.UpdateLinkedField();
    } else {
      return undefined; // Just use text areas
    }
  };

  /**
   * Invokes a method in he given hash with the given name.
   *
   * @param   methods The methods hash.
   * @param   name The name of the method.
   * @param   data (Optional) A JSON object to pass to the method.
   */
  this.invoke = function(methods, name, data) {
    if (methods[name]) {
      if (data) {
        methods[name](current_node, edited_nodes, data);
      } else {
        methods[name](current_node, edited_nodes);
      }
    }
  };
}

var CMS = new CMS_class();