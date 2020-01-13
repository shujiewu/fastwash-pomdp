package cn.edu.buaa.act.pomdp.data;

import java.util.List;
import java.util.Map;

public class TaskItemEntity {
    private String id;
    private String dataSetName;
    private String imageId;
    private String fileName;

    // Maybe unsure
    private String classId;

    private List<String> workerList;
    private List<String> updateTime;

    // Some don't have createTime
    private List<String> createTime;
    private int iterations;

    private String status;

    //timeToAnnList
    private Map<String,List<Annotation>> annotations;
    private String lastUpdateTime;

    //timeToQuality
    private List<Quality> quality;

    private List<Boolean> change;

    private int maxWorkerPerTask;

    public void setStatus(String status) {
        this.status = status;
    }

    public void setQuality(List<Quality> quality) {
        this.quality = quality;
    }

    public String getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public String getImageId() {
        return imageId;
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public int getIterations() {
        return iterations;
    }

    public int getMaxWorkerPerTask() {
        return maxWorkerPerTask;
    }

    public List<Boolean> getChange() {
        return change;
    }

    public List<Quality> getQuality() {
        return quality;
    }

    public List<String> getCreateTime() {
        return createTime;
    }

    public List<String> getUpdateTime() {
        return updateTime;
    }

    public List<String> getWorkerList() {
        return workerList;
    }

    public Map<String, List<Annotation>> getAnnotations() {
        return annotations;
    }

    public String getClassId() {
        return classId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setAnnotations(Map<String, List<Annotation>> annotations) {
        this.annotations = annotations;
    }

    public void setChange(List<Boolean> change) {
        this.change = change;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setCreateTime(List<String> createTime) {
        this.createTime = createTime;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void setMaxWorkerPerTask(int maxWorkerPerTask) {
        this.maxWorkerPerTask = maxWorkerPerTask;
    }

    public void setUpdateTime(List<String> updateTime) {
        this.updateTime = updateTime;
    }

    public void setWorkerList(List<String> workerList) {
        this.workerList = workerList;
    }
}
