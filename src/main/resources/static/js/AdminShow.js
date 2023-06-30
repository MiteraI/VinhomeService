var data;

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
        data = await response.json();
        console.log(data[0].role)
        if (data[0].role == 1) {
            printRow(data, "worker-list");
        } else {
            printRow(data, "customer-list");
        }

    }
}
async function getWorkerList() {
    document.getElementsByClassName("table-customer")[0].style.display = "none";
    document.getElementsByClassName("table-worker")[0].style.display = "block";
    console.log("inside get worker")
    const response = await fetch("http://localhost:8080/UserRestController/getAccountWorker")
    data = await response.json();
    printRow(data, "worker-list");
}

function printRow(data, IdOfTableBody) {
    console.log(IdOfTableBody)
    var table_tag = document.getElementById(`${IdOfTableBody}`);
    // console.log(table_tag.innerHTML)
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
        <tr id="${data[i].accountId}" scope="row"  class='item-to-search' >
        `
        if (data[i].role == 1) {
            output += `<td onclick="showFullWorkerInfo(${data[i].accountId})"><input type="hidden" name="txtID" value="${data[i].accountId}">${data[i].accountId}</td>`;
        } else {
            output += `<td onclick="showFullCustomerInfo(${data[i].accountId})"><input type="hidden" name="txtID" value="${data[i].accountId}">${data[i].accountId}</td>`;
        }
        output += `
                <td style="display: none;" ><input type="hidden" name="txtUsername" value="${data[i].accountName}"></td>
                <td style="display: none;"><input type="hidden" name="txtPassword" value="${data[i].password}"></td>
                <td><input type="hidden" name="txtEmail"value="${data[i].email}">${data[i].email}</td>
                <td><input type="hidden" name="txtFirstname"value="${data[i].firstName}">${data[i].firstName}</td>
                <td><input type="hidden" name="txtLastname"value="${data[i].lastName}">${data[i].lastName}</td>
                <td><input type="hidden" name="txtDob"value="${data[i].dob}">${date_output}</td>
                <td><input type="hidden" name="txtStatus"value="${data[i].accountStatus}">${statusString}</td>
                <td><input type="hidden" name="txtRole"value="${data[i].role}">${roleString}</td>
                <td><button onclick='deleteByID(${data[i].accountId})'>DELETE</button></td>
        `;
        if(data[i].role == 0){
            output += `<td><button class="viewOrderBtn" onclick='getUserOrders(${data[i].accountId})'>View order</button></td></tr>`
        }
        else{
            output += `</tr>`
        }
        table_tag.innerHTML += output;
    }
}

async function showFullCustomerInfo(AccountID) {
    document.getElementById("update-form").reset();
    document.getElementById("update-form-container").style.display = "block"
    ///get account info
    console.log(AccountID)
    const response1 = await fetch(`http://localhost:8080/UserRestController/getAccountInfo/${AccountID}`, {
        method: "GET"
    });
    console.log("yes get all info customer ")
    var account = await response1.json(); console.log(await account);
    //get phone of account
    const response2 = await fetch(`http://localhost:8080/UserRestController/getAccountPhonenumber/${AccountID}`, {
        method: "GET",
        headers: {
            'Content-Type': 'application/json'
        }
    });
    console.log("yes get in phone");
    var phone = await response2.json();
    console.log(await phone)
    //get address of account
    const response3 = await fetch(`http://localhost:8080/UserRestController/getAccountAddress/${AccountID}`, {
        method: "GET"
    });
    console.log("yes get in Address");

    ////dropdown menu set up
    var drop_down_menu = document.getElementById("dropdown-phone"); console.log(drop_down_menu)
    drop_down_menu.innerHTML = "";
    var output = "";
    for (var i in await phone) {
        output =
            `
            <a class="dropdown-item" onclick="phoneInputUpdate('${phone[i].number}', ${phone[i].phoneId})" >
                ${phone[i].number}
            </a>
        `;
        drop_down_menu.innerHTML += output;
    }
    document.getElementById("update-form-phonenumber").value = phone[0].number;
    document.getElementById("update-form-phoneid").value = phone[0].phoneId;
    //this is to get 1 phone to the form if there is one

    ///form info setup
    document.getElementById("update-form-id").value = account.accountId;
    document.getElementById("update-form-username").value = account.accountName;
    document.getElementById("update-form-password").value = account.password;
    document.getElementById("update-form-firstname").value = account.firstName;
    document.getElementById("update-form-lastname").value = account.lastName;
    document.getElementById("update-form-dob").value = account.dob;
    document.getElementById("update-form-email").value = account.email
    console.log(document.getElementById("update-form-id").value)
    //address setup
    var address = await response3.json();
    // console.log(await address)
    var btnChecked = document.querySelectorAll('.form-check-input')
    switch (await address.buildingBlock) {
        case "A":
            btnChecked[0].checked = true;
            break;
        case "B":
            btnChecked[1].checked = true;
            break;
        case "C":
            btnChecked[2].checked = true;
            break;
        default:
            console.log("somethingwrong with address"); alert("something wrong with address, or this guy dont have a house yet")
            break;
    }
    document.getElementById("update-form-room").value = address.buildingRoom;
}

