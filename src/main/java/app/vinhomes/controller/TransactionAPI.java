package app.vinhomes.controller;

import app.vinhomes.entity.Order;
import app.vinhomes.service.OrderService;
import app.vinhomes.service.TransactionService;
import app.vinhomes.vnpay.service.VNPayService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/transaction")
public class TransactionAPI {
    private List<Order> InvalidCancelOrder = new LinkedList<>();
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private VNPayService vnPayService;

    @GetMapping(value = "/getTransaction", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> getAllTransaction() {
        System.out.println("in get All transaction");
        List<Map<String, String>> transactionMap = transactionService.getAllTransactionAndFormat();
        return transactionMap;
    }

    /// for admin
    @PostMapping(value = "/getTransaction/getSingleTransaction", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getSingleTransactionInfo(@RequestBody JsonNode jsonNode) {
        String getTransactionId = jsonNode.get("transactionID").asText();
        // this return ALOT!!!! careful
        return transactionService.getSingleTransactionInfo(getTransactionId);
    }

    /// neu ko dung fetch ma dung form truyen thong thi co the bo @RequestBody, thay body.get(...).asText() = request.getParam(...)
    @PostMapping(value = "/queryTransaction")
    public ResponseEntity<String> queryVNPAY(@RequestBody JsonNode body, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("inside calling queryVNPAY");
        try {// if admin then this
            String getVnp_txnRef = body.get("vnp_txnRef").asText().trim();
            ResponseEntity responseEntity = transactionService.queryVNpayWithVnp_txtRef(getVnp_txnRef, request, response);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok().body(responseEntity.getBody().toString());
            }
        } catch (Exception e) {// if customer then this
            String orderId = body.get("order_id").asText().trim();
            String getVnp_txnRef = transactionService.getVnp_txtRefThroughOrderId(orderId);
            ResponseEntity responseEntity = transactionService.queryVNpayWithVnp_txtRef(getVnp_txnRef, request, response);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok().body(responseEntity.getBody().toString());
            }
        }
        //System.out.println(getVnp_txtRef);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("ERROR EXCEPTION IN API");
    }
    /// for customer under 2 hours
    @GetMapping(value = "/cancelOrder/refundTransaction/{orderId}")// from client
    public ResponseEntity<String> cancel_refundVNPAY(@PathVariable String orderId,HttpServletRequest request, HttpServletResponse response) {
        ///// heree we will check to see if the order is over 2 hour policy, if it is over 2 hours, only send requets to admin
        //// then admin will reconsidered
        // this will be sent by the customer who want to be refunded under 2 hours policy
        try {
            //String getOrderId = request.getParameter("order_id");
            String getOrderId = orderId;
            boolean checkPolicyRefund = transactionService.checkIfOver_2_hourPolicy(getOrderId);
            // true means yes, allow refund
            // false mean no, no refund, only change time, MAYBE
            if (checkPolicyRefund) {
                ResponseEntity<String> callingResult = transactionService.refundVNpayWithOrderID(getOrderId, request, response);
                if (callingResult.getStatusCode().is2xxSuccessful()) {
                    return ResponseEntity.ok().body(callingResult.getBody().trim());
                } else {
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("REFUND REQUEST FAILED, TRY AGAIN");
                }
            } else {
                //TODO call for admin permission to refund, or just change time
                if (getOrderId != null) {
                    // add invalid order cancelation to a list for admin to consider, this is optional, can be changed
                    Order getOrder = orderService.getOrderById(getOrderId);
                    this.InvalidCancelOrder.add(getOrder);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PASS 2 HOUR POLICY REFUND, YOU CAN ONLY CHANGE TIME OR CANCEL ORDER WITHOUT REFUND");
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("FAIL TO GET ORDER ID");
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("cannot find order_id through form, try fix the form");
        }
    }
    @GetMapping(value = "/cancelOrder/getAllInvalidCancelOrder")
    public List<Order> getInvalidCancelOrder() {
        return this.InvalidCancelOrder;
    }

}
