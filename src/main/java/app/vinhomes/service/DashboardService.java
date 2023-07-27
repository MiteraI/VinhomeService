package app.vinhomes.service;

import app.vinhomes.entity.Order;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.order.ServiceRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class DashboardService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ServiceRepository serviceRepository;

    public int[] getOrderQuantityByStatusByMonth(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findAllByCreateTimeBetween(start, end);
        List<Order> completedOrders = new ArrayList<>();
        List<Order> cancelledOrders = new ArrayList<>();
        List<Order> pendingOrders = new ArrayList<>();
        for (Order order : orders) {
            switch (order.getStatus()) {
                case PENDING -> pendingOrders.add(order);
                case SUCCESS -> completedOrders.add(order);
                case CANCEL -> cancelledOrders.add(order);
            }
        }
        return new int[]{pendingOrders.size(),completedOrders.size(),cancelledOrders.size(),orders.size()};
    }

    public int getMonthRevenue(LocalDateTime start, LocalDateTime end) {
        List<Order> completedOrders = orderRepository.findAllByCreateTimeBetweenAndStatus(start,end, OrderStatus.SUCCESS);
        int revenue = 0;
        for (Order order : completedOrders) {
            revenue = revenue + (int)order.getPrice();
        }
        return revenue;
    }

    public HashMap<app.vinhomes.entity.order.Service, Integer> getServiceByOrderQuantity(LocalDateTime start, LocalDateTime end) {
        HashMap<app.vinhomes.entity.order.Service, Integer> serviceByQuantity = new HashMap<>();
        List<Order> orders = orderRepository.findAllByCreateTimeBetween(start,end);
        List<app.vinhomes.entity.order.Service> services = serviceRepository.findAll();
        for (app.vinhomes.entity.order.Service service : services) {
            serviceByQuantity.put(service,0);
        }
        for (Order order : orders) {
            app.vinhomes.entity.order.Service orderService = order.getService();
            if (serviceByQuantity.containsKey(orderService)) {
                int currentCount = serviceByQuantity.get(orderService);
                serviceByQuantity.put(orderService, currentCount + 1);
            }
        }
        List<Map.Entry<app.vinhomes.entity.order.Service, Integer>> sortedEntries = new ArrayList<>(serviceByQuantity.entrySet());
        sortedEntries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        // Create a new LinkedHashMap to maintain the sorted order
        HashMap<app.vinhomes.entity.order.Service, Integer> sortedServiceByQuantity = new LinkedHashMap<>();

        // Copy the sorted entries back to the LinkedHashMap
        for (Map.Entry<app.vinhomes.entity.order.Service, Integer> entry : sortedEntries) {
            sortedServiceByQuantity.put(entry.getKey(), entry.getValue());
        }

        return sortedServiceByQuantity;
    }
}
