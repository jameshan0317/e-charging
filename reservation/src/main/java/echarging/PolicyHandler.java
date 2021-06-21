package echarging;

import echarging.config.kafka.KafkaProcessor;

import java.util.Optional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;


@Service
public class PolicyHandler{
    @Autowired ReservationRepository reservationRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverChargingStarted_UpdateStatus(@Payload ChargingStarted chargingStarted){

        if(!chargingStarted.validate()) return;

        System.out.println("\n\n##### listener UpdateStatus : " + chargingStarted.toJson() + "\n\n");
        Optional<Reservation> reservation = reservationRepository.findById(chargingStarted.getReserveId());
        if(reservation.isPresent()){
            Reservation reservationValue = reservation.get();
            reservationValue.setStatus("CHARGING_STARTED");
            reservationRepository.save(reservationValue);
        }    
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverChargingEnded_UpdateStatus(@Payload ChargingEnded chargingEnded){

        if(!chargingEnded.validate()) return;

        System.out.println("\n\n##### listener UpdateStatus : " + chargingEnded.toJson() + "\n\n");
        Optional<Reservation> reservation = reservationRepository.findById(chargingEnded.getReserveId());
        if(reservation.isPresent()){
            Reservation reservationValue = reservation.get();
            reservationValue.setStatus("CHARGING_ENDED");
            reservationRepository.save(reservationValue);
        }    
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
