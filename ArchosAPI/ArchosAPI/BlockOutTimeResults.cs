using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ArchosAPI
{
    public class BlockOutTimeResults
    {
        public List<Temp> CourseResults { get; set; }
        public string Department { get; set; }
        public string CourseName { get; set; }
        public string CourseId { get; set; }
    }
}