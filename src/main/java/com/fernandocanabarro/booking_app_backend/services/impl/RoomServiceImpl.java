package com.fernandocanabarro.booking_app_backend.services.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

    @Override
    @Transactional(readOnly = true)
    public Page<RoomRatingResponseDTO> findAllRatingsByRoomId(Long roomId, Pageable pageable) {
        return this.roomRatingRepository.findAllByRoomId(roomId, pageable)
            .map(RoomMapper::convertRoomRatingEntityToResponse);
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
        this.verifyIfConnectedUserIsTheOwnerOfTheResourceOrHasAdminPermission(roomRating);
        RoomMapper.updateRoomRating(roomRating, request);
        this.roomRatingRepository.save(roomRating);
    }

    private void verifyIfConnectedUserIsTheOwnerOfTheResourceOrHasAdminPermission(RoomRating roomRating) {
        User user = this.authService.getConnectedUser();
        boolean isOwner = roomRating.getUser().getId().equals(user.getId());
        boolean hasAdminPermission = user.hasRole("ROLE_ADMIN") || user.hasRole("ROLE_OPERATOR");
        if (isOwner || hasAdminPermission) return;
        throw new ForbiddenException("Only the owner or admins/operators can modify this rating");
    }

    @Override
    @Transactional
    public void deleteRating(Long roomRatingId) {
        RoomRating roomRating = this.roomRatingRepository.findById(roomRatingId)
            .orElseThrow(() -> new ResourceNotFoundException("RoomRating", roomRatingId));
        this.verifyIfConnectedUserIsTheOwnerOfTheResourceOrHasAdminPermission(roomRating);
        this.roomRatingRepository.delete(roomRating);
    }
    
}
