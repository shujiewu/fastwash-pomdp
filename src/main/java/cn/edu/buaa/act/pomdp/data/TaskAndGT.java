package cn.edu.buaa.act.pomdp.data;


import java.util.List;

public class TaskAndGT {
    private TaskItemEntity taskItemEntity;
    private List<Annotation> GT;
    private List<List<Annotation>> result;

    private List<Annotation> inferenceResult;

    // private List<Quality> qualities;

    private double acc;
    private double rec;
    private double meanIoU;

    private int[][] multiTask;
    private int multiTaskRight;
    private int goldTaskRight;

    private double[][] great;

    private double difficult;

    private List<Double> difficultList;

    public void setRec(double rec) {
        this.rec = rec;
    }

    public void setMeanIoU(double meanIoU) {
        this.meanIoU = meanIoU;
    }

    public void setAcc(double acc) {
        this.acc = acc;
    }

    public double getRec() {
        return rec;
    }

    public double getMeanIoU() {
        return meanIoU;
    }

    public double getAcc() {
        return acc;
    }

    public double getDifficult() {
        return difficult;
    }

    public double[][] getGreat() {
        return great;
    }

    public int getGoldTaskRight() {
        return goldTaskRight;
    }

    public int getMultiTaskRight() {
        return multiTaskRight;
    }

    public int[][] getMultiTask() {
        return multiTask;
    }

    public List<Annotation> getGT() {
        return GT;
    }

    public List<Annotation> getInferenceResult() {
        return inferenceResult;
    }

    public List<Double> getDifficultList() {
        return difficultList;
    }

    public List<List<Annotation>> getResult() {
        return result;
    }

    public TaskItemEntity getTaskItemEntity() {
        return taskItemEntity;
    }

    public void setDifficult(double difficult) {
        this.difficult = difficult;
    }

    public void setDifficultList(List<Double> difficultList) {
        this.difficultList = difficultList;
    }

    public void setGoldTaskRight(int goldTaskRight) {
        this.goldTaskRight = goldTaskRight;
    }

    public void setGreat(double[][] great) {
        this.great = great;
    }

    public void setGT(List<Annotation> GT) {
        this.GT = GT;
    }

    public void setInferenceResult(List<Annotation> inferenceResult) {
        this.inferenceResult = inferenceResult;
    }

    public void setMultiTask(int[][] multiTask) {
        this.multiTask = multiTask;
    }

    public void setMultiTaskRight(int multiTaskRight) {
        this.multiTaskRight = multiTaskRight;
    }

    public void setResult(List<List<Annotation>> result) {
        this.result = result;
    }

    public void setTaskItemEntity(TaskItemEntity taskItemEntity) {
        this.taskItemEntity = taskItemEntity;
    }
}
