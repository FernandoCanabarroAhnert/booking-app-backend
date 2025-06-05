package com.fernandocanabarro.booking_app_backend.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fernandocanabarro.booking_app_backend.mappers.RoomMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRatingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRatingResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Hotel;
import com.fernandocanabarro.booking_app_backend.models.entities.Image;
import com.fernandocanabarro.booking_app_backend.models.entities.Room;
import com.fernandocanabarro.booking_app_backend.models.entities.RoomRating;
import com.fernandocanabarro.booking_app_backend.models.entities.User;
import com.fernandocanabarro.booking_app_backend.models.enums.ImageTypeEnum;
import com.fernandocanabarro.booking_app_backend.repositories.HotelRepository;
import com.fernandocanabarro.booking_app_backend.repositories.ImageRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoomRatingRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoomRepository;
import com.fernandocanabarro.booking_app_backend.services.AuthService;
import com.fernandocanabarro.booking_app_backend.services.RoomService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.BadRequestException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ForbiddenException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.booking_app_backend.utils.FileUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final ImageRepository imageRepository;
    private final RoomRatingRepository roomRatingRepository;
    private final AuthService authService;

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> findAll() {
        return this.roomRepository.findAll().stream().map(RoomMapper::convertEntityToResponse).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponseDTO> findAllPageable(List<String> types, Integer capacity, BigDecimal minPrice, BigDecimal maxPrice, 
                                                String city, LocalDate checkIn, LocalDate checkOut, Long hotelId, Pageable pageable) {
        minPrice = minPrice != null ? minPrice : roomRepository.findMinPricePerNight();
        maxPrice = maxPrice != null ? maxPrice : roomRepository.findMaxPricePerNight();
        types = types == null || types.isEmpty() ? null : types;                                       
        Page<Room> page = this.roomRepository.findByTypeOrCapacityOrPricePerNightOrByHotelCity(types, capacity, minPrice, maxPrice, city, hotelId, pageable);
        List<RoomResponseDTO> roomResponseDTOs = page.getContent().stream()
            .filter(room -> room.isAvalableToBook(checkIn, checkOut, null))
            .map(RoomMapper::convertEntityToResponse)
            .toList();
        return new PageImpl<RoomResponseDTO>(roomResponseDTOs, pageable, roomResponseDTOs.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponseDTO> findAllPageable(Pageable pageable) {
        return this.roomRepository.findAll(pageable)
            .map(RoomMapper::convertEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomDetailResponseDTO findById(Long id) {
        return this.roomRepository.findById(id)
            .map(RoomMapper::convertEntityToDetailResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Room", id));
    }

    @Override
    @Transactional
    public void create(RoomRequestDTO request, List<MultipartFile> images) {
        Hotel hotel = this.hotelRepository.findById(request.getHotelId())
            .orElseThrow(() -> new ResourceNotFoundException("Hotel", request.getHotelId()));
        User user = this.authService.getConnectedUser();
        if (!user.isAbleToCreateOrUpdateRoom(hotel.getId())) {
            throw new ForbiddenException("Operator is not allowed to create a room in this hotel. The operator can only create a room in the hotel he works at");
        }
        Room entity = RoomMapper.convertRequestToEntity(request, hotel);
        this.addImagesToRoom(entity, images);
        this.roomRepository.save(entity);
    }

    private void addImagesToRoom(Room room, List<MultipartFile> images) {
        for (MultipartFile file : images) {
            if (file != null) {
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
    }

    @Override
    @Transactional
    public void update(Long id, RoomRequestDTO request, List<MultipartFile> images) {
        Room room = this.roomRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Room", id));
        User user = this.authService.getConnectedUser();
        if (!user.isAbleToCreateOrUpdateRoom(room.getHotel().getId())) {
            throw new ForbiddenException("Operator is not allowed to update this room. The operator can only update a room in the hotel he works at");
        }
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
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        try {
            if (!this.roomRepository.existsById(id)) {
                throw new ResourceNotFoundException("Room", id);
            }
            this.roomRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Room cannot be deleted because it has bookings associated with it.");
        }
    }

    @Override
    @Transactional
    public void deleteImages(List<Long> imagesIds) {
        imagesIds.forEach(id -> {
            if (!this.imageRepository.existsById(id)) {
                throw new ResourceNotFoundException("Image", id);
            }
            this.imageRepository.deleteById(id);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomRatingResponseDTO> findAllRatingsByRoomId(Long roomId, Pageable pageable) {
        return this.roomRatingRepository.findAllByRoomId(roomId, pageable)
            .map(RoomMapper::convertRoomRatingEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomRatingResponseDTO> findAllRatingsByUserId(Long userId, Pageable pageable, boolean isSelfUser) {
        userId = isSelfUser ? this.authService.getConnectedUser().getId() : userId;
        return this.roomRatingRepository.findAllByUserId(userId, pageable)
            .map(RoomMapper::convertRoomRatingEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomRatingResponseDTO findRatingById(Long roomRatingId) {
        return this.roomRatingRepository.findById(roomRatingId)
            .map(RoomMapper::convertRoomRatingEntityToResponse)
            .orElseThrow(() -> new ResourceNotFoundException("RoomRating", roomRatingId));
    }

    @Override
    @Transactional
    public void addRating(Long roomId, RoomRatingRequestDTO request) {
        Room room = this.roomRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Room", roomId));
        User user = this.authService.getConnectedUser();
        if (!user.isAbleToRateRoom(room.getId())) {
            throw new ForbiddenException("User is not allowed to rate this room. The user can only rate a room the same quantity of times he booked it");
        }
        RoomRating roomRating = RoomMapper.convertRoomRatingRequestToEntity(request, room, user);
        this.roomRatingRepository.save(roomRating);
    }

    @Override
    @Transactional
    public void updateRating(Long roomRatingId, RoomRatingRequestDTO request) {
        RoomRating roomRating = this.roomRatingRepository.findById(roomRatingId)
            .orElseThrow(() -> new ResourceNotFoundException("RoomRating", roomRatingId));
        this.authService.verifyIfConnectedUserHasAdminPermission(roomRating.getUser().getId());
        RoomMapper.updateRoomRating(roomRating, request);
        this.roomRatingRepository.save(roomRating);
    }

    @Override
    @Transactional
    public void deleteRating(Long roomRatingId) {
        RoomRating roomRating = this.roomRatingRepository.findById(roomRatingId)
            .orElseThrow(() -> new ResourceNotFoundException("RoomRating", roomRatingId));
        this.authService.verifyIfConnectedUserHasAdminPermission(roomRating.getUser().getId());
        this.roomRatingRepository.delete(roomRating);
    }
    
}
