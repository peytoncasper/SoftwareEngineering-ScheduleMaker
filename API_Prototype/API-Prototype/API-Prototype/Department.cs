using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ArchosAPI
{
    public class Department
    {
        public int Id { get; set; }
        public int SemesterId { get; set; }
        public string DepartmentAcronym { get; set; }
        public List<CatalogedCourse> CourseNumbers { get; set; }
    }
}