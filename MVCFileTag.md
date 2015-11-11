# File #

The **file** tag generates a file upload control.

# Association #

A **file** must always be associated with a field or method that takes the type 'org.jcatapult.mvc.parameter.fileupload.FileInfo'. This class is a small struct that contains the information about the file that was uploaded.

Here are the fields in the 'FileInfo' class and their meanings:

  * file - The File object that points to the temporary storage location on the server where the uploaded file was stored. This file is deleted at the end of the request.
  * name - The name of the file as it exists on the client's file system
  * contentType - The MIME type that the browser sends to the server for the file

Here is an example action that contains a FileInfo field:

```
public class Upload {
  public FileInfo file;
}
```

The form for this action looks like this:

```
[@jc.form action="upload"]
  [@jc.file name="file"/]
[/@jc.form]
```

# Attributes #

The **file** tag supports all of the common HTML attributes that are covered in the [forms](MVCForms.md) documentation. In addition to those attributes, the **file** tag also supports these attributes:

  * name - (String) The action property that is associated with the **file** tag and used to populate the associated property of the action when the form is submitted. The name is also used to lookup the localized label for the **file** from the resource bundle
  * size - (int) The size of the file tag
  * disabled - (boolean) Disables the file tag
  * tabindex - (int) The tabindex of the checkbox

# Examples #

```
<!-- Generate a file for upload -->
[@jc.file name="file"/]
```

The resource bundle might look like this:

```
file=Select a file to upload
```

# Collections #

The file upload can handle collections of files like:

```
public class Action {
  public List<FileInfo> files;
}
```

You can also place the annotation on a collection property. In order to handle errors for collections, you need not specify errors for each possible index within the collection as this would be really lame. Instead, just use the name of the property for errors like this:

```
files=Select files to upload
files.required=You must upload a file dude.
files.fileUploadSize=That file is WAY too big man.
```

The form fields for this looks like:

```
[@jc.file name="files"/]
[@jc.file name="files"/]
[@jc.file name="files"/]
```