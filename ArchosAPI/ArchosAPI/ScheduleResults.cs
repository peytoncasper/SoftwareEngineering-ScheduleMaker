using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ArchosAPI
{
    public class ScheduleResults
    {
        public int Id { get; set; }
        public int AccountId { get; set; }
        public List<ScheduleCourses> ScheduleCourses { get; set; }
        public string ScheduleSemester { get; set; }
        public string ScheduleName { get; set; }
    }
}