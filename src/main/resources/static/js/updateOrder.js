const getOrderId = window.location.href.split(/(\\|\/)/g).pop()
var orderid = document.getElementById("orderId");
var price = document.getElementById("Price");
var status = document.getElementById("Status");
var createtime = document.getElementById("CreateTime");
var servicename = document.getElementById("ServiceName");
var serviceprice = document.getElementById("ServicePrice");
var workers = document.getElementById("Workers");
var workday = document.getElementById("WorkDay");
var starttime = document.getElementById("StartTime");
var endime = document.getElementById("EndTime");
let workerListLayout = document.getElementById("WorkerLayout")
let customerLayout = document.getElementById("CustomerLayout");
getOrderById(getOrderId);

async function getOrderById(orderId) {
  let response = await fetch(`/api/order/getOrder/${orderId}`)
  let status = await response.status
  let toJson = await response.json()
  console.log(toJson)
  if (status >= 200 && status <= 300) {
    orderInfoLayout(toJson)
    workerInfoLayout(toJson)
    accountLayout(toJson)
  } else {
    alert("error")
  }
}

async function orderInfoLayout(jsonData) {
  let Order = jsonData
  let Schedule = jsonData.schedule; console.log(Schedule)
  let Payment = jsonData.payment; console.log(Payment)
  let Service = jsonData.service; console.log(Service)
  orderid.innerText = Order.orderId
  price.value = Order.price
  status.value = Order.status
  createtime.value = Order.createTime
  servicename.value = Service.serviceName
  serviceprice.value = Service.price
  workers.value = Service.numOfPeople
  workday.value = Schedule.workDay
  starttime.value = Schedule.timeSlot.startTime
  endime.value = Schedule.timeSlot.endTime
}
async function workerInfoLayout(jsonData) {
  let WorkerList = jsonData.schedule.workers
  var addingDIV = ""
  WorkerList.forEach(element => {
    var blockHTML = ""
    blockHTML += `
                        <div class="container p-1">
                            <p class="workerId">${element.accountId}</p>
                            <p class="workerFirstName">${element.firstName}</p>
                            <p class="workerLastName">${element.lastName}</p>
                            <input type="text" class="accountName" value="${element.username} "  readonly>
                            `
    if (element.phones.length == 0) {
      blockHTML += `
                                <input type="text" style="color:red;" class="phoneNumber" value="No Phone???"  readonly>
                                `
    } else {
      console.log(element.phones)
      blockHTML +=
        `
                                <input type="text" class="phoneNumber" value="${element.phones[0].number} "  readonly>`
    }
    //                 var OrderDate = new Date(jsonData.schedule.workDay)
    //                 var today = new Date()
    //                 if(OrderDate <= today ){

    //                 }else{
    //                     blockHTML +=
    //                 `
    //                 <button class="btn btn-danger" onclick="openWorkerDiv()" disabled >replace</button>
    //             </div>
    // ` 
    //                 }
    //<button class="btn btn-danger" onclick="openWorkerDiv()">replace</button>
    blockHTML +=

      `
                        </div>
            `
    addingDIV += blockHTML;
  });
  workerListLayout.innerHTML = addingDIV
}
async function accountLayout(jsonData) {
  let account = jsonData.account;
  var addingDiv = ""
  addingDiv +=
    `
                    <div class="row p-1">
                        <p for="" class="customerTitle">Id</p>
                        <input type="text" class="customerFields" value="${account.accountId}" readonly>
                    </div>
        `
  addingDiv +=
    `
                    <div class="row p-1">
                        <p for="" class="customerTitle">user</p>
                        <input type="text" class="customerFields" value="${account.username}" readonly>
                    </div>
        `
  addingDiv +=
    `
                    <div class="row p-1">
                        <p for="" class="customerTitle">phone</p>
                        <select name="" id="customerPhone" style="width: 25%">
                            `
  account.phones.forEach(phone => {
    addingDiv +=
      `<option value="${phone.phoneId}">${phone.number}</option>`
  })
  addingDiv +=
    `
                        </select>
                    </div>
        `
  customerLayout.innerHTML = addingDiv;
}
function back() {
  window.location.href = "/see-all-order-by-admin"
}
function closeWorkerDiv() {
  document.getElementById("displayReplaceWorker").style.display = 'none'
}
function openWorkerDiv() {
  document.getElementById("displayReplaceWorker").style.display = 'block'
  openReplaceList()
}
// <div class="container p-1">
//             <p class="">${element.accountId}</p>
//             <p class="">${element.firstName}</p>
//             <p class="">${element.lastName}</p>
//             <input type="text" class="" value="${element.phones[0].number} "  readonly>
//             <button class="btn btn-danger">choose</button>
//         </div>
async function openReplaceList() {
  var getReplaceWorker = document.getElementById("replaceWorkerList")
  var output = "";
  var toJson = await getFreeWorkerForServiceCategory();
  console.log(toJson)
  if (toJson == null) {
    alert("error");
    closeWorkerDiv();
    return
    //display error
  } else {
    toJson.forEach(element => {
      output += `
                 <div class="container p-1">
                     <p class="" style="width: 5%;">${element.accountId}</p>
                    <p class="" style="width: 25%;">${element.firstName}</p>
                    <p class="" style="width: 25%;">${element.lastName}</p>
                    <input type="text" style="width: 25%;" class="" value="${element.phones[0].number} "  readonly>
                     <button class="btn btn-danger" onclick=replaceWorker(${element.accountId})>choose</button>
                </div>
                `
    })
    getReplaceWorker.innerHTML = output
  }
}
async function getFreeWorkerForServiceCategory() {
  var response = await fetch(`http://localhost:8080/api/worker/getFreeWorkerAtTimeSLot/${getOrderId}`)
  var status = await response.status
  var toJson = await response.json()
  if (status >= 200 && status <= 300) {
    return toJson;
  } else {
    return null
  }
}
async function replaceWorker() {
  console.log("inside replace")
}
