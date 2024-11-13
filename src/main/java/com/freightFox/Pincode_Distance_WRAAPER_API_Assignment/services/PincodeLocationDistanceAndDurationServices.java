package com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.services;

import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.POJO.distanceMatrixApi.DistanceMatrixElement;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.POJO.distanceMatrixApi.DistanceMatrixRow;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.POJO.distanceMatrixApi.GoogleDistanceMatrixApiResponse;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.dtos.PincodeLocationDtos;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.entities.PincodeLocation;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.globalException.CatchGlobalException;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.modelMapper.ModelMapper;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.repos.PincodeLocationDistanceAndDurationRepos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

@Service
public class PincodeLocationDistanceAndDurationServices {

    @Value("${google.api.key}")
    private String GOOGLE_API_KEY;

    @Value("${google.api.key}")
    private String GOOGLE_BASE_URL;

    private final PincodeLocationDistanceAndDurationRepos locationDistanceAndDurationRepos;
    private final ModelMapper<PincodeLocation, PincodeLocationDtos> mapper;
    private final WebClient.Builder webClientBuilder;


    public PincodeLocationDistanceAndDurationServices(PincodeLocationDistanceAndDurationRepos locationDistanceAndDurationRepos, ModelMapper<PincodeLocation, PincodeLocationDtos> mapper, WebClient.Builder webClientBuilder) {
        this.locationDistanceAndDurationRepos = locationDistanceAndDurationRepos;
        this.mapper = mapper;
        this.webClientBuilder = webClientBuilder;
    }


    public PincodeLocationDtos getDistanceAndDuration(PincodeLocationDtos pincodeLocationReq) throws CatchGlobalException {

        boolean isPincodeAvailable=pincodeLocationReq.getSourcePincode().isBlank() || pincodeLocationReq.getDestinationPincode().isBlank();


        boolean isCoordinatesDecimalAvailable = pincodeLocationReq.getDestinationLatitude().compareTo(BigDecimal.ZERO) > 0
                && pincodeLocationReq.getDestinationLongitude().compareTo(BigDecimal.ZERO) > 0
                && pincodeLocationReq.getSourceLatitude().compareTo(BigDecimal.ZERO) > 0
                && pincodeLocationReq.getSourceLongitude().compareTo(BigDecimal.ZERO) > 0;



        if(isPincodeAvailable){
            Optional<PincodeLocation> location =locationDistanceAndDurationRepos
                    .findBySourceAndDestinationPinCode(pincodeLocationReq.getSourcePincode(),pincodeLocationReq.getDestinationPincode());

            if (location.isPresent()) {
                return mapper.toDTO(location.get());
            }
        }
        if(isCoordinatesDecimalAvailable){
            Optional<PincodeLocation> location = locationDistanceAndDurationRepos
                    .findBySourceAndDestinationLatAndLong(pincodeLocationReq.getSourceLatitude()
                            ,pincodeLocationReq.getSourceLatitude()
                            ,pincodeLocationReq.getDestinationLatitude()
                            ,pincodeLocationReq.getDestinationLongitude()
                    );

            if (location.isPresent()) return mapper.toDTO(location.get());
        }
        if(!isCoordinatesDecimalAvailable && !isPincodeAvailable) throw new CatchGlobalException("Invalid Request, origin and destination pincode or Lat & long is not valid.", HttpStatus.BAD_REQUEST.toString(),HttpStatus.BAD_REQUEST.value());

        PincodeLocationDtos savedPinCode=null;

        if(isPincodeAvailable){
            savedPinCode = findDistanceAndDurationUsingGoogleApi(pincodeLocationReq.getSourcePincode(),pincodeLocationReq.getDestinationPincode(),pincodeLocationReq);
        }

        if(isCoordinatesDecimalAvailable){
            String origin = pincodeLocationReq.getSourceLatitude()+","+pincodeLocationReq.getSourceLongitude();
            String destinations = pincodeLocationReq.getDestinationLatitude()+","+pincodeLocationReq.getDestinationLongitude();
            savedPinCode = findDistanceAndDurationUsingGoogleApi(origin,destinations,pincodeLocationReq);
        }

        PincodeLocation pincodeLocation = mapper.toEntity(savedPinCode);

        return  mapper.toDTO(locationDistanceAndDurationRepos.save(pincodeLocation));

    }

    private PincodeLocationDtos findDistanceAndDurationUsingGoogleApi(String origin, String destination, PincodeLocationDtos pincodeLocationReq) throws CatchGlobalException {

//        String url = GOOGLE_BASE_URL+"/maps/api/distancematrix/json?origins="
//                + origin + "&destinations=" + destination + "&key=" + GOOGLE_API_KEY;

        try{
            GoogleDistanceMatrixApiResponse distanceMatrixApiResponse = webClientBuilder.build().get()
                    .uri(GOOGLE_BASE_URL+"/maps/api/distancematrix/json"
                            ,uriBuilder -> uriBuilder.queryParam("origins",origin)
                                    .queryParam("destinations",destination)
                                    .queryParam("key",GOOGLE_API_KEY).build())
                    .retrieve()
                    .bodyToMono(GoogleDistanceMatrixApiResponse.class)
                    .block();//to call web services synchronously

            assert distanceMatrixApiResponse != null;

            DistanceMatrixRow row = distanceMatrixApiResponse.rows.get(0);
            DistanceMatrixElement element = row.elements.get(0);

            if(element.status.equals("ZERO_RESULTS")){
                throw new CatchGlobalException("Bad Response From Google Distance Matrix Api",HttpStatus.INTERNAL_SERVER_ERROR.toString(),HttpStatus.INTERNAL_SERVER_ERROR.value());
            }

            pincodeLocationReq.setDistanceInKM(BigDecimal.valueOf(element.distance.value / 1000.0));
            pincodeLocationReq.setDurationInMinutes(BigInteger.valueOf(element.duration.value));

            pincodeLocationReq.setDestination_addresses(distanceMatrixApiResponse.destination_addresses.get(0));
            pincodeLocationReq.setOrigin_addresses(distanceMatrixApiResponse.origin_addresses.get(0));

            return pincodeLocationReq;
        }catch (Exception ex){
            throw new CatchGlobalException(ex.toString(),HttpStatus.INTERNAL_SERVER_ERROR.toString(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }


    }
}
