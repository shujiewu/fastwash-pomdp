package cn.edu.buaa.act.pomdp.data;

public class Quality {
    private double acc;
    private double rec;
    private double fscore;
    private double meanIoU;
    private double quality;
    public Quality(){}
    public Quality(double acc, double rec, double meanIoU){
        this.acc = acc;
        this.rec = rec;
        this.fscore = 2.0/((1.0/acc)+(1.0/(rec)));
        this.meanIoU = meanIoU;
        this.quality = (this.fscore+this.meanIoU)/2.0;
    }

    public double getAcc() {
        return acc;
    }

    public double getFscore() {
        return fscore;
    }

    public double getMeanIoU() {
        return meanIoU;
    }

    public double getQuality() {
        return quality;
    }

    public double getRec() {
        return rec;
    }

    public void setAcc(double acc) {
        this.acc = acc;
    }

    public void setFscore(double fscore) {
        this.fscore = fscore;
    }

    public void setMeanIoU(double meanIoU) {
        this.meanIoU = meanIoU;
    }

    public void setQuality(double quality) {
        this.quality = quality;
    }

    public void setRec(double rec) {
        this.rec = rec;
    }
}
