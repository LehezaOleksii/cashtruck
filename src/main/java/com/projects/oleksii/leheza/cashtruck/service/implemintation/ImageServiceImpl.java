package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.Image;
import com.projects.oleksii.leheza.cashtruck.repository.ImageRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Override
    public Image save(Image image) {
        return imageRepository.save(image);
    }

    @Override
    public String getDefaultAvatarImage() {
        try {
            String defaultImagePath = "static/images/default_user_image.jpg";
            Resource resource = new ClassPathResource(defaultImagePath);
            InputStream inputStream = resource.getInputStream();
            byte[] imageBytes = FileCopyUtils.copyToByteArray(inputStream);
            return java.util.Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            return "";
        }
    }
}
