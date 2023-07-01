const countDown = document.querySelector('.countDown')

const otpForm = document.querySelector('.otpForm')

const input = document.querySelectorAll(".input");
const inputField = document.querySelector(".inputfield");
const resendBtn = document.querySelector('.resendBtn')

const disableLinkText = "Wait until current otp code expire to resend"
const disableLinkClass = ["cursor-default", "pointer-events-none", "opacity-25"]
const enableLinkText = "Click to get a new otp code resend"
const enableLinkClass = ["text-blue-300", "hover:underline", "hover:text-blue-400"]

let inputCount = 0,
  finalInput = "";
let maxTime = 5

countDown.innerHTML = `${maxTime}s`
otpCoundown()

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
    if (e.key == "Backspace") {
      finalInput = finalInput.substring(0, finalInput.length - 1);
      updateInputConfig(inputField.lastElementChild, false);
      inputField.lastElementChild.value = "";
      inputCount -= 1;
    }
  }
});

//Start
const startInput = () => {
  inputCount = 0;
  finalInput = "";
  input.forEach((element) => {
    element.value = "";
  });
  updateInputConfig(inputField.firstElementChild, false);
  resendBtn.classList.add(...disableLinkClass)
  resendBtn.classList.remove(...enableLinkClass)
  resendBtn.title = disableLinkText
};

window.onload = startInput();

function lockInput() {
  input.forEach(i =>
    i.addEventListener('keydown', (e) => {
      i.disabled = true
      e.preventDefault()
    })
  )
  resendBtn.classList.remove(...disableLinkClass)
  resendBtn.classList.add(...enableLinkClass)
  resendBtn.title = enableLinkText
}

function otpCoundown() {
  const interval = setInterval(() => {
    countDown.innerHTML = `${maxTime}s`
    maxTime--
    if (maxTime < 0) {
      clearInterval(interval)
      lockInput()
    }
  }, 1000)
}
