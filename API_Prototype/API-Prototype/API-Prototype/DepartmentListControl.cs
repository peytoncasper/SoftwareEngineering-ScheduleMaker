using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Web;
using System.Web.Script.Serialization;
using System.Windows.Forms;
using HtmlAgilityPack;
using Test;
using HtmlDocument = HtmlAgilityPack.HtmlDocument;
using System.IO;
namespace ArchosAPI
{
    public class DepartmentListControl : System.Windows.Forms.ApplicationContext
    {
        int navigationCounter;
        int timeout = 0;
        private Thread thrd;
        private WebBrowser ieBrowser;
        private ScriptCallback scriptCallback;
        public List<HtmlNode> deptNodes;
        public List<string> deptList { get; set; }
        public DepartmentListControl()
        {
            deptNodes = new List<HtmlNode>();
            thrd = new Thread(new ThreadStart(
                delegate
                {
                    Init();
                    System.Windows.Forms.Application.Run(this);
                }));
            thrd.DisableComObjectEagerCleanup();
            // set thread to STA state before starting
            thrd.SetApartmentState(ApartmentState.STA);
            thrd.Start();
            thrd.Join();
        }
        private void Init()
        {
            try
            {
                scriptCallback = new ScriptCallback(this);

                // create a WebBrowser control
                ieBrowser = new WebBrowser();
                // set the location of script callback functions
                ieBrowser.ObjectForScripting = scriptCallback;
                // set WebBrowser event handle
                ieBrowser.DocumentCompleted +=
                  new WebBrowserDocumentCompletedEventHandler(
                  IEBrowser_DocumentCompleted);
                ieBrowser.Navigating += new
                  WebBrowserNavigatingEventHandler(IEBrowser_Navigating);

                // initialise the navigation counter
                navigationCounter = 0;
                ieBrowser.Navigate(
                                "http://catalog.uta.edu/coursedescriptions/");
            }
            catch (Exception ex)
            {
                thrd.Abort();
                this.Dispose();
            }
        }

        // Navigating event handle
        void IEBrowser_Navigating(object sender,
             WebBrowserNavigatingEventArgs e)
        {
            // navigation count increases by one
            navigationCounter++;
        }

        // DocumentCompleted event handle
        void IEBrowser_DocumentCompleted(object sender,
             WebBrowserDocumentCompletedEventArgs e)
        {
            try
            {

                if (ieBrowser.DocumentTitle == "Course Descriptions < University of Texas Arlington")
                {

                    var html = ((dynamic)ieBrowser.Document.DomDocument).documentElement.outerHTML;

                    var doc = new HtmlDocument();
                    doc.LoadHtml(html);

                    FindNode(doc.GetElementbyId("content"), "class", "sitemaplink");

                    deptList = deptNodes.Select(x => x.InnerText.Substring(x.InnerText.IndexOf('(') + 1, x.InnerText.IndexOf(')') - x.InnerText.IndexOf('(') -1 )).ToList();
                    thrd.Abort();
                }
            }
            catch (Exception ex)
            {
                thrd.Abort();
                this.Dispose();
            }
        }
        public void FindNode(HtmlNode node, string name, string value)
        {
            try
            {
                if (node.Attributes.Any(x => x.Name != null && x.Name.ToLower().Contains(name) && x.Value.ToLower().Contains(value)))
                {
                    deptNodes.Add(node);
                    for (int i = 0; i < node.ChildNodes.Count; i++)
                        FindNode(node.ChildNodes.ElementAt(i), name, value);
                }
                else
                    for (int i = 0; i < node.ChildNodes.Count; i++)
                        FindNode(node.ChildNodes.ElementAt(i), name, value);
                return;
            }
            catch(Exception ex)
            {
                return;
            }

        }
        /// <summary> 
        /// class to hold the functions called
        /// by script codes in the WebBrowser control
        /// </summary> 
        [System.Runtime.InteropServices.ComVisible(true)]
        public class ScriptCallback
        {
            DepartmentListControl owner;

            public ScriptCallback(DepartmentListControl owner)
            {
                this.owner = owner;

            }

            // callback function to get the content
            // of page in the WebBrowser control
            public void getHtmlResult(int count)
            {
                // unequal means the content is not stable
                if (owner.navigationCounter != count) return;

            }
        }
        private void htmlTimerTick(object sender, EventArgs e)
        {
            
        }
    }

}

