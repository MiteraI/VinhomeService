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
const backBtn = document.querySelector('.backBtn')

const countDown = document.querySelector('.countDown')

const otpForm = document.querySelector('.otpForm')
let otp_merge = "";
let otpDuration = 0

const input = document.querySelectorAll(".input");
const inputField = document.querySelector(".inputfield");

const disableLinkText = "Wait until current otp code expire to resend"
const disableLinkClass = ["cursor-default", "pointer-events-none", "opacity-25"]

const errTxt = document.querySelector('.errTxt')
const enableLinkText = "Click to get a new otp code resend"
const enableLinkClass = ["text-blue-500", "hover:underline", "hover:text-blue-600"]
const expandHeight = "h-[45%]"
const errMsg = document.querySelector('.errMsg')

const verifyBtn = document.querySelector('.verifyBtn')


const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+(\.[^\s@]+)*$/;
const numberRegex = /^\d+$/;

let value = "";
let responseTxt = ""

let i = 0

let inputCount = 0,
  finalInput = "";
let maxTime = 0
let isBlock = false
let canProceed = false

let methodUsedToReset = ""
let resetMethodInputValue = ""



nextBtn.forEach(btn => {
  btn.addEventListener('click', (e) => {
    e.preventDefault()
    resetMethod = document.querySelector('input[name=btnRadioVerification]:checked').value === 'SMS' ? 1 : 2

    value = resetMethod === 1 ? document.querySelector('input[value="SMS"]').value : document.querySelector('input[value="EMAIL"]').value

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
      else if (i === 1) {
        console.log(i)
        if (document.querySelector('.resetMethodInput').value != null && document.querySelector('.resetMethodInput').value.length >= 10 && numberRegex.test(document.querySelector('.resetMethodInput').value)) {
          methodUsedToReset = value
          resetMethodInputValue = document.querySelector('.resetMethodInput').value
          sendForgetPasswordMethod(methodUsedToReset, resetMethodInputValue).then((result) => {
            canProceed = result.canProceed
            if (canProceed) {
              console.log("this shit not working")
              errMsg.classList.add('hidden')
              resetField[i].classList.add(...hide)
              resetField[i].classList.remove(...show)
              i++
              finalVerify.querySelector('.enterOtp').classList.remove('hidden')
              finalVerify.querySelector('.sendMail').classList.add('hidden')
              resetField[i].classList.remove(...hide)
              resetField[i].classList.add(...show)
            }
            if (!canProceed) {
              alert(result.message)
            }
          })



        }
        else {
          if (document.querySelector('.resetMethodInput').value.length < 10)
            errMsg.textContent = "phone number must contain equal or more than 10 digits"
          else {
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
      else if (i === 1) {
        console.log(i)
        if (document.querySelector('.resetMethodInput').value != null && emailRegex.test(document.querySelector('.resetMethodInput').value)) {
          methodUsedToReset = value
          resetMethodInputValue = document.querySelector('.resetMethodInput').value

          console.log("sending mail")
          canProceed = sendForgetPasswordMethod(methodUsedToReset, resetMethodInputValue).then((result) => {
            canProceed = result.canProceed
            console.log(canProceed)

            if (canProceed) {
              console.log("why this shit not working")
              resetField[i].classList.add(...hide)
              resetField[i].classList.remove(...show)
              i++
              finalVerify.querySelector('.sendMail').classList.remove('hidden')
              finalVerify.querySelector('.enterOtp').classList.add('hidden')
              resetField[i].classList.remove(...hide)
              resetField[i].classList.add(...show)
            }
            if (!canProceed) {
              alert(result.message)
            }
          })


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


// const updateInputConfig = (element, disabledStatus) => {
//   element.disabled = disabledStatus;
//   if (!disabledStatus) {
//     element.focus();
//   } else {
//     element.blur();
//   }
// };

// input.forEach((element) => {
//   element.addEventListener("keyup", (e) => {
//     e.target.value = e.target.value.replace(/[^0-9]/g, "");
//     let { value } = e.target;

//     if (value.length == 1) {
//       updateInputConfig(e.target, true);
//       if (inputCount <= input.length - 1 && e.key != "Backspace") {
//         finalInput += value;
//         if (inputCount < input.length - 1) {
//           updateInputConfig(e.target.nextElementSibling, false);
//         }
//       }
//       inputCount += 1;
//     } else if (value.length == 0 && e.key == "Backspace") {
//       finalInput = finalInput.substring(0, finalInput.length - 1);
//       if (inputCount == 0) {
//         updateInputConfig(e.target, false);
//         return false;
//       }
//       updateInputConfig(e.target, true);
//       e.target.previousElementSibling.value = "";
//       updateInputConfig(e.target.previousElementSibling, false);
//       inputCount -= 1;
//     } else if (value.length > 1) {
//       e.target.value = value.split("")[0];
//     }
//   });
// });

// window.addEventListener("keyup", (e) => {
//   if (inputCount > input.length - 1) {
//     if (e.key == "Backspace" && isBlock === false) {
//       finalInput = finalInput.substring(0, finalInput.length - 1);
//       updateInputConfig(inputField.lastElementChild, false);
//       inputField.lastElementChild.value = "";
//       inputCount -= 1;
//     }
//   }
// });

// //Start
// const startInput = (duration) => {
//   maxTime = duration
//   countDown.innerHTML = `${maxTime}s`
//   inputCount = 0;
//   finalInput = "";
//   input.forEach((element) => {
//     element.value = "";
//   });
//   updateInputConfig(inputField.firstElementChild, false);
//   resendBtn.classList.add(...disableLinkClass)
//   resendBtn.classList.remove(...enableLinkClass)
//   resendBtn.title = disableLinkText
//   otpCoundown(maxTime)
// };

// function lockInput() {

//   input.forEach(i =>
//     i.addEventListener('keydown', (e) => {
//       if (isBlock === true) {
//         i.disabled = true
//         e.preventDefault()
//       }
//       else {
//         i.disabled = false
//         e.returnValue = true
//       }
//     })
//   )


//   resendBtn.classList.remove(...disableLinkClass)
//   resendBtn.classList.add(...enableLinkClass)
//   resendBtn.title = enableLinkText
// }

// resendBtn.addEventListener('click', (e) => {
//   e.preventDefault()
//   isBlock = false
//   for (let i = 0; i < input.length; i++) {
//     input[i].value = "";
//   }
//   chooseMethod()
// })

// function otpCoundown(duration) {
//   const interval = setInterval(() => {
//     countDown.innerHTML = `${duration}s`
//     duration--
//     if (duration < 0) {
//       maxTime = duration
//       clearInterval(interval)
//       isBlock = true
//       otp_merge = ""
//       lockInput()
//     }
//   }, 1000)
// }

// verifyBtn.addEventListener('click', (e) => {
//   e.preventDefault()
//   if (maxTime > 0) {
//     verify()
//   }
// })

// async function verify() {
//   let sendBody = {
//     accountname: username,
//     OTP: otp_merge
//   }

//   for (let i = 0; i < input.length; i++) {
//     if (input[i].value != "") {
//       console.log(input[i].value);
//       otp_merge += input[i].value
//       console.log(otp_merge)
//     }
//   }
//   sendBody.OTP = otp_merge
//   console.log(sendBody.accountname + "   " + sendBody.OTP)
//   let response = await fetch(`http://localhost:8080/esms/validateOTP`, {
//     method: "POST",
//     headers: {
//       'Content-Type': 'application/json'
//     },
//     body: JSON.stringify(sendBody)
//   })

//   let responseStatus = await response.status;
//   let toText = await response.text()
//   if (responseStatus < 200 || responseStatus > 300) {
//     otp_merge = ""
//     errTxt.innerText = toText
//     errTxt.classList.remove('hidden')
//   } else {
//     otp_merge = ""
//     breakFlag = true;
//     errTxt.classList.add('hidden')
//     window.location.href = "/"
//     console.log("pass alert")

//   }

// };


// async function chooseMethod() {
//   console.log(value)
//   let response = await fetch(`http://localhost:8080/createAccountAPI/verificationMethod/${username}/${value}`, { method: "POST" })
//   let status = await response.status
//   let toText = await response.text()
//   console.log(toText)
//   if (status >= 200 || status <= 300) {
//     if (value == "EMAIL") {
//       alert("yes, already send to your mail, check if you see mail, if not, try again later in profile")
//       console.log("pass alert")
//       delay(1000).then(() => {
//         console.log("inside delay")
//         getHomePage()
//       })
//     } else {
//       breakFlag = false; console.log(breakFlag)
//       countDown.innerHTML = ""
//       errTxt.classList.add('hidden')

//       for (let i = 0; i < input.length; i++) {
//         input[i].value = "";
//       }
//       toText = toText.split("=")
//       otpDuration = parseInt(toText[1]);
//       console.log(otpDuration)
//     }
//   } else {
//     alert("go back to homepage, error in sending verification, try again in profile")
//   }
//   startInput(otpDuration)
// }

function sendForgetPasswordMethod(method, value) {
  let sendBody = {
    method: "",
    input: ""
  }
  let result = {
    canProceed: false,
    message: ""
  }

  sendBody.method = method
  sendBody.input = value

  return fetch(`/createAccountAPI/forgetAccountMethod`, {
    method: "POST",
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(sendBody)
  }).then(res => {
    console.log(res.status)
    if (res.status == 200) {
      result.canProceed = true
      console.log(canProceed)
    }
    if (res.status > 200 && res.status <= 400) {
      result.canProceed = false
      console.log("error")
      return res.text().then(text => {
        result.message = text
        return result
      })
    }
    return result;
  }).catch(err => console.log(err))
}


