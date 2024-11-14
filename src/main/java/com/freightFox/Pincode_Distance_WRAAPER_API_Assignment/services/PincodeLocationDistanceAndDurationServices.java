package com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.services;

import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.POJO.distanceMatrixApi.DistanceMatrixElement;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.POJO.distanceMatrixApi.DistanceMatrixRow;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.POJO.distanceMatrixApi.GoogleDistanceMatrixApiResponse;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.dtos.PincodeLocationDtos;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.entities.PincodeLocation;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.globalException.CatchGlobalException;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.modelMapper.ModelMapper;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.repos.PincodeLocationDistanceAndDurationRepos;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.response.HttpApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class PincodeLocationDistanceAndDurationServices {

    @Value("${google.api.key}")
    private String GOOGLE_API_KEY;

    @Value("${google.base.url}")
    private String GOOGLE_BASE_URL;

    private final PincodeLocationDistanceAndDurationRepos locationDistanceAndDurationRepos;
    private final ModelMapper<PincodeLocation, PincodeLocationDtos> mapper;
    private final WebClient.Builder webClientBuilder;


    public PincodeLocationDistanceAndDurationServices(PincodeLocationDistanceAndDurationRepos locationDistanceAndDurationRepos, ModelMapper<PincodeLocation, PincodeLocationDtos> mapper, WebClient.Builder webClientBuilder) {
        this.locationDistanceAndDurationRepos = locationDistanceAndDurationRepos;
        this.mapper = mapper;
        this.webClientBuilder = webClientBuilder;
    }


    @Cacheable(value = "PincodeLocationDtos", key = "#origin + '-' + #destination")
    public PincodeLocationDtos getDistanceAndDuration(PincodeLocationDtos pincodeLocationReq,boolean isPincodeAvailable
            ,boolean isCoordinatesDecimalAvailable
            ,String origin, String destination) throws CatchGlobalException {


        if(!isCoordinatesDecimalAvailable && !isPincodeAvailable) throw new CatchGlobalException("Invalid Request, origin and destination pincode or Lat & long is not valid.", HttpStatus.BAD_REQUEST.toString(),HttpStatus.BAD_REQUEST.value());

        if(isPincodeAvailable && locationDistanceAndDurationRepos
                .existsBySourcePincodeAndDestinationPincode(pincodeLocationReq.getSourcePincode()
                        ,pincodeLocationReq.getDestinationPincode())){
            System.out.println("isPincodeAvailable"+"DB CALLS");

            Optional<PincodeLocation> location = locationDistanceAndDurationRepos
                    .findBySourcePincodeAndDestinationPincode(pincodeLocationReq.getSourcePincode(),pincodeLocationReq.getDestinationPincode());

            if (location.isPresent()) {
                return mapper.toDTO(location.get());
            }
        }

        if(isCoordinatesDecimalAvailable && locationDistanceAndDurationRepos
                .existsBySourceLatitudeAndSourceLongitudeAndDestinationLatitudeAndDestinationLongitude
                        (pincodeLocationReq.getSourceLatitude()
                                ,pincodeLocationReq.getSourceLongitude()
                                ,pincodeLocationReq.getDestinationLatitude()
                                ,pincodeLocationReq.getDestinationLongitude()
                        )){
            System.out.println("isCoordinatesDecimalAvailable"+"DB CALLS");

            Optional<PincodeLocation> location = locationDistanceAndDurationRepos
                    .findBySourceLatitudeAndSourceLongitudeAndDestinationLatitudeAndDestinationLongitude
                            (pincodeLocationReq.getSourceLatitude()
                                    ,pincodeLocationReq.getSourceLongitude()
                                    ,pincodeLocationReq.getDestinationLatitude()
                                    ,pincodeLocationReq.getDestinationLongitude()
                            );

            if (location.isPresent()) return mapper.toDTO(location.get());
        }



        PincodeLocationDtos savedPinCode  = findDistanceAndDurationUsingGoogleApi(origin,destination,pincodeLocationReq);

        PincodeLocation pincodeLocation = mapper.toEntity(savedPinCode);

        return  mapper.toDTO(locationDistanceAndDurationRepos.save(pincodeLocation));

    }


    @Cacheable(value = "PincodeLocationDtos", key = "#origin + '-' + #destination")
    private PincodeLocationDtos findDistanceAndDurationUsingGoogleApi(String origin, String destination, PincodeLocationDtos pincodeLocationReq) throws CatchGlobalException {

        String url = GOOGLE_BASE_URL+"/maps/api/distancematrix/json?origins="
                + origin + "&destinations=" + destination + "&key=" + GOOGLE_API_KEY;

        System.out.println("url"+url);

        try{
            GoogleDistanceMatrixApiResponse distanceMatrixApiResponse = webClientBuilder.build().get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(GoogleDistanceMatrixApiResponse.class)
                    .block();//to call web services synchronously


            assert distanceMatrixApiResponse != null;
            System.out.println("GoogleDistanceMatrixApiResponse Calling: "+distanceMatrixApiResponse);

            DistanceMatrixRow row = distanceMatrixApiResponse.rows.get(0);
            DistanceMatrixElement element = row.elements.get(0);

            if(element.status.equals("ZERO_RESULTS")){
                Map<String,GoogleDistanceMatrixApiResponse> matrixApiResponseMap = new HashMap<>();
                matrixApiResponseMap.put("DistanceMatrixApiResponse",distanceMatrixApiResponse);
                HttpApiResponse.getExceptionHttpApiResponse("ZERO_RESULTS",matrixApiResponseMap,HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.toString());
            }

            // Convert the distance to kilometers and set precision/scale
            BigDecimal distanceInKM = BigDecimal.valueOf(element.distance.value / 1000.0);

            // Set the precision to 6 digits and scale to 2 decimal places
            distanceInKM = distanceInKM.setScale(2, RoundingMode.HALF_UP);

            // Set the value for the pincodeLocationReq
            pincodeLocationReq.setDistanceInKM(distanceInKM);
            pincodeLocationReq.setDurationInMinutes(BigInteger.valueOf(element.duration.value));

            pincodeLocationReq.setDestination_addresses(distanceMatrixApiResponse.destination_addresses.get(0));
            pincodeLocationReq.setOrigin_addresses(distanceMatrixApiResponse.origin_addresses.get(0));

            return pincodeLocationReq;
        }catch (Exception ex){
            throw new CatchGlobalException(ex.toString(),HttpStatus.INTERNAL_SERVER_ERROR.toString(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }


    }
}
