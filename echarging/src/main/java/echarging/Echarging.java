package echarging;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name="Echarging")
public class Echarging {

    @Transient
    Logger logger = LoggerFactory.getLogger(Echarging.class);

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long chargingId;
    private Long reserveId;
    private Long chargerId;
    private String rsrvDate;
    private String cgStartTime;
    private String cgEndTime;
    private Long amount;
    private Long userId;
    private String status;

    @PreUpdate
    public void onPreUpdate(){

        logger.info("Echarging on PreUpdate Executed");
        
        SimpleDateFormat DateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");        
              

        // 충전 시작 처리 //
        ChargingStarted chargingStarted = new ChargingStarted();
        BeanUtils.copyProperties(this, chargingStarted);

        if("CHARGING_READY".equals(this.status)){
            
            this.setCgStartTime(DateTimeFormat.format(new Date()));
            this.setStatus("CHARGING_STARTED");            
            
            chargingStarted.publishAfterCommit();

        // 충전 종료 처리 //
        }else if("CHARGING_STARTED".equals(this.status)){
            this.setCgEndTime(DateTimeFormat.format(new Date()));
            this.setStatus("CHARGING_ENDED");        
                    
            ChargingEnded chargingEnded = new ChargingEnded();
            BeanUtils.copyProperties(this, chargingEnded);
            chargingEnded.publishAfterCommit();
        }
        

    }    
    


    public Long getChargingId() {
        return chargingId;
    }

    public void setChargingId(Long chargingId) {
        this.chargingId = chargingId;
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
    public String getCgStartTime() {
        return cgStartTime;
    }

    public void setCgStartTime(String cgStartTime) {
        this.cgStartTime = cgStartTime;
    }
    public String getCgEndTime() {
        return cgEndTime;
    }

    public void setCgEndTime(String cgEndTime) {
        this.cgEndTime = cgEndTime;
    }
    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
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
