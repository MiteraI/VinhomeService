var data ;
 
// fecth a get
async function deleteWorker(id){
    console.log(id)
    const response = await fetch(`http://localhost:8080/UserRestController/${id}`,{    
        method : "DELETE" ,
        headers: {
            'Content-Type' : 'application/json'
        },
        body : JSON.stringify(data)
        }
       );
            console.log("yes call api success DELETE")
            console.log(response)
            if(response.status == 500){
                alert("delete fail, might be server side");
                return;
            }else{
                data =await response.json();
                printRow(data);
            }
}



 async function getWorkerList(){
    console.log("inside get worker")
    const response = await fetch("http://localhost:8080/UserRestController/getAccount")
     data =await response.json();
     printRow(data);
 }  

function printRow(data){
    var table_tag = document.getElementById("worker-list");
    table_tag.innerHTML = ""
    console.log(data)
    var output = "";
    var rolestring;var statusstring;
    for(var worker in data){
        roleString = ( data[worker].role == 1) ? "worker" : "user";
        statusString = (data[worker].accountStatus == 1) ? "active" : "off ";
        var date_new; var date_output;
        if(data[worker].dob == null){
            date_output = "not yet imported"
        }else{
            date_new = new Date(data[worker].dob);console.log(date_new)
            date_output = new Intl.DateTimeFormat('en-US').format(date_new);
        }
         
        output = `
        <tr id="${data[worker].accountId}">
                <td><input type="hidden" name="txtID" value="${data[worker].accountId}">${data[worker].accountId}</td>
                <input type="hidden" name="txtUsername" value="${data[worker].accountName}">
                <input type="hidden" name="txtPassword" value="${data[worker].password}">
                <td><input type="hidden" name="txtEmail"value="${data[worker].email}">${data[worker].email}</td>
                <td><input type="hidden" name="txtFirstname"value="${data[worker].firstName}">${data[worker].firstName}</td>
                <td><input type="hidden" name="txtLastname"value="${data[worker].lastName}">${data[worker].lastName}</td>
                <td><input type="hidden" name="txtDob"value="${data[worker].dob}">${date_output}</td>
                <td><input type="hidden" name="txtStatus"value="${data[worker].accountStatus}">${statusString}</td>
                <td><input type="hidden" name="txtRole"value="${data[worker].role}">${roleString}</td>
                <td><button onclick='deleteWorker(${data[worker].accountId})'>DELETE</button></td>
                <td><button onclick='updateWorker(${data[worker].accountId})'>UPDATE</button></td>        
        </tr>
        `;
        table_tag.innerHTML += output;
    }
}


async function updateWorker(id){
    console.log(id);
    const table_row = document.getElementById(`${id}`);
    const table_data = table_row.getElementsByTagName("td");
    const length = table_data.length-2; // -2 cot cuoi la 2 cai button, ko lay du lieu dc
    console.log(table_data)
    var name_list= []; var value_list=[] ;
    const map = new Map();
    for(var i = 0 ; i < length ; i++ ){
        name_list.push(table_row.getElementsByTagName("td")[i].getElementsByTagName("input")[0].name) ;
        value_list.push(table_row.getElementsByTagName("td")[i].getElementsByTagName("input")[0].value);
        map.set(name_list[i],value_list[i])
    }
    const json = Object.fromEntries(map);
    console.log(json)
    const response = await fetch(`http://localhost:8080/UserRestController/updateWorkerAccount`,{    
        method : "PUT" ,
        headers: {
            'Content-Type' : 'application/json'
        },
        body : JSON.stringify(json)
       });
    console.log("yes call api success UPDATE")
    
    if(response.status == 400){
        alert("the update must not be empty and name must not have number, try again");
        // if bad request and null body 
        return;
    }else {
        data =await response.json();
    }
    printRow(data);
}

 function sortById(htmlstuff){
    var ascOrNot = htmlstuff.className;
    if(ascOrNot == "asc"){
        htmlstuff.className = "desc"
        data = data.sort((a,b) => { return a.accountId > b.accountId ? 1 : -1})
        console.log(htmlstuff.className)
        printRow(data)
    }else{
        htmlstuff.className = "asc"
        data = data.sort((a,b) => { return a.accountId > b.accountId ? -1 : 1})
        console.log(htmlstuff.className)
        printRow(data)
    }

}


  function sortByUsername(htmlstuff){
    var ascOrNot = htmlstuff.className;
    if(ascOrNot == "asc"){
        htmlstuff.className = "desc"
        data = data.sort((a,b) => { return a.accountName.toLowerCase() > b.accountName.toLowerCase()  ? 1 : -1})
        console.log(htmlstuff.className)
        printRow(data)
    }else{
        htmlstuff.className = "asc"
        data = data.sort((a,b) => { return a.accountName.toLowerCase() > b.accountName.toLowerCase() ? -1 : 1})
        console.log(htmlstuff.className)
        printRow(data)
    }
  }
  function sortByFirstname(htmlstuff){
    var ascOrNot = htmlstuff.className;
    if(ascOrNot == "asc"){
        htmlstuff.className = "desc"
        data = data.sort((a,b) => { return a.firstName.toLowerCase() > b.firstName.toLowerCase() ? 1 : -1})
        console.log(htmlstuff.className)
        printRow(data)
    }else{
        htmlstuff.className = "asc"
        data = data.sort((a,b) => { return a.firstName.toLowerCase() > b.firstName.toLowerCase() ? -1 : 1})
        console.log(htmlstuff.className)
        printRow(data)
    }
  }
      function sortByLastname(htmlstuff){
        var ascOrNot = htmlstuff.className;
    if(ascOrNot == "asc"){
        htmlstuff.className = "desc"
        data = data.sort((a,b) => { return a.lastName.toLowerCase() > b.lastName.toLowerCase() ? 1 : -1})
        console.log(htmlstuff.className)
        printRow(data)
    }else{
        htmlstuff.className = "asc"
        data = data.sort((a,b) => { return a.lastName.toLowerCase() > b.lastName.toLowerCase() ? -1 : 1})
        console.log(htmlstuff.className)
        printRow(data)
    }
      }

      ////////////////////////////////
      //function below is for form submition

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
       const response = await fetch("http://localhost:8080/UserRestController/createAccountWorker/1",
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
                console.log('yes successs')
                alert("successfully added new worker, press ok to refresh list");
                closeCreateForm();
                
                delay(1000).then(getWorkerList());
                form.reset();
                
                
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

    //delay 1 second
    function delay(time) {
        return new Promise(resolve => setTimeout(resolve, time));
      }
    function clearForm(){

    }
      //delay(1000).then(() => console.log('ran after 1 second1 passed'));

