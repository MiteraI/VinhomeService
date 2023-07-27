package app.vinhomes.controller;

import app.vinhomes.entity.order.Service;
import app.vinhomes.service.DashboardService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;

@RestController
@RequestMapping(value = "/api/admin/dashboard")
public class DashboardApi {
    @Autowired
    private DashboardService dashboard;
    @PostMapping(value = "/data")
    public ResponseEntity<int[]> getData(@RequestBody JsonNode time) {
        LocalDateTime start;
        LocalDateTime end;

        int selectedMonth = time.get("month").asInt();
        int selectedYear = time.get("year").asInt();

        switch (selectedMonth) {
            case 1: // January
                start = YearMonth.of(selectedYear, 1).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 1).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 2: // February
                start = YearMonth.of(selectedYear, 2).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 2).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 3: // March
                start = YearMonth.of(selectedYear, 3).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 3).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 4: // April
                start = YearMonth.of(selectedYear, 4).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 4).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 5: // May
                start = YearMonth.of(selectedYear, 5).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 5).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 6: // June
                start = YearMonth.of(selectedYear, 6).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 6).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 7: // July
                start = YearMonth.of(selectedYear, 7).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 7).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 8: // August
                start = YearMonth.of(selectedYear, 8).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 8).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 9: // September
                start = YearMonth.of(selectedYear, 9).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 9).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 10: // October
                start = YearMonth.of(selectedYear, 10).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 10).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 11: // November
                start = YearMonth.of(selectedYear, 11).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 11).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 12: // December
                start = YearMonth.of(selectedYear, 12).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 12).atEndOfMonth().atTime(23, 59, 59);
                break;
            default:
                // Invalid month selected
                return ResponseEntity.badRequest().build();
        }

        int[] dataArray = dashboard.getOrderQuantityByStatusByMonth(start, end);
        return ResponseEntity.ok(dataArray);
    }

    @PostMapping(value = "/revenue")
    public ResponseEntity<Integer> getRevenue(@RequestBody JsonNode time) {
        LocalDateTime start;
        LocalDateTime end;

        int selectedMonth = time.get("month").asInt();
        int selectedYear = time.get("year").asInt();

        switch (selectedMonth) {
            case 1: // January
                start = YearMonth.of(selectedYear, 1).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 1).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 2: // February
                start = YearMonth.of(selectedYear, 2).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 2).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 3: // March
                start = YearMonth.of(selectedYear, 3).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 3).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 4: // April
                start = YearMonth.of(selectedYear, 4).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 4).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 5: // May
                start = YearMonth.of(selectedYear, 5).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 5).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 6: // June
                start = YearMonth.of(selectedYear, 6).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 6).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 7: // July
                start = YearMonth.of(selectedYear, 7).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 7).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 8: // August
                start = YearMonth.of(selectedYear, 8).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 8).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 9: // September
                start = YearMonth.of(selectedYear, 9).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 9).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 10: // October
                start = YearMonth.of(selectedYear, 10).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 10).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 11: // November
                start = YearMonth.of(selectedYear, 11).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 11).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 12: // December
                start = YearMonth.of(selectedYear, 12).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 12).atEndOfMonth().atTime(23, 59, 59);
                break;
            default:
                // Invalid month selected
                return ResponseEntity.badRequest().build();
        }
        int revenue = dashboard.getMonthRevenue(start,end);
        return ResponseEntity.ok(revenue);
    }

    @PostMapping(value = "/service-quantity")
    public ResponseEntity<HashMap<Service,Integer>> getServiceWithOrderQuantity(@RequestBody JsonNode time) {
        LocalDateTime start;
        LocalDateTime end;

        int selectedMonth = time.get("month").asInt();
        int selectedYear = time.get("year").asInt();

        switch (selectedMonth) {
            case 1: // January
                start = YearMonth.of(selectedYear, 1).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 1).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 2: // February
                start = YearMonth.of(selectedYear, 2).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 2).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 3: // March
                start = YearMonth.of(selectedYear, 3).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 3).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 4: // April
                start = YearMonth.of(selectedYear, 4).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 4).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 5: // May
                start = YearMonth.of(selectedYear, 5).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 5).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 6: // June
                start = YearMonth.of(selectedYear, 6).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 6).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 7: // July
                start = YearMonth.of(selectedYear, 7).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 7).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 8: // August
                start = YearMonth.of(selectedYear, 8).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 8).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 9: // September
                start = YearMonth.of(selectedYear, 9).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 9).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 10: // October
                start = YearMonth.of(selectedYear, 10).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 10).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 11: // November
                start = YearMonth.of(selectedYear, 11).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 11).atEndOfMonth().atTime(23, 59, 59);
                break;
            case 12: // December
                start = YearMonth.of(selectedYear, 12).atDay(1).atStartOfDay();
                end = YearMonth.of(selectedYear, 12).atEndOfMonth().atTime(23, 59, 59);
                break;
            default:
                // Invalid month selected
                return ResponseEntity.badRequest().build();
        }
        HashMap<Service,Integer> serviceByQuantity = dashboard.getServiceByOrderQuantity(start,end);
        return ResponseEntity.ok(serviceByQuantity);
    }
}
