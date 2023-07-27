package app.vinhomes.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "api/admin/dashboard")
public class DashboardApi {
    @PostMapping(value = "data")
    public ResponseEntity<int[]> getData() {
        int[] dataArray = {1, 2, 3, 4, 5};
        return ResponseEntity.ok(dataArray);
    }
}
