package com.projects.oleksii.leheza.cashtruck.domain;

import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Builder(toBuilder = true)
@Entity
@Table(name = "images")
public class Image {

    @Id
    @SequenceGenerator(name = "image_sequence", sequenceName = "image_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_sequence")
    private Long id;

    @Lob
    private byte[] imageBytes;

    public Image(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
}
