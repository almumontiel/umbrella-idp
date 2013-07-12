// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UsernamePasswordLoginServlet.java

package edu.internet2.middleware.shibboleth.idp.authn.provider;

import edu.internet2.middleware.shibboleth.idp.authn.*;
import java.io.IOException;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.opensaml.xml.util.DatatypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Referenced classes of package edu.internet2.middleware.shibboleth.idp.authn.provider:
//            UsernamePasswordCredential

public class UsernamePasswordLoginServlet extends HttpServlet
{
    protected class SimpleCallbackHandler
        implements CallbackHandler
    {

        public void handle(Callback callbacks[])
            throws UnsupportedCallbackException
        {
            if(callbacks == null || callbacks.length == 0)
                return;
            Callback arr$[] = callbacks;
            int len$ = arr$.length;
            for(int i$ = 0; i$ < len$; i$++)
            {
                Callback cb = arr$[i$];
                if(cb instanceof NameCallback)
                {
                    NameCallback ncb = (NameCallback)cb;
                    ncb.setName(uname);
                    continue;
                }
                if(cb instanceof PasswordCallback)
                {
                    PasswordCallback pcb = (PasswordCallback)cb;
                    pcb.setPassword(pass.toCharArray());
                }
            }

        }

        private String uname;
        private String pass;
        final UsernamePasswordLoginServlet this$0;

        public SimpleCallbackHandler(String username, String password)
        {
            this$0 = UsernamePasswordLoginServlet.this;
            super();
            uname = username;
            pass = password;
        }
    }


    public UsernamePasswordLoginServlet()
    {
        jaasConfigName = "ShibUserPassAuth";
        loginPage = "login.jsp";
    }

    public void init(ServletConfig config)
        throws ServletException
    {
        super.init(config);
        if(getInitParameter("jaasConfigName") != null)
            jaasConfigName = getInitParameter("jaasConfigName");
        if(getInitParameter("loginPage") != null)
            loginPage = getInitParameter("loginPage");
        if(!loginPage.startsWith("/"))
            loginPage = (new StringBuilder()).append("/").append(loginPage).toString();
        String method = DatatypeHelper.safeTrimOrNullString(config.getInitParameter("authnMethod"));
        if(method != null)
            authenticationMethod = method;
        else
            authenticationMethod = "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport";
    }

    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String username = request.getParameter("j_username");
        String password = request.getParameter("j_password");
        if(username == null || password == null)
        {
            redirectToLoginPage(request, response);
            return;
        }
        try
        {
            authenticateUser(request, username, password);
            AuthenticationEngine.returnToAuthenticationEngine(request, response);
        }
        catch(LoginException e)
        {
            request.setAttribute("loginFailed", "true");
            request.setAttribute("authnException", new AuthenticationException(e));
            redirectToLoginPage(request, response);
        }
    }

    protected void redirectToLoginPage(HttpServletRequest request, HttpServletResponse response)
    {
        StringBuilder actionUrlBuilder = new StringBuilder();
        if(!"".equals(request.getContextPath()))
            actionUrlBuilder.append(request.getContextPath());
        actionUrlBuilder.append(request.getServletPath());
        request.setAttribute("actionUrl", actionUrlBuilder.toString());
        try
        {
            request.getRequestDispatcher(loginPage).forward(request, response);
            log.debug("Redirecting to login page {}", loginPage);
        }
        catch(IOException ex)
        {
            log.error("Unable to redirect to login page.", ex);
        }
        catch(ServletException ex)
        {
            log.error("Unable to redirect to login page.", ex);
        }
    }

    protected void authenticateUser(HttpServletRequest request, String username, String password)
        throws LoginException
    {
        try
        {
            log.debug("Attempting to authenticate user {}", username);
            SimpleCallbackHandler cbh = new SimpleCallbackHandler(username, password);
            LoginContext jaasLoginCtx = new LoginContext(jaasConfigName, cbh);
            jaasLoginCtx.login();
            log.debug("Successfully authenticated user {}", username);
            Subject loginSubject = jaasLoginCtx.getSubject();
            Set principals = loginSubject.getPrincipals();
            principals.add(new UsernamePrincipal(username));
            Set publicCredentials = loginSubject.getPublicCredentials();
            Set privateCredentials = loginSubject.getPrivateCredentials();
            privateCredentials.add(new UsernamePasswordCredential(username, password));
            Subject userSubject = new Subject(false, principals, publicCredentials, privateCredentials);
            request.setAttribute("subject", userSubject);
            request.setAttribute("authnMethod", authenticationMethod);
        }
        catch(LoginException e)
        {
            log.debug((new StringBuilder()).append("User authentication for ").append(username).append(" failed").toString(), e);
            throw e;
        }
        catch(Throwable e)
        {
            log.debug((new StringBuilder()).append("User authentication for ").append(username).append(" failed").toString(), e);
            throw new LoginException("unknown authentication error");
        }
    }

    private static final long serialVersionUID = 0xf80d01944e498282L;
    private final Logger log = LoggerFactory.getLogger(edu/internet2/middleware/shibboleth/idp/authn/provider/UsernamePasswordLoginServlet);
    private String authenticationMethod;
    private String jaasConfigName;
    private final String jaasInitParam = "jaasConfigName";
    private String loginPage;
    private final String loginPageInitParam = "loginPage";
    private final String failureParam = "loginFailed";
    private final String usernameAttribute = "j_username";
    private final String passwordAttribute = "j_password";
}
