/* Copyright 2006 VPAC
 * 
 * This file is part of Grisu.
 * Grix is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.

 * Grix is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Grix; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.vpac.grisu.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateHelpers {
	
	public static String getUniqueName(String jobname, String dn){
		
		String name = jobname;
		
        SessionFactory sf = HibernateSessionFactory.getSessionFactory();
        
        Session session = sf.getCurrentSession();

        Query q = session.createQuery("select job.jobname from org.vpac.grisu.js.model.job.Job as job where job.dn=? and job.jobname like ?");
        q.setString(0, dn);
        q.setString(1, jobname+"%");

        List jobs = q.list();
        int i = 0;
        boolean taken = false;
        do {
        	taken = false;
        	if ( i != 0 ) name = jobname+"_"+(jobs.size()+i);
        	for ( Object job : jobs ){
        		if ( ((String)job).equals(name) ) {
        			taken = true;
        			break;
        		}
        	}
        	i++;
        } while ( taken );
		return name;
	}

}
