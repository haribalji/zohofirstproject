
const app = document.getElementById("app");
//  track hash 
let currentHash = window.location.hash;

//  router
async function loadPage() {
  let page = location.hash.replace("#", "") || "login";

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

  try {
    const res = await fetch(`pages/${page}.html`);
    const html = await res.text();
        // console.log(" home page");

    app.innerHTML = html;
      
    if (page === "home") {
        console.log("home page");
      
  //  it willl be at the top ,below this home.js code will come
      if (page === "home") initHome();//stater will called from here
// the delay in calling is get the elements to render first
    }

  } catch(err) {
    console.log("error",err);
    app.innerHTML = "<h2>404 Page Not Found</h2>";
  }
}

//  Detect back / forward (your logic)
setInterval(() => {
  if (window.location.hash !== currentHash) {
    currentHash = window.location.hash;
    // alert("Navigation detected: " + currentHash);
    console.log("Navigation detected:", currentHash);
    loadPage();
  }
}, 100);

// to show the message to user
function showToast(message, duration = 3000) {
  // 3000 means 3 sec  
  const toast = document.getElementById("toast");
  toast.textContent = message;
  toast.classList.add("show");

  setTimeout(() => {
    toast.classList.remove("show");
  }, duration);//after duration it will remove the toast message
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





//initial load taking place the  page is fully loaded
window.addEventListener("load", loadPage);
