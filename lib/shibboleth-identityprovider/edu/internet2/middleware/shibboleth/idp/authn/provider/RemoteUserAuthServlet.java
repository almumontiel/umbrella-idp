// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RemoteUserAuthServlet.java

package edu.internet2.middleware.shibboleth.idp.authn.provider;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import edu.internet2.middleware.shibboleth.idp.authn.AuthenticationEngine;
import edu.internet2.middleware.shibboleth.idp.authn.UsernamePrincipal;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.opensaml.xml.util.DatatypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteUserAuthServlet extends HttpServlet
{

	 private java.util.Map<String, String> lookupTable = new HashMap<String, String>();

    public RemoteUserAuthServlet()
    {
    }

    public void init(ServletConfig config)
        throws ServletException
    {
    lookupTable
          .put("amontiel","luis"); //tomatito is my identifier on Umbrella live and Luis is the id on my local Umbrella

        super.init(config);
        String method = DatatypeHelper.safeTrimOrNullString(config.getInitParameter("authnMethod"));
        if(method != null)
            authenticationMethod = method;
        else
            authenticationMethod = "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport";
    }

    protected void service(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
        throws ServletException, IOException
    {
    	// String username = lookupTable.get(httpRequest.getRemoteUser());

        String principalName = DatatypeHelper.safeTrimOrNullString(httpRequest.getRemoteUser());
         if(principalName != null){
	 principalName= lookupTable.get(principalName);}
	if(principalName != null)
        {
            log.debug("Remote user identified as {} returning control back to authentication engine", principalName);
            httpRequest.setAttribute("principal", new UsernamePrincipal(principalName));
            httpRequest.setAttribute("authnMethod", authenticationMethod);
        } else
        {
            log.debug("No remote user information was present in the request");
        }
        AuthenticationEngine.returnToAuthenticationEngine(httpRequest, httpResponse);
    }

    private static final long serialVersionUID = 0xaa99c97dabbef962L;
    private final Logger log = LoggerFactory.getLogger("edu/internet2/middleware/shibboleth/idp/authn/provider/RemoteUserAuthServlet");
    private String authenticationMethod;
}
