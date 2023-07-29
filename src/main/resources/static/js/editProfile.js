let addPhoneBtn = null

const editFormContainer = document.querySelector('.edit-form-container')
const editBtn = document.querySelector('.edit-btn')
const errorMassage = document.querySelectorAll('.error-message')

let file = ''

const profileForm = document.querySelector('.edit-profile-form');
const profileFormInput = document.querySelectorAll('.profile-form-input');
const prohibitInput = document.querySelectorAll('.prohibit-input')
const prohibitInputRadio = document.querySelectorAll('.prohibit-input-radio')
const phoneDropDownButton = document.querySelector('.phoneDropDownButton')
const phoneDropdownContainer = document.querySelector('#phoneDropdownContainer')
let phoneSpan = document.querySelectorAll('.phoneSpan')
const phoneInput = document.querySelector('.phoneInput')
const phoneSpanContainer = document.querySelector('.phoneSpanContainer')
const pfp = document.querySelectorAll('.pfp')
const pfpGroup = document.querySelectorAll('.pfp-group')
const placeholderValue = [...profileFormInput].map(input => input.placeholder)

const cancelBtn = document.querySelector('.cancel-btn')
const changePfpBtn = document.querySelector('.change-pfp-btn')
const cancelPfpBtn = document.querySelector('.cancel-pfp-btn')


const txtPhoneId = document.querySelector('input[name=txtPhoneId]')

const uid = document.querySelector('.uid').value;
const userName = document.querySelector('.username')
const uploadFile = document.getElementById('upload-file')
const uploadPfpBtn = document.querySelector('.upload-pfp-btn')
const uploadBtnContainer = document.querySelector('.upload-btn-container')
const avatar = document.querySelector('.avatar')

const imgFileName = document.querySelector('.img-file-name')
let imgSrc = pfp[0].src
let tmpFileName = ''

const submitBtn = document.querySelector('.submit-btn');

let _validFileExtensions = [".jpg", ".jpeg", ".png"];

let canEdit = false
let i = 0



cancelBtn.addEventListener('click', (e) => {
  e.preventDefault()

  errorMassage.forEach(err => {
    err.classList.add('hidden')
  })

  profileFormInput.forEach(input => {
    input.value = ''
    input.placeholder = placeholderValue[i]
    i++
  });
  i = 0;
  profileFormInput[2].type = 'text'
  phoneDropdownContainer.classList.add('hidden')
  editFormContainer.classList.add('pointer-events-none')
})

editBtn.addEventListener('click', (e) => {
  canEdit = true
  errorMassage.forEach(err => {
    err.classList.add('hidden')
  })
  editFormContainer.classList.remove('pointer-events-none')
  profileFormInput.forEach(input => {
    input.value = placeholderValue[i]
    input.placeholder = ''
    i++
  });
  i = 0
  profileFormInput[2].type = 'date'
})

profileFormInput.forEach(input => {
  input.addEventListener('keydown', (e) => {
    if (canEdit === false) {
      e.preventDefault()
    }

    e.returnValue = true
    for (let i = 0; i < prohibitInput.length; i++) {
      prohibitInput[i].value = ''
    }
  })
})

prohibitInput.forEach(input => {
  input.addEventListener('keydown', (e) => {
    if (e.keyCode === 9) {
      e.returnValue = true
    } else {
      e.preventDefault()
    }
  })
})

prohibitInputRadio.forEach(radio => {
  radio.addEventListener('click', (e) => {
    e.preventDefault()
  })
})

phoneDropDownButton.addEventListener('click', (e) => {
  e.preventDefault()
  phoneDropdownContainer.classList.toggle('hidden')
})


function hideDropDown() {
  document.onclick = () => {
    phoneDropdownContainer.classList.add('hidden')
    dropDownMenu.classList.add('hidden')
  }
}

changePfpBtn.addEventListener('click', () => {
  uploadBtnContainer.classList.toggle('hidden')
  changePfpBtn.classList.toggle('hidden')
})

