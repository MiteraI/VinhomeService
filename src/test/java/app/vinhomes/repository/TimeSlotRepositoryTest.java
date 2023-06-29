//package app.vinhomes.repository;
//
//import app.vinhomes.entity.order.TimeSlot;
//import app.vinhomes.repository.order.TimeSlotRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalTime;
//
//@SpringBootTest
//class TimeSlotRepositoryTest {
//    @Autowired
//    private TimeSlotRepository timeSlotRepository;
//
//    //Recommend to insert time by test
//    @Test
//    public void insertTime() {
//        TimeSlot timeSlot = TimeSlot.builder()
//                .startTime(LocalTime.of(6,0,0))
//                .endTime(LocalTime.of(7, 30, 0))
//                .build();
//        TimeSlot timeSlot2 = TimeSlot.builder()
//                .startTime(LocalTime.of(8,0,0))
//                .endTime(LocalTime.of(9, 30, 0))
//                .build();
//        TimeSlot timeSlot3 = TimeSlot.builder()
//                .startTime(LocalTime.of(10,0,0))
//                .endTime(LocalTime.of(11, 30, 0))
//                .build();
//        TimeSlot timeSlot4 = TimeSlot.builder()
//                .startTime(LocalTime.of(12,30,0))
//                .endTime(LocalTime.of(14, 0, 0))
//                .build();
//        TimeSlot timeSlot5 = TimeSlot.builder()
//                .startTime(LocalTime.of(14,30,0))
//                .endTime(LocalTime.of(16, 0, 0))
//                .build();
//        TimeSlot timeSlot6 = TimeSlot.builder()
//                .startTime(LocalTime.of(16,30,0))
//                .endTime(LocalTime.of(18, 0, 0))
//                .build();
//
//        timeSlotRepository.save(timeSlot);
//        timeSlotRepository.save(timeSlot2);
//        timeSlotRepository.save(timeSlot3);
//        timeSlotRepository.save(timeSlot4);
//        timeSlotRepository.save(timeSlot5);
//        timeSlotRepository.save(timeSlot6);
//    }
//
//    @Test
//    public void printAll() {
//        System.out.println(timeSlotRepository.findAll());
//    }
//}