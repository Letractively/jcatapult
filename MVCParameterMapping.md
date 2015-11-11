# HTTP Request Parameters #

HTTP request parameters are set into the action classes by the JCatapult MVC. These parameters include any GET parameters from the URL as well as any form POST parameters from the HTTP message. Each parameter is used to populate values on the action class. This process is known as parameter mapping and has a number of steps that are described in this document.

# Null Values #

One important difference between the JCatapult MVC and many other MVCs is that it handles null and empty values as the same. HTML and XHTML specify that if a text field is left blank the parameter is sent to the server as an empty String. This can be confusing because empty String and null are two very different values.

The JCatapult parameter mapping assumes that all empty String parameters are actual null. The only caveat to this are primitive values. If an empty String parameter is sent for a primitive, the default value of the primitive is used in place of null. This is covered more in the [type conversion](MVCTypeConversion.md) documentation.

# Naming #

Parameter names are used to map directly to values inside Java objects. Let's take a simple action class (the package, imports, annotations and execute method have been left out for simplicity):

```
@Action
public class CoolAction {
  public String name;
}
```

We can set the value of the name field using HTTP parameters. If we want to set the value of the name field to **Frank**, we might use a URI like this:

```
/cool?name=Frank
```

## Nesting ##

Parameters can also be used to populate complex data models. Using the JavaBean naming convention, values can be set into objects that are defined on the action. Let's expand our original example and add a new data model class called User.

```
public class User {
  public String name;
}

@Action
public class CoolAction {
  public User user;
}
```

The action now contains a reference to the `User` rather than the name. The name has been moved into the `User` class. In order to set the name of the user the URI would now look like this:

```
/cool?user.name=Frank
```

### Object Creation ###

As you might have noticed, the User field of the action defaults to **null**. Any time a null value is encountered during the parameter mapping process, JCatapult creates a new instance of the correct type and sets it back into the correct location. It then proceeds with the parameter mapping using the newly created object. If we were going to write code to populate the **/cool-action?user.name=Frank** URI from above, it would look like this:

```
if (action.user == null) {
  action.user = new User();
}

user.name = "Frank";
```

This isn't too much code, but imagine if you had a deeply nested case like `user.mother.address.city`. This would require a number of null checks, instantiations and value setting. The JCatapult parameter mapping handles all of this for you.

## Indexing ##

The JCatapult parameter mapping also supports indexed properties and collections. This includes support for arrays, Collections and Maps. Indexes are supplied using the [.md](.md) (bracket) notation. Here are some examples:

```
answer[0] = position 0 in an array or Collection
answer['secret'] = Map value with a key of new String("secret")
```

If you wanted to set the second answer to `Spike` (assuming the question is something like "What's your pet's name?"), you would do so like this:

```
/cool?answer[2]=Spike
```

## Generics ##

One of the best features of the JCatapult parameter mapping is that it fully supports generics and the Collection API from the JDK. To illustrate how generics are handled, let's add a few more classes to our data model:

```
public class Address {
  public String street;
  public String city;
  public String state;
  public String zipcode;
}

public class User {
  public String name;
  public Map<String, Address> addresses;
}

@Action
public class CoolAction {
  public User user;
}
```

Let's say that we want to populate the user and their address. We can do so using this URI:

```
/cool?user.address['home'].street=100+Main+st&user.address['home'].city=Springfield+user.address['home'].state=IO&user.address['home'].zipcode=11111&user.name=Frank
```

If we break this down, we have these individual parameters

  * user.address['home'].street = 100 Main st
  * user.address['home'].city = Springfield
  * user.address['home'].state = IO
  * user.address['home'].zipcode = 11111
  * user.name = Frank

The JCatapult parameter mapping will take these actions to set the values into the Objects:

  1. Check if user is null
  1. Create a new instance of the User class since that is the type of the field
  1. Set the new User into the action
  1. Check if the addresses Map in the newly created User is null
  1. Create a new HashMap and set it into the User
  1. Check if the value stored in the Map under the key `home` is null
  1. Create a new Address instance since that is the generic type of the Map
  1. Set the new Address into the Map under the key `home`
  1. Set the street, city, state and zipcode values into the newly created Address
  1. Set the name into the User

As you can see, JCatapult will use the generic information of the Map to create the correct type of Object to put into the Map. In this case the correct type is the Address class.