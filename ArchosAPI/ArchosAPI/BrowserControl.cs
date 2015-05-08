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
using ArchosAPI.Models;
using System.Diagnostics;
namespace ArchosAPI
{
    public class BrowserControl : System.Windows.Forms.ApplicationContext
    {
        int navigationCounter;
        int timeout = 0;
        private Thread thrd;
        private WebBrowser ieBrowser;
        private ScriptCallback scriptCallback;
        public List<UTASearchResult> classResults { get; set; }

        private int processedCourses = 0;

        private int semesterId = 0;
        private int departmentId = 0;


        private string logFile;
        public BrowserControl(List<UTASearchResult> cResults, string log)
        {
            logFile = log;
            Log("Browser Begin");
            classResults = cResults;
            Log("Creating Thread");

            Init();
        }
        private void ProcessNextSearch()
        {
            if (processedCourses >= classResults.Count)
            {
                Application.Exit();
            }

            NavigateToSearchPage();
        }
        private void Init()
        {
            try
            {
                thrd = new Thread(new ThreadStart(delegate
                {
                    AttachHandlers();
                    ProcessNextSearch();

                    System.Windows.Forms.Application.Run(this);

                }));
                thrd.DisableComObjectEagerCleanup();
                thrd.SetApartmentState(ApartmentState.STA);
                thrd.Start();
                thrd.Join();
            }
            catch (Exception ex)
            {
                Log(ex.Message);
                if (ex.InnerException != null && ex.InnerException.Message != null)
                    Log(ex.InnerException.Message);

                Application.Exit();
            }
        }
        private void AttachHandlers()
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
        }
        private void NavigateToSearchPage()
        {
            navigationCounter = 0;
            ieBrowser.Navigate("https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL?&");
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
            //try
            //{

                if (ieBrowser.DocumentTitle == "Class Search")
                {
                    Log("Setting Search Values");
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
            //}
            //catch (Exception ex)
            //{
            //    Log(ex.Message);
            //    if (ex.InnerException != null && ex.InnerException.Message != null)
            //        Log(ex.InnerException.Message);
            //    Debug.WriteLine("Error");
            //    processedCourses++;
            //    ProcessNextSearch();
            //}
        }

        /// <summary> 
        /// class to hold the functions called
        /// by script codes in the WebBrowser control
        /// </summary> 
        [System.Runtime.InteropServices.ComVisible(true)]
        public class ScriptCallback
        {
            BrowserControl owner;

            public ScriptCallback(BrowserControl owner)
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
            //try
            //{
                var html = ((dynamic)ieBrowser.Document.DomDocument).documentElement.outerHTML;
                if (ieBrowser.Document.GetElementById("SSR_CLSRCH_WRK_SUBJECT$0") != null)
                {
                    Log("Navigating to search results");
                    ieBrowser.Document.GetElementById("SSR_CLSRCH_WRK_SUBJECT$0").InnerText = classResults.ElementAt(processedCourses).CourseName;
                    ieBrowser.Document.GetElementById("SSR_CLSRCH_WRK_SUBJECT$0").InvokeMember("onchange");
                    ieBrowser.Document.GetElementById("SSR_CLSRCH_WRK_CATALOG_NBR$1").InnerText = classResults.ElementAt(processedCourses).CourseId;
                    ieBrowser.Document.GetElementById("SSR_CLSRCH_WRK_CATALOG_NBR$1").InvokeMember("onchange");
                    ieBrowser.Document.GetElementById("CLASS_SRCH_WRK2_STRM$273$").InnerText = classResults.ElementAt(processedCourses).Semester;
                    ieBrowser.Document.GetElementById("CLASS_SRCH_WRK2_STRM$273$").InvokeMember("onchange");
                    ieBrowser.Document.GetElementById("CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH").InvokeMember("click");
                }
                if (html.Contains("The search returns no results that match the criteria specified."))
                {
                    Log("Not Valid");
                    var timer = (System.Windows.Forms.Timer)sender;
                    timer.Stop();
                    Debug.WriteLine("Not Valid");
                    processedCourses++;
                    ProcessNextSearch();
                }

                else if (ieBrowser.Document.GetElementById("SSR_CLSRCH_WRK_SUBJECT$0") == null)
                {
                    Log("Parsing Results");
                    var timer = (System.Windows.Forms.Timer)sender;
                    timer.Stop();

                    html = ((dynamic)ieBrowser.Document.DomDocument).documentElement.outerHTML;

                    var doc = new HtmlDocument();
                    doc.LoadHtml(html);
                    int i = 0;
                    HtmlNode classNumber;
                    //className = doc.GetElementbyId("win0divSSR_CLSRSLT_WRK_GROUPBOX2GP$0").InnerText.Replace("&nbsp;", "");
                    while ((classNumber = doc.GetElementbyId("MTG_CLASS_NBR$" + i)) != null)
                    {
                        var classes = classNumber.Ancestors();


                        //var classy = classes.Where(x => x["id"].Value.ToLower().Contains("ACE_$ICField$48$$".ToLower()));
                        var statusTag = doc.GetElementbyId("win0divDERIVED_CLSRCH_SSR_STATUS_LONG$" + i).LastChild;
                        var statusImg = statusTag.ChildNodes.FirstOrDefault(x => x.Name.ToLower() == "img");

                        var scheduleClass = new UTAClass()
                        {
                            CourseNumber = classNumber.InnerText,

                            Section = doc.GetElementbyId("MTG_CLASSNAME$" + i).InnerText.Split('-')[0],
                            Room = doc.GetElementbyId("MTG_ROOM$" + i).InnerText.Replace("&nbsp;", ""),
                            Instructor = doc.GetElementbyId("MTG_INSTR$" + i).InnerText,
                            Status = statusImg.Attributes.FirstOrDefault(x => x.Name.ToLower() == "alt").Value,
                        };
                        var timeData = doc.GetElementbyId("MTG_DAYTIME$" + i).InnerText.Split(' ');
                        for (int j = 0; j < scheduleClass.MeetingDays.Count; j++)
                        {
                            if (!timeData[0].Contains(scheduleClass.MeetingDays.ElementAt(j)))
                            {
                                scheduleClass.MeetingDays.RemoveAt(j);
                            }
                        }
                        if (timeData[0] == "TBA")
                            scheduleClass.MeetingTime = timeData[0];
                        else
                            scheduleClass.MeetingTime = timeData[1] + "-" + timeData[3];
                        classResults.ElementAt(processedCourses).CourseResults.Add(scheduleClass);

                        i++;
                    }
                    processedCourses++;
                    ProcessNextSearch();
                }

            //}
            //catch (Exception ex)
            //{
            //    Log(ex.Message);
            //    if (ex.InnerException != null && ex.InnerException.Message != null)
            //        Log(ex.InnerException.Message);
            //    processedCourses++;
            //    ProcessNextSearch();
            //}
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