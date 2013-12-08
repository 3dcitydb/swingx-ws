/*
 * $Id: YahooCompanyFinanceNews.java 13 2006-07-12 00:10:26Z rbair $
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>A simple JavaBean that queries Yahoo! for news items related to one or more
 * companies, identified by stock ticker symbol. The type of news item returned may
 * either be based on the company itself, or the industry the company is in, depending
 * on the <code>type</code> property.</p>
 *
 * <p>This bean differs from the YahooFinanceNews bean in that the news items returned
 * are based on one or more companies ticker symbols, as opposed to a more customized
 * RSS feed such as "international".</p>
 *
 * @author rbair
 */
public class YahooCompanyFinanceNews extends YahooRSS {
    /**
     * The type of news items to return. May be one of either:
     * <ul>
     *  <li>COMPANY: indicating news items that only relate to the companies associated
     *      with specific ticker symbols</li>
     *  <li>INDUSTRY: indicating news items that relate to the industries which
     *      specific companies are in (again, based on ticker symbols)</li>
     * </ul>
     */
    public static enum Type {COMPANY, INDUSTRY};
    
    /**
     * The set of symbols to perform a search for
     */
    private Set<String> symbols = new HashSet<String>();
    /**
     * The type of search to perform
     */
    private Type type = Type.COMPANY;
    
    /** Creates a new instance of YahooFinance */
    public YahooCompanyFinanceNews() {
    }

    /**
     * Adds the specified symbol to the set of symbols for which to retrieve news
     * articles. This may not be null.
     */
    public void addSymbol(String symbol) {
        symbols.add(symbol.toUpperCase());
    }
    
    /**
     * Removes the specified symbol from the set of symbols for which to retrieve news
     * articles. This may not be null.
     */
    public void removeSymbol(String symbol) {
        symbols.remove(symbol.toUpperCase());
    }
    
    /**
     * @return the set of symbols for which to retieve news articles
     */
    public String[] getSymbols() {
        return symbols.toArray(new String[0]);
    }
    
    /**
     * Specifies the set of ticker symbols representing companies for which to
     * retrieve news articles. For example, SUNW.
     */
    public void setSymbols(String... symbols) {
        this.symbols.clear();
        this.symbols.addAll(Arrays.asList(symbols));
    }
    
    /**
     * The type of news items to return. May be one of either:
     * <ul>
     *  <li>COMPANY: indicating news items that only relate to the companies associated
     *      with specific ticker symbols</li>
     *  <li>INDUSTRY: indicating news items that relate to the industries which
     *      specific companies are in (again, based on ticker symbols)</li>
     * </ul>
     */
    public void setType(Type type) {
        if (type == null) {
            type = Type.COMPANY;
        }
        
        Type old = getType();
        this.type = type;
        firePropertyChange("type", old, getType());
    }
    
    /**
     * @return the Type of search that will be performed (see setType).
     */
    public Type getType() {
        return type;
    }
    
    @Override
    protected String getUrlPrefix() {
        return "http://finance.yahoo.com/rss/" + (getType() == Type.COMPANY ? "headline" : "industry");
    }

    @Override
    protected Map getParameters() {
        HashMap map = new HashMap();
        StringBuffer s = new StringBuffer();
        for (String symbol : getSymbols()) {
            if (s.length() > 0) {
                s.append(",");
            }
            s.append(symbol);
        }
        map.put("s", s);
        return map;
    }
}
