document.querySelector("#search").addEventListener('input', sorttable)
function sorttable() {
    var search_input = document.querySelector("#search").value.toLowerCase();;
    console.log(search_input)
    const list = document.querySelectorAll('.item-to-search')
    list.forEach((element) => {
        text_element = element.textContent;
        if (text_element.toLowerCase().includes(search_input.toLowerCase())) {
            element.style.display = '';
        } else {
            element.style.display = 'none';
        }
    });
}
function sortById(htmlstuff) {
    console.log(htmlstuff.className);
    console.log(htmlstuff.id)
    var ascOrNot = htmlstuff.className; var ID;
    if (htmlstuff.id == "heading-id-w") {
        ID = "worker-list"
    } else {
        ID = "customer-list"
    }
    if (ascOrNot == "asc") {
        htmlstuff.className = "desc"
        data = data.sort((a, b) => { return a.accountId > b.accountId ? 1 : -1 })
        console.log(htmlstuff.className)

        printRow(data, ID)
    } else {
        htmlstuff.className = "asc"
        data = data.sort((a, b) => { return a.accountId > b.accountId ? -1 : 1 })
        console.log(htmlstuff.className)
        printRow(data, ID)
    }

}
function sortByFirstname(htmlstuff) {
    var ID;
    if (htmlstuff.id == "heading-firstname-w") {
        ID = "worker-list"
    } else {
        ID = "customer-list"
    }
    var ascOrNot = htmlstuff.className;
    if (ascOrNot == "asc") {
        htmlstuff.className = "desc"
        data = data.sort((a, b) => { return a.firstName.toLowerCase() > b.firstName.toLowerCase() ? 1 : -1 })
        console.log(htmlstuff.className)
        printRow(data, ID)
    } else {
        htmlstuff.className = "asc"
        data = data.sort((a, b) => { return a.firstName.toLowerCase() > b.firstName.toLowerCase() ? -1 : 1 })
        console.log(htmlstuff.className)
        printRow(data, ID)
    }
}
function sortByLastname(htmlstuff) {
    var ID;
    if (htmlstuff.id == "heading-lastname-w") {
        ID = "worker-list"
    } else {
        ID = "customer-list"
    }
    var ascOrNot = htmlstuff.className;
    if (ascOrNot == "asc") {
        htmlstuff.className = "desc"
        data = data.sort((a, b) => { return a.lastName.toLowerCase() > b.lastName.toLowerCase() ? 1 : -1 })
        console.log(htmlstuff.className)
        printRow(data, ID)
    } else {
        htmlstuff.className = "asc"
        data = data.sort((a, b) => { return a.lastName.toLowerCase() > b.lastName.toLowerCase() ? -1 : 1 })
        console.log(htmlstuff.className)
        printRow(data, ID)
    }
}
function delay(time) {
    return new Promise(resolve => setTimeout(resolve, time));
}
//console.log(document.querySelectorAll('.item-to-search')[0].textContent)
function logOut() {
    fetch('/api/logout', { method: 'POST' }).then(window.location.replace("/"))
}
function getHomePage() {
    console.log("inside return main page")
    window.location.href = "/";
}


