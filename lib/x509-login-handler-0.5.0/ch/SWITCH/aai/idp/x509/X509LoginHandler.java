// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   X509LoginHandler.java

package ch.SWITCH.aai.idp.x509;

import edu.internet2.middleware.shibboleth.idp.authn.provider.AbstractLoginHandler;
import java.io.IOException;
import javax.servlet.http.*;
import org.opensaml.util.URLBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class X509LoginHandler extends AbstractLoginHandler
{

    public X509LoginHandler(String loginPageURL, String authenticationServletURL, String cookieDomain)
    {
        setSupportsPassive(false);
        setSupportsForceAuthentication(false);
        loginPageURL = loginPageURL;
        authenticationServletURL = authenticationServletURL;
        cookieDomain = cookieDomain;
    }

    public void login(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            String redirectURL = null;
            if(isPassThrough(request.getCookies()))
            {
                log.debug("Cookie '{}' is set: continue with clientauthn protected servlet.", "_idp_login_X509_pass-through");
                redirectURL = getRedirectURL(request, authenticationServletURL);
            } else
            {
                log.debug("Cookie '{}' is not set: continue with x509 login page.", "_idp_login_X509_pass-through");
                redirectURL = getRedirectURL(request, loginPageURL);
            }
            if(redirectURL != null)
            {
                log.debug("Redirect to {}", redirectURL);
                response.sendRedirect(redirectURL);
            } else
            {
                log.error("Could not set redirect URL, please check the configuration.");
            }
        }
        catch(IOException ex)
        {
            log.error("Unable to redirect to login page or authentication servlet.", ex);
        }
    }

    private String getRedirectURL(HttpServletRequest request, String url)
    {
        URLBuilder urlBuilder = null;
        if(url.startsWith("http"))
        {
            urlBuilder = new URLBuilder(url);
        } else
        {
            log.debug("No URL configured in loginPageURL: {}", url);
            StringBuilder pathBuilder = new StringBuilder();
            urlBuilder = new URLBuilder();
            urlBuilder.setScheme(request.getScheme());
            urlBuilder.setHost(request.getServerName());
            if(!request.getScheme().equals("http") || request.getScheme().equals("https"))
                urlBuilder.setPort(request.getServerPort());
            pathBuilder.append(request.getContextPath());
            if(!loginPageURL.startsWith("/"))
                pathBuilder.append("/");
            pathBuilder.append(url);
            urlBuilder.setPath(pathBuilder.toString());
        }
        return urlBuilder.buildURL();
    }

    private boolean isPassThrough(Cookie cookies[])
    {
        if(cookies == null)
            return false;
        log.trace("{} Cookie(s) are sent", Integer.valueOf(cookies.length));
        for(int i = 0; i < cookies.length; i++)
        {
            log.trace("Cookie name is {}", cookies[i].getName());
            if(cookies[i].getName().equals("_idp_login_X509_pass-through"))
                return true;
        }

        return false;
    }

    public static Cookie createCookie(String path)
    {
        Cookie cookie = new Cookie("_idp_login_X509_pass-through", "1");
        cookie.setMaxAge(0x1e13380);
        cookie.setPath(path);
        cookie.setSecure(true);
        if(cookieDomain != null && cookieDomain != "")
            cookie.setDomain(cookieDomain);
        return cookie;
    }

    private final Logger log = LoggerFactory.getLogger("ch/SWITCH/aai/idp/x509/X509LoginHandler");
    private static final String COOKIE_NAME = "_idp_login_X509_pass-through";
    private static String loginPageURL;
    private static String authenticationServletURL;
    private static String cookieDomain;
}
