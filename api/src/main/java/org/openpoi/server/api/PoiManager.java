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

import java.util.Collection;

/**
 * <p>Responsible for handling POI queries within one or more layers.
 * OpenPoi is configured to use a class implementing <code>PoiManager</code>
 * per layer, and PoiManager's will then be instantiated to respond
 * to queries.</p>
 * 
 * <p><code>PoiManager</code> instances are intended to be short-lived,
 * i.e. creation time should be quick, and the implementation should not rely
 * on a state per-instance, since new instances will likely be created for
 * new queries.</p>
 * 
 * @author Per Liedman (per@liedman.net)
 */
public interface PoiManager {

	/**
	 * <p>Returns the POIs that match the query specified by the arguments.</p>
	 * 
	 * <p>The implementation can safely assume that the instance's <code>beginTransaction</code>
	 * method has been called before this method is called.</p>
	 * 
	 * @param layerName the text name of the layer being queried
	 * @param zoomLevel the zoom level being queried
	 * @param within the geometry which the returned geometries should be within
	 * @param categoryIds the IDs of the categories being queried
	 * @return the POI objects that match the query
	 */
    public abstract Collection<?> getPoisWithinGeometry(Query query);

    /**
     * Tells the <code>PoiManager</code> that one or more queries will be made to
     * this instance. This method is always called before a query is made,
     * and a call to this method will be matched by a call to <code>commit</code>
     * or <code>rollback</code>.
     */
	public abstract void beginTransaction();
	
	/**
	 * Informs the <code>PoiManager</code> that the current transaction
	 * completed successfully and should be committed.
	 */
	public abstract void commit();

	/**
	 * Informs the <code>PoiManager</code> that the current transaction
	 * failed and should be rolled back.
	 */
	public abstract void rollback();

}