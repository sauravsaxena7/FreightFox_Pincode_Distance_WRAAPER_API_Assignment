package com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.repos;

import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.entities.PincodeLocation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PincodeLocationDistanceAndDurationRepos extends CrudRepository<PincodeLocation, Long> {

    // Find by source and destination pincode
    boolean existsBySourcePincodeAndDestinationPincode(String sourcePincode, String destinationPincode);

    Optional<PincodeLocation> findBySourcePincodeAndDestinationPincode(String sourcePincode, String destinationPincode);


    // Find by source and destination latitude/longitude
    Optional<PincodeLocation> findBySourceLatitudeAndSourceLongitudeAndDestinationLatitudeAndDestinationLongitude(
            BigDecimal sourceLatitude,
            BigDecimal sourceLongitude,
            BigDecimal destinationLatitude,
            BigDecimal destinationLongitude
    );

    boolean existsBySourceLatitudeAndSourceLongitudeAndDestinationLatitudeAndDestinationLongitude(
            BigDecimal sourceLatitude,
            BigDecimal sourceLongitude,
            BigDecimal destinationLatitude,
            BigDecimal destinationLongitude
    );
}