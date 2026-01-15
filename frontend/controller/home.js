
let CURRENT_USER_ID = null;

/*  it will be called when the this going to render */
function initHome() {
  console.log(" home apppered");

  // when the auth is not their then move to login
  if (!localStorage.getItem("auth")) {
    location.hash = "#login";
    return;
  }

  // then try to run the load me to get the user details

  loadMe()
    .then(loadPosts)
    .catch(() => {
      localStorage.removeItem("auth");
      location.hash = "#login";
    });
}

/*  AUTH  */

function logout() {
  fetch("http://localhost:8081/severletpage/logout", {
    credentials: "include"
  }).finally(() => {
    // if we get the pos or neg it will run and logout
    localStorage.removeItem("auth");
         showToast("Logged out successfully!",3000);
    location.hash = "#login";
  });
}//
// this is for getting the user id
async function loadMe() {
  const res = await fetch("http://localhost:8081/severletpage/me",
    { 
      credentials: "include" 

    }
  );

// if the user did not have access then 
  if (res.status === 401) throw "unauthorized";
  

  const data = await res.json();
  CURRENT_USER_ID = data.userId;

  // here if it is nothing returning also it will considerd as promise resolved
}
// 
/*  POSTS  */

async function loadPosts() {
  const container = document.getElementById("posts");
  // console.log(container);
  if (!container) return;//it checks first element  is present or not if not nulll

  const res = await fetch( "http://localhost:8081/severletpage/posts",
    { 
      credentials: "include"
     }
  );

  const posts = await res.json();
  container.innerHTML = "";



  posts.forEach(p => {
    const isOwner = p.user_id === CURRENT_USER_ID;

    container.innerHTML += `
      <div class="post">
        <b>${p.username}</b>
        <img src="http://localhost:8081/severletpage/${p.image_path}">
        <p id="caption-${p.id}">${p.caption}</p>

        <div class="post-actions">
          ${isOwner ? `

            <i class="fa-solid fa-regular fa-pencil" onclick="enableEditPost(${p.id})"></i>
            <i class="fa-regular fa-trash-can" onclick="confirmDeletePost(${p.id})"></i>
          ` : ``}
        </div>


        <div class="comment-box">
          <input id="comment-input-${p.id}" placeholder="Write comment">
          <button onclick="submitComment(${p.id})">Post</button>
        </div>
          <div id="comments-${p.id}"
          class="comments-container"></div>

      </div>
    `;
    loadComments(p.id);
  });


}

// for have comfirmation check before deleting the post
async function confirmDeletePost(postId) {
  console.log( "confirm delete post:", postId);
  // const ok = confirm("Are you sure you want to delete this post?");
    const ok = await customConfirm("Are you sure you want to delete this post?");

  if (ok) {//if true deletion will taking place
    deletePost(postId);
  }
}
//for comments also
async function confirmDeleteComment(commentId, postId) {
      const ok = await customConfirm("Are you sure you want to delete this comment?");

  if (ok) {//true means  delete it 
        // const ok = await customConfirm("Are you sure you want to delete this post?");
console.log("deleting comment:", commentId);
    deleteComment(commentId, postId);
  }
}



// opening and closeing  model
function openPostModal() {
  document.getElementById("postModal").style.display = "flex";
}

function closePostModal(event) {
  if (!event || event.target.id === "postModal") {
    document.getElementById("postModal").style.display = "none";
  }
}







// creating the post 
async function createPost() {
  const formData = new FormData();
  formData.append("image", document.getElementById("image").files[0] //selected file
  );
  formData.append("caption", document.getElementById("caption").value);

  const res = await fetch("http://localhost:8081/severletpage/createpost", {
    method: "POST",
    //  multipart/form-data browser sets it automically  as here along with boundary need to be specify which states
    // start and binary data ends and where the text begin we need
    //to understand it also//this work did by browser automically
    credentials: "include",//hold the cookies for session id
    body: formData
  });

  const data = await res.json();
  
  if (data.success) {
        showToast("Post created successfully!",3000);
  }
  else{
        showToast("Failed to create post",3000);  
  }
     closePostModal();
       loadPosts();

}//

function deletePost(id) {
  // note fetch also a promise 
  fetch(`http://localhost:8081/severletpage/delete-post?id=${id}`, {
    method: "DELETE",
    credentials: "include"
  })
.then(res => {
  // fetch promise resolved because server responded
  if (!res.ok) {//res.ok===true means the request was successful 
    // it checks for status code 200-299
    // if not  false other status  means any other issues
    // throw a error to make the promise fail and got to catch
    throw new Error("Failed to delete post");
  }
 
})
.then(()=>{
  showToast("Post deleted successfully!", 3000);
    loadPosts();
}
)
.catch(err => {
  // it will be called when the promise is rejected
  //err it contails error messsage, errorstack-->  where does error has happened
      console.error(err);
      showToast("post was not deleted",1000);

});



}//


function enableEditPost(postId) {
  const el = document.getElementById(`caption-${postId}`);
  const old = el.innerText;
  el.innerHTML = `
    <input id="edit-post-${postId}" value="${old}">
    <button onclick="savePost(${postId})">Save</button>
  `;
}

