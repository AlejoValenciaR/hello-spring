package com.alejandro.cv.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactMessageRequest(
        @NotBlank(message = "Your email is required.")
        @Email(message = "Provide a valid email address.")
        @Size(max = 320, message = "The sender email is too long.")
        String fromEmail,

        @NotBlank(message = "The subject is required.")
        @Size(max = 160, message = "The subject must be 160 characters or fewer.")
        String subject,

        @NotBlank(message = "The message is required.")
        @Size(max = 5000, message = "The message must be 5000 characters or fewer.")
        String message
) {
}
