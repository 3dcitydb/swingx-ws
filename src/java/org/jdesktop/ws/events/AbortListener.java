/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.ws.events;

import java.util.EventListener;

/**
 *
 * @author Richard
 */
public interface AbortListener extends EventListener {
    public void aborted(AbortEvent event);
}
