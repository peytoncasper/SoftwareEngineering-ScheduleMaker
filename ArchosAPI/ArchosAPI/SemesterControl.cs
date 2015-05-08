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
    public class SemesterControl : System.Windows.Forms.ApplicationContext
    {
        int navigationCounter;
        int timeout = 0;
        private Thread thrd;
        private WebBrowser ieBrowser;
        private ScriptCallback scriptCallback;

        public List<Semester> Semesters { get; set; }

        private string logFile;
        public SemesterControl(string log)
        {
            logFile = log;
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
            Log("Destroying Thread");
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

                Log("Initialized");
                // initialise the navigation counter
                navigationCounter = 0;
                ieBrowser.Navigate(
                                "https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL?&");
                Log("Navigated");
            }
            catch (Exception ex)
            {
                Log(ex.Message);
                if (ex.InnerException != null && ex.InnerException.Message != null)
                    Log(ex.InnerException.Message);
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
                if (ieBrowser.DocumentTitle == "Class Search")
                {
                    var finalHtml = new System.Windows.Forms.Timer();
                    finalHtml.Enabled = true;
                    finalHtml.Interval = 100;
                    finalHtml.Tick += htmlTimerTick;
                    finalHtml.Start();

                }

                else if (ieBrowser.DocumentTitle.ToLower() == "an error has occurred.".ToLower())
                {
                    ieBrowser.Navigate("https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL?&");
                    Log("Error - Navigating");
                }
            }
            catch (Exception ex)
            {
                Log(ex.Message);
                if (ex.InnerException != null && ex.InnerException.Message != null)
                    Log(ex.InnerException.Message);
                thrd.Abort();
                this.Dispose();
            }
        }

        /// <summary> 
        /// class to hold the functions called
        /// by script codes in the WebBrowser control
        /// </summary> 
        [System.Runtime.InteropServices.ComVisible(true)]
        public class ScriptCallback
        {
            SemesterControl owner;

            public ScriptCallback(SemesterControl owner)
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
            try
            {
                timeout += 100;

                if (timeout >= 10000)
                {
                    var timer = (System.Windows.Forms.Timer)sender;
                    timer.Stop();
                    timer.Dispose();
                    Log("Timeout");
                    ieBrowser.Dispose();
                    thrd.Abort();
                    this.Dispose();
                }
                var html = ((dynamic)ieBrowser.Document.DomDocument).documentElement.outerHTML;
                if (ieBrowser.DocumentTitle == "Look Up Term")
                {
                    var i = 1;
                    HtmlElement node = ieBrowser.Document.GetElementById("SEARCH_RESULT1");
                    if (node != null)
                    {
                        var documentBody = ieBrowser.Document.Body;
                        var body = documentBody.ToString();
                        Semesters.Add(new Semester()
                        {
                            SemesterNumber = node.InnerText
                        });
                        while ((node = ieBrowser.Document.GetElementById("RESULT0$" + i)) != null)
                        {
                            Semesters.Add(new Semester()
                            {
                                SemesterNumber = node.InnerText
                            });

                        }
                    }
                    else
                    {
                        ieBrowser.Document.Body.InvokeMember("pAction_win0(document.win0,'CLASS_SRCH_WRK2_STRM$273$$prompt')");
                    }
                }
                else if (ieBrowser.Document.GetElementById("SSR_CLSRCH_WRK_SUBJECT$0") != null)
                {
                    Log("Navigating to search results");
                    //ieBrowser.Document.GetElementById("CLASS_SRCH_WRK2_STRM$273$$prompt").InvokeMember("pAction_win0(document.win0,'CLASS_SRCH_WRK2_STRM$273$$prompt')");
                    //ieBrowser.Navigate("javascript:pAction_win0(document.win0,'CLASS_SRCH_WRK2_STRM$273$$prompt');");
                    ieBrowser.Navigate("javascript:pAction_win0(document.win0,'CLASS_SRCH_WRK2_STRM$273$$prompt');");
                    var finalHtml = (System.Windows.Forms.Timer)sender;
                    finalHtml.Stop();
                    finalHtml.Interval = 1000;

                    finalHtml.Start();
                }


            }
            catch (Exception ex)
            {
                Log(ex.Message);
                if (ex.InnerException != null && ex.InnerException.Message != null)
                    Log(ex.InnerException.Message);
                thrd.Abort();
                this.Dispose();
            }
        }
        public void Log(string s)
        {
            using (StreamWriter writer = new StreamWriter(logFile, true))
            {
                writer.WriteLine(s);
            }
        }
    }

}