////////////////
//////////////          update customer info
const update_form = document.getElementById("update-form");
update_form.addEventListener('submit', async function (event) {
        console.log(update_form)
        event.preventDefault();
        var form_data = new FormData(update_form);
        let data = Object.fromEntries(form_data);
        // console.log(data);
        var response = await fetch(`http://localhost:8080/UserRestController/UpdateAccountCustomer`,
            {
                method: "Put",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });
        console.log(await response.status);//console.log(await response.json())
        if (response.status == 400) {
            error_message = await response.json()
            var list = document.getElementsByClassName("error-message");
            ///////////////////////////////////
            //////////////////////////////////THIS IS 8 because we minus the first 8 message of create account
            var i = 9;///////
            for (var key in error_message) {
                if (error_message[key] !== "") {
                    list[i].innerHTML = error_message[key];
                    list[i].style.visibility = "visible";
                    i += 1;
                } else {
                    console.log(list[i].innerHTML)
                    list[i].innerHTML = "appropriate input";
                    list[i].style.visibility = "hidden";
                    i += 1;
                }

            }

        } else if (response.status == 200) {
            error_message = await response.json()
            var list = document.getElementsByClassName("error-message");
            var i = 9;
            for (var key in error_message) {
                if (error_message[key] !== "") {
                    list[i].innerHTML = error_message[key];
                    list[i].style.visibility = "visible";
                    i += 1;
                } else {
                    console.log(list[i].innerHTML)
                    list[i].innerHTML = "appropriate input";
                    list[i].style.visibility = "hidden";
                    i += 1;
                }
            }
            console.log('yes successs')
            alert("successfully update");
            closeCreateForm();

            delay(1000).then(() => {
                    var list = document.getElementsByClassName("error-message");
                    var i = 9;
                    for (var key in error_message) {
                        console.log(list[i].innerHTML)
                        list[i].innerHTML = "appropriate input";
                        list[i].style.visibility = "hidden";
                        i += 1;
                    }
                }
            ).then(closeUpdateForm).then(getCustomerList());
        }
    }
)
function phoneInputUpdate(number, id) {
    console.log(number)
    console.log(id)
    document.getElementById("update-form-phonenumber").value = number;
    document.getElementById("update-form-phoneid").value = id
}
function closeUpdateForm() {

    document.getElementById("update-form-container").style.display = "none";
}
////////////////
////////////////
////////////////

