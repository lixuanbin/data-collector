package co.speedar.data.collector.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class DataCollectServiceTest {
	@Autowired
	private DataCollectService service;

	@Value("${data.collect.cron}")
	private String cron;

	@Test
	public void testCron() {
		assertNotNull(cron);
		System.out.println(cron);
	}
}
