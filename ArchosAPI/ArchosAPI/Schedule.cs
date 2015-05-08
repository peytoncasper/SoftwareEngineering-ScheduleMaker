using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ArchosAPI
{
    public class Schedule
    {
        public int Id { get; set; }
        public string Email { get; set; }

        public List<ScheduleResults> Schedules { get; set; }
        public List<BlockOutTimeResults> BlockOutTimes { get; set; }
    }
}