
package echarging.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@FeignClient(name="echarger", url="http://localhost:8081")
public interface EchargerService {

    @RequestMapping(method= RequestMethod.GET, path="/echargers/chkAndRsrvTime")
    public boolean chkAndRsrvTime(@RequestParam Long chargerId);

}