package org.openpoi.server.api;

import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;

public interface Query {

	public abstract String getLayerName();

	public abstract Geometry getWithin();

	public abstract int getZoomLevel();

	public abstract Set<Integer> getPoiIds();

	public abstract Set<Integer> getCategoryIds();

	public abstract int getSrid();

	public abstract String getText();

}