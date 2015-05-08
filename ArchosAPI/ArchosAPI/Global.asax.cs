using Microsoft.WindowsAzure.Storage;
using Microsoft.WindowsAzure.Storage.Blob;
using Microsoft.WindowsAzure.Storage.Queue;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Http;
using System.Web.Mvc;
using System.Web.Optimization;
using System.Web.Routing;

namespace ArchosAPI
{
    public class WebApiApplication : System.Web.HttpApplication
    {

        protected void Application_Start()
        {
            AreaRegistration.RegisterAllAreas();
            GlobalConfiguration.Configure(WebApiConfig.Register);
            FilterConfig.RegisterGlobalFilters(GlobalFilters.Filters);
            RouteConfig.RegisterRoutes(RouteTable.Routes);
            BundleConfig.RegisterBundles(BundleTable.Bundles);
            MvcHandler.DisableMvcResponseHeader = true;
        }
        protected void Application_PreSendRequestHeaders(object sender, EventArgs e)
        {
            var app = sender as HttpApplication;
            if (app == null || !app.Request.IsLocal || app.Context == null)
                return;
            var headers = app.Context.Response.Headers;
            headers.Remove("Server");
            headers.Remove("Cache-Control");
            headers.Remove("Content-Type");
        }
    }
}
