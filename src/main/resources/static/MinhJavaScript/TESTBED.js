var jsonData;





// fecth a get
async function deleteByID(id) {
    console.log(id)
    const response = await fetch(`http://localhost:8080/UserRestController/${id}`, {
        method: "DELETE",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    }
    );
    console.log("yes call api success DELETE")
    console.log(response)
    if (response.status == 500) {
        alert("delete fail, might be server side");
        return;
    } else {
        jsonData = await response.json();
        printRow(jsonData,"worker-list");
    }
}



async function getWorkerList() {
    document.getElementsByClassName("table-customer")[0].style.display = "none";
    document.getElementsByClassName("table-worker")[0].style.display = "block";
    console.log("inside get worker")
    const response = await fetch("http://localhost:8080/UserRestController/getAccountWorker")
    jsonData = await response.json();
    console.log(jsonData);
    // printRow(data, "worker-list");
    // $(document).ready(function () {
    //     $('#example').DataTable();
    // });
    $(document).ready(function () {
        $('#example').DataTable({
            data: jsonData,
            columns: [
                { title: 'accountId' },
                { title: 'email' },
                { title: 'firstName' },
                { title: 'lastName' },
                { title: 'dob' },
                { title: 'role' },
                { title: 'Delete' },
                { title: 'UPDATE' }
            ],
        });
    });
}

function printRow(data, IdOfTableBody) {
    console.log(IdOfTableBody)
    var table_tag = document.getElementById(`${IdOfTableBody}`);
    console.log(table_tag.innerHTML)
    table_tag.innerHTML = ""
    console.log(data)
    var output = "";
    for (var i in data) {
        roleString = (data[i].role == 1) ? "worker" : "user";
        statusString = (data[i].accountStatus == 1) ? "active" : "off ";
        var date_new; var date_output;
        if (data[i].dob == null) {
            date_output = "not yet imported"
        } else {
            date_new = new Date(data[i].dob); console.log(date_new)
            date_output = new Intl.DateTimeFormat('en-US').format(date_new);
        }
// onclick='showFullCustomerInfo(${data[i].accountId})'
        output = `
        <tr id="${data[i].accountId}">
                <td>${data[i].accountId}</td>
                <td style="display: none;"></td>
                <td style="display: none;"></td>
                <td>${data[i].email}</td>
                <td>${data[i].firstName}</td>
                <td>${data[i].lastName}</td>
                <td>${date_output}</td>
                <td>${statusString}</td>
                <td>${roleString}</td>
                <td><button onclick='deleteByID(${data[i].accountId})'>DELETE</button></td>
                <td><button onclick='updateWorker(${data[i].accountId})'>UPDATE</button></td>  
         </tr>     
        `;
        table_tag.innerHTML += output;
    }
}

{/* <input type="hidden" name="txtID" value="${data[i].accountId}"></input>
<input type="hidden" name="txtUsername" value="${data[i].accountName}"></input>
<input type="hidden" name="txtPassword" value="${data[i].password}"></input>
<input type="hidden" name="txtEmail"value="${data[i].email}"></input>
<input type="hidden" name="txtFirstname"value="${data[i].firstName}"></input>
<input type="hidden" name="txtLastname"value="${data[i].lastName}"></input>
<input type="hidden" name="txtDob"value="${data[i].dob}"></input>
<input type="hidden" name="txtStatus"value="${data[i].accountStatus}"></input>
<input type="hidden" name="txtRole"value="${data[i].role}"></input> */}


// async function showFullCustomerInfo(AccountID) {
//     const response1 = await fetch(`http://localhost:8080/UserRestController/getAccountInfo/${AccountID}`, {
//         method: "GET"
//     });
//     console.log("yes get all info customer ")
//     var account = await response1.json(); console.log(account);
//     const response2 = await fetch(`http://localhost:8080/UserRestController/getAccountPhonenumber`, {
//         method: "Post",
//         headers: {
//             'Content-Type': 'application/json'
//         },
//         body: JSON.stringify(account)
//     }

//     );
//     console.log("yes get in phone");
//     var phone = await response2.json(); console.log(phone)

// }




// async function updateWorker(id) {
//     console.log(id);
//     const table_row = document.getElementById(`${id}`);
//     const table_data = table_row.getElementsByTagName("td");
//     const length = table_data.length - 2; // -2 cot cuoi la 2 cai button, ko lay du lieu dc
//     console.log(table_data)
//     var name_list = []; var value_list = [];
//     const map = new Map();
//     for (var i = 0; i < length; i++) {
//         name_list.push(table_row.getElementsByTagName("td")[i].getElementsByTagName("input")[0].name);
//         value_list.push(table_row.getElementsByTagName("td")[i].getElementsByTagName("input")[0].value);
//         map.set(name_list[i], value_list[i])
//     }
//     const json = Object.fromEntries(map);
//     console.log(json)
//     const response = await fetch(`http://localhost:8080/UserRestController/updateWorkerAccount`, {
//         method: "PUT",
//         headers: {
//             'Content-Type': 'application/json'
//         },
//         body: JSON.stringify(json)
//     });
//     console.log("yes call api success UPDATE")

//     if (response.status == 400) {
//         alert("the update must not be empty and name must not have number, try again");
//         // if bad request and null body 
//         return;
//     } else {
//         data = await response.json();
//     }
//     printRow(data, "worker-list");
// }

// function sortById(htmlstuff) {
//     console.log(htmlstuff.className);
//     console.log(htmlstuff.id)
//     var ascOrNot = htmlstuff.className; var ID;
//     if(htmlstuff.id == "heading-id-w"){
//         ID = "worker-list"
//     }else{
//         ID = "customer-list"
//     }
//     if (ascOrNot == "asc") {
//         htmlstuff.className = "desc"
//         data = data.sort((a, b) => { return a.accountId > b.accountId ? 1 : -1 })
//         console.log(htmlstuff.className)
        
