const form_create = document.getElementById("create-form");
form_create.addEventListener('submit', async function (event) {
    console.log(form_create)
    event.preventDefault();
    const form_data = new FormData(form_create);
    const data = Object.fromEntries(form_data);
    const submitBtn = document.querySelector('.button-submit');

    console.log(data);

    submitBtn.classList.add('disabled');
    const response = await fetch("/createAccountAPI/createAccountCustomer/0",
        {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });


    console.log(response.status)
    if (response.status == 400) {
        let error_message = await response.json()
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
        let error_message = await response.json()
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
        let error_message = await response.json()
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
