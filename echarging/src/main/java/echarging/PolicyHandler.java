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
    @Autowired EchargingRepository echargingRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReserved_AcceptRsrv(@Payload Reserved reserved){

        if(!reserved.validate()) return;

        System.out.println("\n\n##### listener AcceptRsrv : " + reserved.toJson() + "\n\n");

        // 충전준비(Accept Reservation) //
        Echarging echarging = new Echarging();

        echarging.setStatus("CHARGING_READY");
        echarging.setReserveId(reserved.getReserveId());            
        echarging.setChargerId(reserved.getChargerId());
        echarging.setRsrvDate(reserved.getRsrvDate());
        echarging.setUserId(reserved.getUserId());

        echargingRepository.save(echarging);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRsrvCancelled_CancelRsrv(@Payload RsrvCancelled rsrvCancelled){

        if(!rsrvCancelled.validate()) return;

        System.out.println("\n\n##### listener CancelRsrv : " + rsrvCancelled.toJson() + "\n\n");

        // 충전예약취소 (Reservation Cancell) //     
        Optional<Echarging> echargingOptional = echargingRepository.findByReserveId(rsrvCancelled.getReserveId());
        if(echargingOptional.isPresent()){
            Echarging echarging = echargingOptional.get();
            echarging.setStatus("RESERVE_CANCELLED");
            echargingRepository.save(echarging);
        }    
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
