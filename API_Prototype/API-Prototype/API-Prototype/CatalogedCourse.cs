using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ArchosAPI
{
    public class CatalogedCourse
    {
        public int Id { get; set; }
        public int DepartmentId { get; set; }
        public string CourseName { get; set; }
        public string CourseNumber { get; set; }
    }
}