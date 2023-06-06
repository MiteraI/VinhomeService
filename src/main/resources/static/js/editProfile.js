const txtPassword = document.querySelector('.txt_pwd');
const profileForm = document.querySelector('.profile_form');
const tdData = document.querySelectorAll('.td_data');
const spanData = document.querySelectorAll('.span_data');
const editBtn = document.querySelector('.edit_btn');
const cancelBtn = document.querySelector('.cancel_btn');
const submitBtn = document.querySelector('.submit_btn');
const uid = document.querySelector('.id').value;
const form_data = new FormData(profileForm);
const data = Object.fromEntries(form_data);
let arrData = [...spanData].map(data => data.textContent);
let spanClassArr = ["d-flex", "align-items-center", "justify-content-center"]
let nameList = ["txtUsername", "txtPassword", "txtFirstname", "txtLastname", "dob", "txtEmail"]
let p = txtPassword.textContent;
let hidden = "";
let tmp = ""

console.log(data);

for (let i = 0; i < txtPassword.textContent.length; i++) {
  hidden += "●";
}
tmp = hidden
txtPassword.textContent = hidden;
console.log(tmp);

cancelBtn.addEventListener('click', (e) => {
  e.preventDefault();
  hidden = "";
  for (let i = 0; i < tdData.length; i++) {
    if (i == 1) {
      spanData[i].classList.remove(...spanClassArr)
      p = arrData[i];
      const inputPassword = document.querySelector('.input_pwd');
      for (let i = 0; i < inputPassword.value.length; i++) {
        hidden += "●";
      }
      txtPassword.textContent = hidden;
      tmp = hidden;
      spanData[i].classList.add('ms-2')
      spanData[i].innerHTML = `${txtPassword.textContent}`
    }
    else {
      spanData[i].classList.add('ms-2')
      spanData[i].innerHTML = `${arrData[i]} `
    }
  }
  editBtn.classList.remove('d-none')
  cancelBtn.classList.add('d-none')
  submitBtn.classList.add('d-none');
})

editBtn.addEventListener('click', (e) => {
  e.preventDefault()
  editBtn.classList.add('d-none')
  cancelBtn.classList.remove('d-none')
  submitBtn.classList.remove('d-none');
  for (let i = 0; i < spanData.length; i++) {
    if (i == 1) {
      spanData[i].classList.remove('ms-2')
      spanData[i].classList.add(...spanClassArr)
      spanData[i].innerHTML = `<input class="input_data input_pwd w-100" name="${nameList[i]}" type="text" value="${hidden}" />
            <i class="clarify d-none position-absolute" style="left: 82%;">❌</i>
            <button type="button" class="border-start-0 bg-transparent"><i class="fa fa-eye-slash"></i></button>`
      const inputPassword = document.querySelector('.input_pwd');
      const eyeSlash = document.querySelector('.fa-eye-slash');
      eyeSlash.addEventListener('click', () => {
        eyeSlash.classList.toggle('fa-eye-slash');
        eyeSlash.classList.toggle('fa-eye');
        inputPassword.value = p;
        p = tmp;
        tmp = inputPassword.value;
      })
    }
    else {
      spanData[i].classList.remove('ms-2')
      spanData[i].innerHTML = `<input class="input_data w-100" name="${nameList[i]}" type="text" value="${arrData[i]}" /><i class="clarify position-absolute d-none" style="left: 84%;padding-top: 2px;">❌</i>
      <div class="error-message text-danger ms-2 fst-italic d-none" style="font-size: 12px"></div>`
    }
  }
})
profileForm.addEventListener('submit', async (e) => {
  e.preventDefault()

  const response = await fetch(`/changeProfile/${uid}`,
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
        list[i].classList.remove('d-none')
        list[i].innerHTML = '❌ ' + error_message[key];
        i += 1;
      } else {
        list[i].innerHTML = "✔";
        i += 1;
      }

    }
    submitBtn.classList.remove('disabled');


  } else if (response.status == 200) {
    error_message = await response.json()
    var list = document.getElementsByClassName("error-message");
    var i = 0;
    for (var key in error_message) {
      if (error_message[key] == "") {
        list[i].innerHTML = "appropriate input";
        list[i].style.visibility = "hidden";
        i += 1;
      }
    }
    console.log('yes successs');
    submitBtn.classList.remove('disabled');
    window.location.replace("/");

  } else if (response.status == 500) {
    error_message = await response.json()
    var list = document.getElementsByClassName("error-message");
    var i = 0;
    for (var key in error_message) {
      if (error_message[key] == "") {
        list[i].innerHTML = "appropriate input";
        list[i].style.visibility = "hidden";
        i += 1;
      }
    }
    submitBtn.classList.remove('disabled');
    alert("something wrong with server side, please try again later");
  }
})
