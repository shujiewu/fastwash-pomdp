package cn.edu.buaa.act.pomdp.util;

import cn.edu.buaa.act.pomdp.data.*;
import com.alibaba.fastjson.JSON;

import java.io.*;
import java.util.*;


public class DataUtil {

    public List<TrainingItem> readGTFile() throws Exception{
        FileInputStream fis=new FileInputStream("D:/gt7492.json");
        InputStreamReader isr=new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line="";
        List<TrainingItem> trainingItems = new ArrayList<>();
        while ((line=br.readLine())!=null) {
            TrainingItem trainingItem = JSON.parseObject(line,TrainingItem.class);
            trainingItems.add(trainingItem);
        }
        br.close();
        isr.close();
        fis.close();
        return trainingItems;
    }

    public List<TaskItemEntity> readDataFile() throws Exception{
        FileInputStream fis=new FileInputStream("D:/data7492.json");
        InputStreamReader isr=new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line="";
        List<TaskItemEntity> taskItemEntityList = new ArrayList<>();
        while ((line=br.readLine())!=null) {
            TaskItemEntity taskItemEntity = JSON.parseObject(line,TaskItemEntity.class);
            taskItemEntityList.add(taskItemEntity);
        }
        br.close();
        isr.close();
        fis.close();
        return taskItemEntityList;
    }


    public List<TaskAndGT> getTaskAndGt() throws Exception{
        Set<String> imageId= new HashSet<>();
        List<TaskItemEntity> taskItemEntities = readDataFile();
        List<TrainingItem> trainingItems = readGTFile();
        Map<String,List<Tag>> groundTruthMap = new HashMap<>();
        //将正确答案放入groundTruth
        trainingItems.forEach(taskItemEntity -> {
            groundTruthMap.put(taskItemEntity.getImageId(),taskItemEntity.getTagList());
        });
        List<TaskAndGT> taskAndGTS = new ArrayList<>();

        taskItemEntities.forEach(taskItemEntity -> {
            List<Annotation> annotations = new ArrayList<>();
            groundTruthMap.get(taskItemEntity.getImageId()).forEach(tag -> {
                if(tag.getClassification().getId().equals(taskItemEntity.getClassId())){
                    Annotation annotation = new Annotation();
                    annotation.setBox(tag.getBox());
                    annotation.setClassification(tag.getClassification());
                    annotations.add(annotation);
                }
            });
            TaskAndGT taskAndGT = new TaskAndGT();
            taskAndGT.setGT(annotations);
            taskAndGT.setTaskItemEntity(taskItemEntity);
            if(taskItemEntity.getIterations()>=1){
                taskAndGTS.add(taskAndGT);
            }
        });
        return taskAndGTS;
    }

    public void writeMultiLabel(List<MultiLabel> labels){
        try {
            File csv = new File("D:/fastwashdata/writers.csv"); // CSV数据文件
            BufferedWriter bw = new BufferedWriter(new FileWriter(csv, false)); // 附加
            bw.write("question" + "," + "worker" + "," + "answer");
            bw.newLine();
            labels.forEach(multiLabel -> {
                try {
                    bw.write(multiLabel.getJ() + "," + multiLabel.getI() + "," + multiLabel.getLij());
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeDataToFile(String content,String fileName) throws Exception{
        FileOutputStream fos=new FileOutputStream(new File(fileName));
        OutputStreamWriter osw=new OutputStreamWriter(fos, "UTF-8");
        BufferedWriter bw=new BufferedWriter(osw);
        bw.write(content);
        bw.close();
        osw.close();
        fos.close();
    }
}
