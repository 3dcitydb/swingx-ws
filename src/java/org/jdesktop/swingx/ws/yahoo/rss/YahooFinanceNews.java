/*
 * $Id: YahooFinanceNews.java 12 2006-07-11 23:58:33Z rbair $
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
 * <p>A simple JavaBean encapsulating access to the Yahoo! Finance News RSS feeds. To use this
 * class, you simply need to set the feedName property and call the <code>readFeed</code>
 * method. <code>readFeed</code> may be a long running operation, so be sure to wrap
 * access to it in some way, such as with SwingWorker or BackgroundWorker.</p>
 *
 * <p>There are many valid feedNames, and the list is ever growing. Some common
 * popular names are listed as public static Strings in this class. See
 * <a href="http://finance.yahoo.com/rssindex">Yahoo!</a> for more information about
 * specific feeds that are available. By default, the MOST_POPULAR feed is used. 
 *
 * <p>This class is very similar in style an approach to the YahooNews class, it 
 * simply maintains a different namespace and URL structure.</p>
 *
 * @author rbair
 */
public class YahooFinanceNews extends YahooRSS {
    public static final String MOST_POPULAR="mostpopular";
    public static final String US_MARKETS="usmarkets";
    public static final String INTERNATIONAL_FINANCE="international";
    public static final String BONDS="bonds";
    public static final String COMMODITIES="commodities";
    public static final String CURRENCIES="currencies";
    public static final String FUNDS="funds";
    public static final String BANKING="banking";
    public static final String MORTGAGES="mortgages";
    public static final String RETIREMENT="retirement";
    public static final String TAXES="taxes";

    /**
     * The name of the feed to read
     */
    private String feedName = MOST_POPULAR;
    
    /** Creates a new instance of YahooNews */
    public YahooFinanceNews() {
    }
    
    /**
     * Sets the name of the feed to read from Yahoo! Finance News. Several common feednames
     * are listed in this class (such as YahooNews.FUNDS).
     *
     * @param feedName name of a Yahoo! finance feed. This cannot be null or contain only whitespace
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
        return "http://finance.yahoo.com/rss/" + getFeedName();
    }

    /**
     * @inheritDoc
     */
    protected Map getParameters() {
        return new HashMap();
    }    
}
