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
// at least 6 characters
//  at least one letter (A–Z or a–z)
//  at least one number (0–9)
//  at least one special character (@$!%*?&)
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
    fetch("http://localhost:8081/severletpage/auth/login", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded"
      },
      body: new URLSearchParams({
        //create URL-encoded key–value pairs.create URL-encoded key–value pairs.

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

