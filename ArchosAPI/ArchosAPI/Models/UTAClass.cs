using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Test
{
    public class UTAClass
    {
        public string CourseNumber { get; set; }
        public string Section { get; set; }
        public string Room { get; set; }
        public string Instructor { get; set; }
        public string MeetingTime { get; set; }

        public List<string> MeetingDays = new List<string>(){ "M", "TU", "W", "TH", "F" };
        public string Status { get; set; }

    }
}
