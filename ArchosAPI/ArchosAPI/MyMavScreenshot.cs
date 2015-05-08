using HtmlAgilityPack;
using System;
using System.Collections.Generic;
using System.Data.Common;
using System.Data.SqlClient;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Web;
using Test;
using Dapper;
using System.Diagnostics;

namespace ArchosAPI
{
    public class MyMavScreenshot
    {
        private DbConnection connection = new SqlConnection(@"Data Source=bducdbidmg.database.windows.net;Initial Catalog=UCS;Integrated Security=False;User ID=softeng_uta;Password=Chess123#;Connect Timeout=60;Encrypt=False;TrustServerCertificate=False");


        public List<UTAClass> finalClasses { get; set; }
        CookieContainer cc = new CookieContainer();
        HttpWebRequest request;
        HttpWebResponse response;
        Stream requestStream;
        byte[] byteArray;
        string html = "";
        StringBuilder sb = new StringBuilder();
        int departmentId;

        public Semester semester { get; set; }
        public string department { get; set; }

        public MyMavScreenshot(Semester semester, string department, int departmentId)
        {
            this.semester = semester;
            this.department = department;
            this.departmentId = departmentId;
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
            try
            {
                response = (HttpWebResponse)request.GetResponse();
            }
            catch (Exception ex)
            {
                System.Threading.Thread.Sleep(10000);
                response = (HttpWebResponse)request.GetResponse();
            }
            string searchPage = "https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL?&";
            request = (HttpWebRequest)WebRequest.Create(searchPage);
            request.CookieContainer = cc;
            request.UserAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2";
            try
            {
                response = (HttpWebResponse)request.GetResponse();
            }
            catch (Exception ex)
            {
                System.Threading.Thread.Sleep(10000);
                response = (HttpWebResponse)request.GetResponse();
            }
            using (StreamReader sr = new StreamReader(response.GetResponseStream()))
            {
                html = sr.ReadToEnd();
            }
            var document = new HtmlDocument();
            document.LoadHtml(html);

            while (html.Contains("An error has occurred"))
            {
                request = (HttpWebRequest)WebRequest.Create(searchPage);
                request.CookieContainer = cc;
                request.UserAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2";
                try
                {
                    response = (HttpWebResponse)request.GetResponse();
                }
                catch (Exception ex)
                {
                    System.Threading.Thread.Sleep(10000);
                    response = (HttpWebResponse)request.GetResponse();
                }
                using (StreamReader sr = new StreamReader(response.GetResponseStream()))
                {
                    html = sr.ReadToEnd();
                }



            }
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

            try
            {
                response = (HttpWebResponse)request.GetResponse();
            }
            catch (Exception ex)
            {
                System.Threading.Thread.Sleep(10000);
                response = (HttpWebResponse)request.GetResponse();
            }
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
            if (!semesterNodes.Any())
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

                try
                {
                    response = (HttpWebResponse)request.GetResponse();
                }
                catch (Exception ex)
                {
                    System.Threading.Thread.Sleep(10000);
                    response = (HttpWebResponse)request.GetResponse();
                }
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
                {"ICAction", "#ICRow" + semesterNodes.IndexOf(semester.SemesterNumber)},
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

            try
            {
                response = (HttpWebResponse)request.GetResponse();
            }
            catch (Exception ex)
            {
                System.Threading.Thread.Sleep(5000);
                response = (HttpWebResponse)request.GetResponse();
            }
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

            try
            {
                response = (HttpWebResponse)request.GetResponse();
            }
            catch (Exception ex)
            {
                System.Threading.Thread.Sleep(10000);
                response = (HttpWebResponse)request.GetResponse();
            }
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
                {"SSR_CLSRCH_WRK_CATALOG_NBR$1", "0"},
                {"SSR_CLSRCH_WRK_SSR_EXACT_MATCH1$1", "G"},
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

            try
            {
                response = (HttpWebResponse)request.GetResponse();
            }
            catch (Exception ex)
            {
                System.Threading.Thread.Sleep(5000);
                response = (HttpWebResponse)request.GetResponse();
            }
            html = "";
            using (StreamReader sr = new StreamReader(response.GetResponseStream()))
            {
                try
                {
                    html = sr.ReadToEnd();
                }
                catch (Exception ex)
                {
                    System.Threading.Thread.Sleep(10000);
                    response = (HttpWebResponse)request.GetResponse();
                    var sit = new StreamReader(response.GetResponseStream());
                    html = sit.ReadToEnd();
                }
            }

            document.LoadHtml(html);

            i = 0;
            HtmlNode course;

            while ((course = document.GetElementbyId("win0divSSR_CLSRSLT_WRK_GROUPBOX2GP$" + i)) != null)
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
            Debug.WriteLine(semester.SemesterNumber + " - " + department);
        }
    }
}