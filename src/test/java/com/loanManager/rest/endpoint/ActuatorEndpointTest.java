package com.loanManager.rest.endpoint;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
/**
 * Actuator tests
 * 
 * @author Rados³aw Ba³trukiewicz
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ActuatorEndpointTest extends BaseEndpointTest {

    @Before
    public void setup() throws Exception {

    	super.setup();
    }

    @Test
    public void getInfo() throws Exception {	
    	mockMvc.perform(get("/actuator/info"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(JSON_MEDIA_TYPE))
				.andExpect(jsonPath("$.build", isA(Object.class)))
				.andExpect(jsonPath("$.build.version", is("0.0.3-SNAPSHOT")))
				.andExpect(jsonPath("$.build.artifact", is("loanManagerDemo")))
				.andExpect(jsonPath("$.build.group", is("com.loanManager")))
		    	.andExpect(jsonPath("$.build.time", isA(String.class)))
    	;
    }

	@Test
	public void getHealth() throws Exception {

		mockMvc.perform(get("/actuator/health"))
				.andDo(print())
				.andExpect(jsonPath("$.status", is("UP")))
				.andExpect(status().isOk())
				.andExpect(content().contentType(JSON_MEDIA_TYPE))
		;
	}

	@Test
	@Ignore("This test will be implemented after enabling security")
	public void getEnvironment() throws Exception {

		mockMvc.perform(get("/actuator/env"))
				.andDo(print())
				.andExpect(status().isUnauthorized())
				;
	}

}
