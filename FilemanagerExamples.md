# Introduction #

This section documents examples of how to use the jCatapult Filemanger inside your webapp.  This document will be broken down into 3 categories:

  1. JSP Examples
  1. Class Examples

## JSP Examples ##
As of 1.0-M6, the jCatapult Core library comes shipped with a struts theme called 'semantic' that you can use in your struts forms.  Within the struts theme's file.ftl template, we have added CRUD like UI widget for managing files in your HTML forms.  Specifically, the capability to not only upload, but view, edit, and delete existing files on disk.

Imagine that you have a simple form below:

```
<html>
  <head>
    <title>Simple Employee Edit Form</title>
  </head>
  <body>
    <s:form action="save" method="POST" enctype="multipart/form-data">
      Name: <s:textfield name="employee.name" label="Name:" required="true"/>
      Employee Image: <s:file name="image" label="Employee Image:" labelposition="top"/>
    </form>
  </body>
</html>
```

This form works fine if, for instance, all it does is upload a file.  However, what if you want the form to also provide the capability to view and delete a previous file that was uploaded?  Rather than using the form to 'just upload' you can also use the form to view, edit, and delete existing files to existing users.  I have modified the form below to help illustrate:

```
<html>
  <head>
    <title>Employee Edit Form</title>
  </head>
  <body>
    <s:form action="save" method="POST" enctype="multipart/form-data">
      <s:textfield name="employee.name" label="Name:" required="true"/>
      <s:file name="image" labelposition="top" label="Employee Image:" size="50" fileURI="${employee.fileURI}" deleteURI="deleteFile?id=${employee.id}"/>
    </form>
  </body>
</html>


```

The major difference between the first and second examples is that the second example uses the 'fileURI' and 'deleteURI' attributes.  This flags the file.ftl template located in the JCatapult Core semantic theme to create CRUD like UI for the file upload widget.

  * fileURI:  This param should be set to the location relative to the host where the file you are uploading is accessible via http.  For instance, if I upload a file and it's accessible via ![http://www.testsomethingxxx.com/files/employeeX.gif](http://www.testsomethingxxx.com/files/employeeX.gif), then the fileURI would be set to /files/employeeX.gif

  * deleteURI:  This is the action responsible for deleting the file from disk.  This is implemented by you.  In the example above, the app would have an action called 'deleteImage' which takes an employee id.  This action uses the employee id to load that particular employee from the database and then set the field holding the image uri to null.

## Class Examples ##
Please follow the [Filemanager Configuration and Setup Guide](FilemanagerConfiguration.md) before contiuing due to the fact that these examples depend on having the Filemanger configured and setup correctly.

# Delete File #
Below is a sample class called `DeleteFile` that is referenced in the jsp example above:

```
package org.jcatapult.example.action.admin.attorney;

import java.io.File;

import org.jcatapult.struts.action.BaseAction;
import org.apache.struts2.convention.annotation.Result;

import org.jcatapult.example.domain.Employee;

/**
 * Deletes an employee image by removing the file from disk and setting the employee
 * image file uri to null in the employee db record
 */
@Result(name = "success", location = "edit-employee.jsp")
public class DeleteFile extends BaseAction {

    private Integer id;

    @Override
    public String execute() throws Exception {

        // get the employee by id passed as the query param
        Employee employee = persistenceService.findById(Employee.class, id);
        
        // get the image file uri so that we can delete it from disk
        String fileURI;
        fileURI = employee.getImageURI();

        // set the image file uri to null in the hibernate entity bean and persist it
        employee.setImageURI(null);
        persistenceService.persist(employee);

        // delete the image file from disk
        String fileDir = configuration.getString("jcatapult.file-mgr.file-servlet.dir");
        File file = new File(fileDir, fileURI);
        if (file.exists()) {
            file.delete();
        }

        return SUCCESS;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

```