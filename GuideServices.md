= JCatapult Services =

JCatapult uses the 3-tier convention for writing applications. The standard 3 tier convention has these tiers:

  * Web tier – consisting of actions and views
  * Service tier - consisting of business services
  * Data tier - consisting of the persistence, ORM and database

This document covers the service tier and some of the services that JCatpault provides out of the box.

# Injection #

JCatapult uses Guice for dependency injection and this allows applications to easily and quickly write services that uses other services and write the actions and classes that use the services. Check out the GuiceDependencyInjection document for more information on JCatapult's dependency injection.

# Provided Services #

JCatapult provides a number of services out of the box to make development easier. However, if these services don't fit the bill you can easily write your own services to replace them. Some of the services that JCatapult provides are:

  * Persistence service - this service is one of the best services JCatpault provides. It is located in the JCatapult Core library and provides a nice wrapper around JPA that makes finding, storing and deleting JPA entity classes simple
  * Email service - this service provides a great way to add email support to your application. This service leverages FreeMarker templates to generate the content of an email and then allows you to send the email right away or in the future. This service is located in the JCatapult Email library.
  * Email transport service - this service is a wrapper around the somewhat clunky JavaMail API. It provides simple model objects to create emails and then a service to send the emails out using any SMTP server. This API is useful for bypassing the templating engine of the Email Service and just going directly to the email server. This service is located in the JCatapult Email library.
  * FileManager services - this service provides a mechanism for uploading, moving, copying, renaming and deleting files. It provides an API that can be easily integrated with the FCK editor and also an XML API that can be used by any client. This service also has a file servlet that provides access to uploaded files, which is nice for content management systems and other systems that require access to previously uploaded files. This service makes it easy to integrate file management and retrieval into applications. This service is located in the JCatapult FileMgr library.
  * Commerce service - this service provides a mechanism for charging and authorizing credit cards using payment gateways. The service currently has a single implementation for Authorize.net, but we plan on adding more gateways soon. This service uses a number of model objects such as Money and CreditCard. This service is located in the JCatapult Commerce library.
  * Security services - JCatapult provides a number of different security related services include password encryption, authentication, authorization and login services. These service work closely with the JCatapult Security infrastructure, but can also be used independently. These services are located in the JCatapult Security library.

