package org.openpoi.server.api;

import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;

public interface Query {

	/**
	 * The name of the layer being queried, which is very important in the 
	 * case where more than one layer use the same PoiManager class.
	 */
	public abstract String getLayerName();

	/**
	 * The geometry which the POIs should be within. This is normally a 
	 * bounding box, although the interface allows arbitrary geometries.
	 */
	public abstract Geometry getWithin();

	/**
	 * An implementation may choose to only show a subset of the POIs 
	 * for a certain zoom level, so the zoom level being queried is supplied.
	 */
	public abstract int getZoomLevel();

	/**
	 * Only POIs identified by the integer IDs in this collection
	 * should be returned to the client. An implementation may choose to
	 * ignore this, if it does not use identifiers for its POIs.
	 */
	public abstract Set<Integer> getPoiIds();

	/**
	 * POIs within a layer can be divided into categories, and each query 
	 * specifies which categories are being queried. The interface does not 
	 * make any assumptions about how the categories are organized, except 
	 * that each category must be possible to identify by a unique 
	 * identifier. A PoiManager implementation is free to ignore the 
	 * categories if it does not support them. 
	 */
	public abstract Set<Integer> getCategoryIds();

	/**
	 * Allows an arbitrary text query to be specified. It is up to the
	 * implementation to filter the resulting POIs according to this text,
	 * if specified.
	 */
	public abstract String getText();

}