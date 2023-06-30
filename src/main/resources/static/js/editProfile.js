const profileForm = document.querySelector('.edit-profile-form');
const uid = document.querySelector('.uid').value;
const userName = document.querySelector('.username')
const submitBtn = document.querySelector('.submit-btn');

console.log(uid)

profileForm.addEventListener('submit', async (e) => {
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
    error_message = await response.json()
    error_message = Object.fromEntries(Object.entries(error_message).filter(([_, v]) => v != null));
    console.log(error_message);
    var list = document.getElementsByClassName("error-message");
    var i = 0;
    console.log(error_message);
    for (var key in error_message) {
      console.log(`${i}: ${error_message[key]}`)
      i++;
    }
    i = 0;
    for (var key in error_message) {
      if (error_message[key]) {
        list[i].classList.remove('hidden')
        list[i].innerHTML = '❌ ' + error_message[key];
      }
      else {
        list[i].classList.remove('hidden')
        list[i].innerHTML = ''
      }
      console.log(`${i}: ${error_message[key]}`)
      i++;

    }
    submitBtn.disabled = false

  } else if (response.status == 200) {
    error_message = await response.json()
    error_message = Object.fromEntries(Object.entries(error_message).filter(([_, v]) => v != null));
    console.log(error_message)
    errorMassage.forEach(err => {
      err.classList.add('hidden')
    })
    for (var key in error_message) {
      console.log(placeholderValue[i])
      placeholderValue[i] = error_message[key]
      i += 1;
    }
    i = 0
    userName.innerHTML = placeholderValue[0]
    console.log('yes successs');
    console.log(error_message);
    submitBtn.disabled = false

  } else if (response.status == 500) {
    error_message = await response.json()
    var list = document.getElementsByClassName("error-message");
    var i = 0;
    submitBtn.disabled = false
    alert("something wrong with server side, please try again later");
  }
})