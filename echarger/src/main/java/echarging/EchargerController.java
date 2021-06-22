package echarging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
public class EchargerController {

    @Autowired
    EchargerRepository echargerRepository;

    @RequestMapping(value = "/echargers/chkAndRsrvTime",
        method = RequestMethod.GET,
        produces = "application/json;charset=UTF-8")

    public boolean chkAndRsrvTime(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("##### /echargers/chkAndRsrvTime  called #####");

        boolean status = false;

        Long echargerId = Long.valueOf(request.getParameter("chargerId"));

        Optional<Echarger> echarger = echargerRepository.findById(echargerId);
        if(echarger.isPresent()) {
            Echarger echargerValue = echarger.get();

            //Hystrix Timeout 점검
            if(echargerValue.getChargerId() == 2) {
                System.out.println("### Hystrix 테스트를 위한 강제 sleep 5초 ###");
                Thread.sleep(5000);
            }
            //예약 가능한지 체크
            if(echargerValue.getRsrvTimeAm() == null || echargerValue.getRsrvTimePm() == null) {
                status = true;

                //예약 가능하면 예약할 시간대 선택/저장
                if(echargerValue.getRsrvTimeAm() == null){
                        echargerValue.setRsrvTimeAm("Y");
                }else if(echargerValue.getRsrvTimePm() == null){
                        echargerValue.setRsrvTimePm("Y");
                }    

                echargerRepository.save(echargerValue);
            }
        }

        return status;
    }
}