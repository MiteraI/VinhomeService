package app.vinhomes.controller;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.entity.type_enum.TransactionStatus;
import app.vinhomes.event.event_storage.SendEmailOnRefund_OnFinishOrder;
import app.vinhomes.service.OrderService;
import app.vinhomes.service.TransactionService;
import app.vinhomes.vnpay.service.VNPayService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/transaction")
public class TransactionAPI {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

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

    @GetMapping(value = "/cancelOrder/refundTransaction/{orderId}")// from client
    public ResponseEntity<String> cancelOrder(@PathVariable String orderId, HttpServletRequest request, HttpServletResponse response) {
        try {
            //String getOrderId = request.getParameter("order_id");
            String getOrderId = orderId;
            boolean checkPolicyRefund = orderService.checkIfOver_2_hourPolicy(getOrderId);
            // true means yes, allow refund
            // false mean no, no refund, only change time, MAYBE
//            Order getOrder = orderService.getOrderById(Long.parseLong(getOrderId));
//            Transaction getTransaction = transactionService.getTransactionById(getOrder.getOrderId());
            System.out.println("inside cancel and refund order");
            if (checkPolicyRefund) {
                boolean check_if_order_legit_for_refund = orderService.checkIfOrderLegitForRefund(orderId);
                if (check_if_order_legit_for_refund) {
                    ResponseEntity<String> callingResult = transactionService.refundWithOrderID(getOrderId, request, response);
                    if (callingResult.getStatusCode().is2xxSuccessful()) {
                        Order getOrder = orderService.getOrderById(Long.parseLong(getOrderId));
                        Transaction getTransaction = transactionService.getTransactionById(getOrder.getOrderId());
                        System.out.println("now send mail");
                        eventPublisher.publishEvent(new SendEmailOnRefund_OnFinishOrder(getOrder.getAccount(), getTransaction, false));
                        return ResponseEntity.ok().body(callingResult.getBody().trim());
                    } else {
                        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("REFUND REQUEST FAILED, TRY AGAIN");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("ORDER NOT LEGIT FOR REFUND");
                }
            } else {
                //TODO call for admin permission to refund, or just change time;
                Long parsedOrderId = Long.parseLong(orderId);
                Order getOrder = orderService.getOrderById(parsedOrderId);
                if (parsedOrderId != null) {
                    orderService.addInvalidCancelOrder(getOrder);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PASS 2 HOUR POLICY REFUND, YOU CAN ONLY CHANGE TIME OR CANCEL ORDER WITHOUT REFUND");
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("FAIL TO GET ORDER ID");
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("cannot find order or transaction, something wrong with order id");
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("cannot find order_id through form, try fix the form");
        }
    }

    @GetMapping(value = "/queryTransaction/{vnpTxnRef}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> queryVNPAY(@PathVariable String vnpTxnRef, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("inside calling queryVNPAY");
        try {// if admin then this
            System.out.println(vnpTxnRef);
            Transaction getTransaction = transactionService.getTransactionByVnpTxnRef(vnpTxnRef);
            if (getTransaction != null) {
                String getStatus = getTransaction.getStatus().toString();
                ResponseEntity responseEntity = transactionService.queryVNpayWithVnp_txtRef(vnpTxnRef, request, response);
                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    return ResponseEntity.ok().body(responseEntity.getBody().toString());
                }
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("ERROR query this transaction");
            }
        } catch (Exception e) {// if customer then this
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR EXCEPTION IN API");
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("ERROR this transactoin does not exist");
    }

    @GetMapping(value = "/admin/refundTransaction/{vnpTxnRef}")
    public ResponseEntity<String> refundVNPAY(@PathVariable String vnpTxnRef, HttpServletRequest request, HttpServletResponse response) {
        try {
            System.out.println("inside refund transaction from admin page");

            Transaction getTransaction = transactionService.getTransactionByVnpTxnRef(vnpTxnRef);
            Order getOrder = getTransaction.getOrder();
            String orderId = getOrder.getOrderId().toString().trim();
            ResponseEntity<String> callingResult = transactionService.refundWithOrderID(orderId, request, response);
            if (callingResult.getStatusCode().is2xxSuccessful()) {
                System.out.println("now send mail");
                eventPublisher.publishEvent(new SendEmailOnRefund_OnFinishOrder(getOrder.getAccount(), getTransaction, false));
                return ResponseEntity.ok().body(callingResult.getBody().trim());
                //todo check lại logic
                //orderService.setOrderStatus(getOrder,OrderStatus.CANCEL);
                //transactionService.setTransactionStatus(getTransaction,TransactionStatus.REFUNDED);
//                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("ERROR cannot refund vnpay, this transaction has already refunded or something happen ");
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("REFUND REQUEST FAILED, TRY AGAIN");
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR EXCEPTION IN API");
        }
    }

    @GetMapping(value = "/cancelOrder/getAllInvalidCancelOrder")
    public List<Order> getInvalidCancelOrder() {
        return orderService.getInvalidCancelOrder();
    }

    @GetMapping(value = "/getStatus/{vnpTxnRef}")
    public ResponseEntity<String> getTransactionStatus(@PathVariable String vnpTxnRef) {
        System.out.println("inside getTransactoin Status");
        Transaction getTrasaction = transactionService.getTransactionByVnpTxnRef(vnpTxnRef);
        if (getTrasaction != null) {
            String getStatus = getTrasaction.getStatus().toString();
            return ResponseEntity.status(HttpStatus.OK).body(getStatus);
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("NULL");

    }

    @GetMapping(value = "/admin/cancelOrder/{orderId}")
    public ResponseEntity<String> cancelOrderAdmin(@PathVariable String orderId, HttpServletRequest request, HttpServletResponse response) {
        SecurityContext getContext = SecurityContextHolder.getContext();
        if (getContext == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ERROR unauthorized");
        }
        System.out.println(getContext);
        Authentication getCurrentAuth = getContext.getAuthentication();
        if (getCurrentAuth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ERROR cannot get authentication of this account");
        }
        System.out.println(getCurrentAuth);
        SimpleGrantedAuthority getAuth = (SimpleGrantedAuthority) getCurrentAuth.getAuthorities().toArray()[0];
        System.out.println(getAuth.toString());
        if (getAuth.toString().equals("2") == false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ERROR account is not valid to do this");
        }
        Order getOrder = orderService.getOrderById(Long.parseLong(orderId));
        String getPayment = getOrder.getPayment().getPaymentName();
        Transaction getTransaction = transactionService.getTransactionById(getOrder.getOrderId());
        System.out.println(getPayment);
        if (getPayment.equals("COD")) {
            orderService.setOrderStatus(getOrder, OrderStatus.CANCEL);
            transactionService.setTransactionStatus(getTransaction, TransactionStatus.FAIL);
            return ResponseEntity.status(HttpStatus.OK).body("OK cancel cod");
        } else {
            return refundVNPAY(getTransaction.getVnpTxnRef(), request, response);
        }
    }
}
