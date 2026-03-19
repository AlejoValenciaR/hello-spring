package com.alejandro.hello_spring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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

	@Test
	void cvRouteRendersHtml() throws Exception {
		mockMvc.perform(get("/apps/portfolio/alejandro").contextPath("/apps"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith("text/html"))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Alejandro Valencia - Senior Backend Developer")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Alejandro Valencia")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Senior Backend Developer")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Java & Python Solutions")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Download my CV:")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Full Resume (8 pages)")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Small Resume (2 pages)")));
	}

	@Test
	void cvPdfRouteDownloadsPdf() throws Exception {
		mockMvc.perform(get("/apps/portfolio/alejandro/cv.pdf").contextPath("/apps"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PDF))
			.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, org.hamcrest.Matchers.containsString("Alejandro-Valencia-Rivera-Resume.pdf")))
			.andExpect(result -> {
				byte[] content = result.getResponse().getContentAsByteArray();
				org.junit.jupiter.api.Assertions.assertTrue(content.length > 1000, "PDF should not be empty");
				String pdfHeader = new String(content, 0, 5, java.nio.charset.StandardCharsets.US_ASCII);
				org.junit.jupiter.api.Assertions.assertEquals("%PDF-", pdfHeader);
			});
	}

	@Test
	void compactCvPdfRouteDownloadsPdf() throws Exception {
		mockMvc.perform(get("/apps/portfolio/alejandro/compact-resume.pdf").contextPath("/apps"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PDF))
			.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, org.hamcrest.Matchers.containsString("Alejandro-Valencia-Rivera-Compact-Resume.pdf")))
			.andExpect(result -> {
				byte[] content = result.getResponse().getContentAsByteArray();
				org.junit.jupiter.api.Assertions.assertTrue(content.length > 1000, "Compact PDF should not be empty");
				String pdfHeader = new String(content, 0, 5, java.nio.charset.StandardCharsets.US_ASCII);
				org.junit.jupiter.api.Assertions.assertEquals("%PDF-", pdfHeader);
			});
	}
}
