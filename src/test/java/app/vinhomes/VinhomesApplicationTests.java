package app.vinhomes;

import app.vinhomes.repository.order.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VinhomesApplicationTests {

	@Autowired
	private ScheduleRepository scheduleRepository;
	@Test
	void contextLoads() {
		System.out.println(scheduleRepository.findAllByWorkers_AccountId(60L).size());
	}



}
