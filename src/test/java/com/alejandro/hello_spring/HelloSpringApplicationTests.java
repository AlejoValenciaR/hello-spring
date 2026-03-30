package com.alejandro.hello_spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
    void rootRouteRendersHelloWorldPage() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("text/html"))
            .andExpect(content().string(containsString("Atlas Grove")))
            .andExpect(content().string(containsString("Hello, World!")));
    }

    @Test
    void websiteRouteRendersSeoReadyLandingPage() throws Exception {
        mockMvc.perform(get("/Whisper/"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("text/html"))
            .andExpect(content().string(containsString("Whisper Live Subtitles")))
            .andExpect(content().string(containsString("summary_large_image")))
            .andExpect(content().string(containsString("site.webmanifest")))
            .andExpect(content().string(containsString("SoftwareApplication")));
    }

    @Test
    void aliasRoutesRedirectToCanonicalWebsiteRoute() throws Exception {
        mockMvc.perform(get("/WhisperLiveCaption").queryParam("utm_source", "test"))
            .andExpect(status().isPermanentRedirect())
            .andExpect(header().string("Location", "/Whisper/?utm_source=test"));

        mockMvc.perform(get("/WhisperApp"))
            .andExpect(status().isPermanentRedirect())
            .andExpect(header().string("Location", "/Whisper/"));
    }

    @Test
    void manualRouteRendersFromCanonicalPath() throws Exception {
        mockMvc.perform(get("/Whisper/manual"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("text/html"))
            .andExpect(content().string(containsString("Beginner manual")))
            .andExpect(content().string(containsString("TechArticle")));
    }

    @Test
    void seoSupportEndpointsAreAvailable() throws Exception {
        mockMvc.perform(get("/robots.txt"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("text/plain"))
            .andExpect(content().string(containsString("Sitemap: http://localhost/sitemap.xml")));

        mockMvc.perform(get("/sitemap.xml"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/xml"))
            .andExpect(content().string(containsString("<loc>http://localhost/Whisper/</loc>")))
            .andExpect(content().string(containsString("<loc>http://localhost/Whisper/manual</loc>")));
    }

    @Test
    void websiteAliasStaticResourcesResolveWithoutDuplicatePackaging() throws Exception {
        mockMvc.perform(get("/website/site.webmanifest"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Whisper Live")));
    }
}
