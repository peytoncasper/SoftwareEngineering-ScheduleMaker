<?php
	include_once 'PHPincludes/db_connect.php';
	include_once 'PHPincludes/functions.php';

	sec_session_start();
	
	$error_messages = []; 
	$result = [];

	//echo 'test';

	if (login_check($mysqli) == true){
		
		if(isset($_SESSION['user_id'])){

			$user_id = $_SESSION['user_id'];
			//echo 'UserID:'.$user_id;

			$schedules = [];
			$blockout_times = [];
						
			if ($stmt = $mysqli->prepare("SELECT ScheduleID, ScheduleString
				FROM schedules
				WHERE accountid = ?")) {
		
				$stmt->bind_param('s', $user_id);  // Bind "$username" to parameter.
				$stmt->execute();    // Execute the prepared query.
				$stmt->store_result();


				// get variables from result.
				$stmt->bind_result($schedule_id, $schedule_string);
				while ($stmt->fetch()){
					$schedule = [];
					$schedule = json_decode($schedule_string, true);
					$schedule['ScheduleID'] = $schedule_id;
					$schedules[] = $schedule;
				}
				$result['SCHEDULES'] = $schedules;
                                                              
				//print_r($result);
			} else {
				$error_messages[] = 'Missing fields';
			}
			
			if ($stmt = $mysqli->prepare("SELECT BlockOutID, BlockOutString
				FROM blockout_times
				WHERE accountid = ?")) {
		
				$stmt->bind_param('s', $user_id);  // Bind "$username" to parameter.
				$stmt->execute();    // Execute the prepared query.
				$stmt->store_result();


				// get variables from result.
				$stmt->bind_result($blockout_id, $blockout_string);
				while ($stmt->fetch()){
					$blockout = [];
					$blockout = json_decode($blockout_string, true);
					$blockout['BlockOutTimeID'] = $blockout_id;
					$blockout_times[] = $blockout;
				}
				$result['BLOCKOUTTIMES'] = $blockout_times;
                                                              
				//print_r($result);
			} else {
				$error_messages[] = 'Missing fields';
			}
			
		}
		else {
			$error_messages[] = 'No UserID found. Please login before attempting to retrieve saved info.';
		}
	}
	else {
		$error_messages[] = 'Not Logged In. Please login before attempting to retrieve saved info.';
	}

	if (empty($error_messages)){
		$result['SUCCESS'] = true;
	} else {
		$result['ERRORS'] = $error_messages;
		$result['SUCCESS'] = false;
	}

	echo json_encode($result);
?>
