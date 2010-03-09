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
// Setup the file browser URIs
FCKConfig.LinkBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Connector=/jcatapult/fck/fck-file-manager&Type=File';
FCKConfig.ImageBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Connector=/jcatapult/fck/fck-file-manager&Type=Image';
FCKConfig.FlashBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Connector=/jcatapult/fck/fck-file-manager&Type=Flash';
FCKConfig.LinkUploadURL = '/jcatapult/fck/fck-file-manager?Command=FileUpload&Type=File&CurrentFolder=/';
FCKConfig.ImageUploadURL = '/jcatapult/fck/fck-file-manager?Command=FileUpload&Type=Image&CurrentFolder=/';
FCKConfig.FlashUploadURL = '/jcatapult/fck/fck-file-manager?Command=FileUpload&Type=Flash&CurrentFolder=/';

// Setup the buttons
FCKConfig.ToolbarSets["Default"] = [
['Source','DocProps','Cut','Copy','Paste','PasteText','PasteWord','-','Print','SpellCheck'],
['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],
'/',
['Bold','Italic','Underline','StrikeThrough','-','Subscript','Superscript'],
['OrderedList','UnorderedList','-','Outdent','Indent','Blockquote'],
['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],
['Link','Unlink','Anchor'],
['Image','Flash','Table','Rule','Smiley','SpecialChar','PageBreak'],
'/',
['Style','FontFormat','FontName','FontSize'],
['TextColor','BGColor']
];