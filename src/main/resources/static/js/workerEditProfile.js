let addPhoneBtn = null

const editFormContainer = document.querySelector('.edit-form-container')
const editBtn = document.querySelector('.edit-btn')
const errorMassage = document.querySelectorAll('.error-message')

let file = ''

const profileForm = document.querySelector('.edit-profile-form');
const profileFormInput = document.querySelectorAll('.profile-form-input');
const prohibitInput = document.querySelectorAll('.prohibit-input')
const phoneInput = document.querySelector('.phoneInput')
const phoneSpanContainer = document.querySelector('.phoneSpanContainer')
const placeholderValue = [...profileFormInput].map(input => input.placeholder)
console.log(placeholderValue)

const cancelBtn = document.querySelector('.cancel-btn')


const txtPhoneId = document.querySelector('input[name=txtPhoneId]')

const uid = document.querySelector('.uid').value;
const userName = document.querySelector('.username')
const submitBtn = document.querySelector('.submit-btn');

let canEdit = false
let i = 0



cancelBtn.addEventListener('click', (e) => {
  e.preventDefault()
  console.log(placeholderValue)

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



console.log(uid)

profileForm.addEventListener('submit', async (e) => {
  i = 0
  e.preventDefault()
  submitBtn.disabled = true
  const form_data = new FormData(profileForm);
  const data = Object.fromEntries(form_data);

  const response = await fetch(`/changeProfile/workerProfile/${uid}`,
    {
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    });


  console.log(response)

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
    i = 0
    userName.innerHTML = placeholderValue[0]
    alert("successfully update profile")
    submitBtn.disabled = false

  } else if (response.status == 500) {
    error_message = await response.json()
    var list = document.getElementsByClassName("error-message");
    var i = 0;
    submitBtn.disabled = false
    alert("something wrong with server side, please try again later");
  }
})



// function insertPhoneNumber(phoneNumber, phoneId, addPhoneBtn) {
//   const newLi = document.createElement('li')
//   const newSpan = document.createElement('span')

//   newLi.setAttribute('class', 'relative')
//   newSpan.setAttribute('class', 'phoneSpan block text-black px-3 py-1 hover:bg-gray-300 cursor-pointer')
//   newSpan.innerHTML = phoneNumber
//   newSpan.setAttribute('data-id', phoneId)
//   newLi.appendChild(newSpan)

//   phoneSpanContainer.insertBefore(newLi, addPhoneBtn)

//   phoneSpan = document.querySelectorAll('.phoneSpan')

//   phoneSpan.forEach(p => {
//     p.addEventListener('click', (e) => {
//       e.preventDefault()
//       phoneInput.value = p.innerHTML
//       txtPhoneId.value = p.getAttribute('data-id')
//       console.log(txtPhoneId.value)
//       phoneDropdownContainer.classList.toggle('hidden')

//     })
//   })

//   if (phoneSpan.length >= 3) {
//     addPhoneBtn.remove()
//   }
// }

