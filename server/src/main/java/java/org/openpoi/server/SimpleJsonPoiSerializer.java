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

package org.openpoi.server;

import java.lang.reflect.Type;
import java.util.Collection;

import org.openpoi.server.api.PoiSerializer;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.vividsolutions.jts.geom.Point;

public class SimpleJsonPoiSerializer implements PoiSerializer {
    private Gson gson;
    
    public SimpleJsonPoiSerializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Point.class, new PointSerializer());
        
        gson = gsonBuilder.create();
    }
    
    @Override
    public String getContentType() {
    	return "text/javascript";
    }
    
    @Override
    public String serialize(Collection<?> pois) {
        return gson.toJson(pois);
    }

    private static class PointSerializer implements JsonSerializer<Point> {
        @Override
        public JsonElement serialize(Point src, Type typeOfSrc,
                JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("X", src.getX());
            jsonObject.addProperty("Y", src.getY());
            return jsonObject;
        }
        
    }
}
