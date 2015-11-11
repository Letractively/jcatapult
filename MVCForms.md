# Forms #

JCatapult MVC currently uses FreeMarker for the rendering HTML results. In order to providing an easy mechanism for creating forms and managing all of the complexity that comes along with forms, JCatapult provides a number of tags that can be used from any FreeMarker template. These tags provide support for:

  * Localized labels
  * Field error display
  * Required field indicators (usually an asterisk)
  * Pre-population from action properties

# Tags #

The tags that are provided by JCatapult MVC are:

  * [form](MVCFormTag.md) - Renders the HTML form
  * [button](MVCButtonTags.md) - Renders a button (i.e. `<input type="button">`)
  * [checkboxlist](MVCCheckboxListTag.md) - Renders a set of checkbox controls from a Collection
  * [checkbox](MVCCheckboxTag.md) - Renders a single checkbox
  * [countriesselect](MVCCountriesSelectTag.md) - Renders a selectbox that contains countries
  * [file](MVCFileTag.md) - Renders a file upload control
  * [hidden](MVCHiddenTag.md) - Renders a hidden input tag (i.e. `<input type="hidden">`)
  * [image](MVCButtonTags.md) - Renders an image submit button
  * [monthsselect](MVCMonthsSelectTag.md) - Renders a seleectbox that contains months
  * [password](MVCPasswordTag.md) - Renders a password input tag (i.e. `<input type="password">`)
  * [radiolist](MVCRadioListTag.md) - Renders a group of radio buttons (i.e. `<input type="radio">`)
  * [reset](MVCButtonTags.md) - Renders a reset button (i.e. `<input type="reset">`)
  * [select](MVCSelectTag.md) - Renders a selectbox (i.e. `<select>`)
  * [submit](MVCButtonTags.md) - Renders a submit button (i.e. `<input type="submit">`)
  * [textarea](MVCTextareaTag.md) - Renders a textarea tag (i.e. `<textarea>`)
  * [text](MVCTextTag.md) - Renders a text input tag (i.e. `<input type="text">`)
  * [yearsselect](MVCYearsSelectTag.md) - Renders a selectbox that contains years

All of the JCatapult MVC tags can be used in the FreeMarker templates like this:

```
[#ftl/]
[@jc.form action="some-action"]
  [@jc.text name="firstName"/]
[/@jc.form]
```

JCatapult provides the tags by adding a Hash to the template under the key **jc** that contains each tag as a FreeMarker directive. You can read more about these concepts in the FreeMarker documentation.

# Association #

JCatapult MVC behaves in much the same way that other MVCs do. Input tags are associated with a property in an action. When the form is submitted, JCatapult MVC places the values from the form input tags into the property they are associated with. Likewise, when the form is rendered, JCatapult MVC pulls values from the properties and uses it to populate the form. The tags that work in this manner are checkboxlist, checkbox, countriesselect, hidden, image, monthsselect, password, radio, reset, select, submit, textarea, text, and yearsselect.

The way each tag is associated with a property in the action is via the **name** attribute of the tag. The name can be a property in the action or a nested property inside an object in the action. The action is always the starting point for association. Here is an example of a simple action that has a local property that we want to associate with a field on the form:

```
@Action
public class RegisterAction {
  public String login;
  public String password;

  ...
}
```

This action is used for registering new users to the website. Here is the form that goes with the action:

```
[#ftl/]
[@jc.form action="register"]
  [@jc.text name="login"/]
  [@jc.password name="password"/]
  [@jc.submit name="register"/]
[/@jc.form]
```

# Labels #

You might have noticed that the form from our last examples didn't have any labels for the form fields. By default, JCatapult MVC uses the [localization](MVCMessagesLocalization.md) resource bundles to generate labels. This process is also used to retrieve the text that is displayed on buttons. The **name** attribute of the tag is used as the key into the resource bundle for the labels and button text. Here is a sample resource bundle that contains the labels for our register form.

```
login=Enter a login name
password=Select a password
register=Register
```

# Common HTML Attributes #

Each tag that JCatapult provides supports the full set of HTML attributes as they are defined in the W3C HTML specification. The only exception to this is the **name** attribute for some tags. The standard HTML attributes are:

**Core Attributes**
  * id - (String) The ID of the HTML element
  * class - (String) The CSS class of the HTML element
  * style - (String) The CSS style definition of the HTML element
  * title - (String) The title of the element

**Internationalization Attributes**
  * lang - (String) The language of the HTML element
  * dir - (String) The text direction of the text in the HTML element

**Event Attributes**
  * onclick - (String) A pointer button was clicked
  * ondblclick - (String) A pointer button was double clicked
  * onmousedown - (String) A pointer button was pressed down
  * onmouseup - (String) A pointer button was released
  * onmouseover - (String) A pointer was moved onto
  * onmousemove - (String) A pointer was moved within
  * onmouseout - (String) A pointer was moved away
  * onkeypress - (String) A key was pressed and released
  * onkeydown - (String) A key was pressed down
  * onkeyup - (String) A key was released