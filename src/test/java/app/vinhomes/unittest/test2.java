package app.vinhomes.unittest;

import app.vinhomes.entity.order.TimeSlot;
import app.vinhomes.repository.order.TimeSlotRepository;
import jakarta.persistence.Temporal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Date;

//@SpringBootTest
@DataJpaTest
@ExtendWith(MockitoExtension.class)// this is to replace the mock config, not sure
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class test2 {
    @Value("${order.policy.day_before_service}")
    private String DAY_POLICY_ORDER;
    @Value("${order.policy.hour_before_service}")
    private String HOUR_POLICY_ORDER;
    @Value("${order.policy.max_day_prior_to_service}")
    private String DAY_PRIOR_ORDER;
    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Test
    void testGetPolicyOrder(){
        Assertions.assertEquals("1",DAY_POLICY_ORDER);
        Assertions.assertEquals("8",HOUR_POLICY_ORDER);
    }
    @Test
    void testCheckDate(){
        String getDate = "2023-08-01";
        LocalDate parseDate = LocalDate.parse(getDate);
        LocalDate dateNow = LocalDate.now();
        System.out.println();

        TimeSlot getTimeSlot1= timeSlotRepository.findById(3l).get();
        TimeSlot getTimeSlot2= timeSlotRepository.findById(5l).get();
        System.out.println(parseDate);
        System.out.println(LocalDate.now());
        System.out.println(getTimeSlot1.getStartTime());
        System.out.println(getTimeSlot2.getStartTime());
        System.out.println(LocalTime.now());
        boolean getResult1 = checkDayValidForOrder("2023-08-01",getTimeSlot1.getStartTime());
        Assertions.assertEquals(false,getResult1);
        boolean getResult2 = checkDayValidForOrder("2023-07-15",getTimeSlot1.getStartTime());
        Assertions.assertEquals(true,getResult2);
        boolean getResult3 = checkDayValidForOrder("2023-07-03",getTimeSlot2.getStartTime());
        Assertions.assertEquals(false,getResult3);
        boolean getResult4 = checkDayValidForOrder("2023-07-03",getTimeSlot1.getStartTime());
        Assertions.assertEquals(true,getResult4);
        System.out.println(getResult4);

    }
    //String for orderdate because we get it from jsonNode as text
    private boolean checkDayValidForOrder(String orderDate, LocalTime orderTimeSlot_StartTime){
        TimeSlot getFirstTimeSlot = timeSlotRepository.findById(1l).get();
        LocalDate parseDate = LocalDate.parse(orderDate);
        LocalDate dateNow = LocalDate.now();
        long getDayDiff = ChronoUnit.DAYS.between(dateNow,parseDate);
        long getHourDiff = ChronoUnit.HOURS.between(LocalTime.now(),orderTimeSlot_StartTime);//LocalTime.now()
        if(getDayDiff <= Integer.parseInt(DAY_POLICY_ORDER.trim())){
            System.out.println("date is off policy " +getDayDiff);
            if(getHourDiff < Integer.parseInt(HOUR_POLICY_ORDER.trim())){
                System.out.println("hour is also off policy " +getHourDiff);
                return false;
            }else{
                System.out.println("hour is in policy, success "+ getHourDiff);
                return true;
            }
        }else{
            if(getDayDiff > Integer.parseInt(DAY_PRIOR_ORDER.trim())){
                System.out.println("date violate max prior day order");
                return false;
            }
            System.out.println("date is in policy, success");
            return true;
        }
    }
}
