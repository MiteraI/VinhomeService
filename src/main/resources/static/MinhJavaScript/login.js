const form = document.getElementById("login-form");
const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const loginSpan = document.getElementById("loginStatusText");

form.addEventListener("submit", function (event) {
  event.preventDefault(); // Prevent form submission

  const username = usernameInput.value;
  const password = passwordInput.value;

  // Call a function to make the Axios POST request with the form data
  loginWithAccountNameAndPassword(username, password);
});

let loginWithAccountNameAndPassword = async(username, password) => {
  const loginData = {
    username: username,
    password: password,
  };
  await axios
    .post("/api/login", loginData)
    .then((response) => {
      if (response.status === 200) {
        // Handle successful login
        window.location.replace("/");
      } else {
        // Handle other successful responses or unexpected responses
        console.log("Unexpected response:", response);
      }
    })
    .catch((error) => {
      if (error.response.status === 401) {
        console.log("Account name or password is wrong");
        loginSpan.innerHTML = "Account name or password is wrong"
      }
      else {
        console.log(error);

      }
    });
};
