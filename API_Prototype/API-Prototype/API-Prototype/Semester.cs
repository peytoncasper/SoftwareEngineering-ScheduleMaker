using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ArchosAPI
{
    public class Semester
    {
        public int Id { get; set; }
        public string SemesterNumber { get; set; }
        public List<Department> Departments { get; set; }
    }
}