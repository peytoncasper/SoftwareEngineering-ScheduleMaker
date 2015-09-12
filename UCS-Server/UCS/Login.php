<?php
/*	Simple form page to test Login functionality and ensure data can only be fetched when a user is logged in
 *
 */
	include_once 'PHPincludes/functions.php';

	sec_session_start();
?>
<html>
	
	<head>
		<title>Simple Login Page</title>
		<script type="text/JavaScript" src="js/sha512.js"></script> 
		<script type="text/JavaScript" src="js/forms.js"></script> 
	</head>
	<body>
		<form action="PHPincludes/login_user.php" method="post">
			<p>Username: <input type="text" name="username" /></p>
			<p>Password: <input type="password" name="password" /></p>
			<input type="button" 
				value="Login" 
				onclick="formhash(this.form, this.form.password);" />
		</form>
	</body>
</html>
