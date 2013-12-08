/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.ws;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import org.jdesktop.http.Request;
import org.jdesktop.http.Response;
import org.jdesktop.http.Session;
import javax.swing.SwingWorker;
import static org.jdesktop.http.State.*;

/**
 * An HTTP based REST web service.
 * 
 * @author Richard
 */
public class HttpService extends AbstractHttpService {
    /**
     * The request to use when sending. By default this is a normal Request.
     * If you want to use HTML Form parameters, then use a FormRequest by calling
     * setRequest.
     */
    private Request request = new Request();
    /**
     * The response from the web server. This is reset to null whenever an
     * abort occurs, or at the beginning of a new request/response cycle. Also,
     * when a failure occurs it is set to null. Note that if the request/response
     * cycle succeeds, but the actual call failed (due to semantics or something
     * on the server) "done" will be called and this will have a value.
     */
    private Response response;
    /**
     * The security level to use. If not set, medium level security is used.
     */
    private Session.SecurityLevel securityLevel;
    
    /**
     * Gets the Request used by this service. This will never return null.
     * @return
     */
    public final Request getRequest() { return request; }
    
    /**
     * Sets the Request to use with this service. If null, then a new
     * Request will be created, ensuring that it is never null. This will fire
     * a property change event.
     * 
     * @param r
     */
    public void setRequest(Request r) {
        Request old = request;
        request = r == null ? new Request() : r;
        firePropertyChange("request", old, request);
    }
    
    /**
     * Gets the response, if there is one. This may return null if the state
     * is anything other than DONE. It will not ever be null if the state
     * is DONE.
     * 
     * @return
     */
    public final Response getResponse() { return response; }
    
    /**
     * Sets the Response. This simply sets the value and fires a property
     * change event.
     * 
     * @param r
     */
    private void setResponse(Response r) {
        Response old = response;
        firePropertyChange("response", old, response = r);
    }
    
    /**
     * Gets the security level used with this service. There are three levels of
     * security. The first is "high", which is the default in Java (though not
     * in this class). High security requires that the web server have a certificate
     * signed by a trusted certificate root, such as Verisign. The second
     * level of security is "medium", and is the default for this class. Medium
     * level security means that if a certificate is from a trusted root, then
     * it is trusted. Otherwise, the user is prompted to accept the certificate.
     * This is essentially the mode supported by web browsers. The third
     * level of security is "low" in which case the user will never be prompted.
     * Obviously this is only good for testing (if that) and should never be used
     * when deployed. Note that changing the security level requires priviledges,
     * and thus your app needs to be signed.
     * 
     * @return
     */
    public final Session.SecurityLevel getSecurityLevel() { return securityLevel; }
    
    /**
     * Sets the security level. This cannot be null.
     */
    public void setSecurityLevel(Session.SecurityLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("Security level cannot be null");
        }
        
        Session.SecurityLevel old = securityLevel;
        firePropertyChange("securityLevel", old, securityLevel = level);
    }

    @Override protected void doSend() {
        // the call to super throws an exception if something goes wrong,
        // so following this call I know that I can assume the send is
        // going to begin
        super.doSend();
        setResponse(null);
    }
    
    protected SwingWorker createWorker() {
        return new HttpWorker(this);
    }
    
    /**
     * Instances of this class actually perform the request/response cycle
     * in the background. When a task is aborted, "cancel" is called on the
     * worker and then the worker reference becomes null. The worker might
     * still actually be working in the background, but for all intents and
     * purposes is considered to have been garbage collected.
     * 
     * Therefore, it is important that in this class the various methods
     * (aborted, failed, etc) on BaseService are not called if this is not the
     * current worker.
     */
    private static final class HttpWorker extends SwingWorker<Response,Response> {
        private HttpService svc;
        
        /**
         * To create an HttpWorker I need a reference to the HttpService that
         * created it.
         * 
         * @param svc
         */
        HttpWorker(HttpService svc) {
            this.svc = svc;
        }
        
        /**
         * @inheritDoc
         */
        @Override protected Response doInBackground() throws Exception {
            // nothing to do here besides invoke the request and return the
            // response. All state information (progress, etc) is handled
            // already by the AbstractHttpService
            return svc.getSession().execute(svc.request);
        }

        /**
         * @inheritDoc
         */
        @Override protected void done() {
            if (!isCancelled()) {
                // I haven't been canceled, or at least not canceled fast
                // enough, so go ahead and process the results.
                try {
                    // this next line throws an exception if one occured in
                    // doInBackground
                    Response r = get();
                    svc.setResponse(r);
                    svc.done();
                } catch (CancellationException cancelled) {
                    // it was aborted, nothing to do since the
                    // abort method sets the state
                } catch (ExecutionException e) {
                    //something went awry
                    svc.fail(e.getCause());
                } catch (Exception e) {
                    //something went awry
                    svc.fail(e);
                }
            }
        }
    }
}