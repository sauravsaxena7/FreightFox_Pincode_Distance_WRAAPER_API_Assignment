package com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Geometry;

import java.math.BigDecimal;
import java.math.BigInteger;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "pincode_locations")
public class PincodeLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pincodesLocationId;


    private String sourcePincode;

    private String destinationPincode;

    @Column(precision = 10, scale = 8)
    private BigDecimal sourceLatitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal sourceLongitude;

    @Column(precision = 10, scale = 8)
    private BigDecimal destinationLatitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal destinationLongitude;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal distanceInKM;

    private BigInteger durationInMinutes;

    @Column(nullable = false)
    private String origin_addresses;

    @Column(nullable = false)
    private String destination_addresses;

    @Column(columnDefinition = "GEOMETRY")
    private Geometry polygon;
}

//        In JPA, when using precision and scale attributes, you need to apply them to BigDecimal fields
//        , as they control the number of digits and decimal places stored. Here’s how they work:
//
//Precision: Total number of digits that the column can hold.
//        Scale: Number of digits to the right of the decimal point.
//If you want a column that can store values up to 99999999.99 (eight digits before the decimal and two after)
//        , use precision = 10 and scale = 2. Below is how to apply this to your RouteInfo