package cn.edu.buaa.act.pomdp.data;

import com.alibaba.fastjson.JSONObject;

public class Tag {
    private Classification classification;
    private JSONObject property;
    private Box box;

    public void setProperty(JSONObject property) {
        this.property = property;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public void setBox(Box box) {
        this.box = box;
    }

    public JSONObject getProperty() {
        return property;
    }

    public Classification getClassification() {
        return classification;
    }

    public Box getBox() {
        return box;
    }
}
