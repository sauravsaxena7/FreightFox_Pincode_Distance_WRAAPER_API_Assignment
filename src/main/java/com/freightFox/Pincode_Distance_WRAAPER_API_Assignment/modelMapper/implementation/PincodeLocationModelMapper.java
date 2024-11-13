package com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.modelMapper.implementation;

import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.dtos.PincodeLocationDtos;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.entities.PincodeLocation;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.modelMapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PincodeLocationModelMapper implements ModelMapper<PincodeLocation, PincodeLocationDtos> {
    @Override
    public PincodeLocationDtos toDTO(PincodeLocation e) {
        if(e==null) return null;

       return PincodeLocationDtos.builder()
               .sourcePincode(e.getSourcePincode())
               .destinationPincode(e.getDestinationPincode())
               .pincodesLocationId(e.getPincodesLocationId())
               .sourceLatitude(e.getSourceLatitude())
               .sourceLongitude(e.getSourceLongitude())
               .destinationLatitude(e.getDestinationLatitude())
               .destinationLongitude(e.getDestinationLongitude())
               .polygon(e.getPolygon())
               .origin_addresses(e.getOrigin_addresses())
               .destination_addresses(e.getDestination_addresses())
               .distanceInKM(e.getDistanceInKM())
               .durationInMinutes(e.getDurationInMinutes())

               .build();
    }

    @Override
    public PincodeLocation toEntity(PincodeLocationDtos e) {
        if(e==null) return null;

        return PincodeLocation.builder()
                .sourcePincode(e.getSourcePincode())
                .destinationPincode(e.getDestinationPincode())
                .pincodesLocationId(e.getPincodesLocationId())
                .sourceLatitude(e.getSourceLatitude())
                .sourceLongitude(e.getSourceLongitude())
                .destinationLatitude(e.getDestinationLatitude())
                .destinationLongitude(e.getDestinationLongitude())
                .polygon(e.getPolygon())
                .origin_addresses(e.getOrigin_addresses())
                .destination_addresses(e.getDestination_addresses())
                .distanceInKM(e.getDistanceInKM())
                .durationInMinutes(e.getDurationInMinutes())

                .build();
    }
}
