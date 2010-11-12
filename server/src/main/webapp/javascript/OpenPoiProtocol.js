/*
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
 */

OpenLayers.Protocol.HTTP.OpenPoi = OpenLayers.Class(OpenLayers.Protocol.HTTP, {
	layer: null,
	projectionPattern: /[0-9]+/i,
	
	read: function(options) {
		options = options || {};
		options.params = options.params || {};
		options.params["z"] = this.layer.map.getZoom();
		options.params["srid"] = this.layer.projection.getCode().match(this.projectionPattern);

		return OpenLayers.Protocol.HTTP.prototype.read.apply(this, [options]);
	},
	
    CLASS_NAME: "OpenLayers.Protocol.HTTP.OpenPoi"
});
