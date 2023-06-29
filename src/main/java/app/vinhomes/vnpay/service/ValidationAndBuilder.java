package app.vinhomes.vnpay.service;

import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.order.Service;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.TransactionRepository;
import app.vinhomes.repository.order.PaymentCategoryRepository;
import app.vinhomes.repository.order.PaymentRepository;
import app.vinhomes.repository.order.ServiceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.core.RepositoryCreationException;

@org.springframework.stereotype.Service
public class ValidationAndBuilder {
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    public String getBankCode_CheckCOD_VNpay(JsonNode jsonNode) {
        try {
            String transactionMethod = jsonNode.get("paymentId").asText();
            String bankCode = "";
            if (transactionMethod.equals("COD")) {
                return bankCode;
            } else {
                //bankCode = jsonNode.get("bankcode").asText();
                bankCode = "VNBANK";
                System.out.println(bankCode);
                return bankCode;
            }
        } catch (Exception e) {
            System.out.println("error with get transaction_method");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public double getServicePriceFromOrder(String orderID) {
        long id = Long.valueOf(orderID);
        try {
            boolean checkIfExistService = orderRepository.findById(id).isPresent();
            if (checkIfExistService) {
                Service service = serviceRepository.findById(orderRepository.findById(id).get().getService().getServiceId()).get();
                System.out.println(service);
                double price = Double.parseDouble(String.valueOf(service.getPrice()));
                return price ;
            } else {
                return -1;
            }
        } catch (Exception e) {
            System.out.println("error getting service from db");
            System.out.println(e.getMessage());
            return -1;
        }
    }
    public Transaction BuildTransactionThroughOrder(long orderId, String vnp_txnRef,String paymentMethodID){
        try{
            Order getOrder = orderRepository.findById(orderId).get();
            if(getOrder != null){
                Transaction toSaveTransaction =
                Transaction.builder()
                        .vnpTxnRef(vnp_txnRef)
                        .paymentMethod(paymentRepository.findById(Long.valueOf(paymentMethodID)).get().toString())
                        .transactionId(getOrder.getOrderId())
                        .order(getOrder)
                        .build();
                System.out.println(toSaveTransaction);

                System.out.println("done build transaction, now safe");
                return toSaveTransaction;
            }
        }catch (NullPointerException e){
            System.out.println("WHY cant find Order with its own ID, major error    "+e.getMessage());
            return null;
        }catch (Exception e){
            System.out.println("something happen when savein transaction, might be due to not having enough fields");
            return null;
        }
        System.out.println("some error happen inside BuildTransactionThroughOrder");
        return null;
    }

//    public double getServicePriceFromOrder(HttpServletRequest request) {
//        String id = request.getParameter("serviceId");
//        try {
//            boolean checkIfExistService = serviceRepository.findById(Long.valueOf(id)).isPresent();
//            if (checkIfExistService) {
//                Service service = serviceRepository.findById(Long.valueOf(id)).get();
//                System.out.println(service);
//                double price = Double.parseDouble(String.valueOf(service.getPrice()));
//                return price * 1000;
//            } else {
//                return -1;
//            }
//        } catch (Exception e) {
//            System.out.println("error getting service from db");
//            System.out.println(e.getMessage());
//            return -1;
//        }
//    }
}
