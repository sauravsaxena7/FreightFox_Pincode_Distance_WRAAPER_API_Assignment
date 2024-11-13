package com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.repos;

import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.entities.PincodeLocation;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface PincodeLocationDistanceAndDurationRepos extends CrudRepository<PincodeLocation,Long> {

    boolean existsBySourceAndDestinationPinCode(String sourcePincode, String destinationPincode);

    Optional<PincodeLocation> findBySourceAndDestinationPinCode(String sourcePincode, String destinationPincode);

    boolean existsBySourceAndDestinationLatAndLong(BigDecimal sourceLat, BigDecimal sourceLang, BigDecimal destinationLat, BigDecimal destinationLong);

    Optional<PincodeLocation> findBySourceAndDestinationLatAndLong(BigDecimal sourceLat, BigDecimal sourceLang, BigDecimal destinationLat, BigDecimal destinationLong);


}