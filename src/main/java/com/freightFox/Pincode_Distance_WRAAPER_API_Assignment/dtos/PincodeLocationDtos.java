package com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.locationtech.jts.geom.Geometry;

import java.math.BigDecimal;
import java.math.BigInteger;

@Builder
@Data
@AllArgsConstructor
public class PincodeLocationDtos {
    
    private Long pincodesLocationId;

    private String sourcePincode;

    private String destinationPincode;


    private BigDecimal sourceLatitude;


    private BigDecimal sourceLongitude;

    private BigDecimal destinationLatitude;


    private BigDecimal destinationLongitude;


    private BigDecimal distanceInKM;

    private BigInteger durationInMinutes;


    private String origin_addresses;


    private String destination_addresses;


    private Geometry polygon;
}
