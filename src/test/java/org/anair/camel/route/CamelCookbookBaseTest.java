package org.anair.camel.route;

import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@RunWith(CamelSpringRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpoints
@UseAdviceWith
public class CamelCookbookBaseTest {
	
	@Test
	public void dummy(){}
}
