/*
 * Copyright (C) 2010 Per Liedman (per@liedman.net)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Lesser GNU General Public License for more details.
 * You should have received a copy of the Lesser GNU General Public License
 * along with this program. If not, see < http://www.gnu.org/licenses/>.
 *
 */

package org.openpoi.server.api;

import java.util.Set;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

/**
 * <p>OpenPoi does not enforce any specific organization of POIs, but queries
 * supply the following parameters, which an implementation is free to interpret
 * or even ignore:</p>
 * <ul>
 * 	<li>
 * 		<b>Layer name</b> - the name of the layer being queried, which is very
 *		important in the case where more than one layer use the same
 *		<code>PoiManager</code> class.
 *	</li>
 * 	<li>
 * 		<b>Zoom level</b> - an implementation may choose to only show a subset of
 * 		the POIs for a certain zoom level, so the zoom level being queried is
 * 		supplied.
 *	</li>
 * 	<li>
 * 		<b>Geometry</b> - the geometry which the POIs should be within. This is
 * 		normally a bounding box, although the interface allows arbitrary geometries.
 *	</li>
 * 	<li>
 * 		<b>Categories</b> - POIs within a layer can be divided into categories, and
 * 		each query specifies which categories are being queried. The interface does
 * 		not make any assumptions about how the categories are organized, except that
 * 		each category must be possible to identify by a unique identifier. A
 * 		<code>PoiManager</code> implementation is free to ignore the categories if
 * 		it does not support them.
 *	</li>
 * </ul>

 * @author Per Liedman (per@liedman.net)
 *
 */
public final class Query {
	private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
	
	private String layerName;
	private Geometry within;
	private double xmin;
	private double ymin;
	private double xmax;
	private double ymax;
	private int zoomLevel;
	private Set<Integer> poiIds = new TreeSet<Integer>();
	private Set<Integer> categoryIds = new TreeSet<Integer>();
	private int srid;
	private String text;
	
	public String getLayerName() {
		return layerName;
	}
	
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}
	
	public Geometry getWithin() {
		return within;
	}
	
	public void setBoundingBox(double x1, double y1, double x2, double y2) {
		xmin = Math.min(x1, x2);
		ymin = Math.min(y1, y2);
		xmax = Math.max(x1, x2);
		ymax = Math.max(y1, y2);
		
        CoordinateArraySequence corners = new CoordinateArraySequence(new Coordinate[] {
                new Coordinate(x1, y1),
                new Coordinate(x2, y1),
                new Coordinate(x2, y2),
                new Coordinate(x1, y2),
                new Coordinate(x1, y1)
            });
        this.within = new Polygon(new LinearRing(corners, GEOMETRY_FACTORY), null, GEOMETRY_FACTORY);
	}
	
	public int getZoomLevel() {
		return zoomLevel;
	}
	
	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
	}
	
	public Set<Integer> getPoiIds() {
		return poiIds;
	}
	
	public void addPoiId(int poiId) {
		poiIds.add(poiId);
	}
	
	public Set<Integer> getCategoryIds() {
		return categoryIds;
	}
	
	public void addCategoryId(int categoryId) {
		categoryIds.add(categoryId);
	}
	
	public int getSrid() {
		return srid;
	}
	
	public void setSrid(int srid) {
		this.srid = srid;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(layerName)
			.append('/')
			.append(xmin)
			.append(",")
			.append(ymin)
			.append(",")
			.append(xmax)
			.append(",")
			.append(ymax)
			.append('/')
			.append(zoomLevel)
			.append('/');
			
		addCommaSeparated(b, poiIds);
		b.append('/');
		addCommaSeparated(b, categoryIds);
		
		b.append('/')
			.append(srid)
			.append('/')
			.append(text);
		
		return b.toString();
	}
	
	private void addCommaSeparated(StringBuilder b, Iterable<?> i) {
		boolean isFirst = true;
		for (Object o : i) {
			if (!isFirst) {
				b.append(',');
			} else {
				isFirst = false;
			}
			
			b.append(o);
		}
	}
}
