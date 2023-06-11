package app.vinhomes.vnpay.service;

import app.vinhomes.entity.order.Service;
import app.vinhomes.repository.order.ServiceRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service
public class ExtractMainFormData {
    @Autowired
    private ServiceRepository serviceRepository;
    public String getBankCode_CheckCOD_VNpay(HttpServletRequest request) {
        try {
            String transactionMethod = request.getParameter("transaction_method");
            String bankCode = "";
            if (transactionMethod.equals("COD")) {
                return bankCode;
            } else {
                bankCode = request.getParameter("bankcode");
                return bankCode;
            }
        } catch (Exception e) {
            System.out.println("error with get transaction_method");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public long getServicePriceFromOrder(HttpServletRequest request) {
        String id = request.getParameter("serviceId");
        try {
            Service orderService = serviceRepository.findById(Long.parseLong(id)).orElseThrow(NullPointerException::new);
            long price = 0;
            if (orderService != null) {
                price = Long.parseLong(String.valueOf(orderService.getPrice()));
                return price;
            } else {
                return -1;
            }
        } catch (Exception e) {
            System.out.println("error getting service from db");
            System.out.println(e.getMessage());
            return -1;
        }
    }
}
