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

package org.openpoi.server.backend.file;

import java.util.ArrayList;
import java.util.Collection;

import org.openpoi.server.api.PoiManager;
import org.openpoi.server.domain.simple.Poi;

import com.google.inject.Inject;
import com.vividsolutions.jts.geom.Geometry;

/**
 * A <code>PoiManager</code> that get its POIs from a
 * <code>MemoryBasedPoiManager</code>.
 * @author per
 */
public class MemoryBasedPoiManager implements PoiManager {
	private final PoiMemoryDatabase database;
	
	@Inject
	public MemoryBasedPoiManager(PoiMemoryDatabase database) {
		this.database = database;
	}
	
	@Override
	public void beginTransaction() {
		// Do nothing
	}

	@Override
	public void commit() {
		// Do nothing
	}

	@Override
	public void rollback() {
		// Do nothing
	}

	@Override
	public Collection<?> getPoisWithinGeometry(String layerName, int zoomLevel,
			Geometry within, Collection<Integer> categoryIds) {
		Collection<Poi> result = new ArrayList<Poi>();
		for (Poi poi : database.getPois(layerName)) {
			if (poi.getLocation().within(within)) {
				result.add(poi);
			}
		}
		
		return result;
	}

}