async function updateWorker(id) {
    console.log(id);
    const table_row = document.getElementById(`${id}`);
    const table_data = table_row.getElementsByTagName("td");
    const length = table_data.length - 2; // -2 cot cuoi la 2 cai button, ko lay du lieu dc
    console.log(table_data)
    var name_list = []; var value_list = [];
    const map = new Map();
    for (var i = 0; i < length; i++) {
        name_list.push(table_row.getElementsByTagName("td")[i].getElementsByTagName("input")[0].name);
        value_list.push(table_row.getElementsByTagName("td")[i].getElementsByTagName("input")[0].value);
        map.set(name_list[i], value_list[i])
    }
    const json = Object.fromEntries(map);
    console.log(json)
    const response = await fetch(`http://localhost:8080/UserRestController/updateWorkerAccount`, {
        method: "PUT",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(json)
    });
    console.log("yes call api success UPDATE")

    if (response.status == 400) {
        alert("the update must not be empty and name must not have number, try again");
        // if bad request and null body
        return;
    } else {
        data = await response.json();
    }
    printRow(data, "worker-list");
}
function sortById(htmlstuff) {
    console.log(htmlstuff.className);
    console.log(htmlstuff.id)
    var ascOrNot = htmlstuff.className; var ID;
    if (htmlstuff.id == "heading-id-w") {
        ID = "worker-list"
    } else {
        ID = "customer-list"
    }
    if (ascOrNot == "asc") {
        htmlstuff.className = "desc"
        data = data.sort((a, b) => { return a.accountId > b.accountId ? 1 : -1 })
        console.log(htmlstuff.className)

        printRow(data, ID)
    } else {
        htmlstuff.className = "asc"
        data = data.sort((a, b) => { return a.accountId > b.accountId ? -1 : 1 })
        console.log(htmlstuff.className)
        printRow(data, ID)
    }

}
function sortByFirstname(htmlstuff) {
    var ID;
    if (htmlstuff.id == "heading-firstname-w") {
        ID = "worker-list"
    } else {
        ID = "customer-list"
    }
    var ascOrNot = htmlstuff.className;
    if (ascOrNot == "asc") {
        htmlstuff.className = "desc"
        data = data.sort((a, b) => { return a.firstName.toLowerCase() > b.firstName.toLowerCase() ? 1 : -1 })
        console.log(htmlstuff.className)
        printRow(data, ID)
    } else {
        htmlstuff.className = "asc"
        data = data.sort((a, b) => { return a.firstName.toLowerCase() > b.firstName.toLowerCase() ? -1 : 1 })
        console.log(htmlstuff.className)
        printRow(data, ID)
    }
}
function sortByLastname(htmlstuff) {
    var ID;
    if (htmlstuff.id == "heading-lastname-w") {
        ID = "worker-list"
    } else {
        ID = "customer-list"
    }
    var ascOrNot = htmlstuff.className;
    if (ascOrNot == "asc") {
        htmlstuff.className = "desc"
        data = data.sort((a, b) => { return a.lastName.toLowerCase() > b.lastName.toLowerCase() ? 1 : -1 })
        console.log(htmlstuff.className)
        printRow(data, ID)
    } else {
        htmlstuff.className = "asc"
        data = data.sort((a, b) => { return a.lastName.toLowerCase() > b.lastName.toLowerCase() ? -1 : 1 })
        console.log(htmlstuff.className)
        printRow(data, ID)
    }
}

////////////////////////////////
//function below is for form submition

