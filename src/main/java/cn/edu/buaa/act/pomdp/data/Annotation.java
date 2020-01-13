package cn.edu.buaa.act.pomdp.data;

import com.alibaba.fastjson.JSONObject;
import java.util.Date;

public class Annotation {
    private String id;
    private String workerId;
    private String modelId;

    private String type;
    private Box box;
    private String status;
    private Classification classification;
    private JSONObject property;
    private Date lastUpdatedTime;
    private String lastAnnotationId;

    private int fold;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Box getBox() {
        return box;
    }

    public Classification getClassification() {
        return classification;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public int getFold() {
        return fold;
    }

    public JSONObject getProperty() {
        return property;
    }

    public String getLastAnnotationId() {
        return lastAnnotationId;
    }

    public String getModelId() {
        return modelId;
    }

    public String getStatus() {
        return status;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setBox(Box box) {
        this.box = box;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public void setFold(int fold) {
        this.fold = fold;
    }

    public void setLastAnnotationId(String lastAnnotationId) {
        this.lastAnnotationId = lastAnnotationId;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public void setProperty(JSONObject property) {
        this.property = property;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }
}
