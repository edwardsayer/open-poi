# Introduction #

OpenPoi comes with Javascript classes to ease integration between [OpenLayers](http://openlayers.org) and OpenPoi.

These Javascript classes extend some OpenLayers classes, and are deployed together with the OpenPoi server. If you need to modify the classes, or include them in your own project (for example to bundle and minify them), they are licensed under the [X11 License](http://en.wikipedia.org/wiki/X11_License), which is more flexible than the LGPL license that the Java parts of OpenPoi uses.

Currently, the integration consists of two classes:
  * [OpenPoiProtocol](http://code.google.com/p/open-poi/source/browse/server/src/main/webapp/javascript/OpenPoiProtocol.js), which is an OpenLayers [Protocol](http://dev.openlayers.org/releases/OpenLayers-2.10/doc/apidocs/files/OpenLayers/Protocol-js.html) implementation, which is needed to make the correct HTTP requests to the server
  * [OpenPoiFormat](http://code.google.com/p/open-poi/source/browse/server/src/main/webapp/javascript/OpenPoiFormat.js), which is an OpenLayers [Format](http://dev.openlayers.org/releases/OpenLayers-2.10/doc/apidocs/files/OpenLayers/Format-js.html) implementation, which is needed to parse the JSON responses from the server


# OpenPoiProtocol #

## Properties ##

  * **layer**: the [Layer](http://dev.openlayers.org/releases/OpenLayers-2.10/doc/apidocs/files/OpenLayers/Layer-js.html) which the protocol is associated with

## Remarks ##

The `layer` property must be set for the protocol to work. This should probably be resolved in some other way in the future.

# OpenPoiFormat #

## Remarks ##

The class parses the response as JSON, and builds an array of [Vector](http://dev.openlayers.org/releases/OpenLayers-2.10/doc/apidocs/files/OpenLayers/Feature/Vector-js.html) features, with a [Point](http://dev.openlayers.org/releases/OpenLayers-2.10/doc/apidocs/files/OpenLayers/Geometry/Point-js.html) as the feature's geometry.

`OpenPoiFormat` assumes that the each POIs geographic location is supplied in a JSON object called `location`, which has the attributes `X` and `Y`, this is used to build the coordinates of the features' geometries. An example:

```
"location":{"X":11.94,"Y":57.74}
```

# Example #

Creating a new vector layer which fetches POIs from the layer "example" on the same server as the current page:

```

var poiLayer = new OpenLayers.Layer.Vector("POI", {
                   projection: new OpenLayers.Projection("EPSG:4326"),
                   strategies: [
                       new OpenLayers.Strategy.BBOX()
                   ],
                   protocol: new OpenLayers.Protocol.HTTP.OpenPoi({
                       url: "/OpenPoi/Pois/example",
                       format: new OpenLayers.Format.JSON.OpenPoi(),
                   })
               });

poiLayer.protocol.layer = poiLayer;

```

Note the last line, initializing the `OpenPoiProtocol`'s `layer` property, which of course must be done after the layer itself has been created.