<?php

/*	This page syncs user data to the database
 *
 */
	include_once 'db_connect.php';
	include_once 'functions.php';

	sec_session_start();

	$error_messages = []; 
	$success_messages = [];
	$result = [];

	if (login_check($mysqli) == true){

		$post_data = json_decode(file_get_contents('php://input'), true);

		//echo json_encode($post_data);
		if(isset($_SESSION['user_id'])){
			foreach ($post_data['SCHEDULES'] as $schedule_data) {
				//echo '<p>'.json_encode($schedule_data).'</p>'; 
				if(isset($schedule_data['ScheduleID'])){
					//Schedule already has a scheduleID, which indicates it already exists in the database and should be updated rather than added as a new schedule
					$ScheduleID = $schedule_data['ScheduleID'];
					unset($schedule_data['ScheduleID']);
					//echo '<p>'.json_encode($schedule_data).'<p>';
					if ($stmt = $mysqli->prepare("UPDATE schedules SET schedules.ScheduleString = ? 
						WHERE schedules.ScheduleID = ?
						LIMIT 1")) {
							$stmt->bind_param('ss', json_encode($schedule_data), $ScheduleID);  // Bind to parameters.
							$stmt->execute();    // Execute the prepared query.
							$success_messages[] = 'updated schedule with scheduleID of:'.$ScheduleID;                                                                                                                                                                               
					}
				} else {
					if ($stmt = $mysqli->prepare("INSERT INTO schedules (AccountID, ScheduleString) VALUES (?, ?)")) {
							$stmt->bind_param('ss', $_SESSION['user_id'], json_encode($schedule_data));  // Bind to parameters.
							$stmt->execute();    // Execute the prepared query.
					}
				}
			}

			foreach ($post_data['BLOCKOUTTIMES'] as $blockout_data) {
				if(isset($blockout_data['BlockOutTimeID'])){
					$BlockOutTimeID = $blockout_data['BlockOutTimeID'];
					unset($blockout_data['BlockOutTimeID']);
					if ($stmt = $mysqli->prepare("UPDATE blockout_times SET blockout_times.BlockOutString = ? 
						WHERE blockout_times.BlockOutID = ?
						LIMIT 1")) {
							$stmt->bind_param('ss', json_encode($blockout_data), $BlockOutTimeID);  // Bind to parameters.
							$stmt->execute();    // Execute the prepared query.
					}
				} else {
					if ($stmt = $mysqli->prepare("INSERT INTO blockout_times (AccountID, BlockOutString) VALUES (?, ?)")) {
							$stmt->bind_param('ss', $_SESSION['user_id'], json_encode($blockout_data));  // Bind to parameters.
							$stmt->execute();    // Execute the prepared query.
					}
				}
			}

			if(isset($post_data['MilitaryTime'])){

				if ($stmt = $mysqli->prepare("INSERT INTO member_settings
								 (AccountID, MilitaryTime)
								 VALUES (?, ?)
								 ON DUPLICATE KEY
								 UPDATE
								 MilitaryTime = VALUES(MilitaryTime)")) {
						$stmt->bind_param('ss', $_SESSION['user_id'], $post_data['MilitaryTime']);  // Bind to parameters.
						$stmt->execute();    // Execute the prepared query.
				}
			}

		} else {
			$error_messages[] = 'No UserID found. Please login before attempting to retrieve saved info.';
		}
	} else {
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
