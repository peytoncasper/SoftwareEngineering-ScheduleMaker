using HtmlAgilityPack;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Web;
using Test;

namespace ArchosAPI
{
    public class DownloadCourseData
    {
        public List<UTAClass> finalClasses { get; set; }
        CookieContainer cc = new CookieContainer();
        HttpWebRequest request;
        HttpWebResponse response;
        Stream requestStream;
        byte[] byteArray;
        string html = "";
        StringBuilder sb = new StringBuilder();

        public string semester { get; set; }
        public string department { get; set; }
        public string courseNumber { get; set; }
        public string courseName { get; set; }
        public DownloadCourseData(string semester, string department, string courseNumber)
        {
            this.semester = semester;
            this.department = department;
            this.courseNumber = courseNumber;
            finalClasses = new List<UTAClass>();


        }
        /// <summary>
        /// The first two calls handle the error redirect from MyMav asking for cookies. The rest basically emulate the post requests
        /// from MyMav and you can use FireBug in FireFox to analyze any of MyMavs request and see how these are put together. One thing to note
        /// is that the ICIS code is the most important part as it is how the MyMav ties your session together
        /// </summary>
        public void Execute()
        {
            string errorPage = "https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL?&";

            request = (HttpWebRequest)WebRequest.Create(errorPage);
            request.CookieContainer = cc;
            request.UserAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2";
            response = (HttpWebResponse)request.GetResponse();

            string searchPage = "https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL?&";
            request = (HttpWebRequest)WebRequest.Create(searchPage);
            request.CookieContainer = cc;
            request.UserAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2";
            response = (HttpWebResponse)request.GetResponse();
            using (StreamReader sr = new StreamReader(response.GetResponseStream()))
            {
                html = sr.ReadToEnd();
            }
            var document = new HtmlDocument();
            document.LoadHtml(html);



            var getSemesterList = "https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL";
            var getSemesterListParameters = new Dictionary<string, string>()
            {
                {"CLASS_SRCH_WRK2_STRM$273$", document.GetElementbyId("CLASS_SRCH_WRK2_STRM$273$").InnerText},
                {"FacetPath", document.GetElementbyId("FacetPath") != null ? document.GetElementbyId("FacetPath").GetAttributeValue("value","") : "None"},
                {"ICAJAX","1"},
                {"ACAPPCLSDATA",document.GetElementbyId("ICAPPCLSDATA") != null ? document.GetElementbyId("ICAPPCLSDATA").GetAttributeValue("value","") : ""},
                {"ICAction", "CLASS_SRCH_WRK2_STRM$273$$prompt"},
                {"ICActionPrompt", document.GetElementbyId("ICActionPrompt") != null ? document.GetElementbyId("ICActionPrompt").GetAttributeValue("value","") : "false"},
                {"ICAddCount", document.GetElementbyId("ICAddCount") != null ? document.GetElementbyId("ICAddCount").GetAttributeValue("value","") : ""},
                {"ICChanged", document.GetElementbyId("ICChanged") != null ? document.GetElementbyId("ICChanged").GetAttributeValue("value","") : "-1"},
                {"ICElementNum", document.GetElementbyId("ICElementNum") != null ? document.GetElementbyId("ICElementNum").GetAttributeValue("value","") : "0"},
                {"ICFind", document.GetElementbyId("ICFind") != null ?  document.GetElementbyId("ICFind").GetAttributeValue("value","") : ""},
                {"ICFocus", ""},
                {"ICNAVTYPEDROPDOWN", "0"},
                {"ICResubmit", document.GetElementbyId("ICResubmit")!= null ? document.GetElementbyId("ICResubmit").GetAttributeValue("value","") : "0"},
                {"ICSID", document.GetElementbyId("ICSID") != null ? document.GetElementbyId("ICSID").GetAttributeValue("value","") : ""},
                {"ICSaveWarningFilter",document.GetElementbyId("ICSaveWarningFilter") != null ? document.GetElementbyId("ICSaveWarningFilter").GetAttributeValue("value","") : "0"},
                {"ICStateNum",document.GetElementbyId("ICStateNum") != null ? document.GetElementbyId("ICStateNum").GetAttributeValue("value","") : ""},
                {"ICType", document.GetElementbyId("ICType") != null ? document.GetElementbyId("ICType").GetAttributeValue("value","") : "Panel"},
                {"ICXPos", document.GetElementbyId("ICXPos") != null ? document.GetElementbyId("ICXPos").GetAttributeValue("value","") : "0"},
                {"ICYPos", document.GetElementbyId("ICYPos") != null ? document.GetElementbyId("ICYPos").GetAttributeValue("value","") : "0"},
                {"ResponseToDiffFrame", document.GetElementbyId("ResponsetoDiffFrame") != null ? document.GetElementbyId("ResponsetoDiffFrame").GetAttributeValue("value","") : "-1"},
                {"TargetFrameName",document.GetElementbyId("TargetFrameName") != null ? document.GetElementbyId("TargetFrameName").GetAttributeValue("value","") : "None"},
            };

            foreach (var key in getSemesterListParameters.Keys)
            {
                sb.Append(key + "=" + getSemesterListParameters[key] + "&");
            }
            sb.Remove(sb.Length - 1, 1);

            request = (HttpWebRequest)WebRequest.Create(getSemesterList);
            request.Method = WebRequestMethods.Http.Post;
            request.UserAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2";
            request.AllowWriteStreamBuffering = true;
            request.ProtocolVersion = HttpVersion.Version11;
            request.AllowAutoRedirect = true;
            request.ContentType = "application/x-www-form-urlencoded";
            request.CookieContainer = cc;

            byteArray = Encoding.ASCII.GetBytes(sb.ToString());
            request.ContentLength = byteArray.Length;
            requestStream = request.GetRequestStream();
            requestStream.Write(byteArray, 0, byteArray.Length);
            requestStream.Close();


            response = (HttpWebResponse)request.GetResponse();

            html = "";
            using (StreamReader sr = new StreamReader(response.GetResponseStream()))
            {
                html = sr.ReadToEnd();
                var tempy = html;
            }
            document.LoadHtml(html);

            var semesterNodes = new List<string>();
            HtmlNode node = document.GetElementbyId("SEARCH_RESULT1");
            int i = 1;
            while (node != null)
            {
                semesterNodes.Add(node.InnerText);
                node = document.GetElementbyId("RESULT0$" + i);
                i++;
            }
            node = document.GetElementbyId("SEARCH_RESULTLAST");
            if (node != null)
                semesterNodes.Add(node.InnerText);
            if(!semesterNodes.Any())
            {
                getSemesterList = "https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL";
                getSemesterListParameters = new Dictionary<string, string>()
            {
                {"CLASS_SRCH_WRK2_STRM$273$", document.GetElementbyId("CLASS_SRCH_WRK2_STRM$273$").InnerText},
                {"FacetPath", document.GetElementbyId("FacetPath") != null ? document.GetElementbyId("FacetPath").GetAttributeValue("value","") : "None"},
                {"ICAJAX","1"},
                {"ACAPPCLSDATA",document.GetElementbyId("ICAPPCLSDATA") != null ? document.GetElementbyId("ICAPPCLSDATA").GetAttributeValue("value","") : ""},
                {"ICAction", "CLASS_SRCH_WRK2_STRM$273$$prompt"},
                {"ICActionPrompt", document.GetElementbyId("ICActionPrompt") != null ? document.GetElementbyId("ICActionPrompt").GetAttributeValue("value","") : "false"},
                {"ICAddCount", document.GetElementbyId("ICAddCount") != null ? document.GetElementbyId("ICAddCount").GetAttributeValue("value","") : ""},
                {"ICChanged", document.GetElementbyId("ICChanged") != null ? document.GetElementbyId("ICChanged").GetAttributeValue("value","") : "-1"},
                {"ICElementNum", document.GetElementbyId("ICElementNum") != null ? document.GetElementbyId("ICElementNum").GetAttributeValue("value","") : "0"},
                {"ICFind", document.GetElementbyId("ICFind") != null ?  document.GetElementbyId("ICFind").GetAttributeValue("value","") : ""},
                {"ICFocus", ""},
                {"ICNAVTYPEDROPDOWN", "0"},
                {"ICResubmit", document.GetElementbyId("ICResubmit")!= null ? document.GetElementbyId("ICResubmit").GetAttributeValue("value","") : "0"},
                {"ICSID", document.GetElementbyId("ICSID") != null ? document.GetElementbyId("ICSID").GetAttributeValue("value","") : ""},
                {"ICSaveWarningFilter",document.GetElementbyId("ICSaveWarningFilter") != null ? document.GetElementbyId("ICSaveWarningFilter").GetAttributeValue("value","") : "0"},
                {"ICStateNum",document.GetElementbyId("ICStateNum") != null ? document.GetElementbyId("ICStateNum").GetAttributeValue("value","") : ""},
                {"ICType", document.GetElementbyId("ICType") != null ? document.GetElementbyId("ICType").GetAttributeValue("value","") : "Panel"},
                {"ICXPos", document.GetElementbyId("ICXPos") != null ? document.GetElementbyId("ICXPos").GetAttributeValue("value","") : "0"},
                {"ICYPos", document.GetElementbyId("ICYPos") != null ? document.GetElementbyId("ICYPos").GetAttributeValue("value","") : "0"},
                {"ResponseToDiffFrame", document.GetElementbyId("ResponsetoDiffFrame") != null ? document.GetElementbyId("ResponsetoDiffFrame").GetAttributeValue("value","") : "-1"},
                {"TargetFrameName",document.GetElementbyId("TargetFrameName") != null ? document.GetElementbyId("TargetFrameName").GetAttributeValue("value","") : "None"},
            };

                foreach (var key in getSemesterListParameters.Keys)
                {
                    sb.Append(key + "=" + getSemesterListParameters[key] + "&");
                }
                sb.Remove(sb.Length - 1, 1);

                request = (HttpWebRequest)WebRequest.Create(getSemesterList);
                request.Method = WebRequestMethods.Http.Post;
                request.UserAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2";
                request.AllowWriteStreamBuffering = true;
                request.ProtocolVersion = HttpVersion.Version11;
                request.AllowAutoRedirect = true;
                request.ContentType = "application/x-www-form-urlencoded";
                request.CookieContainer = cc;

                byteArray = Encoding.ASCII.GetBytes(sb.ToString());
                request.ContentLength = byteArray.Length;
                requestStream = request.GetRequestStream();
                requestStream.Write(byteArray, 0, byteArray.Length);
                requestStream.Close();


                response = (HttpWebResponse)request.GetResponse();

                html = "";
                using (StreamReader sr = new StreamReader(response.GetResponseStream()))
                {
                    html = sr.ReadToEnd();
                    var tempy = html;
                }
                document.LoadHtml(html);

                semesterNodes = new List<string>();
                node = document.GetElementbyId("SEARCH_RESULT1");
                i = 1;
                while (node != null)
                {
                    semesterNodes.Add(node.InnerText);
                    node = document.GetElementbyId("RESULT0$" + i);
                    i++;
                }
                node = document.GetElementbyId("SEARCH_RESULTLAST");
                if (node != null)
                    semesterNodes.Add(node.InnerText);
            }
            //var semesterNodes = new List<string>()
            //{
            //    "2158",
            //    "2155",
            //    "2152",
            //    "2145",    
            //};
            var setSemester = "https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL";
            var setSemesterParameters = new Dictionary<string, string>()
            {
                {"FacetPath", document.GetElementbyId("FacetPath") != null ? document.GetElementbyId("FacetPath").GetAttributeValue("value","") : "None"},
                {"ICAJAX","1"},
                {"ACAPPCLSDATA",document.GetElementbyId("ICAPPCLSDATA") != null ? document.GetElementbyId("ICAPPCLSDATA").GetAttributeValue("value","") : ""},
                {"ICAction", "#ICRow" + semesterNodes.IndexOf(semester)},
                {"ICActionPrompt", document.GetElementbyId("ICActionPrompt") != null ? document.GetElementbyId("ICActionPrompt").GetAttributeValue("value","") : "false"},
                {"ICAddCount", document.GetElementbyId("ICAddCount") != null ? document.GetElementbyId("ICAddCount").GetAttributeValue("value","") : ""},
                {"ICChanged", document.GetElementbyId("ICChanged") != null ? document.GetElementbyId("ICChanged").GetAttributeValue("value","") : "-1"},
                {"ICElementNum", document.GetElementbyId("ICElementNum") != null ? document.GetElementbyId("ICElementNum").GetAttributeValue("value","") : "0"},
                {"ICFind", document.GetElementbyId("ICFind") != null ?  document.GetElementbyId("ICFind").GetAttributeValue("value","") : ""},
                {"ICFocus", ""},
                {"ICNAVTYPEDROPDOWN", "0"},
                {"ICResubmit", document.GetElementbyId("ICResubmit")!= null ? document.GetElementbyId("ICResubmit").GetAttributeValue("value","") : "0"},
                {"ICSID", document.GetElementbyId("ICSID") != null ? document.GetElementbyId("ICSID").GetAttributeValue("value","") : ""},
                {"ICSaveWarningFilter",document.GetElementbyId("ICSaveWarningFilter") != null ? document.GetElementbyId("ICSaveWarningFilter").GetAttributeValue("value","") : "0"},
                {"ICStateNum",document.GetElementbyId("ICStateNum") != null ? document.GetElementbyId("ICStateNum").GetAttributeValue("value","") : ""},
                {"ICType", document.GetElementbyId("ICType") != null ? document.GetElementbyId("ICType").GetAttributeValue("value","") : "Panel"},
                {"ICXPos", document.GetElementbyId("ICXPos") != null ? document.GetElementbyId("ICXPos").GetAttributeValue("value","") : "0"},
                {"ICYPos", document.GetElementbyId("ICYPos") != null ? document.GetElementbyId("ICYPos").GetAttributeValue("value","") : "0"},
                {"ResponseToDiffFrame", document.GetElementbyId("ResponsetoDiffFrame") != null ? document.GetElementbyId("ResponsetoDiffFrame").GetAttributeValue("value","") : "-1"},
                {"TargetFrameName",document.GetElementbyId("TargetFrameName") != null ? document.GetElementbyId("TargetFrameName").GetAttributeValue("value","") : "None"},
            };
            sb.Clear();
            foreach (var key in setSemesterParameters.Keys)
            {
                sb.Append(key + "=" + setSemesterParameters[key] + "&");
            }
            sb.Remove(sb.Length - 1, 1);

            request = (HttpWebRequest)WebRequest.Create(setSemester);
            request.Method = WebRequestMethods.Http.Post;
            request.UserAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2";
            request.AllowWriteStreamBuffering = true;
            request.ProtocolVersion = HttpVersion.Version11;
            request.AllowAutoRedirect = true;
            request.ContentType = "application/x-www-form-urlencoded";
            request.CookieContainer = cc;

            byteArray = Encoding.ASCII.GetBytes(sb.ToString());
            request.ContentLength = byteArray.Length;
            requestStream = request.GetRequestStream();
            requestStream.Write(byteArray, 0, byteArray.Length);
            requestStream.Close();


            response = (HttpWebResponse)request.GetResponse();

            html = "";
            using (StreamReader sr = new StreamReader(response.GetResponseStream()))
            {
                html = sr.ReadToEnd();
                var tempy = html;
            }
            document.LoadHtml(html);

            var setDepartment = "https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL";
            var setDepartmentParameters = new Dictionary<string, string>()
            {
                {"FacetPath", document.GetElementbyId("FacetPath") != null ? document.GetElementbyId("FacetPath").GetAttributeValue("value","") : "None"},
                {"ICAJAX","1"},
                {"ACAPPCLSDATA",document.GetElementbyId("ICAPPCLSDATA") != null ? document.GetElementbyId("ICAPPCLSDATA").GetAttributeValue("value","") : ""},
                {"ICAction", "SSR_CLSRCH_WRK_SUBJECT$0"},
                {"ICActionPrompt", document.GetElementbyId("ICActionPrompt") != null ? document.GetElementbyId("ICActionPrompt").GetAttributeValue("value","") : "false"},
                {"ICAddCount", document.GetElementbyId("ICAddCount") != null ? document.GetElementbyId("ICAddCount").GetAttributeValue("value","") : ""},
                {"ICChanged", document.GetElementbyId("ICChanged") != null ? document.GetElementbyId("ICChanged").GetAttributeValue("value","") : "-1"},
                {"ICElementNum", document.GetElementbyId("ICElementNum") != null ? document.GetElementbyId("ICElementNum").GetAttributeValue("value","") : "0"},
                {"ICFind", document.GetElementbyId("ICFind") != null ?  document.GetElementbyId("ICFind").GetAttributeValue("value","") : ""},
                {"ICFocus", "SSR_CLSRCH_WRK_SSR_EXACT_MATCH1$1"},
                {"ICNAVTYPEDROPDOWN", "0"},
                {"ICResubmit", document.GetElementbyId("ICResubmit")!= null ? document.GetElementbyId("ICResubmit").GetAttributeValue("value","") : "0"},
                {"ICSID", document.GetElementbyId("ICSID") != null ? document.GetElementbyId("ICSID").GetAttributeValue("value","") : ""},
                {"ICSaveWarningFilter",document.GetElementbyId("ICSaveWarningFilter") != null ? document.GetElementbyId("ICSaveWarningFilter").GetAttributeValue("value","") : "0"},
                {"ICStateNum",document.GetElementbyId("ICStateNum") != null ? document.GetElementbyId("ICStateNum").GetAttributeValue("value","") : ""},
                {"ICType", document.GetElementbyId("ICType") != null ? document.GetElementbyId("ICType").GetAttributeValue("value","") : "Panel"},
                {"ICXPos", document.GetElementbyId("ICXPos") != null ? document.GetElementbyId("ICXPos").GetAttributeValue("value","") : "0"},
                {"ICYPos", document.GetElementbyId("ICYPos") != null ? document.GetElementbyId("ICYPos").GetAttributeValue("value","") : "0"},
                {"ResponseToDiffFrame", document.GetElementbyId("ResponsetoDiffFrame") != null ? document.GetElementbyId("ResponsetoDiffFrame").GetAttributeValue("value","") : "-1"},
                {"SSR_CLSRCH_WRK_SUBJECT$0", department},
                {"TargetFrameName",document.GetElementbyId("TargetFrameName") != null ? document.GetElementbyId("TargetFrameName").GetAttributeValue("value","") : "None"},
            };

            foreach (var key in setDepartmentParameters.Keys)
            {
                sb.Append(key + "=" + setDepartmentParameters[key] + "&");
            }
            sb.Remove(sb.Length - 1, 1);

            request = (HttpWebRequest)WebRequest.Create(setDepartment);
            request.Method = WebRequestMethods.Http.Post;
            request.UserAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2";
            request.AllowWriteStreamBuffering = true;
            request.ProtocolVersion = HttpVersion.Version11;
            request.AllowAutoRedirect = true;
            request.ContentType = "application/x-www-form-urlencoded";
            request.CookieContainer = cc;

            byteArray = Encoding.ASCII.GetBytes(sb.ToString());
            request.ContentLength = byteArray.Length;
            requestStream = request.GetRequestStream();
            requestStream.Write(byteArray, 0, byteArray.Length);
            requestStream.Close();


            response = (HttpWebResponse)request.GetResponse();

            html = "";
            using (StreamReader sr = new StreamReader(response.GetResponseStream()))
            {
                html = sr.ReadToEnd();
                var tempy = html;
            }
            document.LoadHtml(html);


            var searchClass = "https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL";
            var searchClassParameters = new Dictionary<string, string>()
            {
                {"FacetPath", document.GetElementbyId("FacetPath") != null ? document.GetElementbyId("FacetPath").GetAttributeValue("value","") : "None"},
                {"ICAJAX","1"},
                {"ACAPPCLSDATA",document.GetElementbyId("ICAPPCLSDATA") != null ? document.GetElementbyId("ICAPPCLSDATA").GetAttributeValue("value","") : ""},
                {"ICAction", "CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH"},
                {"ICActionPrompt", document.GetElementbyId("ICActionPrompt") != null ? document.GetElementbyId("ICActionPrompt").GetAttributeValue("value","") : "false"},
                {"ICAddCount", document.GetElementbyId("ICAddCount") != null ? document.GetElementbyId("ICAddCount").GetAttributeValue("value","") : ""},
                {"ICChanged", document.GetElementbyId("ICChanged") != null ? document.GetElementbyId("ICChanged").GetAttributeValue("value","") : "-1"},
                {"ICElementNum", document.GetElementbyId("ICElementNum") != null ? document.GetElementbyId("ICElementNum").GetAttributeValue("value","") : "0"},
                {"ICFind", document.GetElementbyId("ICFind") != null ?  document.GetElementbyId("ICFind").GetAttributeValue("value","") : ""},
                {"ICFocus", ""},
                {"ICNAVTYPEDROPDOWN", "0"},
                {"ICResubmit", document.GetElementbyId("ICResubmit")!= null ? document.GetElementbyId("ICResubmit").GetAttributeValue("value","") : "0"},
                {"ICSID", document.GetElementbyId("ICSID") != null ? document.GetElementbyId("ICSID").GetAttributeValue("value","") : ""},
                {"ICSaveWarningFilter",document.GetElementbyId("ICSaveWarningFilter") != null ? document.GetElementbyId("ICSaveWarningFilter").GetAttributeValue("value","") : "0"},
                {"ICStateNum",document.GetElementbyId("ICStateNum") != null ? document.GetElementbyId("ICStateNum").GetAttributeValue("value","") : ""},
                {"ICType", document.GetElementbyId("ICType") != null ? document.GetElementbyId("ICType").GetAttributeValue("value","") : "Panel"},
                {"ICXPos", document.GetElementbyId("ICXPos") != null ? document.GetElementbyId("ICXPos").GetAttributeValue("value","") : "0"},
                {"ICYPos", document.GetElementbyId("ICYPos") != null ? document.GetElementbyId("ICYPos").GetAttributeValue("value","") : "0"},
                {"ResponseToDiffFrame", document.GetElementbyId("ResponsetoDiffFrame") != null ? document.GetElementbyId("ResponsetoDiffFrame").GetAttributeValue("value","") : "-1"},
                {"SSR_CLSRCH_WRK_CATALOG_NBR$1", courseNumber},
                {"SSR_CLSRCH_WRK_SSR_EXACT_MATCH1$1", "E"},
                {"SSR_CLSRCH_WRK_SSR_OPEN_ONLY$chk$3", "N"},
                {"TargetFrameName",document.GetElementbyId("TargetFrameName") != null ? document.GetElementbyId("TargetFrameName").GetAttributeValue("value","") : "None"},
            };
            sb.Clear();
            foreach (var key in searchClassParameters.Keys)
            {
                sb.Append(key + "=" + searchClassParameters[key] + "&");
            }
            sb.Remove(sb.Length - 1, 1);

            request = (HttpWebRequest)WebRequest.Create(searchClass);
            request.Method = WebRequestMethods.Http.Post;
            request.UserAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2";
            request.AllowWriteStreamBuffering = true;
            request.ProtocolVersion = HttpVersion.Version11;
            request.AllowAutoRedirect = true;
            request.ContentType = "application/x-www-form-urlencoded";
            request.CookieContainer = cc;

            byteArray = Encoding.ASCII.GetBytes(sb.ToString());
            request.ContentLength = byteArray.Length;
            requestStream = request.GetRequestStream();
            requestStream.Write(byteArray, 0, byteArray.Length);
            requestStream.Close();


            response = (HttpWebResponse)request.GetResponse();

            html = "";
            using (StreamReader sr = new StreamReader(response.GetResponseStream()))
            {
                html = sr.ReadToEnd();
                var tempy = html;
            }

            document.LoadHtml(html);

            i = 0;
            HtmlNode classNumber;
            //className = doc.GetElementbyId("win0divSSR_CLSRSLT_WRK_GROUPBOX2GP$0").InnerText.Replace("&nbsp;", "");
            while ((classNumber = document.GetElementbyId("MTG_CLASS_NBR$" + i)) != null)
            {
                var classes = classNumber.Ancestors();


                //var classy = classes.Where(x => x["id"].Value.ToLower().Contains("ACE_$ICField$48$$".ToLower()));
                var statusTag = document.GetElementbyId("win0divDERIVED_CLSRCH_SSR_STATUS_LONG$" + i);
                var statusImg = statusTag.ChildNodes.FirstOrDefault().ChildNodes.FirstOrDefault(x => x.Name.ToLower() == "img");
                var course = document.GetElementbyId("win0divSSR_CLSRSLT_WRK_GROUPBOX2GP$" + i);
                if (course != null)
                {
                    var courseText = course.InnerText.Replace(" &amp;", "").Trim("&nbsp;".ToCharArray());
                    courseName = courseText.Split('-')[1].Trim();
                }
                var scheduleClass = new UTAClass()
                {
                    CourseNumber = classNumber.InnerText,
                   
                    Section = document.GetElementbyId("MTG_CLASSNAME$" + i).InnerText.Split('-')[0],
                    Room = document.GetElementbyId("MTG_ROOM$" + i).InnerText.Replace("&nbsp;", ""),
                    Instructor = document.GetElementbyId("MTG_INSTR$" + i).InnerText,
                    Status = statusImg.Attributes.FirstOrDefault(x => x.Name.ToLower() == "alt").Value,
                };
                var timeData = document.GetElementbyId("MTG_DAYTIME$" + i).InnerText.Split(' ');
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
                finalClasses.Add(scheduleClass);
                i++;
            }
            
        }
    }
}