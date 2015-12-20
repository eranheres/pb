package com.pb.validator;

import com.pb.ValidatorApplication;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;


@SpringApplicationConfiguration(classes = ValidatorApplication.class)
@WebAppConfiguration
public class ValidatorApplicationTests {

	@Test
	public void contextLoads() {
		assertTrue(true);
	}

}
