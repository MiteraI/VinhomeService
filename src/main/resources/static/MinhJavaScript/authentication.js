const logoutBtn = document.querySelector('.logout_btn');
const loginBtn = document.querySelector('.login_btn');
const header_optionContainer = document.querySelector('.header_container');
function getSessionData() {
    fetch('/api/order/getSession')
        .then(response => response.json())
        .then(data => {
            loginBtn.classList.add('hide');
            header_optionContainer.classList.remove('hide');
        })
        .catch((err)=>{
        console.log(err);
        loginBtn.classList.remove('hide');
    })
};
function logOut(){
   fetch('/api/logout', {method: 'POST'}).then(window.location.replace("/"))
}