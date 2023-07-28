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
function applyHeaderToSearchCategory() {
    let title = document.getElementById("dropdown-category");
    let listHeading = document.querySelectorAll('.heading');
    var output = "";
    output += `<a class="dropdown-item cursor-pointer capitalize" onclick="updateServiceCategory(this.textContent)">none</a>`
    listHeading.forEach((item) => {
        output +=
            `
        <a class="dropdown-item cursor-pointer" onclick="updateServiceCategory(this.textContent)">${item.textContent.charAt(0).toUpperCase() + item.textContent.slice(1)}</a>
        `
    }
    )
    title.innerHTML = output;
}

function updateServiceCategory(name) {
    console.log(" update search category")
    console.log(name);
    document.getElementById("dropdown-title").textContent = name.charAt(0).toUpperCase() + name.slice(1);
    const listHeading = document.querySelectorAll('.heading');
    if (name.toLowerCase() === "none") {
        listHeading.forEach((item) => {
            item.classList.add('text-white')
            item.classList.remove('text-yellow-info')
        })
    } else {
        listHeading.forEach((item) => {
            console.log(listHeading.textContent)
            if (item.textContent.trim().toLowerCase().includes(name.trim().toLowerCase())) {
                item.classList.remove('text-white')
                item.classList.add('text-yellow-info')
            } else {
                item.classList.add('text-white')
                item.classList.remove('text-yellow-info')
            }
        })
    }
}


