/*
 * $Id: YahooRSS.java 76 2006-09-18 20:20:47Z rbair $
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

package org.jdesktop.swingx.ws.yahoo.rss;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import org.jdesktop.beans.AbstractBean;

/**
 * Base class from which YahooRSS feeds extend. Implements common functionality,
 * such as constructing the URL and actually reading the RSS feeds.
 *
 * @author rbair
 */
public abstract class YahooRSS extends AbstractBean {
    
    /** Creates a new instance of YahooRSS */
    public YahooRSS() {
    }
    
    /**
     * This method is called while YahooRSS constructs the URL. This prefix
     * MUST NOT contain the trailing "?" or any parameters. YahooRSS will query
     * for those separately and do the work of combining them into a final URL.
     * Hence, the url prefix is everything up to but not including the params
     *
     * @return a String representing the first part of the URL (including the http://)
     */
    protected abstract String getUrlPrefix();
    
    /**
     * @return a Map of parameters used in constructing the URL. The toString() method
     * will be called on each key and value in order to construct the param list. They will
     * be URL encoded by YahooRSS, so you need not worry about url encoding the param ahead of time
     */
    protected abstract Map getParameters();

    /**
     * Constructs the URL to return
     */
    protected final URL constructUrl() throws MalformedURLException {
        StringBuilder buffer = new StringBuilder(getUrlPrefix());
        buffer.append("?");
        Map params = getParameters();
        for (Object key : params.keySet()) {
            buffer.append(URLEncoder.encode(key.toString()));
            buffer.append("=");
            buffer.append(URLEncoder.encode(params.get(key).toString()));
            buffer.append("&");
        }
        if (buffer.charAt(buffer.length() - 1) == '&') {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        return new URL(buffer.toString());
    }
    
    /**
     * Reads a feed and returns the ROME SyndFeed object that was created by
     * reading the feed. Note that this method blocks, and should be called
     * on a background thread.
     *
     * @return the feed
     * @throws RSSException if something goes wrong while accessing the RSS feed
     */
    public SyndFeed readFeed() throws RSSException {
        try {
            URL feedUrl = constructUrl();
            SyndFeedInput input = new SyndFeedInput();
            return input.build(new XmlReader(feedUrl));
        } catch (Exception e) {
            throw new RSSException(e);
        }
    }
}