async function openCreateForm() {
    // document.querySelectorAll(".overlay")[0].style.display = "block";

    document.querySelectorAll(".form")[0].style.display = "block";
    var response = await fetch(`http://localhost:8080/UserRestController/getServiceCategory`, {
        method: "GET"
    });
    console.log("yes get service");
    var service_list = await response.json();
    var drop_down_service = document.getElementById("w-create-form-dropdown-service"); console.log(drop_down_service)
    drop_down_service.innerHTML = "";
    var output = "";
    for (var i in await service_list) {
        output =
            `
            <a class="dropdown-item" onclick="creatAccountService('${service_list[i].serviceCategoryName}', ${service_list[i].serviceCategoryId})" >
                ${service_list[i].serviceCategoryName}
            </a>
        `;
        drop_down_service.innerHTML += output;
    }
    document.getElementById("w-create-form-service").value = service_list[0].serviceCategoryName;
    document.getElementById("w-create-form-serviceid").value = service_list[0].serviceCategoryId;
    //creatAccountService(service_list[0].serviceCategoryName,service_list[0].serviceCategoryId)
}
function closeCreateForm() {
    // document.querySelectorAll(".overlay")[0].style.display = "none";
    document.querySelectorAll(".form")[0].style.display = "none";

}
function creatAccountService(servicename, serviceid) {
    console.log(servicename);
    console.log(serviceid);
    document.getElementById("w-create-form-service").value = servicename;
    document.getElementById("w-create-form-serviceid").value = serviceid;
}
/////////
/////////this is to add
/////////
const form = document.getElementById("create-form");
form.addEventListener('submit', async function (event) {
        console.log(form)
        event.preventDefault();
        const form_data = new FormData(form);
        const data = Object.fromEntries(form_data);

        console.log(data);
        const response = await fetch("http://localhost:8080/UserRestController/createAccountWorker/1",
            {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

        console.log(response.status)
        if (response.status == 400) {
            error_message = await response.json()
            var list = document.getElementsByClassName("error-message");
            var i = 0;
            for (var key in error_message) {
                if (error_message[key] !== "") {
                    list[i].innerHTML = error_message[key];
                    list[i].style.visibility = "visible";
                    i += 1;
                } else {
                    console.log(list[i].innerHTML)
                    list[i].innerHTML = "appropriate input";
                    list[i].style.visibility = "hidden";
                    i += 1;
                }

            }

        } else if (response.status == 200) {
            error_message = await response.json()
            var list = document.getElementsByClassName("error-message");
            var i = 0;
            for (var key in error_message) {
                if (error_message[key] !== "") {
                    list[i].innerHTML = error_message[key];
                    list[i].style.visibility = "visible";
                    i += 1;
                } else {
                    console.log(list[i].innerHTML)
                    list[i].innerHTML = "appropriate input";
                    list[i].style.visibility = "hidden";
                    i += 1;
                }
            }
            console.log('yes successs')
            alert("successfully added new worker, press ok to refresh list");
            closeCreateForm();

            delay(1000).then(getWorkerList());
            form.reset();


        } else if (response.status == 500) {
            error_message = await response.json()
            var list = document.getElementsByClassName("error-message");
            var i = 0;
            for (var key in error_message) {
                if (error_message[key] == "") {
                    console.log(list[i].innerHTML)
                    list[i].innerHTML = "appropriate input";
                    list[i].style.visibility = "hidden";
                    i += 1;
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
async function getCustomerList() {
    document.getElementsByClassName("table-worker")[0].style.display = "none";
    document.getElementsByClassName("table-customer")[0].style.display = "block";
    console.log("inside get worker")
    const response = await fetch("http://localhost:8080/UserRestController/getAccountCustomer")
    data = await response.json();
    printRow(data, "customer-list");
}
//delay(1000).then(() => console.log('ran after 1 second1 passed'));
// function openCreateForm() {
//    console.log(document.querySelectorAll(".form")[0])
//     document.querySelectorAll(".form")[0].style.display = "block";
// }
// function closeCreateForm() {
//     console.log(document.querySelectorAll(".form")[0])
//     document.querySelectorAll(".form")[0].style.display = "none";

// }
////////////////////////////////////////////////
////////////////////////////////////////////////
////////////////////////////////////////////////
////////////////////////////////////////////////
const w_update_form = document.getElementById("w-update-form");
w_update_form.addEventListener('submit', async function (event) {
    console.log(w_update_form)
    event.preventDefault();
    var form_data = new FormData(w_update_form);
    var data = Object.fromEntries(form_data);
    console.log(data);
    var response = await fetch("http://localhost:8080/UserRestController//1",
        {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

    console.log(response.status)
})
async function showFullWorkerInfo(AccountID) {
    document.getElementById("w-update-form").reset();
    document.getElementById("w-update-form-container").style.display = "block"
    //  ///get account info
    console.log(AccountID)
    var response1 = await fetch(`http://localhost:8080/UserRestController/getAccountInfo/${AccountID}`, {
        method: "GET"
    });
    console.log("yes get all info worker ")
    var account = await response1.json(); console.log(await account);
    //get phone of account
    var response2 = await fetch(`http://localhost:8080/UserRestController/getAccountPhonenumber/${AccountID}`, {
        method: "GET",
        headers: {
            'Content-Type': 'application/json'
        },
    });
    console.log("yes get in phone");
    var phone = await response2.json(); console.log(await phone)
    /////get worker status
    var response3 = await fetch(`http://localhost:8080/UserRestController/getWorkerStatus/${AccountID}`, {
        method: "GET",
        headers: {
            'Content-Type': 'application/json'
        }
    });
    var worker_status = await response3.json();console.log(await worker_status)

    var response4 = await fetch(`http://localhost:8080/UserRestController/getServiceCategory`, {
        method: "get",
    });
    console.log("yest get worker status success");

    ////dropdown menu set up
    var drop_down_menu = document.getElementById("w-dropdown-phone"); console.log(drop_down_menu)
    drop_down_menu.innerHTML = "";
    var output = "";
    for (var i in await phone) {
        output =
            `
            <a class="dropdown-item" onclick="phoneInputUpdate(${phone[i].number}, ${phone[i].phoneId})" >
                ${phone[i].number}
            </a>
        `;
        drop_down_menu.innerHTML += output;
    }
    if (phone.length != 0) {
        document.getElementById("w-update-form-phonenumber").value = phone[0].number;
        document.getElementById("w-update-form-phoneid").value = phone[0].phoneId;
    } else {
        document.getElementById("w-update-form-phonenumber").value = "";
        document.getElementById("w-update-form-phoneid").value = "";
    }

    //this is to get 1 phone to the form if there is one
    ///form info setup
    document.getElementById("w-update-form-id").value = account.accountId;
    document.getElementById("w-update-form-username").value = account.accountName;
    document.getElementById("w-update-form-password").value = account.password;
    document.getElementById("w-update-form-firstname").value = account.firstName;
    document.getElementById("w-update-form-lastname").value = account.lastName;
    document.getElementById("w-update-form-dob").value = account.dob;
    document.getElementById("w-update-form-email").value = account.email
    console.log(document.getElementById("w-update-form-id").value)
    document.getElementById("w-dayoff").value = worker_status.allowedDayOff;
    document.getElementById("w-workcount").value = worker_status.workCount;
    console.log(worker_status.status)
    if (worker_status.status == 0) {
        workerStatus("w-status-off")
    } else {
        workerStatus("w-status-free")
    }
    console.log("get service list success")
    var service_list = await response4.json();
    var drop_down_service = document.getElementById("w-dropdown-service"); console.log(drop_down_service)
    drop_down_service.innerHTML = "";
    var output = "";
    for (var i in await service_list) {
        output =
            `
            <a class="dropdown-item" onclick="updateAccountService('${service_list[i].serviceCategoryName}', ${service_list[i].serviceCategoryId})" >
                ${service_list[i].serviceCategoryName}
            </a>
        `;
        drop_down_service.innerHTML += output;
    }
    document.getElementById("w-service").value = worker_status.serviceCategory.serviceCategoryName;
    document.getElementById("w-serviceid").value = worker_status.serviceCategory.serviceCategoryId;

}
function updateAccountService(name, id) {
    console.log(name);
    console.log(id);
    document.getElementById("w-service").value = name;
    document.getElementById("w-serviceid").value = id;
}
function closeWorkerUpdateForm() {
    document.getElementById("w-update-form-container").style.display = "none";
}

function workerStatus(button_id) {
    console.log(button_id)
    if (button_id == "w-status-free") {
        document.getElementById("w-status-free").checked = true;
        document.getElementById("w-status-off").checked = false;
        document.getElementById("w-status").value = 1;
        document.getElementById("w-status-text").value = "free";
    } else {
        document.getElementById("w-status-free").checked = false;
        document.getElementById("w-status-off").checked = true;
        document.getElementById("w-status").value = 0;
        document.getElementById("w-status-text").value = "off";
    }
}
function plusAndMinus(number_id, caltype) {
    var num = parseInt(document.getElementById(`${number_id}`).value);
    parseInt()
    if (caltype === "+") {
        num += 1;
        document.getElementById(`${number_id}`).value = num;
    } else {
        if (num <= 0) {
            return;
        } else {
            num -= 1;
            document.getElementById(`${number_id}`).value = num;
        }

    }
}

const getUserOrders = async(uid) => {
    window.location.href = `/api/admin/order/user?id=${uid}`

}