const getCustomerId = window.location.href.split(/(\\|\/)/g).pop()
console.log(getCustomerId)
showFullCustomerInfo(getCustomerId)
var error_username, error_email, error_firstname, error_lastname, error_phonenumber, error_dob, error_address
error_username = document.getElementById("error-username")
error_email = document.getElementById("error-email")
error_firstname = document.getElementById("error-firstname")
error_lastname = document.getElementById("error-lastname")
error_phonenumber = document.getElementById("error-phonenumber")
error_dob = document.getElementById("error-dob")
error_address = document.getElementById("error-address")


async function showFullCustomerInfo(AccountID) {
  document.getElementById("update-form").reset();
  document.getElementById("update-form-container").style.display = "block"
  ///get account info
  console.log(AccountID)
  const response1 = await fetch(`http://localhost:8080/UserRestController/getAccountInfo/${AccountID}`, {
    method: "GET"
  });
  console.log("yes get all info customer ")
  var account = await response1.json(); console.log(account);
  //get phone of account
  const response2 = await fetch(`http://localhost:8080/UserRestController/getAccountPhonenumber/${AccountID}`, {
    method: "GET",
    headers: {
      'Content-Type': 'application/json'
    }
  });
  console.log("yes get in phone");
  var phone = await response2.json();
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
  //document.getElementById("update-form-password").value = account.password;
  document.getElementById("update-form-firstname").value = account.firstName;
  document.getElementById("update-form-lastname").value = account.lastName;
  document.getElementById("update-form-dob").value = account.dob;
  document.getElementById("update-form-email").value = account.email

  var is_block = account.isBlock
  var is_enabled = account.isEnable
  console.log(is_block)
  console.log(is_enabled)
  if (is_enabled == 1) {
    isEnabled("isEnableYes")
  } else {
    isEnabled("isEnableNo")
  } if (is_block == 1) {
    isBlocked("isBlockYes")
  } else {
    isBlocked("isBlockNo")
  }
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
function phoneInputUpdate(number, id) {
  console.log(number)
  console.log(id)
  document.getElementById("update-form-phonenumber").value = number;
  document.getElementById("update-form-phoneid").value = id
}
////////////update customer info
////////////update customer info
////////////update customer info




function isEnabled(button_id) {
  console.log(button_id)
  if (button_id == "isEnableYes") {
    document.getElementById("isEnableYes").checked = true;
    document.getElementById("isEnableNo").checked = false;
    document.getElementById("isEnableValue").value = 1;
    document.getElementById("isEnableText").value = "yes";
  } else {
    document.getElementById("isEnableYes").checked = false;
    document.getElementById("isEnableNo").checked = true;
    document.getElementById("isEnableValue").value = 0;
    document.getElementById("isEnableText").value = "No";
  }
}
function isBlocked(button_id) {
  console.log(button_id)
  if (button_id == "isBlockYes") {
    document.getElementById("isBlockYes").checked = true;
    document.getElementById("isBlockNo").checked = false;
    document.getElementById("isBlockValue").value = 1;
    document.getElementById("isBlockText").value = "yes";
  } else {
    document.getElementById("isBlockYes").checked = false;
    document.getElementById("isBlockNo").checked = true;
    document.getElementById("isBlockValue").value = 0;
    document.getElementById("isBlockText").value = "no";
  }
}
