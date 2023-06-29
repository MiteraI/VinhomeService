function printRow(data, IdOfTableBody) {
    console.log(IdOfTableBody)
    var table_tag = document.getElementById(`${IdOfTableBody}`);
    // console.log(table_tag.innerHTML)
    table_tag.innerHTML = ""
    console.log(data)
    var output = "";
    for (var i in data) {
        output += `
        <tr id="${data[i].accountId}" scope="row"  class='item-to-search' >
                <td style="display: none;" ><input type="hidden" name="txtUsername" value="${data[i].accountName}"></td>
                <td style="display: none;"><input type="hidden" name="txtPassword" value="${data[i].password}"></td>
                <td><input type="hidden" name="txtEmail"value="${data[i].email}">${data[i].email}</td>
                <td><input type="hidden" name="txtFirstname"value="${data[i].firstName}">${data[i].firstName}</td>
                <td><input type="hidden" name="txtLastname"value="${data[i].lastName}">${data[i].lastName}</td>
                <td><input type="hidden" name="txtDob"value="${data[i].dob}">${date_output}</td>
                <td><input type="hidden" name="txtStatus"value="${data[i].accountStatus}">${statusString}</td>
                <td><input type="hidden" name="txtRole"value="${data[i].role}">${roleString}</td>
                <td><button onclick='deleteByID(${data[i].accountId})'>DELETE</button></td>
        </tr>
        `;
        table_tag.innerHTML += output;
    }
}




