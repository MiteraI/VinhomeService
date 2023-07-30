var vnp_Amount = document.getElementById("vnp_Amount")
var vnp_FeeAmount = document.getElementById("vnp_FeeAmount")
var vnp_TransactionStatus = document.getElementById("vnp_TransactionStatus")
const transaction_body = document.getElementById("TRANSACTIONBODY")
const warning_COD = document.getElementById("WARNING_COD")
const refundButton = document.getElementById("btnGetRefundCustomer")

async function getQueryTransaction(txn, text) {
  if (txn.startsWith("admin") || txn.toLowerCase() == "NULL".toLowerCase()) {
    alert("fail to get transaction")
    transaction_body.style.display = "none";
    warning_COD.style.display = "block";
    refundButton.classList.add('hidden')
    return;
  }
  let response = await fetch(`http://localhost:8080/transaction/queryTransaction/${txn}`, {
    method: "get"
  })
  var status = await response.status
  var toJson = await response.json()
  console.log(toJson)
  if (status >= 200 && status <= 300) {
    if (toJson.vnp_ResponseCode == "00") {
      if (toJson.vnp_TransactionStatus == "00") {
        console.log(toJson.vnp_Amount)
        console.log(text)
        vnp_Amount.value = typeof toJson.vnp_Amount === 'undefined' ? 'UNAVAILABLE' : formatMoney(toJson.vnp_Amount / 100)
        vnp_FeeAmount.value = typeof toJson.vnp_FeeAmount === 'undefined' ? 'UNAVAILABLE' : toJson.vnp_FeeAmount
        vnp_TransactionStatus.value = typeof text === 'undefined' ? 'UNAVAILABLE' : text
      } else {
        console.log(toJson.vnp_Amount)
        console.log(text)
        vnp_Amount.value = typeof toJson.vnp_Amount === 'undefined' ? 'UNAVAILABLE' : formatMoney(toJson.vnp_Amount / 100)
        vnp_FeeAmount.value = typeof toJson.vnp_FeeAmount === 'undefined' ? 'UNAVAILABLE' : toJson.vnp_FeeAmount
        vnp_TransactionStatus.value = typeof text === 'undefined' ? 'UNAVAILABLE' : text
      }
    } else {
      console.log(toJson.vnp_Amount)
      console.log(text)
      refundButton.classList.add('hidden')
      vnp_Amount.value = typeof toJson.vnp_Amount === 'undefined' ? 'UNAVAILABLE' : formatMoney(toJson.vnp_Amount / 100)
      vnp_FeeAmount.value = typeof toJson.vnp_FeeAmount === 'undefined' ? 'UNAVAILABLE' : toJson.vnp_FeeAmount
      vnp_TransactionStatus.value = typeof text === 'undefined' ? 'UNAVAILABLE' : text
    }
  } else {
    transaction_body.style.display = "none";
    warning_COD.style.display = "block";
    refundButton.classList.add('hidden')
  }
}
async function getTransactionStatus(txn) {
  var response = await fetch("http://localhost:8080/transaction/getStatus/" + txn, {
    method: "GET"
  })
  refundButton.classList.remove('hidden')
  var status = await response.status
  var toText = await response.text()
  console.log("getTransactionStatus")
  console.log(toText)
  if (status >= 200 & status <= 300) {
    console.log(status)
    if (toText.startsWith("REFUNDED")) {
      console.log("startWith refunded")
      refundButton.classList.add('hidden')
    } else {
      console.log("not startWith refunded")
      refundButton.classList.remove('hidden')
    }
  } else {
    transaction_body.style.display = "none";
    warning_COD.style.display = "block";
    refundButton.classList.add('hidden')
  }
  getQueryTransaction(txn, toText)
}
function getAdminCustomerPage() {
  console.log("inside return main page")
  window.location.href = "/see-all-order-by-admin";
}

function formatMoney(money) {
  let number = parseInt(money);
  let [integerPart, decimalPart] = number.toFixed(0).toString().split('.');
  let formattedIntegerPart = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
  let formattedNumber = decimalPart ? `${formattedIntegerPart}.${decimalPart}` : formattedIntegerPart;

  return `₫${formattedNumber}`
}




