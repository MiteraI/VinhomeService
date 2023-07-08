const hide = ["-z-10", "w-[0%]"]
const show = ["w-[100%]"]
const avgHeight = "h-[25%]"

const proceedBtn = document.querySelector('.proceedBtn')
const finalVerify = document.querySelector('.finalVerify')

const resetMethodName = document.querySelector('.resetMethodName')
const resendBtn = document.querySelector('.resendBtn')
const resetMethodInput = document.querySelector('.resetMethodInput')
let resetPwdMethod = null
let resetMethod = 0

let resetField = document.querySelectorAll('.resetField')
const resetFieldContainer = document.querySelector('.resetFieldContainer')

const nextBtn = document.querySelectorAll('.nextBtn')

const countDown = document.querySelector('.countDown')

const otpForm = document.querySelector('.otpForm')
let otp_merge = "";
let otpDuration = 0

const input = document.querySelectorAll(".input");
const in1 = document.querySelector('#otc-1')
const inputField = document.querySelector(".inputfield");

const queryString = window.location.search;
const username = new URLSearchParams(queryString).get('username');


const disableLinkText = "Wait until current otp code expire to resend"
const disableLinkClass = ["cursor-default", "pointer-events-none", "opacity-25"]

const errTxt = document.querySelector('.errTxt')
const enableLinkText = "Click to get a new otp code resend"
const enableLinkClass = ["text-blue-500", "hover:underline", "hover:text-blue-600"]
const expandHeight = "h-[45%]"

const verifyBtn = document.querySelector('.verifyBtn')

let btnRadioVerification = document.querySelectorAll(".btnRadioVerification");
let value = "";

let breakFlag = false;
let i = 0

let inputCount = 0,
  finalInput = "";
let maxTime = 0
let isBlock = false

nextBtn.forEach(btn => {
  btn.addEventListener('mouseup', (e) => {
    resetMethod = document.querySelector('input[name=btnRadioVerification]:checked').value === 'SMS' ? 1 : 2
    value = resetMethod === 1 ? document.querySelector('input[value="SMS"]').value : document.querySelector('input[value="EMAIL"]').value

    if (resetMethod === 1) {
      console.log(i)
      resetField[i].classList.add(...hide)
      resetField[i].classList.remove(...show)
      i++
      finalVerify.querySelector('.enterOtp').classList.remove('hidden')
      finalVerify.querySelector('.sendMail').classList.add('hidden')
      resetField[i].classList.remove(...hide)
      resetField[i].classList.add(...show)
      resetFieldContainer.classList.remove(avgHeight)
      resetFieldContainer.classList.add(expandHeight)
      chooseMethod()
    }
    if (resetMethod === 2) {
      console.log(i)
      resetField[i].classList.add(...hide)
      resetField[i].classList.remove(...show)
      i++
      finalVerify.querySelector('.sendMail').classList.remove('hidden')
      finalVerify.querySelector('.enterOtp').classList.add('hidden')
      resetField[i].classList.remove(...hide)
      resetField[i].classList.add(...show)
      chooseMethod()
    }




  })
})


const updateInputConfig = (element, disabledStatus) => {
  element.disabled = disabledStatus;
  if (!disabledStatus) {
    element.focus();
  } else {
    element.blur();
  }
};

input.forEach((element) => {
  element.addEventListener("keyup", (e) => {
    e.target.value = e.target.value.replace(/[^0-9]/g, "");
    let { value } = e.target;

    if (value.length == 1) {
      updateInputConfig(e.target, true);
      if (inputCount <= input.length - 1 && e.key != "Backspace") {
        finalInput += value;
        if (inputCount < input.length - 1) {
          updateInputConfig(e.target.nextElementSibling, false);
        }
      }
      inputCount += 1;
    } else if (value.length == 0 && e.key == "Backspace") {
      finalInput = finalInput.substring(0, finalInput.length - 1);
      if (inputCount == 0) {
        updateInputConfig(e.target, false);
        return false;
      }
      updateInputConfig(e.target, true);
      e.target.previousElementSibling.value = "";
      updateInputConfig(e.target.previousElementSibling, false);
      inputCount -= 1;
    } else if (value.length > 1) {
      e.target.value = value.split("")[0];
    }
  });
});

window.addEventListener("keyup", (e) => {
  if (inputCount > input.length - 1) {
    if (e.key == "Backspace" && isBlock === false) {
      finalInput = finalInput.substring(0, finalInput.length - 1);
      updateInputConfig(inputField.lastElementChild, false);
      inputField.lastElementChild.value = "";
      inputCount -= 1;
    }
  }
});

