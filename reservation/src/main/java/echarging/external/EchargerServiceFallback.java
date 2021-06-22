
package echarging.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class EchargerServiceFallback implements EchargerService {
    @Override
    public boolean chkAndRsrvTime(@RequestParam Long chargerId) {
        System.out.println("Circuit breaker has been opened. Fallback returned instead.");
        return false;
    }  

}

