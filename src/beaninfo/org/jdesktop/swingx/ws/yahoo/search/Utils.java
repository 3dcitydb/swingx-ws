/*
 * Utils.java
 *
 * Created on July 14, 2006, 3:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.ws.yahoo.search;

import org.jdesktop.swingx.EnumerationValue;

/**
 *
 * @author rbair
 */
public final class Utils {
    
    /** Creates a new instance of Utils */
    private Utils() {}
    
    private static String convert(String upperCase) {
        //converts to normal text things like AFGHANISTAN and UNITED_KINGDOM
        StringBuffer s = new StringBuffer(upperCase.toLowerCase().replaceAll("_", " ").trim());
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            if (i == 0) {
                s.replace(i, i+1, "" + Character.toUpperCase(c));
            } else if (c == ' ') {
                i++;
                s.replace(i, i+1, "" + Character.toUpperCase(c));
            }
        }
        return s.toString();
    }
    
    public static EnumerationValue[] getCountryEnumValues() {
        Country[] values = Country.values();
        EnumerationValue[] results = new EnumerationValue[values.length + 1];
        results[0] = new EnumerationValue("<default>", Country.UNITED_STATES, "null");
        for (int i=0; i<values.length; i++) {
            results[i+1] = new EnumerationValue(convert(values[i].name()), values[i], "org.jdesktop.swingx.ws.yahoo.search.Country." + values[i].name());
        }
        return results;
    }
    
    public static EnumerationValue[] getFormatEnumValues() {
        Format[] values = Format.values();
        EnumerationValue[] results = new EnumerationValue[values.length+1];
        results[0] = new EnumerationValue("<default>", Format.ANY, "null");
        for (int i=0; i<values.length; i++) {
            results[i+1] = new EnumerationValue(convert(values[i].name()), values[i], "org.jdesktop.swingx.ws.yahoo.search.Format." + values[i].name());
        }
        return results;
    }
    
    public static EnumerationValue[] getLanguageEnumValues() {
        Language[] values = Language.values();
        EnumerationValue[] results = new EnumerationValue[values.length+1];
        results[0] = new EnumerationValue("<default>", Language.ENGLISH, "null");
        for (int i=0; i<values.length; i++) {
            results[i+1] = new EnumerationValue(convert(values[i].name()), values[i], "org.jdesktop.swingx.ws.yahoo.search.Language." + values[i].name());
        }
        return results;
    }
    
    public static EnumerationValue[] getLicenseEnumValues() {
        License[] values = License.values();
        EnumerationValue[] results = new EnumerationValue[values.length+1];
        results[0] = new EnumerationValue("<default>", License.ANY, "null");
        for (int i=0; i<values.length; i++) {
            results[i+1] = new EnumerationValue(convert(values[i].name()), values[i], "org.jdesktop.swingx.ws.yahoo.search.License." + values[i].name());
        }
        return results;
    }
    
    public static EnumerationValue[] getRegionEnumValues() {
        Region[] values = Region.values();
        EnumerationValue[] results = new EnumerationValue[values.length+1];
        results[0] = new EnumerationValue("<default>", Region.UNITED_STATES, "null");
        for (int i=0; i<values.length; i++) {
            results[i+1] = new EnumerationValue(convert(values[i].name()), values[i], "org.jdesktop.swingx.ws.yahoo.search.Region." + values[i].name());
        }
        return results;
    }
    
    public static EnumerationValue[] getSubscriptionEnumValues() {
        Subscription[] values = Subscription.values();
        EnumerationValue[] results = new EnumerationValue[values.length+1];
        results[0] = new EnumerationValue("<default>", "", "null");
        for (int i=0; i<values.length; i++) {
            results[i+1] = new EnumerationValue(convert(values[i].name()), values[i], "org.jdesktop.swingx.ws.yahoo.search.Subscription." + values[i].name());
        }
        return results;
    }
    
    public static EnumerationValue[] getTypeEnumValues() {
        Type[] values = Type.values();
        EnumerationValue[] results = new EnumerationValue[values.length+1];
        results[0] = new EnumerationValue("<default>", Type.ANY, "null");
        for (int i=0; i<values.length; i++) {
            results[i+1] = new EnumerationValue(convert(values[i].name()), values[i], "org.jdesktop.swingx.ws.yahoo.search.Type." + values[i].name());
        }
        return results;
    }
}
