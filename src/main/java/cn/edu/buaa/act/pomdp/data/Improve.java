package cn.edu.buaa.act.pomdp.data;


public class Improve {

    private String workerId;
    private String lastWorkerId;
    private Quality lastQuality;
    private Quality currentQuality;
    private double difficult;

    public void setDifficult(double difficult) {
        this.difficult = difficult;
    }

    public double getDifficult() {
        return difficult;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public Quality getCurrentQuality() {
        return currentQuality;
    }

    public Quality getLastQuality() {
        return lastQuality;
    }

    public String getLastWorkerId() {
        return lastWorkerId;
    }

    public void setCurrentQuality(Quality currentQuality) {
        this.currentQuality = currentQuality;
    }

    public void setLastQuality(Quality lastQuality) {
        this.lastQuality = lastQuality;
    }

    public void setLastWorkerId(String lastWorkerId) {
        this.lastWorkerId = lastWorkerId;
    }
}
