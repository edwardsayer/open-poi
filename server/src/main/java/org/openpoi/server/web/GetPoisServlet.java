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
import org.openpoi.server.api.Cache;
import org.openpoi.server.api.PoiManager;
import org.openpoi.server.api.PoiSerializer;
import org.openpoi.server.api.Query;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * Servlet implementation class GetPoisServlet
 */
@Singleton
public class GetPoisServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
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
		Query query = parseRequest(request);
		String cacheKey = query.toString();
	    String result = cache.get(cacheKey);
	    
	    if (result == null) {
            try {
	            PoiManager poiManager = poiManagerFactory.create(query.getLayerName());
	    	    poiManager.beginTransaction();
	            
	    	    try {
	        	    Collection<?> pois = poiManager.getPoisWithinGeometry(query);
	        	            		
	        	    result = poiSerializer.serialize(pois);
	        	    cache.put(cacheKey, result);
	        	    
	    	    	poiManager.commit();
	    	    } catch (RuntimeException e) {
	    	        poiManager.rollback();
	    	        throw e;
	    	    }
            } catch (MissingLayerException mle) {
            	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No layer called \"" 
            			+ query.getLayerName() + "\" has been defined.");
            	return;
            }
	    }

		String jsonp = request.getParameter("jsonp");
	    if (jsonp != null) {
	    	result = jsonp + "(" + result + ")";
	    }
	    
        response.setContentType(poiSerializer.getContentType());
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
		writer.write(result);
		writer.close();
	}

	private Query parseRequest(HttpServletRequest request) throws ServletException {
		Query query = new Query();
		
		String path = request.getRequestURI();
		int lastSlash = path.lastIndexOf('/');
		if (lastSlash >= 0) {
			query.setLayerName(path.substring(lastSlash + 1));
		} else {
			query.setLayerName(path);
		}

	    String bboxStr = request.getParameter("bbox");
        String sridStr = request.getParameter("srid");
	    String categoriesStr = request.getParameter("cat");
	    String poisStr = request.getParameter("poi");
	    String zoomLevelStr = request.getParameter("z");
	    
        Coordinate[] corners = parseBoundingBox(bboxStr, sridStr);
        query.setBoundingBox(corners[0].x, corners[0].y, corners[1].x, corners[1].y);
        
        for (int catId : parseCommaSeparatedString(categoriesStr)) {
        	query.addCategoryId(catId);
        }
        for (int poiId : parseCommaSeparatedString(poisStr)) {
        	query.addPoiId(poiId);
        }
        query.setZoomLevel(parseZoomLevel(zoomLevelStr));
        if (sridStr != null) {
            try {
                query.setSrid(Integer.parseInt(sridStr));
            } catch (NumberFormatException e) {
                throw new ServletException("SRID is not an integer (\"srid\").", e);
            }
        }
        query.setText(request.getParameter("q"));
        
		return null;
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

	private Collection<Integer> parseCommaSeparatedString(String parameter) throws ServletException {
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

    private Coordinate[] parseBoundingBox(String parameter, String sridStr) throws ServletException {
        if (parameter == null) {
            throw new ServletException("Missing bounding box parameter (\"bbox\").");
        }
        
        String[] parts = parameter.split(",");
        if (parts.length != 4) {
            throw new ServletException("Bounding box must contain exactly four values: x1, y1, x2, y2.");
        }

        try {
            double x1 = Double.parseDouble(parts[0]);
            double y1 = Double.parseDouble(parts[1]);
            double x2 = Double.parseDouble(parts[2]);
            double y2 = Double.parseDouble(parts[3]);
            return new Coordinate[] {
                new Coordinate(x1, y1),
                new Coordinate(x2, y2),
            };
        } catch (NumberFormatException e) {
            throw new ServletException("Bounding box (\"bbox\") could not be " +
                    "parsed, since one or more coordinate is not a valid double.", e);
        }
    }

    /**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	
}
