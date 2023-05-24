function createForm(){
    //document.querySelectorAll(".overlay")[0].style.display = "block";
    document.querySelectorAll(".form")[0].style.display = "flex";
}
function closeCreateForm(){
   //document.querySelectorAll(".overlay")[0].style.display = "none";
    document.querySelectorAll(".form")[0].style.display = "none";

}

const form =  document.getElementById("create-form");
form.addEventListener('submit',async function(event){
    console.log(form)
    event.preventDefault();
    const form_data = new FormData(form);
   const data = Object.fromEntries(form_data);
   
   console.log(data);
   const response = await fetch("http://localhost:8080/UserRestController/createAccountCustomer/0",
    {    
        method : "POST" ,
        headers: {
            'Content-Type' : 'application/json'
        },
        body : JSON.stringify(data)
       } );
       

       console.log(response.status)
       if(response.status == 400){
            error_message= await response.json()
            var list = document.getElementsByClassName("error-message");
            var i = 0;
            for(var key in error_message){
                if(error_message[key] !== ""){
                    list[i].innerHTML = error_message[key];      
                    list[i].style.visibility = "visible";      
                    i+=1;
                }else{
                    list[i].innerHTML = "appropriate input";
                    list[i].style.visibility = "hidden";
                    i+=1;    
                }
                      
            }
            
       }else if(response.status == 200){
            error_message= await response.json()
            var list = document.getElementsByClassName("error-message");
            var i = 0;
            for(var key in error_message){
                if(error_message[key] == ""){
                    list[i].innerHTML = "appropriate input";      
                    list[i].style.visibility = "hidden";      
                    i+=1;
                }
            }
            console.log('yes successs')
            window.location.href = "/";
       }else if(response.status == 500){
            error_message= await response.json()
            var list = document.getElementsByClassName("error-message");
            var i = 0;
            for(var key in error_message){
                if(error_message[key] == ""){
                    list[i].innerHTML = "appropriate input";      
                    list[i].style.visibility = "hidden";      
                    i+=1;
                }
            }
            alert("something wrong with server side, please try again later");
       }
}
)