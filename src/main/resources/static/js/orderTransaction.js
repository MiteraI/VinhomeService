var vnp_Amount = document.getElementById("vnp_Amount")
var vnp_FeeAmount = document.getElementById("vnp_FeeAmount")
var vnp_TransactionStatus = document.getElementById("vnp_TransactionStatus")
const transaction_body = document.getElementById("TRANSACTIONBODY")
const warning_COD = document.getElementById("WARNING_COD")
const refundButton = document.getElementById("btnGetRefundCustomer")
//const queryString = window.location.search;
//const getVnpTxnRef = new URLSearchParams(queryString).get('vnpTxnRef'); console.log(getVnpTxnRef)





async function getQueryTransaction(txn, text) {
  if (txn.startsWith("admin") || txn.toLowerCase() == "NULL".toLowerCase()) {
    alert("fail to get transaction")
    transaction_body.style.display = "none";
    warning_COD.style.display = "block";
    refundButton.disabled = true;
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
        alert("yes get transaction Success");
        vnp_Amount.value = toJson.vnp_Amount
        vnp_FeeAmount.value = toJson.vnp_FeeAmount
        vnp_TransactionStatus.value = text == 'FAIL' ? 'CANCELLED' : text
      } else {
        alert("yes get transaction cancel")
        vnp_Amount.value = toJson.vnp_Amount
        vnp_FeeAmount.value = toJson.vnp_FeeAmount
        vnp_TransactionStatus.value = text == 'FAIL' ? 'CANCELLED' : text
        // refundButton.disabled = true;
      }
    } else {
      alert("fail to get transaction, this might be the transaction is not exist or pre cancel")
      vnp_Amount.value = "UNAVAILABLE"
      vnp_FeeAmount.value = "UNAVAILABLE"
      vnp_TransactionStatus.value = "UNAVAILABLE";
      vnp_Amount.classList.toggle('text-red-400')
      vnp_FeeAmount.classList.toggle('text-red-400')
      vnp_TransactionStatus.classList.toggle('text-red-400')
      // refundButton.disabled = true;
    }
  } else {
    alert("fail to get transaction")
    transaction_body.style.display = "none";
    warning_COD.style.display = "block";
    refundButton.disabled = true;
  }
}
async function getTransactionStatus(txn) {
  var response = await fetch("http://localhost:8080/transaction/getStatus/" + txn, {
    method: "GET"
  })
  var status = await response.status
  var toText = await response.text()
  console.log(status)
  console.log(toText)
  if (status >= 200 & status <= 300) {
    if (toText.startsWith("REFUNDED")) {
      refundButton.disabled = true;
    } else {
      refundButton.disabled = false;
    }
  } else {
    transaction_body.style.display = "none";
    warning_COD.style.display = "block";
    refundButton.disabled = true;
  }
  getQueryTransaction(txn, toText)
}
function getAdminCustomerPage() {
  console.log("inside return main page")
  window.location.href = "/see-all-order-by-admin";
}
async function getRefundTransaction() {

  const txn = transaction_body.querySelector('#transactionId').value
  var response = await fetch("http://localhost:8080/transaction/admin/refundTransaction/" + txn,
    {
      method: "GET"
    });
  var status = await response.status; console.log(status)
  var toText = await response.text(); console.log(toText)
  if (status < 200 || status > 300) {
    console.log("refund fail");
    alert("refund fail: " + toText)
  } else {
    console.log("refund success");
    alert("refund success: " + toText)
  }
  getAdminCustomerPage()
}
