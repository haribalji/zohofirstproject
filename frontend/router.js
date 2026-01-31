
const app = document.getElementById("app");
//  track hash 
let currentHash = window.location.hash;

//  router
async function loadPage() {
  let page = location.hash.replace("#", "") || "login";
    console.log("i am going to home page",page);

  //  Auth 
  if (!localStorage.getItem("auth") && page === "home") {
    location.hash = "#login";
    return;
  }

  if (localStorage.getItem("auth") && (page === "login" || page === "signup")) {
    console.log("Already logged in, redirecting to home");

    location.hash = "#home";

    return;
  }
//now just fetching the page
  try {
    const res = await fetch(`pages/${page}.html`);
    const html = await res.text();

    app.innerHTML = html;
      
    if (page === "home") {
        console.log("home page");
      
 
         initHome();//stater will called from here
// the delay in calling is get the elements to render first
 
}

 else if (page === "signup") {
  // wait until DOM is painted
  // setTimeout(renderGoogleSignupButton, 300);

setTimeout(() => {
    if (window.google && google.accounts && google.accounts.id) {

      // Initialize (safe to call multiple times)
      google.accounts.id.initialize({
        client_id: "723475941274-cqqj09stb0b4gsn4mehlr6128mi4b02o.apps.googleusercontent.com",
        callback: handleGoogleLogin
      });

      // Render button manually
      google.accounts.id.renderButton(
        document.querySelector(".g_id_signin"),
        {
          theme: "outline",
          size: "large",
          text: "continue_with",
          shape: "rectangular"
        }
      );
    } else {
      console.error("Google Identity Services not loaded yet");
    }
  }, 100); // wait for DOM paint

}
else if (page === "login") {
  setTimeout(() => {

if (window.google && google.accounts && google.accounts.id) {

      // Initialize (safe to call multiple times)
      google.accounts.id.initialize({
        client_id: "723475941274-cqqj09stb0b4gsn4mehlr6128mi4b02o.apps.googleusercontent.com",
        callback: handleGoogleLogin
      });

      // Render button manually
      google.accounts.id.renderButton(
        document.querySelector(".g_id_signin"),
        {
          theme: "outline",
          size: "large",
          text: "continue_with",
          shape: "rectangular"
        }
      );
    } else {
      console.error("Google Identity Services not loaded yet");
    }
  }, 100);
}
  } catch(err) {
    console.log("error",err);
    app.innerHTML = "<h2>404 Page Not Found</h2>";
  }
}



//  Detect back / forward (your logic)
// it run again and again to check the hash value
setInterval(() => {
  if (window.location.hash !== currentHash) {
    currentHash = window.location.hash;

    loadPage();
  }
}, 100);
// if we want to stop this interval then use clearinterval(id); as this 
// we can get it from setinterval method

// to show the message to user
function showToast(message, duration = 3000) {
  // 3000 means 3 sec  
  const toast = document.getElementById("toast");
  toast.textContent = message;
  toast.classList.add("show");

  setTimeout(() => {
    toast.classList.remove("show");
  }, duration);
  //after duration it will remove the toast message
// if we want to stop this timeout then use clearTimeout(id); as this 
// we can get it from setTimeout method

}

//for asking yes or no from user
function customConfirm(message) {
  console.log("custom confirm called");
//   we call resolve(value) when we want
// the value we return becomes the result of the Promise
  return new Promise((resolve) => {
    const overlay = document.getElementById("confirmOverlay");
    // this for styling we are getting
    const msg = document.getElementById("confirmMessage");
    const yesBtn = document.getElementById("confirmYes");
    const noBtn = document.getElementById("confirmNo");

    msg.textContent = message;
    overlay.style.display = "flex";
//makes modal to visible
//dark background appears
//dialog box appears

// yes the user want to delete  it
    yesBtn.onclick = () => {
      overlay.style.display = "none";
      resolve(true);
      // this true or false will be return to parent method
    };
// no the user want to delete  it

    noBtn.onclick = () => {
      overlay.style.display = "none";
      resolve(false);
    };
  });
}



function handleGoogleLogin(response) {
  const idToken = response.credential;
   fetch("http://localhost:8081/severletpage/google-login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ idToken })
  })
  .then(res => res.json())
  .then(data => {
    if (data.success) {
      showToast(data.message, 3000);
      localStorage.setItem("auth", "true");
      location.hash = "#home";
    }
    else{
         showToast(data.message, 3000);

    }
  });
}

function checkGoogleLoginRedirect() {
  const url = new URL(window.location.href);
  const hash = window.location.hash;

  // If backend comeback to index.html?authu=true
  if (url.searchParams.get("authu")==="true") {
    
    // set auth
    localStorage.setItem("auth", "true");

    // showToast("Google Login Successful!", 2000);

// it is used to replace the the browser history of current entries 
// without  reloading the page
    history.replaceState(null, "", "index.html#home");
// we used null to say that we don't need any extra data to store in
// order to store in history,""-->current 
// index.htmlauth=true to   index.html#home
// in null there will be a stateobject used to support the back behaviour
        // ""->current tab title ,not changing keeping it as it
    // load home page
    console.log("i am going to home page");
    // loadPage();
  }
  else if((url.searchParams.get("authu")==="false")){
    showToast("Google login is cancel",2000);
        history.replaceState(null, "", "index.html#login");

  }
}




//initial load taking place the  page is fully loaded
// window.addEventListener("load", loadPage);

window.addEventListener("load", () => {
  checkGoogleLoginRedirect(); 
  loadPage();
});

