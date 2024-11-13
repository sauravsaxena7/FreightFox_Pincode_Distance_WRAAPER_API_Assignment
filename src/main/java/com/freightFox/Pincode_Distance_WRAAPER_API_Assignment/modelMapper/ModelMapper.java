package com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.modelMapper;



public interface ModelMapper<Entity,DTO> {
    // Mapping entity to DTO
    DTO toDTO(Entity e);

    // Mapping DTO to entity
    Entity toEntity(DTO dto);
}
