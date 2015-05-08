using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ArchosAPI
{
    public class Course
    {
        public int ScheduleId { get; set; }
        public string Department { get; set; }
        public string CourseNumber { get; set; }
        public string Semester { get; set; }

    }
}