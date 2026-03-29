package com.alejandro.hello_spring;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
public class WebsiteController {

    private static final String SITE_NAME = "Whisper Live Subtitles";
    private static final String HOME_DESCRIPTION =
            "Free Windows subtitle overlay for live listening, transcription, and translation.";
    private static final String MANUAL_DESCRIPTION =
            "Beginner-friendly manual for Whisper Live Subtitles with setup help, controls, and troubleshooting.";

    @GetMapping({"/Whisper", "/Whisper/index.html", "/website", "/website/", "/website/index.html"})
    public String websiteLandingRedirect() {
        return "redirect:" + WebsiteAliases.WEBSITE_PATH;
    }

    @GetMapping(WebsiteAliases.WEBSITE_PATH)
    public String websiteHome(Model model) {
        String baseUrl = baseUrl();
        addCommonMetadata(
                model,
                baseUrl,
                SITE_NAME + " | Live Captions for Windows",
                HOME_DESCRIPTION,
                WebsiteAliases.WEBSITE_PATH,
                "website");
        return "website/index";
    }

    @GetMapping({"/Whisper/manual/", "/Whisper/manual.html", "/website/manual", "/website/manual/", "/website/manual.html"})
    public String websiteManualRedirect() {
        return "redirect:" + WebsiteAliases.WEBSITE_MANUAL_PATH;
    }

    @GetMapping(WebsiteAliases.WEBSITE_MANUAL_PATH)
    public String websiteManual(Model model) {
        String baseUrl = baseUrl();
        addCommonMetadata(
                model,
                baseUrl,
                SITE_NAME + " Manual | Setup and Controls",
                MANUAL_DESCRIPTION,
                WebsiteAliases.WEBSITE_MANUAL_PATH,
                "article");
        return "website/manual";
    }

    @ResponseBody
    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String robotsTxt() {
        return "User-agent: *\n"
                + "Allow: /\n\n"
                + "Sitemap: " + absoluteUrl(baseUrl(), "/sitemap.xml") + "\n";
    }

    @ResponseBody
    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemapXml() {
        String baseUrl = baseUrl();
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                  <url>
                    <loc>%s</loc>
                  </url>
                  <url>
                    <loc>%s</loc>
                  </url>
                </urlset>
                """.formatted(
                absoluteUrl(baseUrl, WebsiteAliases.WEBSITE_PATH),
                absoluteUrl(baseUrl, WebsiteAliases.WEBSITE_MANUAL_PATH));
    }

    private void addCommonMetadata(
            Model model,
            String baseUrl,
            String title,
            String description,
            String canonicalPath,
            String ogType) {
        String canonicalUrl = absoluteUrl(baseUrl, canonicalPath);
        model.addAttribute("metaTitle", title);
        model.addAttribute("metaDescription", description);
        model.addAttribute("canonicalUrl", canonicalUrl);
        model.addAttribute("pageUrl", canonicalUrl);
        model.addAttribute("homeUrl", absoluteUrl(baseUrl, WebsiteAliases.WEBSITE_PATH));
        model.addAttribute("shareImageUrl", absoluteUrl(baseUrl, "/Whisper/social-preview.png"));
        model.addAttribute("faviconUrl", absoluteUrl(baseUrl, "/Whisper/favicon.ico"));
        model.addAttribute("setupDownloadUrl", absoluteUrl(baseUrl, "/Whisper/downloads/WhisperLiveSubtitles-Setup.exe"));
        model.addAttribute("portableDownloadUrl", absoluteUrl(baseUrl, "/Whisper/downloads/WhisperLiveSubtitles.exe"));
        model.addAttribute("manualUrl", absoluteUrl(baseUrl, WebsiteAliases.WEBSITE_MANUAL_PATH));
        model.addAttribute("pdfManualUrl", absoluteUrl(baseUrl, "/Whisper/Whisper-Live-Subtitles-Manual.pdf"));
        model.addAttribute("siteName", SITE_NAME);
        model.addAttribute("ogType", ogType);
        model.addAttribute("currentYear", java.time.Year.now().getValue());
    }

    private String baseUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }

    private String absoluteUrl(String baseUrl, String path) {
        return baseUrl + path;
    }
}
