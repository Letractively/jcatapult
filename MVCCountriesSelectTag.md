# Countries Select #

The **countriesselect** tag generates a select box that is populated with the countries of the world. The options of the select always have a value of the 2 character ISO country code (i.e. United States = US, Germany = DE). The text of the options is the localized country name, based on the current [Locale](MVCLocale.md) of the MVC. The countries in the select box are always sorted alphabetically. You can place specific countries at the top using the **preferredCodes** attribute.

# Association #

The **countriesselet** tag is usually always associated with an String property of the action. However, you could also create a custom type using a custom type converter. The HTML generated might look like this:

```
<label>Country</label>
<select name="creditCard.address.country">
<option value="US">United States</option>
...
</select>
```

The association on the action would look like this:

```
public class Register {
  public CreditCard creditCard;
}

public class CreditCard {
  public Address address;
}

public class Address {
  public String country;
}
```

When the form is submitted the **creditCard.address.country** field in the action is populated with the value from the selected option.

# Selected State #

When the form is rendered, the selected state of the options in the select box are determined by using the associated property from the action and the value of each option (the 2 character ISO country code). If the value of the associated property matches the value of an option, that options selected state is set to selected.

# Attributes #

The countriesselect tag supports all of the common HTML attributes that are covered in the [forms](MVCForms.md) documentation. In addition to those attributes, the countriesselect tag also supports these attributes:

  * name - (String) The action property that is associated with the countriesselect and used to determine if any options in the select box are selected when the form is rendered. It is also used to populate the associated property of the action when the form is submitted. The name is also used to lookup the localized label for the countriesselect from the resource bundle
  * preferredCodes - (String) A comma separated list of codes that should be listed ahead of the other codes (i.e. "US,DE,CA")
  * includeBlank - (boolean) Determines if the top option in the select box should be an empty option whose value is null
  * size - (int) The size of the select box
  * alt - (String) A short description of the select box (used in tool-tips)
  * tabindex - (int) The tabindex of the select box
  * accesskey - (String) The access key of the select box
  * onfocus - (String) The select box got the focus
  * onblur - (String) The select box lost focus

# Examples #

```
<!-- Generate a countries select box -->
[@jc.countriesselect name="creditCard.address.country"/]

<!-- Generate a countries select box that has a blank and United States at the top -->
[@jc.countriesselect name="creditCard.address.country" includeBlank=true preferredCodes="US"/]
```

The resource bundle might look like this:

```
creditCard.address.country=Country
```