cancelPfpBtn.addEventListener('click', () => {
  uploadFile.value = ''
  uploadFile.files.length = 0
  imgFileName.innerHTML = ''
  imgFileName.classList.add('hidden')
  console.log(uploadFile.files.length)
  console.log(uploadFile.value)
  uploadBtnContainer.classList.toggle('hidden')
  changePfpBtn.classList.toggle('hidden')
  pfp.forEach(pfp => pfp.src = imgSrc)

})
uploadPfpBtn.addEventListener('click', (e) => {
  if (checkFileExist() === false) {
    imgFileName.innerHTML = 'there is no image to upload'
    imgFileName.classList.add('italic', 'text-red-500')
    imgFileName.classList.remove('hidden')
    e.preventDefault()
  } else {
    e.returnValue = true
    pfpGroup.forEach(pfp => pfp.disabled = true)
    console.log(uploadFile.value)
    console.log(uploadFile.files.length)
    //AXIOS POST REQUEST//
    const formData = new FormData();
    formData.append('file', uploadFile.files[0]);
    formData.append('uid', uid);
    formData.append('userName', userName.innerHTML);
    formData.append('imgSrc', imgSrc);
    formData.append('tmpFileName', tmpFileName);
    axios.post('http://localhost:8080/images/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
      .then((res) => {
        console.log(res)
        //CHECK IF STATUS IS 200//
        if (res.status === 200) {
          console.log('success')
          imgFileName.innerHTML = 'upload success'
          imgFileName.classList.add('italic', 'text-green-500')
        }
        else {
          console.log('fail')
          imgFileName.innerHTML = 'upload fail'
          imgFileName.classList.add('italic', 'text-red-500')
        }
      })
      .catch((err) => {
        console.log(err)
      })

  }

})

const checkFileExist = () => {
  return uploadFile.files.length != 0;
}

console.log(phoneSpan.length)

phoneSpan.forEach(p => {
  p.addEventListener('click', (e) => {
    e.preventDefault()
    phoneInput.value = p.innerHTML
    txtPhoneId.value = p.getAttribute('data-id')
    placeholderValue[3] = p.innerHTML
    console.log(txtPhoneId.value)
    phoneDropdownContainer.classList.toggle('hidden')

  })
})
if (phoneSpan.length < 3) {
  let li = document.createElement('li')
  let span = document.createElement('span')
  li.setAttribute('class', 'addPhoneBtn relative')
  span.setAttribute('class', 'block px-3 py-1 hover:bg-gray-300 cursor-pointer capitalize italic text-slate-400 text-[13px] opacity-35')
  span.textContent = 'add new phone number...'

  li.appendChild(span)
  phoneSpanContainer.appendChild(li)
  addPhoneBtn = document.querySelector('.addPhoneBtn')

  addPhoneBtn.addEventListener('click', () => {
    phoneInput.value = ""
    phoneInput.placeholder = "Enter new phone number"
    txtPhoneId.value = -1
    phoneDropdownContainer.classList.toggle('hidden')

  })
}

console.log(uid)

profileForm.addEventListener('submit', async (e) => {
  i = 0
  e.preventDefault()
  submitBtn.disabled = true
  const form_data = new FormData(profileForm);
  const data = Object.fromEntries(form_data);

  const response = await fetch(`/changeProfile/${uid}`,
    {
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    });

  if (response.status == 400) {
    i = 0
    error_message = await response.json()
    error_message = Object.fromEntries(Object.entries(error_message).filter(([_, v]) => v != null));
    console.log(error_message);
    for (var key in error_message) {
      if (error_message[key]) {
        errorMassage[i].classList.remove('hidden')
        errorMassage[i].innerHTML = '❌ ' + error_message[key];
      }
      else {
        errorMassage[i].innerHTML = ''
      }
      console.log(`${i}: ${error_message[key]}`)
      i++;

    }
    i = 0
    submitBtn.disabled = false

  } else if (response.status == 200) {
    i = 0
    error_message = await response.json()
    error_message = Object.fromEntries(Object.entries(error_message).filter(([_, v]) => v != null));
    console.log(error_message)
    errorMassage.forEach(err => {
      err.classList.add('hidden')
    })
    for (var key in error_message) {
      if (i <= placeholderValue.length) {
        console.log(placeholderValue[i])
        placeholderValue[i] = error_message[key]
        i += 1;

      }
    }
    phoneSpan.forEach(p => {
      if (p.getAttribute('data-id') == txtPhoneId.value) {
        p.innerHTML = phoneInput.value
      }
    })
    if (txtPhoneId.value === "-1") {
      if (error_message["phoneNumberId"] !== null) {
        console.log(error_message["phoneNumberId"])
        insertPhoneNumber(error_message["phonenumberErr"], error_message["phoneNumberId"], addPhoneBtn)
      }
      console.log("new phone number")
    }
    i = 0
    userName.innerHTML = placeholderValue[0]
    alert("successfully update profile")
    submitBtn.disabled = false

    errorMassage.forEach(err => {
      err.classList.add('hidden')
    })

    profileFormInput.forEach(input => {
      input.value = ''
      input.placeholder = placeholderValue[i]
      i++
    });
    i = 0;
    profileFormInput[2].type = 'text'
    phoneDropdownContainer.classList.add('hidden')
    editFormContainer.classList.add('pointer-events-none')

  } else if (response.status == 500) {
    error_message = await response.json()
    var list = document.getElementsByClassName("error-message");
    var i = 0;
    submitBtn.disabled = false
    alert("something wrong with server side, please try again later");
  }
})

uploadFile.onchange = function ValidateSingleInput() {
  console.log();
  imgFileName.classList.remove('italic', 'text-red-500')
  var sFileName = uploadFile.value;
  console.log("sFileName:" + sFileName);
  if (sFileName.length > 0) {
    console.log("sFileName.length:" + sFileName.length);
    var blnValid = false;
    for (var j = 0; j < _validFileExtensions.length; j++) {
      var sCurExtension = _validFileExtensions[j];
      console.log("sCurExtension: " + sCurExtension);
      if (sFileName.substr(sFileName.length - sCurExtension.length, sCurExtension.length).toLowerCase() === sCurExtension.toLowerCase()) {
        imgFileName.innerHTML = ""
        imgFileName.innerHTML += `Image name: ${sFileName.split(/(\\|\/)/g).pop()}`
        imgFileName.classList.remove('hidden')
        blnValid = true;
        break;
      }
    }

    if (!blnValid) {
      imgFileName.innerHTML = ''
      imgFileName.classList.add('hidden')
      alert("Sorry, " + sFileName.split(/(\\|\/)/g).pop() + " is invalid, allowed extensions are: " + _validFileExtensions.join(", "));
      uploadFile.value = "";
      return false;
    }
  }
  let tgt = window.event.srcElement
  file = tgt.files[0];
  if (file) {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.addEventListener("loadend", () => {
      tmpFileName = reader.result
      pfp.forEach(p => {
        p.src = reader.result;
      })
    });
  }
  return true;
}

function insertPhoneNumber(phoneNumber, phoneId, addPhoneBtn) {
  const newLi = document.createElement('li')
  const newSpan = document.createElement('span')

  newLi.setAttribute('class', 'relative')
  newSpan.setAttribute('class', 'phoneSpan block text-black px-3 py-1 hover:bg-gray-300 cursor-pointer')
  newSpan.innerHTML = phoneNumber
  newSpan.setAttribute('data-id', phoneId)
  newLi.appendChild(newSpan)

  phoneSpanContainer.insertBefore(newLi, addPhoneBtn)

  phoneSpan = document.querySelectorAll('.phoneSpan')

  phoneSpan.forEach(p => {
    p.addEventListener('click', (e) => {
      e.preventDefault()
      phoneInput.value = p.innerHTML
      txtPhoneId.value = p.getAttribute('data-id')
      console.log(txtPhoneId.value)
      phoneDropdownContainer.classList.toggle('hidden')

    })
  })

  if (phoneSpan.length >= 3) {
    addPhoneBtn.remove()
  }
}

function upload(accountId) {
  console.log(accountId)
  accId = accountId;
  var formData = new FormData();
  formData.append("file", file);
  fetch("/changeProfile/image/" + accountId, {
    method: 'POST',
    body: formData
  })
    .then(res => {
      if (res.status === 200) {
        alert("Successfully update profile picture")
        pfpGroup.forEach(pfp => pfp.disabled = false);
        uploadFile.value = ''
        uploadFile.files.length = 0
        imgFileName.innerHTML = ''
        imgFileName.classList.add('hidden')
        uploadBtnContainer.classList.toggle('hidden')
        changePfpBtn.classList.toggle('hidden')
        imgSrc = pfp.src
      }
    })
}

