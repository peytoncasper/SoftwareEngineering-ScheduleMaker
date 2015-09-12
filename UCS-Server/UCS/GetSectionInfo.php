<?php

/*	This page will format get requests for execution by python scripts by transforming the get requests into a JSON string
 *	
 */
  	$SearchCommand = 'python3 Python/ThreadSectionSearch.py ';

	if( $_GET["semester"] || $_GET["sections"] )
	{
		
		$raw_sections = trim($_GET["sections"]);
		//Remove whitespace
		$sections_array = explode(",",$raw_sections);
		//Seperate sections by comma

		$data = [
			"semester"=>(int)trim($_GET["semester"]),
			"sections"=>$sections_array
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
