package echarging;

public class RsrvCancelled extends AbstractEvent {

    private Long reserveId;
    private Long chargerId;
    private String rsrvDate;
    private String rsrvTimeAm;
    private String rsrvTimePm;
    private Long userId;
    private String status;

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