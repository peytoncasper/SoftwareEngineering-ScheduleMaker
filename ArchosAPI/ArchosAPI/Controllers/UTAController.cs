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

using System.Web.Mvc;
using Dapper;
using Microsoft.WindowsAzure.Storage.Blob;
using System.Threading.Tasks;
using Microsoft.WindowsAzure.Storage.RetryPolicies;
using Newtonsoft.Json;
using System.Text;

namespace ArchosAPI.Controllers
{

    public class UTAController : Controller
    {
        /// <summary>
        /// Add your SQL Connection String Here
        /// </summary>
        private DbConnection connection = new SqlConnection(@"Data Source=bducdbidmg.database.windows.net;Initial Catalog=UCS;Integrated Security=False;User ID=softeng_uta;Password=Chess123#;Connect Timeout=60;Encrypt=False;TrustServerCertificate=False");
        public UTAController()
        {


        }
        /// <summary>
        /// This pulls a snapshot of the MyMav system and stores the data. Note that it relies on the old method of using WinForms so it 
        /// wont run on the server and will need to run locally. Needs to be updated to using HttpRequests
        /// </summary>
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
                    SemesterName = "Fall 2015",
                    Departments = new List<Department>()
                },
                new Semester()
                {
                    SemesterNumber = "2155",
                    SemesterName = "Summer 2015",
                    Departments = new List<Department>()
                },
                new Semester()
                {
                    SemesterNumber = "2152",
                    SemesterName = "Spring 2015",
                    Departments = new List<Department>()
                }, 
                new Semester()
                {
                    SemesterNumber = "2145",
                    SemesterName = "Summer 2014",
                    Departments = new List<Department>()

                }

            };
            var departments = new DepartmentListControl();
            //var control = new ClassListControl(semesterList, departments.deptList, Server.MapPath("~/Log.txt"));
            int semesterId = 0;
            foreach (var semester in semesterList)
            {
                Semester data = connection.Query<Semester>("select * from Semester where SemesterNumber='" + semester.SemesterNumber + "'").FirstOrDefault();
                if (data == null)
                {
                    semesterId = connection.Query<int>("insert Semester (SemesterNumber, SemesterName) values (@SemesterNumber, @SemesterName); select cast(scope_identity() as int)", semester).First();
                    
                }
                else
                    semesterId = data.Id;
                semester.Id = semesterId;
                foreach (var currentDepartment in departments.deptList)
                {
                    Department department = connection.Query<Department>("select * from Department where DepartmentAcronym='" + currentDepartment.Substring(currentDepartment.IndexOf('(') + 1, currentDepartment.IndexOf(')') - currentDepartment.IndexOf('(') - 1) + "' and SemesterId='" + semesterId + "'").FirstOrDefault();

                    if (department == null)
                    {
                        var departmentName = currentDepartment.Split('(')[0];
                        departmentName = departmentName.Insert(currentDepartment.IndexOf('\''), "'");
                        department = new Department()
                        {
                            DepartmentName = departmentName.Trim(),
                            DepartmentAcronym = currentDepartment.Substring(currentDepartment.IndexOf('(') + 1, currentDepartment.IndexOf(')') - currentDepartment.IndexOf('(') - 1),
                            SemesterId = semesterId,
                        };
                        var departmentId = connection.Query<int>("insert Department (SemesterId, DepartmentAcronym) values (@SemesterId, @DepartmentAcronym); select cast(scope_identity() as int)", department).First();
                        department.Id = departmentId;

                        semester.Departments.Add(department);
                    }
                    else
                    {
                        semester.Departments.Add(department);
                    }
                }
            }
            foreach(var semester in semesterList)
            {
                Parallel.ForEach(semester.Departments, new ParallelOptions { MaxDegreeOfParallelism = 2 }, x => new MyMavScreenshot(semester, x.DepartmentAcronym, x.Id).Execute());
            }
            //var tempy = new MyMavScreenshot(semesterList.ElementAt(0), "CSE", );
            //    tempy.Execute();
            watch.Stop();
            //return Json(new
            //{
            //    Success = false,
            //    ClassData = semesterList,
            //}, JsonRequestBehavior.AllowGet);
        }
        /// <summary>
        /// Note that DepartmentListControl is now deprecated and should be converted to using HttpRequest 
        /// as per the other MyMav requests. This method is obselete in the sense that it was written to add additional data after the fact. 
        /// The Batch pull already inserts this data currently.
        /// </summary>
        public void UpdateDepartments()
        {
            var departments = new DepartmentListControl();

            List<Department> mainDepartments = connection.Query<Department>("select * from Department").ToList();
            foreach(var department in departments.deptList)
            {
                var currentDepartment = department.Substring(department.IndexOf('(') + 1, department.IndexOf(')') - department.IndexOf('(') - 1);
                var departmentName = department.Split('(')[0].Trim();
                if (currentDepartment.Contains('\''))
                    currentDepartment.Insert(currentDepartment.IndexOf('\''), "'");
                if (departmentName.Contains('\''))
                    departmentName = departmentName.Insert(departmentName.IndexOf('\''), "'");
                    connection.Query<int>("update Department set DepartmentName='" + departmentName+ "' where DepartmentAcronym = '" + currentDepartment +"'");
                
            }
        }
        /// <summary>
        /// Gets the auto complete data from the database and returns it via JSON to the phone.
        /// </summary>
        /// <returns></returns>
        public JsonResult GetDepartmentClassData()
        {
            List<Semester> semesters = connection.Query<Semester>("select * from Semester").ToList();
            foreach (var semester in semesters)
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
                Success = true,
                Semesters = semesters,
            }, JsonRequestBehavior.AllowGet);
        }
        /// <summary>
        /// Validates the login and returns the users saved settings.
        /// </summary>
        /// <param name="username"></param>
        /// <param name="password"></param>
        /// <returns></returns>
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
                    Email = data.Email,
                    Settings = data.Settings,
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
        /// <summary>
        /// Deltes the account based off email
        /// </summary>
        /// <param name="email"></param>
        /// <returns></returns>
        public JsonResult DeleteAccount(string email)
        {
            try
            {
                connection.Query<int>("delete from Account where Email='" + email + "'");
                return Json(new
                {
                    Success = true,

                }, JsonRequestBehavior.AllowGet);
            }
            catch(Exception ex)
            {
                return Json(new
                {
                    Success = false,
                }, JsonRequestBehavior.AllowGet);
            }
        }
        /// <summary>
        /// Logs the user out and saves the account settings/schedule to the DB
        /// </summary>
        /// <param name="email"></param>
        /// <param name="accountSettings"></param>
        /// <returns></returns>
        public JsonResult Logout(string email, string accountSettings)
        {
            try
            {

                Account data = connection.Query<Account>("select * from Account where Email='" + email + "'").FirstOrDefault();
                if (data != null)
                {
                    data.Settings = accountSettings;
                    connection.Query<int>("update Account set Settings=@Settings where Id = @Id", data);


                }
                else
                {
                    return Json(new
                    {
                        Success = false,
                        Message = "Not a valid email",
                    }, JsonRequestBehavior.AllowGet);
                }
                return Json(new
                {
                    Success = true,

                }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception ex)
            {
                return Json(new
                {
                    Success = false,

                }, JsonRequestBehavior.AllowGet);
            }
            return Json(new
            {
                Success = false,

            }, JsonRequestBehavior.AllowGet);
        }

        /// <summary>
        /// Change Password
        /// </summary>
        /// <param name="scheduleId"></param>
        /// <param name="schedule"></param>
        /// <returns></returns>
        public JsonResult ChangePassword(string email, string oldPassword, string newPassword)
        {
            Account data = connection.Query<Account>("select * from Account where Email='" + email + "'").FirstOrDefault();
            if (data != null)
            {
                data.Password = newPassword;
                connection.Query<int>("update Account set Password=@Password where Id = @Id", data);
                return Json(new
                {
                    Success = true,
                }, JsonRequestBehavior.AllowGet);
            }
            else
            {
                return Json(new
                {
                    Success = true,
                    Message = "This account doesnt exist."
                }, JsonRequestBehavior.AllowGet);
            }
        }

        /// <summary>
        /// Change Email
        /// </summary>
        /// <param name="scheduleId"></param>
        /// <param name="schedule"></param>
        /// <returns></returns>
        public JsonResult ChangeEmail(string oldEmail, string newEmail)
        {
            Account data = connection.Query<Account>("select * from Account where Email='" + oldEmail + "'").FirstOrDefault();
            if (data != null)
            {
                data.Email = newEmail;
                connection.Query<int>("update Account set Email=@Email where Id = @Id", data);
                return Json(new
                {
                    Success = true,
                }, JsonRequestBehavior.AllowGet);
            }
            else
            {
                return Json(new
                {
                    Success = true,
                    Message = "This account doesnt exist."
                }, JsonRequestBehavior.AllowGet);
            }

        }
        /// <summary>
        /// Accepts the data in comma delimited strings and exectues HttpRequests in parallel to get the data from MyMav. 
        /// Returns the current status (Open, Closed, Waitlisted) to via JSON.
        /// </summary>
        /// <param name="semester"></param>
        /// <param name="departments"></param>
        /// <param name="classNumbers"></param>
        /// <returns></returns>
        public JsonResult ValidateCourses(string semester = "2152", string departments = "CSE", string classNumbers = "21931")
        {
            try
            {
                Stopwatch watch = new Stopwatch();
                watch.Start();

                var semesterList = semester.Split(',');
                var classNumberList = classNumbers.Split(',');
                var departmentList = departments.Split(',');
                var searchResults = new List<ValidateCourses>();
                for (int i = 0; i < semesterList.Count(); i++)
                {
                    searchResults.Add(new ValidateCourses(semesterList[i], departmentList[i], classNumberList[i]));
                }

                Parallel.ForEach(searchResults, x => x.Execute());




                var results = new List<UTASearchResult>();
                foreach (var result in searchResults)
                {
                    results.Add(new UTASearchResult()
                    {
                        CourseId = result.number,
                        Department = result.department,
                        CourseName = result.courseName,
                        Semester = result.semester,
                        CourseResults = result.finalClasses

                    });
                }







                watch.Stop();
                return Json(new
                {
                    Success = true,
                    Results = results,
                    TimeTaken = watch.Elapsed.TotalSeconds,
                }, JsonRequestBehavior.AllowGet);
            }
            catch(Exception ex)
            {
                return Json(new
                {
                    Success = false,
                    Message = ex.InnerException,
                }, JsonRequestBehavior.AllowGet);
            }
        }
        /// <summary>
        /// Gets the full course info from MyMav and returns it via JSON. Accepts comma delimited strings.
        /// </summary>
        /// <param name="semester"></param>
        /// <param name="department"></param>
        /// <param name="courseNumber"></param>
        /// <returns></returns>
        public JsonResult GetCourseInfo(string semester = "2152,2152,2152,2152", string department = "CSE,MATH,MATH,KORE", string courseNumber = "3302,1301,1302,1441")
        {
            try
            {
                Stopwatch watch = new Stopwatch();
                watch.Start();

                var semesterList = semester.Split(',');
                var departmentList = department.Split(',');
                var courseList = courseNumber.Split(',');
                var searchResults = new List<DownloadCourseData>();
                for (int i = 0; i < semesterList.Count(); i++)
                {
                    searchResults.Add(new DownloadCourseData(semesterList[i], departmentList[i], courseList[i]));
                }

                Parallel.ForEach(searchResults, x => x.Execute());




                var results = new List<UTASearchResult>();
                foreach (var result in searchResults)
                {
                    results.Add(new UTASearchResult()
                    {
                        CourseId = result.courseNumber,
                        CourseName = result.courseName,
                        Department = result.department,
                        Semester = result.semester,
                        CourseResults = result.finalClasses

                    });
                }







                watch.Stop();
                return Json(new
                {
                    Success = true,
                    Results = results,
                    TimeTaken = watch.Elapsed.TotalSeconds,
                }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception ex)
            {
                return Json(new
                {
                    Success = false,
                    Message = ex.InnerException,
                }, JsonRequestBehavior.AllowGet);
            }
        }
        //public JsonResult CreateSchedule(string scheduleName, string courses = "")
        //{

        //}
        public JsonResult EditSchedule(int scheduleId, Schedule schedule)
        {
            try
            {
                //var scheduleId = connection.Query<int>(
                //            "insert Schedule (ScheduleName) values (@ScheduleName); select cast(scope_identity() as int)", schedule).First();
                //foreach (var course in schedule.Courses)
                //{
                //    var courseId = connection.Query<int>(
                //            "insert Schedule (ScheduleId, Department, CourseNumber, Semester) values (@ScheduleId, @Department, @CourseNumber, @Semester); select cast(scope_identity() as int)", course).First();
                //}
                return Json(new
                {
                    Success = true,

                }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception ex)
            {
                return Json(new
                {
                    Success = false,
                    Message = ex.InnerException,

                }, JsonRequestBehavior.AllowGet);
            }
            return Json(new
            {
                Success = false,

            }, JsonRequestBehavior.AllowGet);
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
                    Message = ex.InnerException,
                    //Message = "There was an error processing this account, try again later.",
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