<!-- Used to test functionality of GetCourseInfo.php and GetSectionInfo.php -->
<!-- This page will send the content of the text fields to the appropriate page as get requests. Values are expected to be valid and are not checked for validity -->


<html>
   <body>
      <form action="GetCourseInfo.php" method="GET">
		<!-- 4 digit semester number, such as 2158 for Fall `15 or 2155 for Summer `15 -->
         Semester: <input type="text" name="semester" placeholder="EX:2158" />
		<!-- Comma seperated course names EX: CSE-3330,CSE-1310 -->
         Courses: <input type="text" name="courses" placeholder="EX:CSE-3330,CSE-1310"/>
         <input type="submit" value="Execute Courses Search" />
      </form>

      <form action="GetSectionInfo.php" method="GET">
		<!-- 4 digit semester number, such as 2158 for Fall `15 or 2155 for Summer `15 -->
         Semester: <input type="text" name="semester" placeholder="EX:2158" />
		<!-- Comma seperated section numbers (unique 5 digit number) EX: 87727,85881  -->
         Sections: <input type="text" name="sections" placeholder="EX:87727,85881" />
         <input type="submit" value="Execute Sections Search" />
      </form>
      
   </body>
</html>