document.getElementById("search").addEventListener('input', sorttableCategory)
function sorttableCategory() {
    var getCategoryTitle = document.getElementById("dropdown-title").textContent.trim();
    var search_input = document.querySelector("#search").value.toLowerCase();
    var getClassName = ''
    const list = document.querySelectorAll('.item-to-search')
    const listCategory = document.getElementsByClassName(getCategoryTitle.replace(/\s/g, "").trim().toLowerCase())
    if (getCategoryTitle.toLowerCase() == "none") {
        list.forEach((element) => {
            text_element = element.textContent.trim();
            if (text_element.toLowerCase().includes(search_input.trim().toLowerCase())) {
                element.style.display = '';
            } else {
                element.style.display = 'none';
            }
        });
    } else {
        list.forEach((element) => {
            var getChild = element.getElementsByTagName("td")
            console.log(getChild)
            for (var i in getChild) {
                getCategoryTitle = getCategoryTitle.replace(/\s/g, "").toLowerCase().trim()
                console.log(getCategoryTitle)
                getClassName = getChild[i].className === undefined ? undefined : Array.from(getChild[i].classList).find(cname => cname.toLowerCase() == getCategoryTitle)
                if (getClassName !== undefined) {
                    console.log(i + " " + getCategoryTitle)
                    if (getClassName == getCategoryTitle) {
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
            }

        });
    }
}
function sortById(columnIndex) {
    var table = document.querySelector(".table-worker") || document.querySelector(".table-customer") || document.querySelector(".sort");
    var tbody = table.tBodies[0];
    var rows = Array.from(tbody.rows);
    var sortOrder = table.getAttribute('data-sort-order') || 'asc';
    rows.sort((a, b) => {
        var x = Number(a.cells[columnIndex].textContent);
        var y = Number(b.cells[columnIndex].textContent);
        return sortOrder === 'asc' ? x - y : y - x;
    });
    rows.forEach(row => tbody.appendChild(row));
    sortOrder = sortOrder === 'asc' ? 'desc' : 'asc';
    table.setAttribute('data-sort-order', sortOrder);

}
function sortByFirstname(columnIndex) {
    var table = document.querySelector(".table-worker") || document.querySelector(".table-customer");
    var tbody = table.tBodies[0];
    var rows = Array.from(tbody.rows);
    var sortOrder = table.getAttribute('data-sort-order') || 'asc';
    rows.sort((a, b) => {
        var x = a.cells[columnIndex].textContent.toLowerCase()
        var y = b.cells[columnIndex].textContent.toLowerCase();
        console.log(columnIndex)
        console.log(a.cells[columnIndex])
        console.log(b.cells[columnIndex])
        console.log(a.cells[columnIndex].textContent.toLowerCase())
        console.log(b.cells[columnIndex].textContent.toLowerCase())
        return sortOrder === 'asc' ? x.localeCompare(y) : y.localeCompare(x);
    });
    rows.forEach(row => tbody.appendChild(row));
    sortOrder = sortOrder === 'asc' ? 'desc' : 'asc';
    table.setAttribute('data-sort-order', sortOrder);
}

function sortByLastname(columnIndex) {
    var table = document.querySelector(".table-worker") || document.querySelector(".table-customer");
    var tbody = table.tBodies[0];
    var rows = Array.from(tbody.rows);
    var sortOrder = table.getAttribute('data-sort-order') || 'asc';
    rows.sort((a, b) => {
        var x = a.cells[columnIndex].textContent.toLowerCase()
        var y = b.cells[columnIndex].textContent.toLowerCase();
        console.log(columnIndex)
        console.log(a.cells[columnIndex])
        console.log(b.cells[columnIndex])
        console.log(a.cells[columnIndex].textContent.toLowerCase())
        console.log(b.cells[columnIndex].textContent.toLowerCase())
        return sortOrder === 'asc' ? x.localeCompare(y) : y.localeCompare(x);
    });
    rows.forEach(row => tbody.appendChild(row));
    sortOrder = sortOrder === 'asc' ? 'desc' : 'asc';
    table.setAttribute('data-sort-order', sortOrder);
}
// function sortById(htmlstuff) {
//     console.log(htmlstuff.className);
//     console.log(htmlstuff.id)
//     var ascOrNot = Array.from(htmlstuff.classList).find(className => className === 'asc');
//     console.log("asc" + ascOrNot)
//     var ID;
//     if (htmlstuff.id == "heading-id-w") {
//         ID = "worker-list"
//     } else {
//         ID = "customer-list"
//     }
//     if (ascOrNot == "asc") {
//         htmlstuff.classList.toggle("asc")
//         data = data.sort((a, b) => { return a.accountId > b.accountId ? -1 : 1 })
//         console.log("desc")
//         console.log(htmlstuff.className)

//         printRow(data, ID)
//     } else {
//         htmlstuff.classList.toggle("asc")
//         data = data.sort((a, b) => { return a.accountId > b.accountId ? 1 : -1 })
//         console.log("asc")
//         console.log(htmlstuff.className)
//         printRow(data, ID)
//     }

// }
// function sortByFirstname(htmlstuff) {
//     var ascOrNot = Array.from(htmlstuff.classList).find(className => className === 'asc');
//     var ID;
//     if (htmlstuff.id == "heading-firstname-w") {
//         ID = "worker-list"
//     } else {
//         ID = "customer-list"
//     }
//     if (ascOrNot == "asc") {
//         htmlstuff.classList.toggle("asc")
//         data = data.sort((a, b) => { return a.firstName.toLowerCase() > b.firstName.toLowerCase() ? -1 : 1 })
//         console.log(htmlstuff.className)

//         printRow(data, ID)
//     } else {
//         htmlstuff.classList.toggle("asc")
//         data = data.sort((a, b) => { return a.firstName.toLowerCase() > b.firstName.toLowerCase() ? 1 : -1 })
//         console.log(htmlstuff.className)
//         printRow(data, ID)
//     }
// }
// function sortByLastname(htmlstuff) {
//     var ascOrNot = Array.from(htmlstuff.classList).find(className => className === 'asc');
//     var ID;
//     if (htmlstuff.id == "heading-lastname-w") {
//         ID = "worker-list"
//     } else {
//         ID = "customer-list"
//     }
//     if (ascOrNot == "asc") {
//         htmlstuff.classList.toggle("asc")
//         data = data.sort((a, b) => { return a.lastName.toLowerCase() > b.lastName.toLowerCase() ? -1 : 1 })
//         console.log(htmlstuff.className)
//         printRow(data, ID)
//     } else {
//         htmlstuff.classList.toggle("asc")
//         data = data.sort((a, b) => { return a.lastName.toLowerCase() > b.lastName.toLowerCase() ? 1 : -1 })
//         console.log(htmlstuff.className)
//         printRow(data, ID)
//     }
// }
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


