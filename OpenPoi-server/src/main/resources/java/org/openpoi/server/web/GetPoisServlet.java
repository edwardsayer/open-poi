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

package org.openpoi.server.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openpoi.server.MissingLayerException;
import org.openpoi.server.PoiManagerFactory;
import org.openpoi.server.api.Cache;
import org.openpoi.server.api.PoiManager;
import org.openpoi.server.api.PoiSerializer;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

/**
 * Servlet implementation class GetPoisServlet
 */
@Singleton
public class GetPoisServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
	
	private final PoiSerializer poiSerializer;;
	private final Cache cache;
	private final PoiManagerFactory poiManagerFactory;
	
	@Inject
	public GetPoisServlet(PoiSerializer poiSerializer, Cache cache, PoiManagerFactory poiManagerFactory) {
		this.poiSerializer = poiSerializer;
        this.cache = cache;
		this.poiManagerFactory = poiManagerFactory;
	    
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String layer;
		String path = request.getRequestURI();
		int lastSlash = path.lastIndexOf('/');
		if (lastSlash >= 0) {
			layer = path.substring(lastSlash + 1);
		} else {
			layer = path;
		}
		
	    String bboxStr = request.getParameter("bbox");
        String sridStr = request.getParameter("srid");
	    String categoriesStr = request.getParameter("cat");
	    String zoomLevelStr = request.getParameter("z");
	    String jsonp = request.getParameter("jsonp");
	    
	    String cacheKey = layer + "/" + bboxStr + "/" + sridStr + "/" 
	    	+ zoomLevelStr + "/" + categoriesStr;
	    
	    String result = cache.get(cacheKey);
	    
	    if (result == null) {
            Polygon bbox = parseBoundingBox(bboxStr, sridStr);
            Collection<Integer> categoryIds = parseCategoryIds(categoriesStr);
            int zoomLevel = parseZoomLevel(zoomLevelStr);
    	    
            try {
	            PoiManager poiManager = poiManagerFactory.create(layer);
	    	    poiManager.beginTransaction();
	            
	    	    try {
	        	    Collection<?> pois = poiManager.getPoisWithinGeometry(layer, zoomLevel, bbox, categoryIds);
	        	            		
	        	    result = poiSerializer.serialize(pois);
	        	    cache.put(cacheKey, result);
	        	    
	    	    	poiManager.commit();
	    	    } catch (RuntimeException e) {
	    	        poiManager.rollback();
	    	        throw e;
	    	    }
            } catch (MissingLayerException mle) {
            	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No layer called \"" 
            			+ layer + "\" has been defined.");
            	return;
            }
	    }

	    if (jsonp != null) {
	    	result = jsonp + "(" + result + ")";
	    }
	    
        response.setContentType(poiSerializer.getContentType());
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
		writer.write(result);
		writer.close();
	}

	private int parseZoomLevel(String zoomLevelStr) throws ServletException {
		if (zoomLevelStr == null) {
			throw new ServletException("Missing zoom level parameter (\"z\").");
		}
		
		try {
			return Integer.parseInt(zoomLevelStr);
		} catch (NumberFormatException e) {
			throw new ServletException("Zoom level parameter is not an integer (\"z\").");
		}
	}

	private Collection<Integer> parseCategoryIds(String parameter) throws ServletException {
	    if (parameter == null) {
	        return new ArrayList<Integer>(0);
	    }
	    
	    String[] parts = parameter.split(",");
	    Collection<Integer> result = new ArrayList<Integer>(parts.length);
        for (int i = 0; i < parts.length; i++) {
            result.add(Integer.parseInt(parts[i]));
        }
        
        return result;
    }

    private Polygon parseBoundingBox(String parameter, String sridStr) throws ServletException {
        if (parameter == null) {
            throw new ServletException("Missing bounding box parameter (\"bbox\").");
        }
        
        String[] parts = parameter.split(",");
        if (parts.length != 4) {
            throw new ServletException("Bounding box must contain exactly four values: x1, y1, x2, y2.");
        }

        CoordinateSequence corners;
        try {
            double x1 = Double.parseDouble(parts[0]);
            double y1 = Double.parseDouble(parts[1]);
            double x2 = Double.parseDouble(parts[2]);
            double y2 = Double.parseDouble(parts[3]);
            corners = new CoordinateArraySequence(new Coordinate[] {
                new Coordinate(x1, y1),
                new Coordinate(x2, y1),
                new Coordinate(x2, y2),
                new Coordinate(x1, y2),
                new Coordinate(x1, y1)
            });
        } catch (NumberFormatException e) {
            throw new ServletException("Bounding box (\"bbox\") could not be " +
                    "parsed, since one or more coordinate is not a valid double.", e);
        }
     
        Polygon polygon = new Polygon(new LinearRing(corners, GEOMETRY_FACTORY), null, GEOMETRY_FACTORY);
        if (sridStr != null) {
            try {
                polygon.setSRID(Integer.parseInt(sridStr));
            } catch (NumberFormatException e) {
                throw new ServletException("SRID is not an integer (\"srid\").", e);
            }
        }
        
        return polygon;
    }

    /**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	
}