//         printRow(data,ID)
//     } else {
//         htmlstuff.className = "asc"
//         data = data.sort((a, b) => { return a.accountId > b.accountId ? -1 : 1 })
//         console.log(htmlstuff.className)
//         printRow(data,ID)
//     }

// }



// function sortByFirstname(htmlstuff) {
//     var ID;
//     if(htmlstuff.id == "heading-firstname-w"){
//         ID = "worker-list"
//     }else{
//         ID = "customer-list"
//     }
//     var ascOrNot = htmlstuff.className;
//     if (ascOrNot == "asc") {
//         htmlstuff.className = "desc"
//         data = data.sort((a, b) => { return a.firstName.toLowerCase() > b.firstName.toLowerCase() ? 1 : -1 })
//         console.log(htmlstuff.className)
//         printRow(data,ID)
//     } else {
//         htmlstuff.className = "asc"
//         data = data.sort((a, b) => { return a.firstName.toLowerCase() > b.firstName.toLowerCase() ? -1 : 1 })
//         console.log(htmlstuff.className)
//         printRow(data,ID)
//     }
// }
// function sortByLastname(htmlstuff) {
//     var ID;
//     if(htmlstuff.id == "heading-lastname-w"){
//         ID = "worker-list"
//     }else{
//         ID = "customer-list"
//     }
//     var ascOrNot = htmlstuff.className;
//     if (ascOrNot == "asc") {
//         htmlstuff.className = "desc"
//         data = data.sort((a, b) => { return a.lastName.toLowerCase() > b.lastName.toLowerCase() ? 1 : -1 })
//         console.log(htmlstuff.className)
//         printRow(data,ID)
//     } else {
//         htmlstuff.className = "asc"
//         data = data.sort((a, b) => { return a.lastName.toLowerCase() > b.lastName.toLowerCase() ? -1 : 1 })
//         console.log(htmlstuff.className)
//         printRow(data,ID)
//     }
// }

////////////////////////////////
//function below is for form submition

// function createForm() {
//     //document.querySelectorAll(".overlay")[0].style.display = "block";
//     document.querySelectorAll(".form")[0].style.display = "flex";
// }
// function closeCreateForm() {
//     //document.querySelectorAll(".overlay")[0].style.display = "none";
//     document.querySelectorAll(".form")[0].style.display = "none";

// }

// const form = document.getElementById("create-form");
// form.addEventListener('submit', async function (event) {
//     console.log(form)
//     event.preventDefault();
//     const form_data = new FormData(form);
//     const data = Object.fromEntries(form_data);

//     console.log(data);
//     const response = await fetch("http://localhost:8080/UserRestController/createAccountWorker/1",
//         {
//             method: "POST",
//             headers: {
//                 'Content-Type': 'application/json'
//             },
//             body: JSON.stringify(data)
//         });

//     console.log(response.status)
//     if (response.status == 400) {
//         error_message = await response.json()
//         var list = document.getElementsByClassName("error-message");
//         var i = 0;
//         for (var key in error_message) {
//             if (error_message[key] !== "") {
//                 list[i].innerHTML = error_message[key];
//                 list[i].style.visibility = "visible";
//                 i += 1;
//             } else {
//                 console.log(list[i].innerHTML)
//                 list[i].innerHTML = "appropriate input";
//                 list[i].style.visibility = "hidden";
//                 i += 1;
//             }

//         }

//     } else if (response.status == 200) {
//         error_message = await response.json()
//         var list = document.getElementsByClassName("error-message");
//         var i = 0;
//         for (var key in error_message) {
//             if (error_message[key] !== "") {
//                 list[i].innerHTML = error_message[key];
//                 list[i].style.visibility = "visible";
//                 i += 1;
//             } else {
//                 console.log(list[i].innerHTML)
//                 list[i].innerHTML = "appropriate input";
//                 list[i].style.visibility = "hidden";
//                 i += 1;
//             }
//         }
//         console.log('yes successs')
//         alert("successfully added new worker, press ok to refresh list");
//         closeCreateForm();

//         delay(1000).then(getWorkerList());
//         form.reset();


//     } else if (response.status == 500) {
//         error_message = await response.json()
//         var list = document.getElementsByClassName("error-message");
//         var i = 0;
//         for (var key in error_message) {
//             if (error_message[key] == "") {
//                 console.log(list[i].innerHTML)
//                 list[i].innerHTML = "appropriate input";
//                 list[i].style.visibility = "hidden";
//                 i += 1;
//             }
//         }
//         alert("something wrong with server side, please try again later");
//     }
// }
// )

// //delay 1 second
// function delay(time) {
//     return new Promise(resolve => setTimeout(resolve, time));
// }


// async function getCustomerList() {
//     document.getElementsByClassName("table-worker")[0].style.display = "none";
//     document.getElementsByClassName("table-customer")[0].style.display = "block";
//     console.log("inside get worker")
//     const response = await fetch("http://localhost:8080/UserRestController/getAccountCustomer")
//     data = await response.json();
//     printRow(data, "customer-list");
// }
// //delay(1000).then(() => console.log('ran after 1 second1 passed'));

// function openCreateForm() {
//    console.log(document.querySelectorAll(".form")[0])
//     document.querySelectorAll(".form")[0].style.display = "block";
// }
// function closeCreateForm() {
//     console.log(document.querySelectorAll(".form")[0])
//     document.querySelectorAll(".form")[0].style.display = "none";

// }
