package com.fernandocanabarro.booking_app_backend.services.impl;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fernandocanabarro.booking_app_backend.mappers.HotelMapper;
import com.fernandocanabarro.booking_app_backend.mappers.RoomMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelSearchResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Hotel;
import com.fernandocanabarro.booking_app_backend.models.entities.Image;
import com.fernandocanabarro.booking_app_backend.models.enums.ImageTypeEnum;
import com.fernandocanabarro.booking_app_backend.repositories.HotelRepository;
import com.fernandocanabarro.booking_app_backend.repositories.ImageRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoomRepository;
import com.fernandocanabarro.booking_app_backend.services.HotelService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.BadRequestException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.booking_app_backend.utils.FileUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final ImageRepository imageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HotelSearchResponseDTO> findAllByName(String name) {
        return this.hotelRepository.findAllByNameContainingIgnoreCase(name).stream()
            .map(hotel -> new HotelSearchResponseDTO(hotel.getId(), hotel.getName()))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> findAll() {
        return this.hotelRepository.findAll().stream().map(HotelMapper::convertEntityToResponse).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<HotelResponseDTO> findAllPageable(Pageable pageable, String name) {
        return this.hotelRepository.findAllByNameContainingIgnoreCase(name, pageable).map(HotelMapper::convertEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponseDTO> findRoomsByHotelId(Long hotelId, Pageable pageable) {
        return this.roomRepository.findByHotelId(hotelId, pageable).map(RoomMapper::convertEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public HotelDetailResponseDTO findById(Long id) {
        return this.hotelRepository.findById(id)
            .map(HotelMapper::convertEntityToDetailResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Hotel", id));
    }

    @Override
    @Transactional
    public void create(HotelRequestDTO request, List<MultipartFile> images) {
        Hotel entity = HotelMapper.convertRequestToEntity(request);
        addImagesToHotel(entity, images);
        this.hotelRepository.save(entity);
    }

    private void addImagesToHotel(Hotel hotel, List<MultipartFile> images) {
        for (MultipartFile file : images) {
            if (file != null) {
                String base64Image = FileUtils.generateBase64Image(file);
                Image image = Image.builder()
                    .base64Image(base64Image)
                    .imageType(ImageTypeEnum.HOTEL)
                    .hotel(hotel)
                    .build();
                image = this.imageRepository.save(image);
                hotel.getImages().add(image);
            }
        }
    }

    @Override
    @Transactional
    public void update(Long id, HotelRequestDTO request, List<MultipartFile> images) {
        Hotel hotel = this.hotelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Hotel", id));
        HotelMapper.updateEntity(hotel, request);
        if (images != null) {
            addImagesToHotel(hotel, images);
        }
        this.hotelRepository.save(hotel);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!this.hotelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hotel", id);
        }
        try {
            this.hotelRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Hotel cannot be deleted because it has rooms associated with it.");
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

}
