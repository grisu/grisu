/* Copyright 2007, 2008 ARCS
 *
 * This file is part of Grisu.
 * Grisu is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.

 * Grisu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Grisu; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.vpac.grisu.hibernate;

import org.hibernate.Session;


/**
 * Data access object (DAO) for domain model
 * @author MyEclipse - Hibernate Tools
 */
public class BaseHibernateDAO implements IBaseHibernateDAO {
	
//	private static Session session = null;
	
	public Session getSession(String dn) {
//		if ( session == null ) 
//			session = HibernateSessionFactory.getSession();
//		return session;
		return HibernateSessionFactory.getSession(dn);
	}
	
	
}