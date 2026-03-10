package com.alejandro.hello_spring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HelloSpringApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void homeRouteRendersHtml() throws Exception {
		mockMvc.perform(get("/apps/").contextPath("/apps"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith("text/html"))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Atlas Grove")));
	}

	@Test
	void helloRouteRendersHtml() throws Exception {
		mockMvc.perform(get("/apps/api/hello").contextPath("/apps"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith("text/html"))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Hello, World!")));
	}

	@Test
	void usersRouteRendersHtml() throws Exception {
		mockMvc.perform(get("/apps/api/users").contextPath("/apps"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith("text/html"))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Alice")));
	}

	@Test
	void userDetailRouteRendersHtml() throws Exception {
		mockMvc.perform(get("/apps/api/users/2").contextPath("/apps"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith("text/html"))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("User with id: 2")));
	}
}
