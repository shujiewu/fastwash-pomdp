package cn.edu.buaa.act.pomdp.data;


public class Classification {
    private String id;
    private String value;
    private String fillColor;
    private String strokeColor;
    public Classification(){}
    public Classification(String id, String value){
        this.id = id;
        this.value = value;
    }

    public String getFillColor() {
        return fillColor;
    }

    public String getId() {
        return id;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public String getValue() {
        return value;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setValue(String value) {
        this.value = value;
    }
}