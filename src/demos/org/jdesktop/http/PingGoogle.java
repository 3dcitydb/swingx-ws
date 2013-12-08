/*
 * $Id: PingGoogle.java 215 2007-02-27 17:45:39Z rbair $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jdesktop.http;

/**
 * Simple demo that hits a public website (google.com) and reports success or
 * failure.
 * 
 * @author rbair
 */
public class PingGoogle {
    public static void main(String... args) throws Exception {
        Session s = new Session();
        Response r = s.get("http://www.google.com");
        if (r.getStatusCode() == StatusCode.OK) {
            System.out.println("Ping Succeeded");
        } else {
            System.out.println(r.getStatusCode() + ": " + r.getStatusText());
        }
    }
}
