/*
 * $Id: YahooNews.java 9 2006-06-30 21:54:30Z rbair $
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>A simple JavaBean encapsulating access to the Yahoo! News RSS feeds. To use this
 * class, you simply need to set the feedName property and call the <code>readFeed</code>
 * method. <code>readFeed</code> may be a long running operation, so be sure to wrap
 * access to it in some way, such as with SwingWorker or BackgroundWorker.</p>
 *
 * <p>There are many valid feedNames, and the list is ever growing. Some common
 * popular names are listed as public static Strings in this class. See
 * <a href="http://news.yahoo.com/rss">Yahoo!</a> for more information about
 * specific feeds that are available. By default, the TOP_STORIES feed is used. 
 *
 * @author rbair
 */
public class YahooNews extends YahooRSS {
    public static final String TOP_STORIES="topstories";
    public static final String US="us";
    public static final String WORLD="world";
    public static final String BUSINESS="business";
    public static final String BUSINESS_STOCKS="stocks";
    public static final String BUSINESS_ECONOMY="economy";
    public static final String TECH="tech";
    public static final String SPORTS="sports";
    public static final String ENTERTAINMENT="entertainment";
    public static final String MOVIES="movies";
    public static final String MUSIC="music";
    public static final String MOST_EMAILED="mostemailed";
    public static final String MOST_VIEWED="mostviewed";
    public static final String HIGHEST_RATED="highestrated";
    public static final String OPINION_EDITORIAL="oped";

    /**
     * The name of the feed to read
     */
    private String feedName = TOP_STORIES;
    
    /** Creates a new instance of YahooNews */
    public YahooNews() {
    }
    
    /**
     * Sets the name of the feed to read from Yahoo! News. Several common feednames
     * are listed in this class (such as YahooNews.SPORTS).
     *
     * @param feedName name of a yahoo feed. This cannot be null or contain only whitespace
     */
    public void setFeedName(String feedName) {
        //tests both for null (throwing NPE) and only whitespace
        if (feedName.trim().equals("")) {
            throw new IllegalArgumentException("feedName cannot contain only whitespace");
        }
        
        String oldName = getFeedName();
        this.feedName = feedName;
        firePropertyChange("feedName", oldName, getFeedName());
    }
    
    /**
     * @return the name of the Yahoo! News feed
     */
    public String getFeedName() {
        return feedName;
    }

    /**
     * @inheritDoc
     */
    protected String getUrlPrefix() {
        return "http://rss.news.yahoo.com/rss/" + getFeedName();
    }

    /**
     * @inheritDoc
     */
    protected Map getParameters() {
        return new HashMap();
    }    
}
