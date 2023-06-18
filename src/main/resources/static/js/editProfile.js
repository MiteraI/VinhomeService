const profileForm = document.querySelector('.profile_form');
let divData = document.querySelectorAll('.div_data');
let spanData = document.querySelectorAll('.span_data');
const cancelBtn = document.querySelector('.cancel_btn');
const submitBtn = document.querySelector('.submit_btn');
const uid = document.querySelector('.id').value;
const blockList = ["A", "B", "C"]
let fones = ''

let arrDivData = [...divData].map(data => data.innerHTML);
let arrSpanData = [...spanData].map(data => data.innerHTML);
let spanClassArr = ["d-flex", "align-items-center", "justify-content-center"]
let nameList = ["txtUsername", "txtEmail", "txtFirstname", "txtLastname", "txtDate", "txtPhone", "txtRoom", "radioBlock"]

console.log(arrSpanData);
console.log(arrDivData);

const getPhone = async () => {
  fones = await axios.get('/api/order/getSession/f');
  console.log(fones)
  console.log(fones.data)
  console.log(fones.data[0])
  console.log(fones.data[0].phoneId)
}
getPhone()

const editBtnClicked = (i) => {
  console.log("1");
  let cancelBtn;
  console.log('click');
  divData[i].classList.add(...spanClassArr)
  switch (i) {
    case 4:
      divData[i].innerHTML = `<input class="input_data input_pwd w-100" name="${nameList[i]}" type="date" value="${arrSpanData[i]}" />
          <input type="button" class="cancel_btn btn border-danger" style="box-shadow: none; margin-left: 5px; padding: 3px"
          value="Cancel">`
      break;
    case 5:
      divData[i].innerHTML = `<input class="input_data input_pwd w-100" name="${nameList[i]}" type="text" value="${arrSpanData[i]}" />
          <input type="hidden" class="d-none" value="${fones.data[0].phoneId}" name="txtPhoneId">
          <input type="button" class="cancel_btn btn border-danger" style="box-shadow: none; margin-left: 5px; padding: 3px"
          value="Cancel">`
      break;
    case 6:

      divData[i].innerHTML = `<span class="room_block w-100 d-flex"><label class="me-2">Room: </label><input class="input_data input_pwd w-50 me-4" name="${nameList[i]}" type="text" value="${arrSpanData[i]}" /> Block: </span>`
      const room_block = divData[i].querySelector('.room_block');
      for (let j = 0; j < blockList.length; j++) {

        room_block.innerHTML += blockList[j] == arrSpanData[i + 1] ?
          `<input type="radio" class="form-check-input ms-4" name="${nameList[i + 1]}" value="${blockList[j]}" checked> Block ${blockList[j]}` :
          `<input type="radio" class="form-check-input ms-4" name="${nameList[i + 1]}" value="${blockList[j]}"> Block ${blockList[j]}`

      }
      divData[i].innerHTML += `<input type="button" class="cancel_btn btn border-danger" style="box-shadow: none; margin-left: 5px; padding: 3px"
        value="Cancel">`;
      break;
    default:
      divData[i].innerHTML = `<input class="input_data input_pwd w-100" name="${nameList[i]}" type="text" value="${arrSpanData[i]}" />
          <input type="button" class="cancel_btn btn border-danger" style="box-shadow: none; margin-left: 5px; padding: 3px"
          value="Cancel">`
      break;
  }
  cancelBtn = divData[i].querySelector('.cancel_btn')
  cancelBtn.addEventListener('click', (e) => {
    console.log("cancel");
    divData[i].previousElementSibling.previousElementSibling.classList.add('d-none')
    divData[i].classList.remove(...spanClassArr)
    divData[i].innerHTML = arrDivData[i]
  })
}

profileForm.addEventListener('submit', async (e) => {
  e.preventDefault()
  submitBtn.classList.add('disabled');
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
  getPhone()

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
        list[i].classList.remove('d-none')
        list[i].innerHTML = '❌ ' + error_message[key];
      }
      else {
        list[i].classList.remove('d-none')
        list[i].innerHTML = ''
      }
      console.log(`${i}: ${error_message[key]}`)
      i++;

    }
    submitBtn.classList.remove('disabled');

  } else if (response.status == 200) {
    error_message = await response.json()
    error_message = Object.fromEntries(Object.entries(error_message).filter(([_, v]) => v != null));
    console.log(error_message)
    var list = document.getElementsByClassName("error-message");
    var i = 0;
    for (var key in error_message) {
      list[i].classList.add('d-none')
      divData[i].classList.remove(...spanClassArr)
      if (i == 6) {
        divData[i].innerHTML = `
          <span class=""><div class="form-group d-flex">
              <label class="me-2">Room: </label>
              <div class="me-3 span_data">${error_message.addressAddr[0]}</div>
              <label class="me-2">Block:</label>
              <div class="span_data">${error_message.addressAddr[1]}</div>
              <button class="edit_btn border-0 bg-transparent position-absolute" style="right: 2%" type="button"
                      title="click to edit" onclick="editBtnClicked(${i})"><i class="fas fa-pen"></i></button></div>
          </span>`
      }
      else {
        divData[i].innerHTML = `<span class="span_data"></span>
              <button class="edit_btn border-0 bg-transparent position-absolute" style="right: 2%" type="button"
                title="click to edit" onclick="editBtnClicked(${i})"><i class="fas fa-pen"></i></button>`
        if (i == 5) {
          console.log(i)
          console.log(fones.data[0].phoneId);
          divData[i].innerHTML += `<input type="hidden" name="txtPhoneId" class="d-none" value="${fones.data[0].phoneId}">`
        }
        spanData = divData[i].querySelector('.span_data')
        spanData.innerHTML = error_message[key];
      }
      console.log(error_message[key]);
      i += 1;

    }

    divData = document.querySelectorAll('.div_data');
    spanData = document.querySelectorAll('.span_data');
    arrDivData = [...divData].map(data => data.innerHTML);
    arrSpanData = [...spanData].map(data => data.innerHTML);


    console.log('yes successs');
    console.log(error_message);
    submitBtn.classList.remove('disabled');

  } else if (response.status == 500) {
    error_message = await response.json()
    var list = document.getElementsByClassName("error-message");
    var i = 0;
    submitBtn.classList.remove('disabled');
    alert("something wrong with server side, please try again later");
  }
})