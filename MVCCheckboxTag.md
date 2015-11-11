# Checkbox #

The **checkbox** tag generates a single checkbox.

# Association #

In some cases, a **checkbox** tag is associated to a collection or array on the action. This is because the user can select multiple checkboxes and each selected checkbox might have the same name. The HTML generated might look like this:

```
<label>User role</label>
<input type="checkbox" name="roleIds" value="1"/>

<label>Admin role</label>
<input type="checkbox" name="roleIds" value="2"/>
```

The association on the action would look like this:

```
public class Register {
  public int[] roleIds;
}
```

When the form is submitted the **roleIds** field in the action is populated with the ids from the selected checkboxes.

On the flip-side, some checkboxes might have a unique name on the form. In this case you can use any type that you want inside the action. Booleans are often a good choice, but any types should work. If the checkbox is not selected, null is set into the associated property. However, if the associated property is a primitive, then the default value for the primitive is used.

# Checked State #

When the form is rendered, the checked state of the checkbox is determined by using the associated property from the action and the value of the checkbox. If the associated property is an array or collection, each element in the array or collection is compared to the value of the checkbox. If the value of the checkbox is found in the array or collection, the checkbox is checked. Otherwise, the checkbox is left unchecked.

If the associated property is not an array or collection, it is compared directly to the value of the checkbox. If they are equal, the checkbox is checked, otherwise it is left unchecked.

# Attributes #

The checkbox tag supports all of the common HTML attributes that are covered in the [forms](MVCForms.md) documentation. In addition to those attributes, the checkbox tag also supports these attributes:

  * name - (String) The action property that is associated with the checkbox and used to determine if the checkbox is checked when the form is rendered and also used to populate the associated property of the action when the form is submitted. The name is also used to lookup the localized label for the checkbox from the resource bundle
  * value - (String) The value of the checkbox. This is used to determine the checked state of the checkbox during render
  * size - (int) The size of the checkbox
  * alt - (String) A short description of the checkbox (used in tool-tips)
  * tabindex - (int) The tabindex of the checkbox
  * accesskey - (String) The access key of the checkbox
  * onfocus - (String) The checkbox got the focus
  * onblur - (String) The checkbox lost focus

# Examples #

```
<!-- Generate a checkbox for the newsletter choice -->
[@jc.checkbox name="newsletter" value="true"/]
```

The resource bundle might look like this:

```
newsletter=Would you like to receive our newsletter?
```