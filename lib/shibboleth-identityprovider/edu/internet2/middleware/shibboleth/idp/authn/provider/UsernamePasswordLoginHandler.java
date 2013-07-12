// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UsernamePasswordLoginHandler.java

package edu.internet2.middleware.shibboleth.idp.authn.provider;

import edu.internet2.middleware.shibboleth.idp.util.HttpServletHelper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.opensaml.util.URLBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Referenced classes of package edu.internet2.middleware.shibboleth.idp.authn.provider:
//            AbstractLoginHandler

public class UsernamePasswordLoginHandler extends AbstractLoginHandler
{

    public UsernamePasswordLoginHandler(String servletPath)
    {
        setSupportsPassive(false);
        setSupportsForceAuthentication(true);
        authenticationServletPath = servletPath;
    }

    public void login(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
        try
        {
            String authnServletUrl = HttpServletHelper.getContextRelativeUrl(httpRequest, authenticationServletPath).buildURL();
            log.debug("Redirecting to {}", authnServletUrl);
            httpResponse.sendRedirect(authnServletUrl);
            return;
        }
        catch(IOException ex)
        {
            log.error("Unable to redirect to authentication servlet.", ex);
        }
    }

    private final Logger log = LoggerFactory.getLogger(edu/internet2/middleware/shibboleth/idp/authn/provider/UsernamePasswordLoginHandler);
    private String authenticationServletPath;
}
