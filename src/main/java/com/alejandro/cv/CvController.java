package com.alejandro.cv;

import java.nio.charset.StandardCharsets;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/portfolio")
public class CvController {

    private final AlejandroResumePdfService alejandroResumePdfService;

    public CvController(AlejandroResumePdfService alejandroResumePdfService) {
        this.alejandroResumePdfService = alejandroResumePdfService;
    }

    @GetMapping("/alejandro")
    public String showAlejandroPortfolio() {
        return "cv/index6";
    }

    @GetMapping(value = "/alejandro/cv.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadAlejandroCvPdf() {
        byte[] pdfBytes = alejandroResumePdfService.generateAlejandroResumePdf();
        ContentDisposition contentDisposition = ContentDisposition.attachment()
            .filename("Alejandro-Valencia-Rivera-Resume.pdf", StandardCharsets.UTF_8)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(pdfBytes.length)
            .body(pdfBytes);
    }

    @GetMapping(value = "/alejandro/compact-resume.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadAlejandroCompactResumePdf() {
        byte[] pdfBytes = alejandroResumePdfService.generateAlejandroCompactResumePdf();
        ContentDisposition contentDisposition = ContentDisposition.attachment()
            .filename("Alejandro-Valencia-Rivera-Compact-Resume.pdf", StandardCharsets.UTF_8)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(pdfBytes.length)
            .body(pdfBytes);
    }
}
