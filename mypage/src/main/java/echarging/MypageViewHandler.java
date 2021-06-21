package echarging;

import echarging.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MypageViewHandler {


    @Autowired
    private MypageRepository mypageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenReserved_then_CREATE_1 (@Payload Reserved reserved) {
        try {

            if (!reserved.validate()) return;

            // view 객체 생성
            Mypage mypage = new Mypage();
            // view 객체에 이벤트의 Value 를 set 함
            mypage.setReserveId(reserved.getReserveId());
            mypage.setChargerId(reserved.getChargerId());
            mypage.setRsrvDate(reserved.getRsrvDate());
            mypage.setRsrvTimeAm(reserved.getRsrvTimeAm());
            mypage.setRsrvTimePm(reserved.getRsrvTimePm());
            mypage.setUserId(reserved.getUserId());
            mypage.setStatus(reserved.getStatus());
            // view 레파지 토리에 save
            mypageRepository.save(mypage);
        
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenRsrvCancelled_then_UPDATE_1(@Payload RsrvCancelled rsrvCancelled) {
        try {
            if (!rsrvCancelled.validate()) return;
                // view 객체 조회
            Optional<Mypage> mypageOptional = mypageRepository.findByReserveId(rsrvCancelled.getReserveId());
            if( mypageOptional.isPresent()) {
                Mypage mypage = mypageOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                    mypage.setStatus(rsrvCancelled.getStatus());
                // view 레파지 토리에 save
                mypageRepository.save(mypage);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenChargingStarted_then_UPDATE_2(@Payload ChargingStarted chargingStarted) {
        try {
            if (!chargingStarted.validate()) return;
                // view 객체 조회
            Optional<Mypage> mypageOptional = mypageRepository.findByReserveId(chargingStarted.getReserveId());
            if( mypageOptional.isPresent()) {
                Mypage mypage = mypageOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                    mypage.setStatus(chargingStarted.getStatus());
                // view 레파지 토리에 save
                mypageRepository.save(mypage);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenChargingEnded_then_UPDATE_3(@Payload ChargingEnded chargingEnded) {
        try {
            if (!chargingEnded.validate()) return;
                // view 객체 조회
            Optional<Mypage> mypageOptional = mypageRepository.findByReserveId(chargingEnded.getReserveId());
            if( mypageOptional.isPresent()) {
                Mypage mypage = mypageOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                    mypage.setStatus(chargingEnded.getStatus());
                // view 레파지 토리에 save
                mypageRepository.save(mypage);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}