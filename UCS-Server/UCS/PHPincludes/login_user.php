<?php
	include_once 'db_connect.php';
	include_once 'functions.php';

	sec_session_start();

	$error_messages = []; 
	$result = [];


	//echo '<p>About to start login process</p>';

	//print_r($_POST);
 
	if (isset($_POST['username'], $_POST['hashedpwd'])) {
		// Sanitize and validate the data passed in
		//echo '<p>About to sanitize.</p>';
		$username = filter_input(INPUT_POST, 'username', FILTER_SANITIZE_STRING);
		$password = filter_input(INPUT_POST, 'hashedpwd', FILTER_SANITIZE_STRING);
		//echo '<p>Sanitation complete</p>';
    		if ($stmt = $mysqli->prepare("SELECT id, username, password, salt 
		FROM members
		WHERE username = ?
		LIMIT 1")) {
			$stmt->bind_param('s', $username);  // Bind "$username" to parameter.
			$stmt->execute();    // Execute the prepared query.
			$stmt->store_result();

			// get variables from result.
			$stmt->bind_result($user_id, $username, $db_password, $salt);
			$stmt->fetch();

			//echo '<p> User ID:'.$user_id.'</p>'.'<p> Username:'.$username.'</p>'.'<p> DB Pass:'.$db_password.'</p>'.'<p> SALT:'.$salt.'</p>';

			// hash the password with the unique salt.
			$password = hash('sha512', $password . $salt);
			//echo '<p>Hash result:'.$password.'</p>';
			if ($stmt->num_rows == 1) {
				// If the user exists we check if the account is locked
				// from too many login attempts 

				if (checkbrute($user_id, $mysqli) == true) {
					// Account is locked 
					// Send an email to user saying their account is locked
					$error_messages[] = 'Too many bad login attempts.';
				} else {
					// Check if the password in the database matches
					// the password the user submitted.
					if ($db_password == $password) {
						// Password is correct!
						// Get the user-agent string of the user.
						$user_browser = $_SERVER['HTTP_USER_AGENT'];
						// XSS protection as we might print this value
						$user_id = preg_replace("/[^0-9]+/", "", $user_id);
						$_SESSION['user_id'] = $user_id;
						// XSS protection as we might print this value
						$username = preg_replace("/[^a-zA-Z0-9_\-]+/", 
	                                                        "", 
	                                                        $username);
						$_SESSION['username'] = $username;
						$_SESSION['login_string'] = hash('sha512', 
						$password . $user_browser);
						// Login successful.

					} else {
	            				// Password is not correct
	            				// We record this attempt in the database
	            				$now = time();
	            				$mysqli->query("INSERT INTO login_attempts(user_id, time)
	                            			VALUES ('$user_id', '$now')");
	            				$error_messages[] = 'Username or password is incorrect';
	        			}
    				}
			} else {
	    			// No user exists.
	    			$error_messages[] = 'Username or password is incorrect';
			}
    		}
	} else {
		$error_messages[] = 'Missing fields';
	}

	if (empty($error_messages)){
		$result['SUCCESS'] = true;
	} else {
		$result['ERRORS'] = $error_messages;
		$result['SUCCESS'] = false;
	}

	echo json_encode($result);
	//print_r($_SESSION);
?>
