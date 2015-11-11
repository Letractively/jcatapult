# Scopes #

Scopes are a relatively old concept that JCatapult has added directly into the MVC. The standard JEE scopes are:

  * Request
  * Session
  * Application (a.k.a. Context)

JCatapult MVC also provides support for a few new scopes:

  * Flash
  * ActionSession

The **Flash** scope provides a mechanism that allows a value to be stored so that it is only accessible during the next request. Any subsequent request will not have access to a previously Flashed value. This is performed using a special location in the Session scope and then transferring the any flashed values from this location to the Request scope at the start of each request.

The **ActionSession** scope provides an safety mechanism on top of the Session scope. Session scoped values are accessible anywhere in the application via their name. ActionSession values are stored in the Session scope so that they are accessible across requests, but they are only accessible to a specific action.

# Annotation #

Any value from an action can be placed into any of the JCatapult MVC scopes  using annotations inside the action. Here is an example action that uses the Session scope.

```
@Action
public class CheckOutAction {
  @Session
  public ShoppingCart shoppingCart;

  ...
}
```

When the JCatapult MVC Workflow is processing the request, if there is a value in the session under the name **shoppingCart**, that value is set into the action's field. Likewise, when the action has been executed and the JCatapult MVC Workflow is post-processing the request, the value of the shoppingCart field from the action is back into the session.

# Naming #

All scopes use the same convention for naming. If you don't set the value() attribute of the annotation, the default key that the value is stored and retrieved from the scope is the name of the field the annotation is on. If you specify a value() attribute on the annotation, that will be used as the key instead.

The ActionSession annotation also has an attribute named action that allows you to specify which action to scope the value to. This allows one action to access another action's session.