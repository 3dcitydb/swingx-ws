/*
 * EndsWith.java
 *
 * Created on September 27, 2006, 10:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.xpath.function;

import java.util.List;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

/**
 *
 * @author richardallenbair
 */
public class Replace extends AbstractFunction {
    
    /** Creates a new instance of EndsWith */
    public Replace() {
        super("replace", 3);
    }

    public Object evaluate(List args) throws XPathFunctionException {
        String s = getStringParam(args.get(0));
        String pattern = getStringParam(args.get(1));
        String replace = getStringParam(args.get(2));
        return s.replaceAll(pattern, replace);
    }
}
