package echarging;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.ResourceSupport;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import echarging.external.EchargerService;

@Entity
@Table(name="Reservation")
public class Reservation extends ResourceSupport {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long reserveId;
    private Long chargerId;
    private String rsrvDate;
    private String rsrvTimeAm;
    private String rsrvTimePm;
    private Long userId;
    private String status;

    
    @PrePersist
    public void onPrePersist() throws Exception {
        this.setStatus("RESERVED");
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMdd");
        String today = DateFormat.format(new Date());        
        this.setRsrvDate(today); 

        //SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMdd");                   

        if(ReservationApplication.applicationContext.getBean(EchargerService.class)
            .chkAndRsrvTime(this.chargerId)){
                Reserved reserved = new Reserved();
                BeanUtils.copyProperties(this, reserved);                                  
                                            
                reserved.publishAfterCommit();
            }
        else{
            throw new Exception("Out of available Time Exception Raised.");
        }

    }    


    @PreUpdate
    @PostRemove
    public void onCancelled(){
        if("RESERVE_CANCELLED".equals(this.status)){
            RsrvCancelled rsrvCancelled = new RsrvCancelled();
            BeanUtils.copyProperties(this, rsrvCancelled);
            rsrvCancelled.publishAfterCommit();
        }
    }


    public Long getReserveId() {
        return reserveId;
    }

    public void setReserveId(Long reserveId) {
        this.reserveId = reserveId;
    }
    public Long getChargerId() {
        return chargerId;
    }

    public void setChargerId(Long chargerId) {
        this.chargerId = chargerId;
    }
    public String getRsrvDate() {
        return rsrvDate;
    }

    public void setRsrvDate(String rsrvDate) {
        this.rsrvDate = rsrvDate;
    }
    public String getRsrvTimeAm() {
        return rsrvTimeAm;
    }

    public void setRsrvTimeAm(String rsrvTimeAm) {
        this.rsrvTimeAm = rsrvTimeAm;
    }
    public String getRsrvTimePm() {
        return rsrvTimePm;
    }

    public void setRsrvTimePm(String rsrvTimePm) {
        this.rsrvTimePm = rsrvTimePm;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }




}
