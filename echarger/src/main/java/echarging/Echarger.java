package echarging;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Echarger")
public class Echarger {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long chargerId;
    private String cgName;
    private String rsrvDate;
    private String rsrvTimeAm;
    private String rsrvTimePm;

    @PostPersist
    public void onPostPersist(){
        Registered registered = new Registered();
        BeanUtils.copyProperties(this, registered);
        registered.publishAfterCommit();
    }

    @PreUpdate
    public void onPreUpdate(){
        RsrvTimeModified rsrvTimeModified = new RsrvTimeModified();
        BeanUtils.copyProperties(this, rsrvTimeModified);
        rsrvTimeModified.publishAfterCommit();


    }


    public Long getChargerId() {
        return chargerId;
    }

    public void setChargerId(Long chargerId) {
        this.chargerId = chargerId;
    }
    public String getCgName() {
        return cgName;
    }

    public void setCgName(String cgName) {
        this.cgName = cgName;
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

    /**
     * 예약 가능 여부를 확인
     * 해당일 오전/오후 시간대가 null 인 경우는 예약 가능
     * @return true : 예약 가능, false : 예약 불가능
     */
    public boolean canRsrv(){        
            return rsrvTimeAm == null || rsrvTimePm == null;             
    }


}
