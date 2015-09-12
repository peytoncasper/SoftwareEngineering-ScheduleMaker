<?php
	include_once 'db_connect.php';

	$error_messages = [];
	$result = [];


	//echo '<p>About to start registration</p>';
 
	if (isset($_POST['username'], $_POST['email'], $_POST['hashedpwd'])) {
		// Sanitize and validate the data passed in
		//echo '<p>about to sanitize.</p>';
		$username = filter_input(INPUT_POST, 'username', FILTER_SANITIZE_STRING);
		$email = filter_input(INPUT_POST, 'email', FILTER_SANITIZE_EMAIL);
		$email = filter_var($email, FILTER_VALIDATE_EMAIL);
		//echo '<p>Sanitation complete</p>';
		if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
			// Not a valid email
			$error_messages[] = 'The email address you entered is not valid';
		}

		$password = filter_input(INPUT_POST, 'hashedpwd', FILTER_SANITIZE_STRING);

		if (strlen($password) != 128) {
			// The hashed pwd should be 128 characters long.
			// If it's not, something really odd has happened
			$error_messages[] = 'Invalid password configuration.';
		}
 
		// Username validity and password validity have been checked client side.
		// This should should be adequate as nobody gains any advantage from
		// breaking these rules.
		//

		$prep_stmt = "SELECT id FROM members WHERE email = ? LIMIT 1";
		//echo '<p>About to fire query:'.$prep_stmt.'</p>';
		$stmt = $mysqli->prepare($prep_stmt);

		// check existing email  
		if ($stmt) {
			$stmt->bind_param('s', $email);
			$stmt->execute();
			$stmt->store_result();
 
			if ($stmt->num_rows == 1) {
				// A user with this email address already exists
				$error_messages[] = 'A user with this email address already exists.';
				$stmt->close();
			}
			$stmt->close();
		} else {
			$error_messages[] = 'Database error Line 39';
			$stmt->close();
		}

		$prep_stmt = "SELECT id FROM members WHERE username = ? LIMIT 1";
		//echo '<p>About to fire query:'.$prep_stmt.'</p>';
		$stmt = $mysqli->prepare($prep_stmt);

		// check existing username  
		if ($stmt) {
			$stmt->bind_param('s', $username);
			$stmt->execute();
			$stmt->store_result();
 
			if ($stmt->num_rows == 1) {
				// A user with this email address already exists
				$error_messages[] = 'This username is already taken.';
				$stmt->close();
			}
			$stmt->close();
		} else {
			$error_messages[] = 'Database error Line 39';
			$stmt->close();
		}
		// TODO: 
		// We'll also have to account for the situation where the user doesn't have
		// rights to do registration, by checking what type of user is attempting to
		// perform the operation.
 
		if (empty($error_messages)) {
			$result['SUCCESS'] = true;
 			// Create a random salt
			//$random_salt = hash('sha512', uniqid(openssl_random_pseudo_bytes(16), TRUE)); // Did not work old: mt_rand(1, mt_getrandmax()),
			$random_salt = hash('sha512', uniqid( openssl_random_pseudo_bytes(16), true));
 
			// Create salted password 
			$password = hash('sha512', $password . $random_salt);
 
			// Insert the new user into the database 
			if ($insert_stmt = $mysqli->prepare("INSERT INTO members (username, email, password, salt) VALUES (?, ?, ?, ?)")) {
				$insert_stmt->bind_param('ssss', $username, $email, $password, $random_salt);
				// Execute the prepared query.
				if (! $insert_stmt->execute()) {
					header('Location: ../error.php?err=Registration failure: INSERT');
				}
			}
			
			
		}
		else {
			$result['ERRORS'] = $error_messages;
			$result['SUCCESS'] = false;
		}
	
		echo json_encode($result);


	}
?>
