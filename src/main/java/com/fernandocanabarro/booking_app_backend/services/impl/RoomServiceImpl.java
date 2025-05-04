package com.fernandocanabarro.booking_app_backend.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fernandocanabarro.booking_app_backend.mappers.RoomMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Hotel;
import com.fernandocanabarro.booking_app_backend.models.entities.Image;
import com.fernandocanabarro.booking_app_backend.models.entities.Room;
import com.fernandocanabarro.booking_app_backend.models.enums.ImageTypeEnum;
import com.fernandocanabarro.booking_app_backend.repositories.HotelRepository;
import com.fernandocanabarro.booking_app_backend.repositories.ImageRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoomRepository;
import com.fernandocanabarro.booking_app_backend.services.RoomService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.booking_app_backend.utils.FileUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final ImageRepository imageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> findAll() {
        return this.roomRepository.findAll().stream().map(RoomMapper::convertEntityToResponse).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponseDTO> findAllPageable(Pageable pageable) {
        return this.roomRepository.findAll(pageable).map(RoomMapper::convertEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomDetailResponseDTO findById(Long id) {
        return this.roomRepository.findById(id)
            .map(RoomMapper::convertEntityToDetailResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Room", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalDate> getUnavailableDatesFromRoomByRoomId(Long id) {
        Room room = this.roomRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Room", id));
        return room.getUnavailableDates();
    }

    @Override
    @Transactional
    public void create(RoomRequestDTO request, List<MultipartFile> images) {
        Hotel hotel = this.hotelRepository.findById(request.getHotelId())
            .orElseThrow(() -> new ResourceNotFoundException("Hotel", request.getHotelId()));
        Room entity = RoomMapper.convertRequestToEntity(request, hotel);
        this.addImagesToRoom(entity, images);
        this.roomRepository.save(entity);
    }

    private void addImagesToRoom(Room room, List<MultipartFile> images) {
        for (MultipartFile file : images) {
            String base64Image = FileUtils.generateBase64Image(file);
            Image image = Image.builder()
                .base64Image(base64Image)
                .imageType(ImageTypeEnum.ROOM)
                .room(room)
                .build();
            image = this.imageRepository.save(image);
            room.getImages().add(image);
        }
    }

    @Override
    @Transactional
    public void update(Long id, RoomRequestDTO request, List<MultipartFile> images) {
        Room room = this.roomRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Room", id));
        RoomMapper.updateRoom(room, request);
        if (!request.getHotelId().equals(room.getHotel().getId())) {
            Hotel hotel = this.hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", request.getHotelId()));
            room.setHotel(hotel);
        }
        if (images != null) {
            this.addImagesToRoom(room, images);
        }
        this.roomRepository.save(room);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!this.roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Room", id);
        }
        this.roomRepository.deleteById(id);
    }

    
}
