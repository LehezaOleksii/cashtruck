package com.projects.oleksii.leheza.cashtruck.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class EmailContext {

    private String from;
    private String to;
    private String subject;
    private String email;
    private MultipartFile attachment;
    private String fromDisplayName;
    private String emailLanguage;
    private String displayName;
    private String templateLocation;
    private Map<String, Object> context;
}
