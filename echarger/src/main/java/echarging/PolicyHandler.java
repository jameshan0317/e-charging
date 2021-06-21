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
    @Autowired EchargerRepository echargerRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRsrvCancelled_ModifyRsrvTime(@Payload RsrvCancelled rsrvCancelled){

        if(!rsrvCancelled.validate()) return;

        System.out.println("\n\n##### listener ModifyRsrvTime : " + rsrvCancelled.toJson() + "\n\n");

        // 예약취소건 --> 예약 가능으로 변경 //
        Optional<Echarger> echarger = echargerRepository.findById(rsrvCancelled.getChargerId());
        
        if(echarger.isPresent()){
            Echarger echargerValue = echarger.get();
            if(echargerValue.getRsrvTimeAm() == null){
                echargerValue.setRsrvTimePm(null);
            }else if(echargerValue.getRsrvTimePm() == null){
                echargerValue.setRsrvTimeAm(null);
            }    
            echargerRepository.save(echargerValue);
        }
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverChargingEnded_ModifyRsrvTime(@Payload ChargingEnded chargingEnded){

        if(!chargingEnded.validate()) return;

        System.out.println("\n\n##### listener ModifyRsrvTime : " + chargingEnded.toJson() + "\n\n");

        // 충전완료건 --> 예약 가능 시간으로 변경 //
        Optional<Echarger> echarger = echargerRepository.findById(chargingEnded.getChargerId());
        if(echarger.isPresent()){
            Echarger echargerValue = echarger.get();
            if(echargerValue.getRsrvTimeAm() != null){
                echargerValue.setRsrvTimeAm(null);
            }else if(echargerValue.getRsrvTimePm() != null){
                echargerValue.setRsrvTimePm(null);
            }    
            echargerRepository.save(echargerValue);
        }    
        

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
