package cn.edu.buaa.act.pomdp.data;

public class Box {
    private double x;
    private double y;
    private double w;
    private double h;
    private double score;
    public Box(){}
    public Box(double x, double y, double w, double h, double score){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.score = score;
    }

    public double getH() {
        return h;
    }

    public double getScore() {
        return score;
    }

    public double getW() {
        return w;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setH(double h) {
        this.h = h;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setW(double w) {
        this.w = w;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
