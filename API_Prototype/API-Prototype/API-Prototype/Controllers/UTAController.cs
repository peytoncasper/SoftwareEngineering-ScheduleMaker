using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading;
using System.Web;
using System.Web.Helpers;
using System.Web.Mvc;
using System.IO;
using System.Web.Script.Serialization;
using ArchosAPI.Models;
using HtmlAgilityPack;
using Microsoft.Ajax.Utilities;
using Microsoft.WindowsAzure.Storage;
using Microsoft.WindowsAzure.Storage.Auth;
using Microsoft.WindowsAzure.Storage.Queue;
using System.Configuration;
using System.Net;
using System.Web;
using System.Windows.Forms;
using Test;
using HtmlDocument = HtmlAgilityPack.HtmlDocument;
using System;
using System.Collections.Generic;
using System.Data.Common;
using System.Data.SqlClient;
using System.Linq;
using System.Web;
using System.Web.Helpers;
using System.Web.Http;
using System.Web.Mvc;
using Dapper;

namespace ArchosAPI.Controllers
{

    public class UTAController : Controller
    {
        private DbConnection connection = new SqlConnection(@"Data Source=bducdbidmg.database.windows.net;Initial Catalog=UCS;Integrated Security=False;User ID=softeng_uta;Password=Chess123#;Connect Timeout=60;Encrypt=False;TrustServerCertificate=False");

        public UTAController()
        {


        }
        public JsonResult ClassStatus(string classes = "2155*CSE-1301,CSE-2320")
        {
            try
            {

                if (classes != "" || classes != null)
                {


                    var watch = new Stopwatch();
                    watch.Start();
                    List<UTASearchResult> searchResults = new List<UTASearchResult>();
                    
                    var data = classes.Split('*');
                    var semester = data[0];
                    var ids = data[1].Split(',');
                    foreach(var id in ids)
                    {
                        var courseData = id.Split('-');
                        searchResults.Add(new UTASearchResult()
                            {
                                Semester = semester,
                                CourseId = courseData[1],
                                CourseName = courseData[0],
                                CourseResults = new List<UTAClass>(),
                            });
                    }

                    var control = new BrowserControl(searchResults, Server.MapPath("~/Log.txt"));

                    watch.Stop();
                    return Json(new
                    {
                        Results = searchResults,
                        TimeTaken = watch.Elapsed.TotalSeconds,
                        Success = true
                    }, JsonRequestBehavior.AllowGet);
                }
                else
                    throw new Exception();

            }
            catch (Exception ex)
            {
                Log(ex.Message);
                if (ex.InnerException != null && ex.InnerException.Message != null)
                    Log(ex.InnerException.Message);

                return Json(new
                {
                    Success = false,
                }, JsonRequestBehavior.AllowGet);
            }
        }

        public void UTADataPull()
        {
            Stopwatch watch = new Stopwatch();
            watch.Start();
            //TODO Implement a way to get all the semester numbers.
            //new SemesterControl(Server.MapPath("~/Log.txt")).Semesters;
            var semesterList = new List<Semester>()
            {
                new Semester()
                {
                    SemesterNumber = "2158",
                    Departments = new List<Department>()
                },
                new Semester()
                {
                    SemesterNumber = "2155",
                    Departments = new List<Department>()
                },
                new Semester()
                {
                    SemesterNumber = "2152",
                    Departments = new List<Department>()
                }, 
                new Semester()
                {
                    SemesterNumber = "2145",    
                    Departments = new List<Department>()
                }

            };
            var departments = new DepartmentListControl();
            var control = new ClassListControl(semesterList, departments.deptList, Server.MapPath("~/Log.txt"));

            watch.Stop();
            //return Json(new
            //{
            //    Success = false,
            //    ClassData = semesterList,
            //}, JsonRequestBehavior.AllowGet);
        }
        public JsonResult GetDepartmentClassData()
        {
            List<Semester> semesters = connection.Query<Semester>("select * from Semester").ToList();
            foreach(var semester in semesters)
            {
                semester.Departments = connection.Query<Department>("select * from Department where SemesterId='" + semester.Id + "'").ToList();
                if (semester.Departments != null)
                {
                    foreach (var department in semester.Departments)
                    {
                        department.CourseNumbers = connection.Query<CatalogedCourse>("select * from CatalogedCourse where DepartmentId='" + department.Id + "'").ToList();

                    }
                }
            }
            return Json(new
            {
                Success = false,
                Semesters = semesters,
            }, JsonRequestBehavior.AllowGet);
        }
        public JsonResult ValidateLogin(string username, string password)
        {
            var test = new Account() { Username = username, Password = password };
            Account data =
                connection.Query<Account>(
                    "select * from Account where Username='" + username + "' and Password='" + password + "'").FirstOrDefault();
            if (data != null)
            {
                return Json(new
                {
                    Success = true,
                    Email = data.Email
                }, JsonRequestBehavior.AllowGet);
            }
            else
            {
                return Json(new
                {
                    Success = false,
                    Email = "",
                }, JsonRequestBehavior.AllowGet);
            }
        }
        public JsonResult EmailExists(string email)
        {
            Account data = connection.Query<Account>("select * from Account where Email='" + email + "'").FirstOrDefault();
            if (data != null)
            {
                return Json(new
                {
                    Success = true,
                }, JsonRequestBehavior.AllowGet);
            }
            else
            {
                return Json(new
                {
                    Success = false,
                }, JsonRequestBehavior.AllowGet);
            }
        }
        public JsonResult CreateAccount(string username, string password, string email)
        {
            try
            {
                Account data = connection.Query<Account>("select * from Account where Username='" + username + "'").FirstOrDefault();
                if (data == null)
                {
                    var test = new Account() { Username = username, Password = password, Email = email };
                    var testId = connection.Query<int>(
                        "insert Account (Username, Password, Email) values (@Username, @Password, @Email); select cast(scope_identity() as int)", test).First();
                    if (testId != 0)
                    {
                        return Json(new
                        {
                            Success = true,
                            Email = email,
                            Username = username,
                            Message = "Account Added."
                        }, JsonRequestBehavior.AllowGet);
                    }
                }
                else
                {
                    return Json(new
                    {
                        Success = false,
                        Message = "Username is taken.",
                        Email = email,
                        Username = username,
                    }, JsonRequestBehavior.AllowGet);
                }
                return Json(new
                {
                    Success = false,
                    Message = "There was an error processing this account, try again later.",
                    Email = email,
                    Username = username,
                }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception ex)
            {
                return Json(new
                {
                    Success = false,
                    Message = "There was an error processing this account, try again later.",
                    Email = email,
                    Username = username,
                }, JsonRequestBehavior.AllowGet);
            }
        }
        public void Log(string s)
        {

            using (StreamWriter writer = new StreamWriter(Server.MapPath("~/Log.txt")))
            {
                writer.WriteLine(s);
            }
        }
    }
}