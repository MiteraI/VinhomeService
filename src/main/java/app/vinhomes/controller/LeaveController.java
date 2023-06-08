package app.vinhomes.controller;

import app.vinhomes.entity.Account;

import app.vinhomes.entity.worker.Leave;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.worker.LeaveRepository;
import app.vinhomes.repository.worker.WorkerStatusRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/daysOff")
public class LeaveController {

    @Autowired
    private WorkerStatusRepository workerStatusRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LeaveRepository leaveRepository;
    @PostMapping()
    public ResponseEntity DaysOff (@RequestBody JsonNode Data) {
        String firstName = Data.get("firstName").asText();
        String lastName = Data.get("lastName").asText();
        String dateStr = Data.get("date").asText();
        String numDaysOffStr = Data.get("numOffDays").asText();

        LocalDate dateNow = LocalDate.now();

        if (firstName.isEmpty() || lastName.isEmpty() || dateStr.isEmpty() || numDaysOffStr.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Don't leave any field empty");
        }

        LocalDate date = LocalDate.parse(dateStr);
        Integer numDaysOff = Integer.parseInt(numDaysOffStr);

        if (dateNow.isAfter(date)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid Date due to the date in the past!!!");
        }
        Account account = accountRepository.findByFirstNameAndLastName(firstName, lastName);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Wrong worker");
        }

        if (numDaysOff > 1) {
            for (int i = 0; i < numDaysOff; i++) {
                date = date.plusDays(1);
                Leave offDays = Leave.builder()
                        .leaveDay(date)
                        .account(account)
                        .build();
                leaveRepository.save(offDays);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Adding multiple days-off of the worker to table-leave");
        }

        Leave offDays = Leave.builder()
                .leaveDay(date)
                .account(account)
                .build();
        leaveRepository.save(offDays);
        return ResponseEntity.status(HttpStatus.CREATED).body("Adding to the worker to table-leave");
    }
}
