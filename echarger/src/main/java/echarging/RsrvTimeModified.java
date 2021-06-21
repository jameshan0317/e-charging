package echarging;

public class RsrvTimeModified extends AbstractEvent {

    private Long chargerId;
    private String cgName;
    private String rsrvDate;
    private String rsrvTimeAm;
    private String rsrvTimePm;

    public RsrvTimeModified(){
        super();
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
}
