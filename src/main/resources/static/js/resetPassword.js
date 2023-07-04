const hide = ["-z-10", "w-[0%]"]
const show = ["w-[100%]"]
const avgHeight = "h-[25%]"

const resetMethodName = document.querySelector('.resetMethodName')
const proceedBtn = document.querySelector('.proceedBtn')
const finalVerify = document.querySelector('.finalVerify')

const resetMethodInput = document.querySelector('.resetMethodInput')
let resetPwdMethod = "null"
let resetMethod = 0

let resetField = document.querySelectorAll('.resetField')
const resetFieldContainer = document.querySelector('.resetFieldContainer')

const nextBtn = document.querySelectorAll('.nextBtn')
const backBtn = document.querySelector('.backBtn')

const countDown = document.querySelector('.countDown')

const otpForm = document.querySelector('.otpForm')

const input = document.querySelectorAll(".input");
const inputField = document.querySelector(".inputfield");
const resendBtn = document.querySelector('.resendBtn')

const disableLinkText = "Wait until current otp code expire to resend"
const disableLinkClass = ["cursor-default", "pointer-events-none", "opacity-25"]

const enableLinkText = "Click to get a new otp code resend"
const enableLinkClass = ["text-blue-500", "hover:underline", "hover:text-blue-600"]
const expandHeight = "h-[45%]"
const errMsg = document.querySelector('.errMsg')

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+(\.[^\s@]+)*$/;
const numberRegex = /^[1-9]\d*$/;


let i = 0

let inputCount = 0,
  finalInput = "";
let maxTime = 0
let isBlock = false

nextBtn.forEach(btn => {
  btn.addEventListener('mouseup', (e) => {
    resetMethod = document.querySelector('input[name=btnRadioVerification]:checked').value === 'SMS' ? 1 : 2

    if (resetMethod === 1) {
      if (i === 0) {
        console.log(i)
        resetField[i].classList.add(...hide)
        resetField[i].classList.remove(...show)
        i++
        resetMethodName.textContent = "Please enter your phone number"
        resetMethodInput.value = ""
        errMsg.textContent = ""
        resetField[i].classList.remove(...hide)
        resetField[i].classList.add(...show)
      }
      else {
        console.log(i)
        if (document.querySelector('.resetMethodInput').value != null && document.querySelector('.resetMethodInput').value.length >= 10 && numberRegex.test(document.querySelector('.resetMethodInput').value)) {
          errMsg.classList.add('hidden')
          resetField[i].classList.add(...hide)
          resetField[i].classList.remove(...show)
          i++
          finalVerify.querySelector('.enterOtp').classList.remove('hidden')
          finalVerify.querySelector('.sendMail').classList.add('hidden')
          resetField[i].classList.remove(...hide)
          resetField[i].classList.add(...show)
          if (i >= resetField.length - 1) {
            resetFieldContainer.classList.remove(avgHeight)
            resetFieldContainer.classList.add(expandHeight)
            startInput()
          }
        }
        else {
          if (document.querySelector('.resetMethodInput').value.length < 10)
            errMsg.textContent = "phone number must contain equal or more than 10 digits"
          else if (!numberRegex.test(document.querySelector('.resetMethodInput').value)) {
            errMsg.textContent = "phone number must only contain digit"
          }
          errMsg.classList.remove('hidden')
        }

      }
    }
    if (resetMethod === 2) {
      if (i === 0) {
        console.log(i)
        resetField[i].classList.add(...hide)
        resetField[i].classList.remove(...show)
        i++
        resetMethodName.textContent = "Please enter your email"
        resetMethodInput.value = ""
        errMsg.textContent = ""
        resetField[i].classList.remove(...hide)
        resetField[i].classList.add(...show)
      }
      else {
        console.log(i)
        if (document.querySelector('.resetMethodInput').value != null && emailRegex.test(document.querySelector('.resetMethodInput').value)) {
          resetField[i].classList.add(...hide)
          resetField[i].classList.remove(...show)
          i++
          finalVerify.querySelector('.sendMail').classList.remove('hidden')
          finalVerify.querySelector('.enterOtp').classList.add('hidden')
          resetField[i].classList.remove(...hide)
          resetField[i].classList.add(...show)
        }
        else {
          if (!emailRegex.test(document.querySelector('.resetMethodInput').value)) {
            errMsg.classList.remove('hidden')
            errMsg.textContent = "please enter your real email"
          }
        }
      }
    }




  })
})
backBtn.addEventListener('click', () => {
  resetField[i].classList.add(...hide)
  resetField[i].classList.remove(...show)
  i--
  resetField[i].classList.remove(...hide)
  resetField[i].classList.add(...show)
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
const startInput = () => {
  maxTime = 5
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
  otpCoundown()
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
  startInput()
})

function otpCoundown() {
  const interval = setInterval(() => {
    countDown.innerHTML = `${maxTime}s`
    maxTime--
    if (maxTime < 0) {
      clearInterval(interval)
      isBlock = true
      lockInput()
    }
  }, 1000)
}


