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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openpoi.server.domain.simple.Poi;

import com.google.inject.Inject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class PoiMemoryDatabase {
	private Map<String, Collection<Poi>> layerPois = new HashMap<String, Collection<Poi>>();
	
	@Inject
	public PoiMemoryDatabase(LayerFileMapping layerFileMappings) throws IOException {
		GeometryFactory geometryFactory = new GeometryFactory();
		
		for (String layer : layerFileMappings.keySet()) {
			String poiFilePath = layerFileMappings.get(layer);
			Collection<Poi> pois = new ArrayList<Poi>();
			
			FileReader r = new FileReader(poiFilePath);
			try {
				BufferedReader br = new BufferedReader(r);
				try {
					String line;
					int lineCount = 1;
					while ((line = br.readLine()) != null) {
						line = line.trim();
						if (line.length() > 0 && !line.startsWith("#")) {
							String[] cols = line.split(";");
							if (cols.length == 5) {
								Poi poi = new Poi();
								poi.setId(Long.parseLong(cols[0]));
								poi.setName(cols[1]);
								poi.setDetails(cols[2]);
								Coordinate coordinate = 
									new Coordinate(
											Double.parseDouble(cols[3]), 
											Double.parseDouble(cols[4]));
								poi.setLocation(geometryFactory.createPoint(coordinate));
								pois.add(poi);
							} else {
								throw new IOException("Malformed input on line " + lineCount 
										+ " of \"" + poiFilePath 
										+ "\". Five columns (id, name, description, x, y), "
										+ "separated by \";\" must be present. " + cols.length + " columns where found.");
							}
						}
						
						lineCount++;
					}
				} finally {
					br.close();
				}
			} finally {
				r.close();
			}
			
			layerPois.put(layer, pois);
		}
	}
	
	public Iterable<Poi> getPois(String layer) {
		return layerPois.get(layer);
	}
	
}
