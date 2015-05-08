using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ArchosAPI
{
    public class Schedule
    {
        public string Email { get; set; }
        public string MilitartTime { get; set; }
        public List<ScheduleResults> Schedules { get; set; }
        public List<BlockOutTimeResults> BlockOutTimes { get; set; }
    }
}