using System.Web;
using System.Web.Optimization;

namespace ArchosAPI
{
    public class BundleConfig
    {
        // For more information on bundling, visit http://go.microsoft.com/fwlink/?LinkId=301862
        public static void RegisterBundles(BundleCollection bundles)
        {

            bundles.UseCdn = true;
            bundles.Add(new ScriptBundle("~/bundles/jquery").Include(
                           "~/Scripts/jquery-{version}.js"));

            bundles.Add(new ScriptBundle("~/bundles/bootstrap").Include("~/Scripts/bootstrap.js"));

            bundles.Add(new ScriptBundle("~/bundles/modernizr").Include(
               "~/Scripts/modernizr-*"));

            //bundles.Add(new ScriptBundle("~/bundles/charts").Include("~/Scripts/chart.js"));

            bundles.Add(new ScriptBundle("~/bundles/aes").Include("~/Scripts/aes.js"));

            bundles.Add(new ScriptBundle("~/bundles/fileupload").Include("~/Scripts/jquery.ui.widget.js",
                "~/Scripts/jquery.iframe-transport.js",
                "~/Scripts/jquery.fileupload.js"));
            bundles.Add(new StyleBundle("~/Content/fileupload").Include("~/Content/jquery.fileupload.css",
                "~/Content/jquery.fileupload-ui.css"));

            bundles.Add(new ScriptBundle("~/bundles/eCharts").Include("~/Scripts/echarts-all.js"));

            bundles.Add(new StyleBundle("~/Content/css").Include("~/Content/site.css"));


            bundles.Add(new StyleBundle("~/Content/fontawesome").Include("~/Content/font-awesome.css"));

            bundles.Add(new StyleBundle("~/Content/Montserrat").Include("~/Content/fonts/Montserrat-Regular.ttf",
                                                                        "~/Content/fonts/Montserrat-Bold.ttf"));


            bundles.Add(new StyleBundle("~/Content/bootstrap").Include(
                "~/Content/bootstrap.css",
                "~/Content/bootstrap-theme.css"));
        }
    }
}
