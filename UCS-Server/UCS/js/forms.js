function formhash(form, password) {
 
    // Create a new element input, this will be our hashed password field. 
    var hashedpwd = document.createElement("input");
 
    // Add the new element to our form.
    form.appendChild(hashedpwd);
    hashedpwd.name = "hashedpwd";
    hashedpwd.type = "hidden";
    hashedpwd.value = hex_sha512(password.value);
 
    // Make sure the plaintext password doesn't get sent. 
    password.value = "";

    // Finally submit the form. 
    form.submit();
}
 
function regformhash(form, uid, email, password, conf) {
     // Check each field has a value
    if (uid.value == ''         || 
          email.value == ''     || 
          password.value == ''  || 
          conf.value == '') {
 
        alert('You must provide all the requested details. Please try again');
        return false;
    }
 
    // Check the username
 
    re = /^\w+$/; 
    if(!re.test(form.username.value)) { 
        alert("Username must contain only letters, numbers and underscores. Please try again"); 
        form.username.focus();
        return false; 
    }
 
    // Check password and confirmation are the same
    if (password.value != conf.value) {
        alert('Your password and confirmation do not match. Please try again');
        form.password.focus();
        return false;
    }
 
    // Create a new element input, this will be our hashed password field. 
    var hashedpwd = document.createElement("input");
 
    // Add the new element to our form.
    form.appendChild(hashedpwd);
    hashedpwd.name = "hashedpwd";
    hashedpwd.type = "hidden";
    hashedpwd.value = hex_sha512(password.value);
 
    // Make sure the plaintext password doesn't get sent. 
    password.value = "";
    conf.value = "";
 
    // Finally submit the form. 
    form.submit();
    return true;
}
