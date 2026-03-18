package com.alejandro.cv.contact;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
public class ContactMessageController {

    private final ContactMailService contactMailService;

    public ContactMessageController(ContactMailService contactMailService) {
        this.contactMailService = contactMailService;
    }

    @PostMapping(path = "/send", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> sendMessage(
            @Valid @RequestBody ContactMessageRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(validationErrorResponse(bindingResult));
        }

        try {
            contactMailService.send(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Your message was sent successfully."));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                    "success", false,
                    "message", ex.getMessage()));
        } catch (MessagingException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                    "success", false,
                    "message", "The message could not be built for delivery. Please try again."));
        }
    }

    private Map<String, Object> validationErrorResponse(BindingResult bindingResult) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        return Map.of(
                "success", false,
                "message", "Please correct the highlighted fields and try again.",
                "errors", errors);
    }
}
