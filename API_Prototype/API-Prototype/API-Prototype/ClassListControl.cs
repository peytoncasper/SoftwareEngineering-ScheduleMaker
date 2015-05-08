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
using System.Text.RegularExpressions;
using Dapper;
using System.Data.Common;
using System.Data.SqlClient;
using System.Diagnostics;

namespace ArchosAPI
{
    public class ClassListControl : System.Windows.Forms.ApplicationContext
    {
        private DbConnection connection = new SqlConnection(@"Data Source=bducdbidmg.database.windows.net;Initial Catalog=UCS;Integrated Security=False;User ID=softeng_uta;Password=Chess123#;Connect Timeout=60;Encrypt=False;TrustServerCertificate=False");

        int navigationCounter;
        int timeout = 0;
        private Thread thrd;
        private WebBrowser ieBrowser;
        private ScriptCallback scriptCallback;
        private List<Semester> Semesters;
        private List<string> Departments;
        
        public List<CatalogedCourse> classList;

        private int processedSemesters = 0;
        private int processedDepartments = 0;

        private string currentSemester = "";
        private string currentDepartment = "";

        private int semesterId = 0;
        private int departmentId = 0;

        private string logFile;
        public ClassListControl(List<Semester> semesters, List<string> departments, string log)
        {
            classList = new List<CatalogedCourse>();
            logFile = log;

            Semesters = semesters;
            Departments = departments;

            Init();
        }
        private void ProcessNextSearch()
        {

            if (processedDepartments >= Departments.Count)
            {
                processedSemesters++;
                Semester data = connection.Query<Semester>("select * from Semester where SemesterNumber='" + Semesters.ElementAt(processedSemesters).SemesterNumber + "'").FirstOrDefault();
                if (data == null)
                {
                    var semester = new Semester()
                    {
                        SemesterNumber = Semesters.ElementAt(processedSemesters).SemesterNumber,
                    };
                    semesterId = connection.Query<int>("insert Semester (SemesterNumber) values (@SemesterNumber); select cast(scope_identity() as int)", semester).First();
                }
                else
                    semesterId = data.Id;

                processedDepartments = 0;

            }
            if (processedSemesters >= Semesters.Count)
                Application.Exit();

            currentSemester = Semesters.ElementAt(processedSemesters).SemesterNumber;
            currentDepartment = Departments.ElementAt(processedDepartments);

            Department department = connection.Query<Department>("select * from Department where DepartmentAcronym='" + currentDepartment + "' and SemesterId='" + semesterId + "'").FirstOrDefault();

            if (department == null)
            {
                department = new Department()
                {
                    DepartmentAcronym = currentDepartment,
                    SemesterId = semesterId,
                };
                departmentId = connection.Query<int>("insert Department (SemesterId, DepartmentAcronym) values (@SemesterId, @DepartmentAcronym); select cast(scope_identity() as int)", department).First();
            }
            else
                departmentId = department.Id;



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

                this.ExitThread();
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
        void IEBrowser_Navigating(object sender, WebBrowserNavigatingEventArgs e)
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

                    ieBrowser.Document.GetElementById("SSR_CLSRCH_WRK_SUBJECT$0").InnerText = currentDepartment;
                    ieBrowser.Document.GetElementById("SSR_CLSRCH_WRK_SUBJECT$0").InvokeMember("onchange");
                    ieBrowser.Document.GetElementById("CLASS_SRCH_WRK2_STRM$273$").InnerText = currentSemester;
                    ieBrowser.Document.GetElementById("CLASS_SRCH_WRK2_STRM$273$").InvokeMember("onchange");


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
            }
            catch (Exception ex)
            {
                Log(ex.Message);
                if (ex.InnerException != null && ex.InnerException.Message != null)
                    Log(ex.InnerException.Message);
                Debug.WriteLine(currentSemester + "-" + currentDepartment + "-Error");
                processedDepartments++;
                ProcessNextSearch();
            }
        }

        /// <summary> 
        /// class to hold the functions called
        /// by script codes in the WebBrowser control
        /// </summary> 
        [System.Runtime.InteropServices.ComVisible(true)]
        public class ScriptCallback
        {
            ClassListControl owner;

            public ScriptCallback(ClassListControl owner)
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
                var html = ((dynamic)ieBrowser.Document.DomDocument).documentElement.outerHTML;
                if (ieBrowser.Document.GetElementById("SSR_CLSRCH_WRK_SUBJECT$0") != null)
                {
                    Log("Navigating to search results");

                    ieBrowser.Document.GetElementById("CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH").InvokeMember("click");
                }
                if (html.Contains("The search returns no results that match the criteria specified."))
                {
                    Log("Not Valid");
                    var timer = (System.Windows.Forms.Timer)sender;
                    timer.Stop();
                    Debug.WriteLine(currentSemester + "-" + currentDepartment + "-Not Valid");
                    processedDepartments++;
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
                    HtmlNode course;

                    while ((course = doc.GetElementbyId("win0divSSR_CLSRSLT_WRK_GROUPBOX2GP$" + i)) != null)
                    {
                        var courseText = course.InnerText.Replace(" &amp;", "").Trim("&nbsp;".ToCharArray());
                        //var courseNumber = Regex.Match(courseText, @"^\d+$").ToString();
                        var courseNumber = courseText.Split(' ')[1];
                        //var courseName = Regex.Match(courseText, @"- .*?").ToString();
                        var courseName = courseText.Split('-')[1].Trim();
                        var courseToBeAdded = new CatalogedCourse()
                            {
                                CourseName = courseName,
                                CourseNumber = courseNumber,
                                DepartmentId = departmentId,
                            };

                        CatalogedCourse catalogCourse = connection.Query<CatalogedCourse>("select * from CatalogedCourse where DepartmentId='" + departmentId + "' and CourseNumber='" + courseNumber + "'").FirstOrDefault();
                        if (catalogCourse == null)
                            connection.Query<int>("insert CatalogedCourse (DepartmentId, CourseNumber, CourseName) values (@DepartmentId, @CourseNumber, @CourseName); select cast(scope_identity() as int)", courseToBeAdded).First();
                        i++;
                    }
                    //classList = classList.Remove(classList.Length - 1);

                    //thrd.Abort();
                    Debug.WriteLine(currentSemester + "-" + currentDepartment);
                    processedDepartments++;
                    ProcessNextSearch();
                }

            }
            catch (Exception ex)
            {
                Log(ex.Message);
                if (ex.InnerException != null && ex.InnerException.Message != null)
                    Log(ex.InnerException.Message);
                Debug.WriteLine(currentSemester + "-" + currentDepartment + "-Error");
                processedDepartments++;
                ProcessNextSearch();
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

