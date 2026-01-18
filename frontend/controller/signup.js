function signup() {

  // Clear old errors
  document.getElementById("usernameError").innerText = "";
  document.getElementById("emailError").innerText = "";
  document.getElementById("passwordError").innerText = "";

  const username = document.getElementById("username").value.trim();
  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value.trim();

  let isValid = true;

  // Username validation
  if (username === "") {
    document.getElementById("usernameError").innerText = "Username is required";
    isValid = false;
  } else if (username.length < 3) {
    document.getElementById("usernameError").innerText = "Username must be at least 3 characters maximum 20 characters";
    isValid = false;
  }
  // Email validation
  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  // here the characters before @ should not have spaces
  // after @ should not have spaces
  // 2 @ is not allowed
  //domain is required after @
  // then . then com or org or in

  if (email === "") {
    document.getElementById("emailError").innerText = "Email is required";
    isValid = false;
  } else if (!emailPattern.test(email)) {
    document.getElementById("emailError").innerText = "Enter a valid email";
    isValid = false;
  }

  /// Password validation
const passwordPattern =
  /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/;

if (password === "") {
  document.getElementById("passwordError").innerText =
    "Password is required";
  isValid = false;
}
else if (!passwordPattern.test(password)) {
//  uppercase?
//  lowercase?
//number?
//special char?
// length >= 8?
// if any one fails then it will be rejected
  document.getElementById("passwordError").innerText =
    "Password must contain at least 1 letter, 1 number, 1 special character and be 6+ characters long";
  isValid = false;
}


  // Stop if validation fails
  if (!isValid) return;

  // Send request only if valid
  fetch("http://localhost:8081/severletpage/register", {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded"
      // Form data is sent as key=value pairs joined by &, and special characters 
      // are URL-encoded. with some ther character
    },
    body: new URLSearchParams({
      username: username,
      email: email,
      password: password
    })
  })
  .then(res => res.json())
  .then(data => {
    if (data.success) {
      // alert("Signup successful");
      showToast("Signup successful! Please login.",3000);

   
      location.hash="login";

    } else {
            showToast("Signup failed",3000);

    }
  })
  .catch(err => {
    alert("Server error");
    console.error(err);
  });
}
function togglePassword() {
  const pwd = document.getElementById("password");
  pwd.type = pwd.type === "password" ? "text" : "password";
}



// on typing it self we are validationg 
// function validateUsername() {
//   // const usernameInput = document.getElementById("username");
//   const errorDiv = document.getElementById("usernameError");
//   // errorDiv.textContent = "";

// //   const value = usernameInput.value.trim();
// //   // Empty check
// //   if (value === "") {
// //     errorDiv.textContent = "Username is required";
// //     return false;
// //   }

// //   // Minimum length
// //   if (value.length < 3) {
// //     errorDiv.textContent = "Username must be at least 3 characters maximum 20 characters ";
// //     return false;
// //   }
// //  if (value.length >20) {
// //     errorDiv.textContent = "maximum 20 characters allowed";
// //     return false;
// //   }
// //   // Only letters & numbers
// //   const regex = /^[a-zA-Z0-9_]+$/;
// //   if (!regex.test(value)) {
// //     errorDiv.textContent = "Only letters, numbers and _ allowed";
// //     return false;
// //   }
// //   // Valid
// //   errorDiv.textContent = "";
// //   return true;
// }



// function validatePassword() {
//   const input = document.getElementById("password");
//   const error = document.getElementById("passwordError");
//  if (input.value.length <= 16) {
//    console.log("valid only");
//     input.value = input.value.slice(0, 16);
//     error.textContent = "";
//     return;
//   }
//   else if (input.value.length > 16) {
//     input.value = input.value.slice(0, 16);
//     error.textContent = "Maximum 16 characters allowed";
//     return false;
//   }
 
// }

