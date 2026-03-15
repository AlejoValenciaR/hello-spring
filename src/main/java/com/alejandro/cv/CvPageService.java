package com.alejandro.cv;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.alejandro.cv.model.ContactSection;
import com.alejandro.cv.model.CvPage;
import com.alejandro.cv.model.EducationSection;
import com.alejandro.cv.model.ExperienceSection;
import com.alejandro.cv.model.HeroSection;
import com.alejandro.cv.model.LanguagesSection;
import com.alejandro.cv.model.NavigationSection;
import com.alejandro.cv.model.PersonalInfoSection;
import com.alejandro.cv.model.ProfileSection;
import com.alejandro.cv.model.ReferencesSection;
import com.alejandro.cv.model.SkillsSection;
import com.alejandro.cv.model.TechnologiesSection;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CvPageService {

    private final ObjectMapper objectMapper;

    public CvPageService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CvPage loadAlejandroCv() {
        return new CvPage(
            readSection("cv/personal-info.json", PersonalInfoSection.class),
            readSection("cv/navigation.json", NavigationSection.class),
            readSection("cv/hero.json", HeroSection.class),
            readSection("cv/profile.json", ProfileSection.class),
            readSection("cv/skills.json", SkillsSection.class),
            readSection("cv/technologies.json", TechnologiesSection.class),
            readSection("cv/experience.json", ExperienceSection.class),
            readSection("cv/education.json", EducationSection.class),
            readSection("cv/languages.json", LanguagesSection.class),
            readSection("cv/references.json", ReferencesSection.class),
            readSection("cv/contact.json", ContactSection.class)
        );
    }

    private <T> T readSection(String resourcePath, Class<T> sectionType) {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, sectionType);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load CV section from " + resourcePath, exception);
        }
    }
}
