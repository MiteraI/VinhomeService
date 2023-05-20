function createForm(){
    document.querySelectorAll(".overlay")[0].style.display = "block";
    document.querySelectorAll(".form")[0].style.display = "block";
}
function closeCreateForm(){
   document.querySelectorAll(".overlay")[0].style.display = "none";
    document.querySelectorAll(".form")[0].style.display = "none";

}
async function submitForm(){
   const form = document.getElementById("create-form");
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
        body : JSON.stringify(data)
       } ).then(res => res.json()).then(data => console.log(data))
       .catch(error => console.log(error));
   
}