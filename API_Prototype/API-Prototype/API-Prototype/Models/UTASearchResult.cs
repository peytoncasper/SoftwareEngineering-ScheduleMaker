using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Test;

namespace ArchosAPI.Models
{
    public class UTASearchResult
    {
        public string Semester { get; set; }
        public string CourseId { get; set; }
        public string CourseName { get; set; }
        public List<UTAClass> CourseResults { get; set; } 
    }
}
