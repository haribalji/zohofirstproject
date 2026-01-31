function signup() {
  // Clear old errors
  document.getElementById("usernameError").innerText = "";
  document.getElementById("emailError").innerText = "";
  document.getElementById("passwordError").innerText = "";
  const image=document.getElementById("profileImage").files[0];
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

 if (!image) {
    document.getElementById("imageError").innerText = "Image is required";
         isValid = false;
  }
  // Stop if validation fails
  if (!isValid) return;
const formData = new FormData();
if (document.getElementById("profileImage").files.length > 0) {
  formData.append("image", document.getElementById("profileImage").files[0]);
}
else{
  showToast("image requried",2000);
  return;
}
formData.append("username",username);
formData.append("email",email);
formData.append("password",password);


  // Send request only if valid
  fetch("http://localhost:8081/severletpage/auth/register", {
    method: "POST",
    credentials: "include",
        body: formData
  })
  .then(res => res.json())
  .then(data => {
    if (data.success) {
      showToast("Signup successful! Please login.",3000);

   
      location.hash="#login";

    } else {
            showToast(data.message,3000);

    }
  })
  .catch(err => {
    alert("Server error",err.message);
    console.error(err);
  });
}
function togglePassword() {
  const pwd = document.getElementById("password");
  pwd.type = pwd.type === "password" ? "text" : "password";
}


document.addEventListener("change", function (event) {
  console.log("Change detected:", event.target);
  if (event.target && event.target.id === "profileImage") {
    const fileNameSpan = document.getElementById("fileName");
//by getting the file name from the file input
    if (event.target.files.length > 0) {
      fileNameSpan.textContent = event.target.files[0].name;//here we will get the file name
    } else {
      fileNameSpan.textContent = "No file selected";
    }
  }
});//
