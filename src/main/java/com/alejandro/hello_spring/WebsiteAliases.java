package com.alejandro.hello_spring;

import java.util.Locale;
import java.util.Set;

public final class WebsiteAliases {

    public static final String WEBSITE_PATH = "/Whisper/";
    public static final String WEBSITE_MANUAL_PATH = "/Whisper/manual";
    public static final Set<String> NORMALIZED_ALIASES = Set.of(
            "whisper",
            "whisperapp",
            "whisperlive",
            "whispercaption",
            "whispercaptions",
            "whispersubtitle",
            "whispersubtitles",
            "whisperoverlay",
            "whisperlivecaption",
            "whisperlivecaptions",
            "whisperlivesubtitle",
            "whisperlivesubtitles",
            "livecaption",
            "livecaptions",
            "livecaptionstudio",
            "livesubtitle",
            "livesubtitles",
            "captionoverlay",
            "subtitleoverlay",
            "website");

    private WebsiteAliases() {
    }

    public static boolean isAliasPath(String requestPath) {
        if (requestPath == null || requestPath.isBlank()) {
            return false;
        }

        String trimmed = requestPath.trim();
        if (!trimmed.startsWith("/") || trimmed.startsWith(WEBSITE_PATH)) {
            return false;
        }

        String normalizedPath = trimmed.endsWith("/") && trimmed.length() > 1
                ? trimmed.substring(0, trimmed.length() - 1)
                : trimmed;

        int lastSlash = normalizedPath.lastIndexOf('/');
        if (lastSlash < 0 || lastSlash != 0) {
            return false;
        }

        String segment = normalizedPath.substring(1);
        if (segment.isBlank() || segment.contains(".")) {
            return false;
        }

        return NORMALIZED_ALIASES.contains(normalizeSegment(segment));
    }

    public static String normalizeSegment(String segment) {
        return segment
                .replaceAll("[^A-Za-z0-9]", "")
                .toLowerCase(Locale.ROOT);
    }
}
