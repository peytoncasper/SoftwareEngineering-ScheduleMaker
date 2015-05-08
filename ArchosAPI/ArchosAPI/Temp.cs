using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ArchosAPI
{
    public class Temp
    {
        public int Id { get; set; }
        public int CourseId { get; set; }
        public string CourseNumber { get; set; }
        public string Section { get; set; }
        public string Room { get; set; }
        public string Instructor { get; set; }
        public string MeetingTime { get; set; }

        public List<string> MeetingDays { get; set; }
        public string Status { get; set; }
    }
}