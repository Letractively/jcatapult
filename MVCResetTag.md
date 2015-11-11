# Buttons #

The JCatapult MVC has three different types of buttons:

  * button
  * image
  * submit

Each of the buttons has the same attributes (the image tag has a few additional attributes). The only difference between them is the type of input tag that they generate. Here are the three types of HTML buttons that are generated:

```
button - <input type="button"/>
image - <input type="image"/>
submit - <input type="submit"/>
```

# Button Attributes #

The button tag supports all of the common HTML attributes that are covered in the [forms](MVCForms.md) documentation. In addition to those attributes, all of the button tags also support these attributes:

  * action - (String) Specify an alternate action to invoke (besides the action of the form) if the button is clicked
  * name - (String) Used to lookup the localized text of the button
  * value - (String) The value sent to the server if the button is clicked. In most cases you won't need this attribute and if you specify this attribute it might cause errors unless you add a property to your action with the same name as the button.
  * size - (int) The size of the button
  * alt - (String) A short description of the button (used in tool-tips)
  * tabindex - (int) The tabindex of the button
  * accesskey - (String) The access key of the button
  * onfocus - (String) The button got the focus
  * onblur - (String) The button lost focus

## Image button attributes ##

These are the attributes that are only used by the image button.

  * src - (String) Used only for image buttons to set the image source
  * usemap - (boolean) Used only for image buttons to indicate a client-side image map
  * ismap - (boolean) Used only for image buttons to indicate a server-side image map

# Examples #

```
[@jc.submit name="login"/]
[@jc.image name="register" src="/images/register-button.gif"/]
[@jc.button name="cancel" action="cancel"/]
```