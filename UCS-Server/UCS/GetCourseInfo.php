<?php

/*	This page will format get requests for execution by python scripts by transforming the get requests into a JSON string
 *	
 */
  	$SearchCommand = 'python3 Python/ThreadCourseSearch.py ';

	if( $_GET["semester"] || $_GET["courses"] )
	{
		
		$raw_courses = trim($_GET["courses"]);
		//Remove whitespace
		$courses_array = explode(",",$raw_courses);
		//Seperate courses by comma

		$data = [
			"semester"=>(int)trim($_GET["semester"]),
			"classes"=>$courses_array
		];
		$command = $SearchCommand.escapeshellarg(json_encode($data));
		//Build python command line request and ensure request has no malicious data

		exec($command, $output);
		//Execute command line
		echo '<p>'.implode($output).'</p>';
		//Concatenate program output into one string and print
      
		exit();
	}
?>
