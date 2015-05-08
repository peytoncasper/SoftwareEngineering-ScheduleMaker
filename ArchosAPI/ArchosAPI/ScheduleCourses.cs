using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ArchosAPI
{
    public class ScheduleCourses
    {
        public int Id { get; set; }
        public int ScheduleId { get; set; }
        public List<Temp> CourseResults { get; set; }
        public string CourseName { get; set; }
        public string CourseId { get; set; }
        public string Department { get; set; }
    }
}