//Start
const startInput = (duration) => {
  maxTime = duration
  countDown.innerHTML = `${maxTime}s`
  inputCount = 0;
  finalInput = "";
  input.forEach((element) => {
    element.value = "";
  });
  updateInputConfig(inputField.firstElementChild, false);
  resendBtn.classList.add(...disableLinkClass)
  resendBtn.classList.remove(...enableLinkClass)
  resendBtn.title = disableLinkText
  otpCoundown(maxTime)
};

function lockInput() {

  input.forEach(i =>
    i.addEventListener('keydown', (e) => {
      if (isBlock === true) {
        i.disabled = true
        e.preventDefault()
      }
      else {
        i.disabled = false
        e.returnValue = true
      }
    })
  )


  resendBtn.classList.remove(...disableLinkClass)
  resendBtn.classList.add(...enableLinkClass)
  resendBtn.title = enableLinkText
}

resendBtn.addEventListener('click', (e) => {
  e.preventDefault()
  isBlock = false
  for (let i = 0; i < input.length; i++) {
    input[i].value = "";
  }
  chooseMethod()
})

function otpCoundown(duration) {
  const interval = setInterval(() => {
    countDown.innerHTML = `${duration}s`
    duration--
    if (duration < 0) {
      maxTime = duration
      clearInterval(interval)
      isBlock = true
      otp_merge = ""
      lockInput()
    }
  }, 1000)
}


function splitNumber(e) {
  let data = e.data || e.target.value; // Chrome doesn't get the e.data, it's always empty, fallback to value then.
  if (!data) return; // Shouldn't happen, just in case.
  if (data.length === 1) return; // Here is a normal behavior, not a paste action.
  popuNext(e.target, data);
  inputCount = input.length - 1
  //for (i = 0; i < data.length; i++ ) { ins[i].value = data[i]; }
}
function popuNext(el, data) {
  el.value = data[0]; // Apply first item to first input
  data = data.substring(1); // remove the first char.
  if (el.nextElementSibling && data.length) {
    // Do the same with the next element and next data
    popuNext(el.nextElementSibling, data);
  }
};

in1.addEventListener('input', splitNumber)


verifyBtn.addEventListener('click', (e) => {
  e.preventDefault()
  if (maxTime > 0) {
    verify()
  }
})

async function verify() {
  let sendBody = {
    accountname: username,
    OTP: otp_merge
  }
  let toText = ''

  for (let i = 0; i < input.length; i++) {
    if (input[i].value != "") {
      console.log(input[i].value);
      otp_merge += input[i].value
      console.log(otp_merge)
    }
  }
  sendBody.OTP = otp_merge
  console.log(sendBody.accountname + "   " + sendBody.OTP)
  let response = fetch(`/esms/validateOTP`, {
    method: "POST",
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(sendBody)
  }).then(res => {
    let responseStatus = res.status;
    console.log(responseStatus)
    res.text().then(text => {
      if (responseStatus < 200 || responseStatus > 300) {
        otp_merge = ""
        errTxt.innerText = text
        errTxt.classList.remove('hidden')
        inputCount = 0;
        finalInput = "";
        input.forEach((element) => {
          element.value = "";
        });
        updateInputConfig(inputField.firstElementChild, false);
      }
      if (responseStatus === 200) {
        console.log("status: 200")
        otp_merge = ""
        breakFlag = true;
        errTxt.classList.add('hidden')
        window.location.href = "/"
        console.log("pass alert")

      }
    })


  }).catch(err => console.log(err))



};

async function chooseMethod() {
  console.log(value)
  let response = await fetch(`http://localhost:8080/createAccountAPI/verificationMethod/${username}/${value}`, { method: "POST" })
  let status = await response.status
  let toText = await response.text()
  console.log(toText)
  if (status >= 200 || status <= 300) {
    if (value == "EMAIL") {
      alert("yes, already send to your mail, check if you see mail, if not, try again later in profile")
      console.log("pass alert")
    } else {
      breakFlag = false; console.log(breakFlag)
      countDown.innerHTML = ""
      errTxt.classList.add('hidden')

      for (let i = 0; i < input.length; i++) {
        input[i].value = "";
      }
      toText = toText.split("=")
      otpDuration = parseInt(toText[1]);
      console.log(otpDuration)
    }
  } else {
    alert("go back to homepage, error in sending verification, try again in profile")
  }
  startInput(otpDuration)
}




