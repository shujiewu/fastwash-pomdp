package cn.edu.buaa.act.pomdp.data;


import java.util.List;

public class TrainingItem {
    private String id;

    private String dataSetName;
    private String imageId;
    private List<Tag> tagList;
    //private Date lastUpdatedTime;
    //groundTruth or crowdAggregation
    private String type;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public String getImageId() {
        return imageId;
    }

    public String getType() {
        return type;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public void setType(String type) {
        this.type = type;
    }
}
