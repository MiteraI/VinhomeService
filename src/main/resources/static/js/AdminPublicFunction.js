// document.querySelector("#search").addEventListener('input', sorttable)
// function sorttable() {
//     var search_input = document.querySelector("#search").value.toLowerCase();;
//     console.log(search_input)
//     const list = document.querySelectorAll('.item-to-search')
//     list.forEach((element) => {
//         text_element = element.textContent;
//         if (text_element.toLowerCase().includes(search_input.toLowerCase())) {
//             element.style.display = '';
//         } else {
//             element.style.display = 'none';
//         }
//     });
// }
function applyHeaderToSearchCategory(){
    console.log("load search category")
    let title = document.getElementById("dropdown-category");
    let listHeading = document.querySelectorAll('.heading');
    var output = "";
    output += `<a class="dropdown-item" onclick="updateServiceCategory(this.textContent)">none</a>`
    listHeading.forEach( (item) => {
        output += 
        `
        <a class="dropdown-item" onclick="updateServiceCategory(this.textContent)">${item.textContent.trim()}</a>
        `
        }    
    )
    title.innerHTML = output;
    console.log(title.innerHTML)
}

function updateServiceCategory(name) {
    console.log(" update search category")
    console.log(name);
    document.getElementById("dropdown-title").textContent = name.trim();
    const listHeading = document.querySelectorAll('.heading');
    if(name.toLowerCase() === "none"){
        listHeading.forEach( (item) =>{
        item.style.color = "black";
    })
    }else{
        listHeading.forEach( (item) =>{
            console.log(listHeading.textContent)
        if(item.textContent.trim().toLowerCase().includes(name.trim().toLowerCase()) ){
            item.style.color = "green";
        }else{
            item.style.color = "black";
        }
    })
    }
}


document.getElementById("search").addEventListener('input', sorttableCategory)
function sorttableCategory() {
    var getCategoryTitle = document.getElementById("dropdown-title").textContent.trim();
    var search_input = document.querySelector("#search").value.toLowerCase();;
    const list = document.querySelectorAll('.item-to-search')
    const listCategory = document.getElementsByClassName(getCategoryTitle.replace(/\s/g, "").trim().toLowerCase())
    console.log(list)
    console.log(listCategory)
    console.log(getCategoryTitle)
    if(getCategoryTitle == "none"){
        console.log("inside none search")
        list.forEach((element) => {
        text_element = element.textContent.trim();
        if (text_element.toLowerCase().includes(search_input.trim().toLowerCase())) {
            element.style.display = '';
        } else {
            element.style.display = 'none';
        }
    });
    }else{
        console.log("inside class search")
        list.forEach((element) => {
        var getChild = element.getElementsByTagName("td")
        console.log(getChild)
        for(var i in getChild){
            if(getChild[i].className == getCategoryTitle.replace(/\s/g, "").trim().toLowerCase()){
            text_element = getChild[i].textContent.trim();
            console.log(text_element)
            if (text_element.toLowerCase().includes(search_input.trim().toLowerCase())) {
                element.style.display = '';
            } else {
                element.style.display = 'none';
            } 
            console.log(getChild[i].className)
        } 
        }
        
    });
    }
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


