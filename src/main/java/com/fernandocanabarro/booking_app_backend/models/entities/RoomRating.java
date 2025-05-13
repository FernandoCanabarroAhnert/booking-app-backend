package com.fernandocanabarro.booking_app_backend.models.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "room_ratings")
@EqualsAndHashCode(of = "id")
public class RoomRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "fk_room_rating_room_id", value = ConstraintMode.CONSTRAINT))
    private Room room;
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_room_rating_user_id", value = ConstraintMode.CONSTRAINT))
    private User user;
    private BigDecimal rating;
    private String description;
    private LocalDateTime createdAt;

}
