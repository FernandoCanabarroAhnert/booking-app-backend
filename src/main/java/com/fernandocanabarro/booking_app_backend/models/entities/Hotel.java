package com.fernandocanabarro.booking_app_backend.models.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Entity
@Table(name = "hotels")
@Builder
@EqualsAndHashCode(of = "id")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Integer roomQuantity;
    private String street;
    private String number;
    private String city;
    private String zipCode;
    private String state;
    private String phone;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY)
    private List<Room> rooms;

    @OneToMany(mappedBy = "workingHotel", fetch = FetchType.LAZY)
    private List<User> workers;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY)
    private List<Image> images;

    public BigDecimal getAverageRating() {
        if (this.rooms.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return this.rooms.stream()
            .map(Room::getAverageRating)
            .reduce(BigDecimal.ZERO, (a, b) -> a.add(b))
            .divide(BigDecimal.valueOf(this.rooms.size())).setScale(2, RoundingMode.HALF_UP);
    }

}
