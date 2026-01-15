  function togglePassword() {
    const pwd = document.getElementById("password");
    pwd.type = pwd.type === "password" ? "text" : "password";
  }

  function login() {
    // Clear previous errors
    document.getElementById("usernameError").innerText = "";
    document.getElementById("passwordError").innerText = "";

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();

    let isValid = true;
const passwordPattern =
  /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/;

    // Username validation
    if (username === "") {
      document.getElementById("usernameError").innerText =
        "Username is required";
      isValid = false;
    }
    else if (username.length < 3) {
    document.getElementById("usernameError").innerText = "Username must be at least 3 characters maximum 20 characters";
    isValid = false;
    }
     

    // Password validation
    if (password === "") {
      document.getElementById("passwordError").innerText =
        "Password is required";
      isValid = false;
    } else if (password.length < 6) {
      document.getElementById("passwordError").innerText =
        "Password must be at least 6 characters";
      isValid = false;
    }
 else if (!passwordPattern.test(password)) {
  document.getElementById("passwordError").innerText =
    "Password must contain at least 1 letter, 1 number, 1 special character and be 6+ characters long";
  isValid = false;
}
    // Stop request if validation fails
    if (!isValid) return;

    //  Send request only if valid
    fetch("http://localhost:8081/severletpage/login", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded"
      },
      body: new URLSearchParams({
        username: username,
        password: password
      })
    })
    .then(res => res.json())
    .then(data => {
      if (data.success) {
        localStorage.setItem("auth", "true");
      showToast("Login successful!",3000);

        
       location.hash="home";
      } else {
        document.getElementById("passwordError").innerText =
          "Invalid username or password";

    showToast("Login failed!",1000);

      }
    })
    .catch(err => {
                showToast("Server error",3000);

      console.error(err);
    });
  }

// for validation 
//   function validateUsername() {
//   const usernameInput = document.getElementById("username");
//   const errorDiv = document.getElementById("usernameError");
//   const value = usernameInput.value.trim();
//   // Empty check
//   if (value === "") {
//     errorDiv.textContent = "Username is required";
//     return false;
//   }
//   // Minimum length
//   if (value.length < 3) {
//     error.textContent = "Username must be at least 3 characters and maximum 20 characters";
//     return false;
//   }
//  if (value.length>20) {
//     errorDiv.textContent = "maximum 20 characters allowed";
//     return false;
//   }
//   // Only letters & numbers
//   const regex = /^[a-zA-Z0-9_]+$/;
//   if (!regex.test(value)) {
//     errorDiv.textContent = "Only letters, numbers and _ allowed";
//     return false;
//   }

//   // Valid
//   errorDiv.textContent = "";
//   return true;
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
//     return;
//   }
 
// }
