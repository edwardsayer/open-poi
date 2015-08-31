# OpenPoi Client Protocol #

OpenPoi has a quite simple protocol for querying for POIs. This page describes both how to query the server manually by URL. For information on how to integrate this with [OpenLayers](http://openlayers.org) using the Javascript classes packaged with OpenPoi, see the [OpenLayers integration page](OpenLayersIntegration.md).


# Querying for POIs using a URL #

POIs are configured as layers, each layer with its own name. Querying for POIs within a layer is done by the following URL pattern:

` http://www.yourdomain.com/OpenPoi/Pois/<layername>?bbox=<bounding box>&z=<zoomlevel>&cats=<categories>&srid=<SRID> `

A description of the parameters and their format follows:
  * **layername** - simply the name of the layer, as text
  * **boundingbox** - the bounding box to return POIs within. Bounding box is specified as xmin, ymin, xmax, ymax; coordinates separated by comma (","). For example: "12.5,57.5,13.5,58.5". The spatial reference system used is specified by the **SRID** parameter, see below
  * **zoomLevel** - an integer describing the zoomlevel (as defined by the client) used for the query
  * **categories** - a comma separated list of integer ids for the categories to return POIs from
  * **SRID** - an integer for the spatial reference system used in the bounding box. Most commonly, this refers to the EPSG code for the coordinate system, for example 4326 for WGS84 coordinates


# Response format #

By default, POI queries return a [JSON](http://json.org) array as result. The array consists of one JSON object for each POI in the response. The default serializer serializes any bean properties of the POI domain object, so normally you do not need to do tweaks here. An example response looks like this:

```
[{"id":1,"name":"Kamomillvägen 3","details":"","categories":[],"location":{"X":11.94,"Y":57.74}}]
```



# JSONP support #

In some cases, you may need [JSONP](http://en.wikipedia.org/wiki/JSON#JSONP) when querying. OpenPoi supports this through the optional parameter `jsonp`. Submitting this parameter will cause the response to be wrapped in a Javascript function call. For example, adding `jsonp=poiResponse` will cause the response to look like this:

```
poiResponse([{"id":1,"name":"Kamomillvägen 3","details":"","categories":[],"location":{"X":11.94,"Y":57.74}}])
```