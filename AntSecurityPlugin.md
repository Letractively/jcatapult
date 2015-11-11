# Introduction #

This plugin provides applications with the ability to output hashed password that are useful for building database seed and testing scripts.

This plugin contains a single XML file that should be imported like this:

```
<import file="${ant.home}/plugins-jcatapult/security/1.0/password.xml"/>
```

# Targets #

The plugin provides these targets:

## password ##

This target is an interactive process that converts a plain text password and salt into a hashed password that will be usable from JCatapult Security framework (i.e. ACEGI).