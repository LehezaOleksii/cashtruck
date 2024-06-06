package com.projects.oleksii.leheza.cashtruck.util;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class ImageConvertor {

    public String convertByteImageToString(byte[] byteImage) {
        return byteImage != null && byteImage.length > 0
                ? Base64.getEncoder().encodeToString(byteImage)
                : "";
    }

    public byte[] convertStringToByteImage(String stringImage) {
        return stringImage != null && !stringImage.isEmpty()
                ? Base64.getDecoder().decode(stringImage)
                : new byte[0];
    }
}
