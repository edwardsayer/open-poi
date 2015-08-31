_**Note: OpenPoi is a new project, and this website is in the process of being filled with useful documentation. Please note that its currently work in progress.**_

# Introduction #
OpenPoi aims to help you putting points of inerests (POIs) on your maps in an easy, flexible fashion, while still providing high performance.

OpenPoi supports both the dead-simple scenario - you have POIs in a database table layout specified by OpenPoi, up to as advanced as you need: the database (or whatever persistence method you like) is fully [plug-in driven](PlugIns.md), and you can quickly write support for your own storage backend.

OpenPoi has a simple API towards clients, which by default serves responses formatted as [JSON](http://json.org). It also supports [JSONP](http://en.wikipedia.org/wiki/JSON#JSONP). For other formats, you can also write your own protocol implementation, since it is also plug-in driven.

OpenPoi caches query results aggressively by default, in a simple memory cache. Of course, this can also be changed to suite your needs by the same plug-in architecture.

Finally, OpenPoi also includes Javascript classes for integrating the server side map clients in [OpenLayers](http://openlayers.org), but [the protocol](ClientProtocol.md) is so simple that it is easy to implement for other clients as well.

Go on to [basic setup](BasicSetup.md) for instructions on installing OpenPoi for the first time.

# Licensing #
OpenPoi is, like the name implies, Open Source. It's mostly (see below for exceptions) distributed under the [GNU Lesser Public License](http://www.gnu.org/licenses/lgpl-2.1.html) (sometimes referred to as "GNU Library Public License" or LGPL). In short, this means you are free to both use and link to OpenPoi (which you if you use its plug-in functionality), without having to distribute your own code as open source. Changes made to OpenPoi itself, must however be open source, if you distribute them to a third party.

Some minor parts, like the Javascript classes for communicating with the server, are distributed under the [X11 license](http://en.wikipedia.org/wiki/MIT_License), to allow modifications without having to re-distribute the source - you will only need to preserve the copyright notice.

# Comparison to other techniques #
The main driving force behind developing OpenPoi was the apparent lack of simple software to solve the basic and mostly tedious task of getting a set of POIs from your database to a map client with a minimal amount of fuzz, and still in a way that can survive a high load.

Some software is available, notably [OpenStreetMap's Dynamic POI example](http://wiki.openstreetmap.org/wiki/OpenLayers_Dynamic_POI), which OpenPoi lends some ideas from. They suffer from being examples rather than complete solutions, and are in many ways hardcoded. They are also written in PHP.

On the other end, OpenPoi is in some ways comparable to [Web Feature Service](http://en.wikipedia.org/wiki/Web_Feature_Service) (WFS), and the easiest way to getting a POI database on your maps, before OpenPoi, would probably be a combination of a spatial database like [PostGIS](http://postgis.refractions.net/), WFS through [GeoServer](http://geoserver.org/display/GEOS/Welcome) and OpenLayers. Compared to WFS, OpenPoi is at the same time more basic, but also offers some flexibility:
  * Where WFS supports any type of spatial data, like polygons, linestrings and what not, OpenPoi is for _points_ of interest. Point geometries only, that is.
  * WFS supports arbitrarily complex queries, filters and so on. OpenPoi supports bounding box queries, and can filter POIs by categories; more than one set of POIs can be handled though, by having several POI layers, just like GeoServer can have more than one WFS layer.
  * The WFS query and response format is verbose, due to its formal nature. OpenPoi is pragmatic and prioritizes brievity, its JSON response is considerably more efficient in terms of bandwidth.
  * Standard WFS solutions, such as GeoServer, lacks caching mechanisms, resulting in a database hit for every WFS query being made. Combined with the very dynamic nature of WFS queries, this is a performance issue for high traffic sites. Caching support exists in [GeoWebCache](http://geowebcache.sourceforge.net/), but is experimental and might be removed in the future. OpenPoi is specifically designed with caching in mind.
  * Standard WFS support in GeoServer only supports flat database table (or view) design. Joins and more complex data designs are not supported unless [Complex Features](http://docs.geoserver.org/2.0.0/user/data/app-schema/complex-features.html) are used, and they are... complex. OpenPoi supports an easy plug-in architecture that easily adapts to your database needs, but also has a default implementation for basic needs.

To summarize, WFS is a truely flexible and generic protocol for working with spatial queries, but attempts to solve a much broader category of problems that POIs. Hence, it does not offer the same simplicity and performance that a solution aimed specifically at POIs can. That's where OpenPoi comes in.