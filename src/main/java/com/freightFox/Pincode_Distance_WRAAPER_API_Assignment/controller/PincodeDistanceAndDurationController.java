package com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.controller;

import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.dtos.PincodeLocationDtos;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.globalException.CatchGlobalException;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.response.HttpApiResponse;
import com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.services.PincodeLocationDistanceAndDurationServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class PincodeDistanceAndDurationController {

    private final PincodeLocationDistanceAndDurationServices pincodeLocationDistanceAndDurationServices;

    public PincodeDistanceAndDurationController(PincodeLocationDistanceAndDurationServices pincodeLocationDistanceAndDurationServices) {
        this.pincodeLocationDistanceAndDurationServices = pincodeLocationDistanceAndDurationServices;
    }

    @PostMapping("/getDistanceAndDuration")
    public ResponseEntity<HttpApiResponse> getDistanceAndDuration(@RequestBody PincodeLocationDtos pincodeLocationDtos) throws CatchGlobalException {
       try{

           boolean isPincodeAvailable=false;
           if(!Objects.isNull(pincodeLocationDtos.getSourcePincode())
                   && !Objects.isNull(pincodeLocationDtos.getDestinationPincode())){
               isPincodeAvailable= (!pincodeLocationDtos.getSourcePincode().isBlank() && !pincodeLocationDtos.getDestinationPincode().isBlank());
           }

           boolean isCoordinatesDecimalAvailable=(!Objects.isNull(pincodeLocationDtos.getDestinationLatitude())
                   && !Objects.isNull(pincodeLocationDtos.getDestinationLongitude())
                   && !Objects.isNull(pincodeLocationDtos.getSourceLatitude())
                   && !Objects.isNull(pincodeLocationDtos.getSourceLongitude()));

           String origin = "";
           String destination="";
           if(isPincodeAvailable){
               origin=pincodeLocationDtos.getSourcePincode().trim();
               destination=pincodeLocationDtos.getDestinationPincode().trim();
           }else {

               origin = pincodeLocationDtos.getSourceLatitude()+","+pincodeLocationDtos.getSourceLongitude();
               destination = pincodeLocationDtos.getDestinationLatitude()+","+pincodeLocationDtos.getDestinationLongitude();

           }
           PincodeLocationDtos savedlocation = pincodeLocationDistanceAndDurationServices.getDistanceAndDuration(pincodeLocationDtos,isPincodeAvailable,isCoordinatesDecimalAvailable,origin,destination);
           Map<String,PincodeLocationDtos> locationDtosMap = new HashMap<>();
           locationDtosMap.put("DistanceAndDuration",savedlocation);
           return new ResponseEntity<>(HttpApiResponse
                   .getSuccessHttpApiResponse("Successful",locationDtosMap,HttpStatus.OK.value()),HttpStatus.OK);

       }catch (Exception ex){
           throw new CatchGlobalException(ex.getMessage(),null,500);
       }
    }
}
