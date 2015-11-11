# Textarea #

The **textarea** tag generates a textarea.

# Association #

In most cases, a **textarea** tag is associated to a String property in the action. The value of the property is used when to populate the textarea when the form is rendered. The HTML generated might look like this:

```
<label>News story</label>
<textarea name="content">
The text here is from the property on the action.
</textarea>
```

The association on the action would look like this:

```
public class AddNews {
  public String content;
}
```

When the form is submitted the **content** field in the action is populated with the value from the textarea.

# Attributes #

The **textarea** tag supports all of the common HTML attributes that are covered in the [forms](MVCForms.md) documentation. In addition to those attributes, the tag also supports these attributes:

  * name - (String) The action property that is associated with the textarea and used to populate the textarea when the form is rendered and also used to populate the associated property of the action when the form is submitted. The name is also used to lookup the localized label for the textarea from the resource bundle
  * defaultValue - (String) Used to populate the textarea, if-and-only-if the value from the property in the action is null
  * value - (String) This value overrides the value from the property in the action when the form is rendered. This attribute is rarely used because if the form fails validation the users input into the textarea will be lost
  * cols - (int) The number of columns wide the textarea is
  * rows - (int) The number of rows high the textarea is
  * alt - (String) A short description of the textarea (used in tool-tips)
  * tabindex - (int) The tabindex of the textarea
  * accesskey - (String) The access key of the textarea
  * onfocus - (String) The textarea got the focus
  * onblur - (String) The textarea lost focus

# Examples #

```
<!-- Generate a textarea for the news story content -->
[@jc.textarea name="content"/]
```

The resource bundle might look like this:

```
content=News story
```