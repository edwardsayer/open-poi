# Introduction #

This page describes the steps necessary for setting up a basic installation of OpenPoi on your server.


# Requirements #

The requirements for installing OpenPoi:
  * Java 6
  * A servlet container of some sort. [Tomcat 6](http://tomcat.apache.org/download-60.cgi) has been used in the development of OpenPoi, so its certainly the safest bet, but any servlet container compliant with Servlet API 2.4 or later should do.

# Installing OpenPoi #

Download the latest OpenPoi package from the [downloads](http://code.google.com/p/open-poi/downloads/list) page.

OpenPoi is modular and uses plug-ins even for basic setup, so the distribution contains some components that you will not use for this first installation.

The main component is `OpenPoi.war`, this is the web application that you need to install in your servlet container. For installation in Tomcat, simply drop the WAR file into your `webapps` directory, it will be unpacked automatically to a folder called `OpenPoi` within the same directory.

# Installing required plug-ins #

OpenPoi tries hard at being flexible and adapt to your needs. One of the aspects of this is that the persistence, where OpenPoi gets the actual POI information from, is completely configurable. This also means that from a clean install, like described above, OpenPoi actually doesn't have peristence backend at all. (See [the plug-ins page](PlugIns.md) for more information.)

One option is to write your own backend, but luckily the distribution comes with two backends that are ready to use. For this quick setup, we are going to use the most basic backend, called the text backend. It stores POIs in a simple text file, that can be edited in a text editor or a spreadsheet like OpenOffice. Of course, this backend is not aimed for high load, it performs terribly if you use it for more than a couple of POIs. Its advantage is that it is straight forward to setup, and easy to use as an example when writing your own backend.

Install the text backend plugin by copying the file `backend-text-1.0.jar` from the distribution package into the webapp's `WEB-INF/lib/` folder. If you are using Tomcat as your container, this means you should drop the JAR file into `webapps/OpenPoi/WEB-INF/lib/`.

# Configuring OpenPoi #

Finally, you will need to configure some details. Configuration is done by tweaking the webapp's `web.xml` file, located under `webapps/OpenPoi/WEB-INF/web.xml`. Open `web.xml` in a text editor of your choice.

Two configuration settings will have to be added: first, we have to tell OpenPoi that we will be using the file and memory based POI manager for our test layer. Secondly, we will have to point out where the file with our POIs is stored. For the second setting, the distribution's `example` folder, contains a file called `pois.txt`, containing an example set of POIs (you might never have guessed). Unpack this file to a location of your choice, if you have not already done so, and note the path to the file.

In `web.xml`, right below the line which contains the tag `<display-name>...</display-name>`, paste the section below:

```
  <context-param>
  	<param-name>layerPoiManager-test</param-name>
  	<param-value>org.openpoi.server.backend.file.MemoryBasedPoiManager</param-value>
  </context-param>

  <context-param>
  	<param-name>layerFile-test</param-name>
  	<param-value>/path/to/pois.txt</param-value>
  </context-param>
```

In the second parameter, replace the `/path/to/pois` to the path where you unpacked the example POIs.

# Starting OpenPoi #

After going through the steps above, restart your servlet container (or ensure that OpenPoi's servlet context is re-created in some other way), since all the settings and plug-ins that you have installed are configured at application startup.

Bring up a web browser and go to `/OpenPoi/` on your server (for example, `http://localhost:8080/OpenPoi/`).

# Continuing from here... #
Read more about how to set up OpenPoi here:
  * [About OpenPoi plug-ins](PlugIns.md)
  * [Client protocol](ClientProtocol.md) - integrating OpenPoi with a client