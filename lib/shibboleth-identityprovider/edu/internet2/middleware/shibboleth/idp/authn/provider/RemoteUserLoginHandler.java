// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RemoteUserLoginHandler.java

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

public class RemoteUserLoginHandler extends AbstractLoginHandler
{

    public RemoteUserLoginHandler()
    {
    }

    public void setServletURL(String url)
    {
        servletURL = url;
    }

    public String getServletURL()
    {
        return servletURL;
    }

    public void login(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
        try
        {
            String profileUrl = HttpServletHelper.getContextRelativeUrl(httpRequest, servletURL).buildURL();
            log.debug("Redirecting to {}", profileUrl);
            httpResponse.sendRedirect(profileUrl);
            return;
        }
        catch(IOException ex)
        {
            log.error("Unable to redirect to remote user authentication servlet.", ex);
        }
    }

    private final Logger log = LoggerFactory.getLogger(edu/internet2/middleware/shibboleth/idp/authn/provider/RemoteUserLoginHandler);
    private String servletURL;
}
