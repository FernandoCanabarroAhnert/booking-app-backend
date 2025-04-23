package com.fernandocanabarro.booking_app_backend.models.enums;

public enum RoomTypeEnum {

    SINGLE(1),
    DOUBLE(2),
    SUITE(3);

    private final Integer roomType;

    RoomTypeEnum(int roomType) {
        this.roomType = roomType;
    }

    public int getDescription() {
        return roomType;
    }

    public static RoomTypeEnum fromValue(int value) {
        for (RoomTypeEnum type : RoomTypeEnum.values()) {
            if(type.roomType.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid priority value: " + value);
    }

}
