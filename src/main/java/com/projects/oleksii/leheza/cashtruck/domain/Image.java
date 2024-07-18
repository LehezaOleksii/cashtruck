package com.projects.oleksii.leheza.cashtruck.domain;

import jakarta.persistence.*;
import lombok.*;

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
    @SequenceGenerator(name = "image_sequence", sequenceName = "image_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_sequence")
    private Long id;
    @Lob
    @Column(name = "image_bytes")
    private byte[] imageBytes;

    public Image(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
}
