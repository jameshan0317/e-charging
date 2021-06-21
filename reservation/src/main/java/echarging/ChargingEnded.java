package echarging;

public class ChargingEnded extends AbstractEvent {

    private Long chargingId;
    private Long reserveId ;
    private Long chargerId;
    private String cgStartTime;
    private String cgEndTime;
    private Long amount;
    private Long userId;
    private String status;

    public Long getChargingId() {
        return chargingId;
    }

    public void setChargingId(Long chargingId) {
        this.chargingId = chargingId;
    }
    public Long getReserveId() {
        return reserveId ;
    }

    public void setReserveId(Long reserveId ) {
        this.reserveId  = reserveId ;
    }
    public Long getChargerId() {
        return chargerId;
    }

    public void setChargerId(Long chargerId) {
        this.chargerId = chargerId;
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