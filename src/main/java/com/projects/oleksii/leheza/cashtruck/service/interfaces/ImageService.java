package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.Image;

public interface ImageService {

    void save(Image image);

    String getDefaultAvatarImage();
}
