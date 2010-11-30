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

package org.openpoi.server.backend.hibernatespatial;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernatespatial.GeometryUserType;
import org.openpoi.server.api.Query;
import org.openpoi.server.api.PoiManager;

import com.google.inject.Inject;

public class HibernateSpatialPoiManager implements PoiManager {
    private Session session;
    
    @Inject
    public HibernateSpatialPoiManager() {
        // Do nothing
    }
    
    /* (non-Javadoc)
     * @see net.liedman.poiserver.PoiManager#getPoisWithinGeometry(com.vividsolutions.jts.geom.Geometry, java.util.Collection)
     */
    public Collection<?> getPois(Query query) {
        if (query.getCategoryIds().size() > 0) {
            return session.getNamedQuery("getPoisWithinGeometryAndCategories")
                .setParameter("within", query.getWithin(), GeometryUserType.TYPE)
                .setParameterList("categoryIds", query.getCategoryIds())
                .list();
        } else {
            return session.getNamedQuery("getPoisWithinGeometry")
	            .setParameter("within", query.getWithin(), GeometryUserType.TYPE)
	            .list();
        }
    }

	@Override
	public void beginTransaction() {
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
	}

	@Override
	public void commit() {
		session.getTransaction().commit();
	}

	@Override
	public void rollback() {
		session.getTransaction().rollback();		
	}
}
