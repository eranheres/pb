package com.pb.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ValidatorApplication.class)
@WebAppConfiguration
public class ValidatorApplicationTests {

	@Test
	public void contextLoads() {
		assertTrue(true);
	}

}
