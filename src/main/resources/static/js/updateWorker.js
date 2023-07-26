const getWorkerId = window.location.href.split(/(\\|\/)/g).pop()
console.log(getWorkerId)
showFullWorkerInfo(getWorkerId)


const updateFormInput = document.querySelectorAll('.updateFormInput')



function workerStatus(button_id) {
  console.log(button_id)
  if (button_id == "w-status-free") {
    document.getElementById("w-status-free").checked = true;
    document.getElementById("w-status-off").checked = false;
    document.getElementById("w-status").value = 0;
    document.getElementById("w-status-text").value = "On";
  } else {
    document.getElementById("w-status-free").checked = false;
    document.getElementById("w-status-off").checked = true;
    document.getElementById("w-status").value = 1;
    document.getElementById("w-status-text").value = "Off";
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

async function showFullWorkerInfo(AccountID) {
  document.getElementById("w-update-form").reset();
  //  ///get account info
  console.log(AccountID)
  var response1 = await fetch(`/UserRestController/getAccountInfo/${getWorkerId}`, {
    method: "GET"
  });
  console.log("yes get all info worker ")
  var account = await response1.json(); console.log(await account);
  //get phone of account
  var response2 = await fetch(`/UserRestController/getAccountPhonenumber/${getWorkerId}`, {
    method: "GET",
    headers: {
      'Content-Type': 'application/json'
    },
  });
  console.log("yes get in phone");
  var phone = await response2.json(); console.log(await phone)
  /////get worker status
  var response3 = await fetch(`/UserRestController/getWorkerStatus/${getWorkerId}`, {
    method: "GET",
    headers: {
      'Content-Type': 'application/json'
    }
  });
  var worker_status = await response3.json(); console.log(await worker_status)
  /////get service category
  var response4 = await fetch(`/UserRestController/getServiceCategory`, {
    method: "get",
  });
  console.log("yest get worker status success");

  ////////////////////////////////////////////////
  ////dropdown menu set up
  // var drop_down_menu = document.getElementById("w-dropdown-phone"); console.log(drop_down_menu)
  // drop_down_menu.innerHTML = "";
  // var output = "";
  // for (var i in await phone) {
  //   output = `
  //           <a class="dropdown-item" onclick="phoneInputUpdate(${phone[i].number}, ${phone[i].phoneId})" >
  //               ${phone[i].number}
  //           </a>`;
  //   drop_down_menu.innerHTML += output;
  // }
  // if (phone.length != 0) {
  //   document.getElementById("w-update-form-phonenumber").value = phone[0].number;
  //   document.getElementById("w-update-form-phoneid").value = phone[0].phoneId;
  // } else {
  //   document.getElementById("w-update-form-phonenumber").value = "";
  //   document.getElementById("w-update-form-phoneid").value = "";
  // }

  //this is to get 1 phone to the form if there is one
  ///form info setup
  document.getElementById("w-update-form-id").value = account.accountId;
  document.getElementById("w-update-form-username").value = account.accountName;
  document.getElementById("w-update-form-firstname").value = account.firstName;
  document.getElementById("w-update-form-lastname").value = account.lastName;
  document.getElementById("w-update-form-dob").value = account.dob;
  document.getElementById("w-update-form-email").value = account.email
  console.log(document.getElementById("w-update-form-id").value)
  document.getElementById("w-dayoff").value = worker_status.allowedDayOff;
  document.getElementById("w-workcount").value = worker_status.workCount;
  console.log(worker_status.status)
  if (worker_status.status == 1) {
    workerStatus("w-status-off")
  } else {
    workerStatus("w-status-free")
  }
  console.log("get service list success")
  var service_list = await response4.json();
  // var drop_down_service = document.getElementById("w-dropdown-service"); console.log(drop_down_service)
  // drop_down_service.innerHTML = "";
  // var output = "";
  // for (var i in await service_list) {
  //   output =
  //     `
  //           <a class="dropdown-item" onclick="updateAccountService('${service_list[i].serviceCategoryName}', ${service_list[i].serviceCategoryId})" >
  //               ${service_list[i].serviceCategoryName}
  //           </a>
  //       `;
  //   drop_down_service.innerHTML += output;
  // }
  // document.getElementById("w-service").value = worker_status.serviceCategory.serviceCategoryName;
  // document.getElementById("w-serviceid").value = worker_status.serviceCategory.serviceCategoryId;

}
function phoneInputUpdate(number, id) {
  console.log(number)
  console.log(id)
  document.getElementById("update-form-phonenumber").value = number;
  document.getElementById("update-form-phoneid").value = id
}




function updateAccountService(name, id) {
  console.log(name);
  console.log(id);
  document.getElementById("w-service").value = name;
  document.getElementById("w-serviceid").value = id;
}