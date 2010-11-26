<!--
 * Copyright (c) 2010 Per Liedman
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 -->
<%@page import="org.openpoi.server.web.OpenPoiWebApp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>OSM - Dynamic POI update</title>
    <style type="text/css">
#map {
        width: 100%;
        height: 100%;
        border: 0px;
        padding: 0px;
        position: absolute;
     }
body {
        border: 0px;
        margin: 0px;
        padding: 0px;
        height: 100%;
     }
    </style>
    <script src="http://openlayers.org/api/OpenLayers.js"></script>
    <script src="javascript/OpenPoiProtocol.js"></script>
    <script src="javascript/OpenPoiFormat.js"></script>
    <script type="text/javascript"> 

        var map;

		function createPoiLayer(layerName) {
		    var colors = ["black", "blue", "green", "red"];
			 
		    var style = new OpenLayers.Style({
	                pointRadius: "${radius}",
	                fillColor: "red",
	                fillOpacity: 0.8,
	                strokeColor: "#ff5555",
	                strokeWidth: 2,
	                strokeOpacity: 0.8
	            }, {
	                context: {
	                    radius: function(feature) {
				return Math.min(feature.attributes.count, 7) + 3;
	                    },
	                }
	            });

		    var pois = new OpenLayers.Layer.Vector("POI", {
				projection: new OpenLayers.Projection("EPSG:4326"),
				strategies: [
					new OpenLayers.Strategy.BBOX(),
					new OpenLayers.Strategy.Cluster()
				],
				protocol: new OpenLayers.Protocol.HTTP.OpenPoi({
                    url: "Pois/" + layerName,
					format: new OpenLayers.Format.JSON.OpenPoi(),
				}),
				styleMap: new OpenLayers.StyleMap({
		                        "default": style,
		                        "select": {b
		                            fillColor: "#8aeeef",
		                            strokeColor: "#32a8a9"
		                        }
		                })
		    });
		    pois.protocol.layer = pois;

		    return pois;
		}
		
        function init(){
            map = new OpenLayers.Map('map',
                    { maxExtent: new OpenLayers.Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34),
                      numZoomLevels: 19,
                      maxResolution: 156543.0399,
                      units: 'm',
                      projection: new OpenLayers.Projection("EPSG:900913"),
                      displayProjection: new OpenLayers.Projection("EPSG:4326")
                    });
 
         	var layerOSM = new OpenLayers.Layer.OSM();
            
			var poiLayers = [];
<%for (String layer : OpenPoiWebApp.getLayerNames()) {%>
				poiLayers.push(createPoiLayer("<%= layer %>"));
<%			} %>
	 
		    map.addLayer(layerOSM);
		    map.addLayers(poiLayers);
	 
	    	/*selectControl = new OpenLayers.Control.SelectFeature(pois,
	                {onSelect: onFeatureSelect, onUnselect: onFeatureUnselect}); 
   		    map.addControl(selectControl);
   		    selectControl.activate();
   		    */
	 
	        var centre = new OpenLayers.LonLat(11.94, 57.744);
	        var zoom = 11; 
	
	 
		    map.setCenter(centre.transform(map.displayProjection,map.projection), zoom);
        }
        
        function onPopupClose(evt) {
            selectControl.unselect(selectedFeature);
        }
 
        function onFeatureSelect(feature) {
            selectedFeature = feature;
	    text = '';
	    for (var i in feature.cluster){
		var feat = feature.cluster[i];
		text += '<h3>'+feat.attributes.name + "</a></h3><div>" + feat.attributes.name + "</div><br />";
	    }
            popup = new OpenLayers.Popup("chicken", 
                                     feature.geometry.getBounds().getCenterLonLat(),
                                     null,
                                     text,
                                     true, onPopupClose);
            feature.popup = popup;
	    popup.setOpacity(0.7);
            map.addPopup(popup);
        }
 
        function onFeatureUnselect(feature) {
            map.removePopup(feature.popup);
            feature.popup.destroy();
            feature.popup = null;
        }
 
        // -->
    </script>
  </head>
  <body onload="init()">
    <div id="map"></div>
  </body>
</html>

