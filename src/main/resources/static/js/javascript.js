
// const response = await fetch('http://localhost:8080');

// const json = await response.json();

// console.log(JSON.stringify(json));

function loginForm(){
     document.querySelectorAll(".popup")[0].style.display = "block";
     document.querySelectorAll(".form")[0].style.display = "block";
}
function closeLoginForm(){
    document.querySelectorAll(".popup")[0].style.display = "none";
     document.querySelectorAll(".form")[0].style.display = "none";

}
async function submitForm(){
    const form = document.getElementById("login-form");
    const form_data = new FormData(form);
    const data = Object.fromEntries(form_data);
    //console.log(form_data.get("txtUsername"));
    console.log(data);
    const response = 
    await fetch("http://localhost:8080/admin/form",
    {
        method : "POST" ,
        headers: {
            'Content-Type' : 'application/json'
        },
        //body: data
         body : JSON.stringify(data)
        } ).then(res => res.json()).then(data => console.log(data))
        .catch(error => console.log(error));
    
}