function savePost(postId) {
  const caption = document.getElementById(`edit-post-${postId}`).value;

  fetch("http://localhost:8081/severletpage/edit-post", {
    method: "POST",
    credentials: "include",
    headers: {"Content-Type": "application/x-www-form-urlencoded"},
    body: `postId=${postId}&caption=${encodeURIComponent(caption)}`
  }).then(loadPosts);
}

/*  comments   */
function submitComment(postId) {
  const input = document.getElementById(`comment-input-${postId}`);
  const text = input.value.trim();
  if (!text) return;

  fetch("http://localhost:8081/severletpage/add-comment", {
    method: "POST",
    credentials: "include",
    headers: {"Content-Type": "application/x-www-form-urlencoded"},
    body: `postId=${postId}&comment=${encodeURIComponent(text)}`
  // if the server is responed then the promise will is resolve and .then() will run
    //note for fetching it does not care about the http status code

})
.then(res => {
  // fetch promise resolved because server responded
  if (!res.ok) {//res.ok===true means the request was successful 
    // it checks for status code 200-299
    // if not  false other status  means any other issues
    // throw a error to make the promise fail and got to catch
    throw new Error("Failed to add comment");
  }
})
.then(() => loadComments(postId))
.catch(err => {
  //err it contails error messsage, errorstack-->  where does error has happened
      console.error(err);
      showToast("Comment not added",1000);

});


  input.value = "";
}//

async function loadComments(postId) {
  const res = await fetch(
    `http://localhost:8081/severletpage/comments?postId=${postId}`,
    { credentials: "include" }
  );

  const comments = await res.json();
  const div = document.getElementById(`comments-${postId}`);
  div.innerHTML = "";

  comments.forEach(c => {
    const isOwner = c.user_id === CURRENT_USER_ID;

    div.innerHTML += `
      <div class="comment" id="comment-${c.id}">
        • ${c.username}:
        <span id="comment-text-${c.id}">${c.comment}</span>

        ${isOwner ? `
          <button onclick="editComment(${c.id}, '${c.comment}')">Edit</button>
          <button onclick="confirmDeleteComment(${c.id}, ${postId})">Delete</button>
        ` : ``}
      </div>
    
`;

  });
}//

function editComment(id, text) {
  const el = document.getElementById(`comment-${id}`);
  el.innerHTML = `
    <input id="edit-comment-${id}" value="${text}">
    <button onclick="saveComment(${id})">Save</button>
  `;
}//

function saveComment(id) {
  const text = document.getElementById(`edit-comment-${id}`).value;

  fetch("http://localhost:8081/severletpage/edit-comment", {
    method: "POST",
    credentials: "include",
    headers: {"Content-Type": "application/x-www-form-urlencoded"},
    body: `commentId=${id}&comment=${encodeURIComponent(text)}`
    // encodeURIComponent is used to encode special characters in the comment text because 
    // the  url can a different meaning
    // like ? which act as start of query parameters
    // &separates multiple parameters and = to assing value
  }).then(res => {
  // fetch promise resolved because server responded
  if (!res.ok) {//res.ok===true means the request was successful 
    // it checks for status code 200-299
    // if not  false other status  means any other issues
    // throw a error to make the promise fail and got to catch
    throw new Error("Failed to save comment");
  }
  // if nothing send then it will considred as undefined as value
})
.then(()=>{
  //then undefined value will be hold
   showToast("comment saved successfully!", 3000);
    loadPosts();
  }
)
.catch(err => {
  // it will be called when the promise is rejected
  //err it contails error messsage, errorstack-->  where does error has happened
      console.error(err);
      showToast("comment was not saved",1000);

});

  

}//

function deleteComment(id, postId) {
  fetch(`http://localhost:8081/severletpage/delete-comment?commentId=${id}`, {
    method: "DELETE",
    credentials: "include"
  })
.then(res => {
  // fetch promise resolved because server responded
  if (!res.ok) {//res.ok===true means the request was successful 
    // it checks for status code 200-299
    // if not  false other status  means any other issues
    // throw a error to make the promise fail and got to catch
    throw new Error("Failed to delete comment");
  }
  // if nothing send then it will considred as undefined as value
})
.then(()=>{
  //then undefined value will be hold
   showToast("comment deleted successfully!", 3000);
   loadComments(postId)}
)
.catch(err => {
  // it will be called when the promise is rejected
  //err it contails error messsage, errorstack-->  where does error has happened
      console.error(err);
      showToast("comment was not deleted",1000);

});







}//

// extracting the file name
// if any form element changes, this listener will run.
// like a input field 
document.addEventListener("change", function (event) {
  console.log("Change detected:", event.target);
  if (event.target && event.target.id === "image") {
    const fileNameSpan = document.getElementById("fileName");
//by getting the file name from the file input
    if (event.target.files.length > 0) {
      fileNameSpan.textContent = event.target.files[0].name;//here we will get the file name
    } else {
      fileNameSpan.textContent = "No file selected";
    }
  }
});//
