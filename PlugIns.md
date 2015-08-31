# Introduction #
OpenPoi is written with extensibility and flexibility in mind. If you only have basic needs, you can most likely use the default implementations supplied with OpenPoi, but the system is designed around the assumption that different users will have very different needs, and might need to integrate with all sorts of legacy databases (or files, or something else).

The solution is an architecture based around plug-ins. Many parts of OpenPoi can be replaced with your own implementation, if you need it.

Currently, the following parts can be controlled by plug-ins:
  * The information about POIs. OpenPoi makes no assumptions about what information your POIs contain, so you can implement them as you like (although OpenPoi's Javascript classes makes the assumption that they must have a location attribute, which is kind of sensible for a _point_ of interest). A default POI implementation is supplied by the `domain-simple` plug-in.
  * How POIs are stored. OpenPoi specifies an interface for how POI queries are formed, but you are free to supply your own implementation to integrate with your database. Two plug-ins are supplied with OpenPoi:
    * `backend-text` for really simple storage, where the POIs are stored in a flat text file
    * `backend-hibernate-spatial` which assumes POIs are stored in a spatial database, supported by [Hibernate Spatial](http://www.hibernatespatial.org/)

Plug-in architecture for how POIs are serialized to the client, and how POIs are cached is mostly written, but needs some further work to be configurable as plug-ins.

# How plug-ins are added #
Each plug-in is packaged as a separate JAR file. Installing a plug-in in OpenPoi is as easy as copying the JAR file into the web application's `WEB-INF/lib` folder (for example, `/path/to/tomcat/webapps/OpenPoi/WEB-INF/lib/`, if you are using Tomcat as your container).

After installing a plug-in, you will have to restart your container (or restart the application context, at least), since plug-ins are scanned when OpenPoi is started.

# Configuring plug-ins #
Plug-ins are configured from the web applications context parameters. This can be done by modifying the application's `web.xml` file (`/path/to/tomcat/webapps/OpenPoi/WEB-INF/web.xml`, if you are using Tomcat as your container).

An example for setting the memory cache's maximum size:
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE web-app PUBLIC
 "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
 "http://java.sun.com/dtd/web-app_2_3.dtd" >
<web-app>
  <display-name>OpenPoi</display-name>
  
  <context-param>
  	<param-name>cacheMaxSizeKb</param-name>
  	<param-value>128</param-value>
  </context-param>

  ...
```