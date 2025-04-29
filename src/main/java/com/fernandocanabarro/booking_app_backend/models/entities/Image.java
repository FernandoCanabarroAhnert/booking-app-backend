package com.fernandocanabarro.booking_app_backend.models.entities;

import com.fernandocanabarro.booking_app_backend.models.enums.ImageTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String base64Image;

    @Enumerated(EnumType.STRING)
    private ImageTypeEnum imageType;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = true, foreignKey = @ForeignKey(name = "fk_image_hotel", value = ConstraintMode.CONSTRAINT))
    private Hotel hotel;
    
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = true, foreignKey = @ForeignKey(name = "fk_image_room", value = ConstraintMode.CONSTRAINT))
    private Room room;

}
