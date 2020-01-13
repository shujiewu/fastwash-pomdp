package cn.edu.buaa.act.pomdp;


import cn.edu.buaa.act.pomdp.data.*;
import cn.edu.buaa.act.pomdp.util.DataUtil;
import cn.edu.buaa.act.pomdp.util.IoU;
import cn.edu.buaa.act.pomdp.util.ReadJsonFile;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.*;


public class EstimateService {
    private static DataUtil dataUtil;
    public static void main(String[] args) throws Exception {
        dataUtil = new DataUtil();
        List<TaskAndGT> taskAndGTS = dataUtil.getTaskAndGt();
        makeMultiLabel(taskAndGTS);
        computeTaskDifficult(taskAndGTS);
        computeRECAndACC(taskAndGTS);
        makeTask(taskAndGTS);
//        makeTask(taskAndGTS);
        // makeMutiLabelFile(taskAndGTS,UUID.randomUUID().toString());
//        String command = "python D:/fastwashdata/method_no_agg.py "+taskId;
//        try {
//            exeCmd(command);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        // computeDifficult(taskAndGTS,taskId);
        // JSONArray jsonArray = makeTask(taskAndGTS);
    }
    private static void exeCmd(String commandStr) {
        BufferedReader br = null;
        try {
            System.out.println("exec python");
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            System.out.println(sb.toString());
            System.out.println("exec python complete");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static double MAX_OVERLAP = 0.5;
    private static void makeMultiLabel(List<TaskAndGT> taskAndGTS){
        int count = 0;
        for(TaskAndGT taskAndGT:taskAndGTS){
            List<List<Annotation>> annotations = new ArrayList<>();
            taskAndGT.getTaskItemEntity().getUpdateTime().forEach(time->{
                annotations.add(taskAndGT.getTaskItemEntity().getAnnotations().get(time));
            });
            // 增加了gt
            annotations.add(taskAndGT.getGT());

            taskAndGT.setResult(annotations);
            for (Iterator<Annotation> iterator= annotations.get(0).iterator();iterator.hasNext();) {
                if(iterator.next().getBox().getScore()<0.5)
                    iterator.remove();
            }
            long allBoxSize = annotations.stream().flatMap(annotation->annotation.stream()).count();
            int[][] matrix = new int[annotations.size()][(int)allBoxSize];

            int right = annotations.get(0).size();
            int gtRight = annotations.get(0).size();
            for(int i = 0;i<annotations.size();i++){
                if(annotations.get(i).size()==0){
                    //默认为0
                }
                for(int j = 0;j<annotations.get(i).size();j++){
                    if(i==0){
                        matrix[i][j]=1;
                        annotations.get(0).get(j).setFold(j);
                    }else if(i<annotations.size()-1){
                        Annotation annotation = annotations.get(i).get(j);
                        for(int m = i-1;m>=0;m--){
                            double[] iou = new double[annotations.get(m).size()];
                            for(int k=0;k<annotations.get(m).size();k++){
                                iou[k]= IoU.box_iou(annotation.getBox(), annotations.get(m).get(k).getBox());
                            }
                            // 如果这一行有值
                            if(annotations.get(m).size()>0){
                                double max = Arrays.stream(iou).max().getAsDouble();
                                if(max>MAX_OVERLAP){
                                    for(int k=0;k<annotations.get(m).size();k++){
                                        if(Math.abs(max-iou[k])<0.000001){
                                            // 设置位置
                                            matrix[i][annotations.get(m).get(k).getFold()]=1;
                                            annotation.setFold(annotations.get(m).get(k).getFold());
                                        }
                                    }
                                    break;
                                }else{
                                    // 如果已经找到了最上面一行，还没找到，那么就设置为新的box
                                    if(m==0){
                                        matrix[i][right]=1;
                                        annotation.setFold(right);
                                        right++;
                                    }
                                }
                            }else {
                                // 如果第一行都没有，那么就设置为新的box
                                if(m==0){
                                    matrix[i][right]=1;
                                    annotation.setFold(right);
                                    right++;
                                }
                            }
                        }
                        // 不断更新gtRight
                        gtRight = right;
                    }else{
                        Annotation annotation = annotations.get(i).get(j);
                        for(int m = i-1;m>=0;m--){
                            double[] iou = new double[annotations.get(m).size()];
                            for(int k=0;k<annotations.get(m).size();k++){
                                iou[k]= IoU.box_iou(annotation.getBox(), annotations.get(m).get(k).getBox());
                            }
                            // 如果这一行有值
                            if(annotations.get(m).size()>0){
                                double max = Arrays.stream(iou).max().getAsDouble();
                                if(max>MAX_OVERLAP){
                                    for(int k=0;k<annotations.get(m).size();k++){
                                        if(Math.abs(max-iou[k])<0.000001){
                                            // 设置位置
                                            matrix[i][annotations.get(m).get(k).getFold()]=1;
                                            annotation.setFold(annotations.get(m).get(k).getFold());
                                        }
                                    }
                                    break;
                                }else{
                                    // 如果已经找到了最上面一行，还没找到，那么就设置为新的box
                                    if(m==0){
                                        matrix[i][gtRight]=1;
                                        annotation.setFold(gtRight);
                                        gtRight++;
                                    }
                                }
                            }else {
                                // 如果第一行都没有，那么就设置为新的box
                                if(m==0){
                                    matrix[i][gtRight]=1;
                                    annotation.setFold(gtRight);
                                    gtRight++;
                                }
                            }
                        }
                    }
                }
            }
            taskAndGT.setMultiTaskRight(right);
            taskAndGT.setMultiTask(matrix);
            taskAndGT.setGoldTaskRight(gtRight);
            count++;
        }
        System.out.println("count" + count);
    }
    public static void makeMutiLabelFile(List<TaskAndGT> taskAndGTS,String taskId){
        List<MultiLabel> labels = new ArrayList<MultiLabel>();
        Map<String, Integer> workers = new HashMap<String, Integer>();
        Map<String, Integer> items = new HashMap<String, Integer>();
        Map<String, Integer> classes = new HashMap<String, Integer>();
        Map<Integer, Integer> goldLabels = new HashMap<Integer, Integer>();
        classes.put("0",0);
        classes.put("1",1);
        int itemId = 0;
        for(TaskAndGT taskAndGT:taskAndGTS) {
            int[][] matrix = taskAndGT.getMultiTask();
            for (int i = 0; i < taskAndGT.getMultiTaskRight(); i++) {
                itemId++;
                for (int j = 0; j < matrix.length - 1; j++) {
                    if (taskAndGT.getTaskItemEntity().getWorkerList().get(j).equals("baseModel")) {
                        // model1 or model2
                        if(Long.parseLong(taskAndGT.getTaskItemEntity().getUpdateTime().get(0))<1572003600000L){
                            taskAndGT.getTaskItemEntity().getWorkerList().set(j, "10" + taskAndGT.getTaskItemEntity().getClassId());
                        }else{
                            taskAndGT.getTaskItemEntity().getWorkerList().set(j, "20" + taskAndGT.getTaskItemEntity().getClassId());
                        }
                    }
                    String workerId = taskAndGT.getTaskItemEntity().getWorkerList().get(j);
                    String cls = matrix[j][i] == 0 ? String.valueOf(0) : String.valueOf(1);

                    if (!workers.containsKey(workerId))
                        workers.put(workerId, Integer.parseInt(workerId));

                    if (!items.containsKey(String.valueOf(itemId)))
                        items.put(String.valueOf(itemId), items.size());

                    labels.add(new MultiLabel(workers.getOrDefault(workerId, -1),
                            items.getOrDefault(String.valueOf(itemId), -1),
                            classes.getOrDefault(cls, -1)));
                }
                goldLabels.put(items.getOrDefault(String.valueOf(itemId), -1), matrix[matrix.length - 1][i]);
            }
        }
        writeMultiLabel(labels,taskId);
        writeGoldLabel(goldLabels,taskId);
    }

    public static void writeMultiLabel(List<MultiLabel> labels,String taskId){
        try {
            File csv = new File("D:/fastwashdata/writers_"+taskId+".csv"); // CSV数据文件
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
    public static void writeGoldLabel(Map<Integer, Integer> goldLabels,String taskId){
        try {
            File csv = new File("D:/fastwashdata/gt_"+taskId+".csv"); // CSV数据文件
            BufferedWriter bw = new BufferedWriter(new FileWriter(csv, false)); // 附加
            bw.write("question" + "," + "answer");
            bw.newLine();
            goldLabels.forEach((question,answer)-> {
                try {
                    bw.write(question + "," + answer);
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
    private static void computeTaskDifficult(List<TaskAndGT> taskAndGTS) {
        JSONObject jsonObject = ReadJsonFile.ReadFile("D:/fastwashdata/weight_file_zc_compare3.json");
        int pos = 0;
        for(TaskAndGT taskAndGT:taskAndGTS){
            int[][] matrix = taskAndGT.getMultiTask();
            List<Annotation> annotations = new ArrayList<>();
            // boolean add =true;
            //去掉gt
            int iterSize = taskAndGT.getMultiTask().length-2;
//            double[][] greatProb = new double[iterSize][2];
//            double last = 0.0;
            int lastRight = 0;
            for(int it = 0;it<=iterSize;it++){
                // 代表第几个子任务
                int start = pos;
                int right = taskAndGT.getMultiTaskRight();
                for(int x = taskAndGT.getMultiTaskRight()-1;x>=lastRight;x--){
                    if(taskAndGT.getMultiTask()[it][x]==0){
                        right--;
                    }else{
                        break;
                    }
                }
                lastRight = right;
                double acc = 1.0;
                //double second = 1.0;
                //迭代寻找下一个任务
                for(int j = 0;j<right;j++){
                    JSONArray iter = jsonObject.getJSONArray(String.valueOf(start));
                    // pos位置的第迭代行
                    JSONObject res = iter.getJSONObject(it);
                    //如果工人回答0
                    if(taskAndGT.getMultiTask()[it][j]==0){
                        acc = acc* Double.parseDouble(res.get("0").toString());
                    }else{
                        acc = acc* Double.parseDouble(res.get("1").toString());
                    }
                    start++;
                }
                if(taskAndGT.getDifficultList() ==null){
                    List<Double> difficult = new ArrayList<>();
                    taskAndGT.setDifficultList(difficult);
                }
                if(acc==1.0){
                     System.out.println("right"+right);
                     System.out.println("it"+it);
                    // System.out.println(taskAndGT.getTaskItemEntity().getId());
                    taskAndGT.getDifficultList().add(1.0);
                }else{
                    // System.out.println(acc);
                    taskAndGT.getDifficultList().add(1-acc);
                }
            }
            pos = pos + taskAndGT.getMultiTaskRight();
        }
    }

    private static void computeRECAndACC(List<TaskAndGT> taskAndGTS){
        int count =0 ;
        double accCount =0.0;
        double recCount = 0.0;
        double IoUCount = 0.0;
        Map<String,List<Improve>> improveMap = new HashMap<>();
        Quality lastQuality = new Quality(0,0,0);
        String lastWorker = null;
        for(TaskAndGT taskAndGT:taskAndGTS) {
            TaskItemEntity taskItemEntity = taskAndGT.getTaskItemEntity();
            List<Annotation> gt = taskAndGT.getGT();
            int pos = 0;
            double difficult = 1.0;
            for(String timeStamp: taskItemEntity.getUpdateTime()){
                List<Annotation> result = taskItemEntity.getAnnotations().get(timeStamp);
                if(gt.size()>0&&result.size()==0){
                    // 防止开始没有标注
                    taskAndGT.setAcc(0);
                    taskAndGT.setRec(0);
                    taskAndGT.setMeanIoU(0);
                    accCount+=taskAndGT.getAcc();
                    recCount+=taskAndGT.getRec();
                    IoUCount+=taskAndGT.getMeanIoU();

                    Quality quality = new Quality(0,0,0);
                    // 每个时间点的质量
                    if(taskItemEntity.getQuality()==null){
                        taskItemEntity.setQuality(new ArrayList<>());
                    }
                    taskItemEntity.getQuality().add(quality);
                    if(pos == 0){
                        difficult = 1.0;
                    }
                    if (taskAndGT.getTaskItemEntity().getWorkerList().get(pos).equals("baseModel")) {
                        if(Long.parseLong(taskAndGT.getTaskItemEntity().getUpdateTime().get(0))<1572003600000L){
                            lastWorker = "10" + taskAndGT.getTaskItemEntity().getClassId();
                        }else{
                            lastWorker = "20" + taskAndGT.getTaskItemEntity().getClassId();
                        }
                    }else{
                        lastWorker = taskAndGT.getTaskItemEntity().getWorkerList().get(pos);
                    }
                    pos++;
                    continue;
                }
                double[][] matrix = new double[gt.size()][result.size()];
                for (int i = 0; i < gt.size(); i++) {
                    for (int j = 0; j < result.size(); j++) {
                        matrix[i][j] = IoU.box_iou(gt.get(i).getBox(), result.get(j).getBox());
                    }
                }

                int[] gtFlag = new int[gt.size()];
                double[] resFlag = new double[result.size()];
                int tp = 0;
                double iou = 0.0;
                for (int m = 0; m < result.size(); m++) {
                    // 找到IoU最大position
                    double max = Arrays.stream(matrix)
                            .flatMapToDouble(a -> Arrays.stream(a))
                            .max().getAsDouble();
                    for (int i = 0; i < gt.size(); i++) {
                        for (int j = 0; j < result.size(); j++) {
                            if (Math.abs(matrix[i][j] - (max)) < 0.000001) {
                                if (gtFlag[i] == 0 && matrix[i][j] > 0.5) {
                                    gtFlag[i] = 1;
                                    resFlag[j] = matrix[i][j];// 找到了
                                } else {
                                    if (gtFlag[i] == 1) {
                                        resFlag[j] = -1.0;// 被别的占用
                                    } else {
                                        resFlag[j] = -2.0;// iou太小了                                }
                                    }
                                }
                                for (int k = 0; k < gt.size(); k++) {
                                    // 不再分配给其他box
                                    matrix[k][j] = -1.0;
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < resFlag.length; i++) {
                    if (resFlag[i] > 0) {
                        tp++;
                        iou+=resFlag[i];
                    }
                    if(resFlag[i]==-1.0){
                        // System.out.println("111");
                    }
                    if(resFlag[i]==-2.0){
                        // System.out.println("222");
                    }
                }
                double meanIoU = tp > 0 ?(iou/(double) tp):0;
                double acc = result.size() > 0 ? ((double) tp / (double)result.size()) : 0;
                double rec = (double)tp / (double)gt.size();


                Quality quality = new Quality(acc,rec,meanIoU);
                // 每个时间点的质量
                if(taskItemEntity.getQuality()==null){
                    taskItemEntity.setQuality(new ArrayList<>());
                }
                taskItemEntity.getQuality().add(quality);
//                if(quality.getQuality()==0.0){
//                    System.out.println(meanIoU);
//                    System.out.println(acc);
//                    System.out.println(rec);
//                    System.out.println(taskItemEntity.getId()+"全部错误"+pos);
//                    // System.out.println(result.size());
//                    for(int i = 0;i<taskAndGT.getMultiTask().length;i++){
//                        for(int j = 0;j<taskAndGT.getGoldTaskRight();j++){
//                            System.out.print(taskAndGT.getMultiTask()[i][j]+" ");
//                        }
//                        System.out.println();
//                    }
//                    System.out.println();
//                }

                if(pos == 0){
                    double score = 1.0;
                    boolean find = false;
                    for (int j = 0; j < result.size(); j++) {
                        if(result.get(j).getBox().getScore()>=0.5){
                            score = score *result.get(j).getBox().getScore();
                            find = true;
                        }
                    }
                    if(find){
                        difficult = 1 - score;
                    }else{
                        difficult = 1.0;
//                        System.out.println(result.get(0).getModelId());
//                        System.out.println(result.get(0).getBox().getScore());
//                        System.out.println(difficult);
//                        System.out.println(quality.getQuality());
                    }
                }

                if(pos>0){
                    Improve improve = new Improve();
                    improve.setCurrentQuality(quality);
                    improve.setLastQuality(lastQuality);
                    String workerId = taskAndGT.getTaskItemEntity().getWorkerList().get(pos);
                    improve.setWorkerId(workerId);
                    improve.setLastWorkerId(lastWorker);
                    // 上一个人做完后的难度更新
                    improve.setDifficult(taskAndGT.getDifficultList().get(pos-1));
                    if(taskAndGT.getDifficultList().get(pos-1)==1.0){
                        System.out.println(lastWorker);
                    }
                    if(!improveMap.containsKey(taskAndGT.getTaskItemEntity().getWorkerList().get(pos))){
                        improveMap.put(workerId,new ArrayList<>());
                    }
                    improveMap.get(workerId).add(improve);
                }
                lastQuality = new Quality(quality.getAcc(),quality.getRec(),quality.getMeanIoU());
                if (taskAndGT.getTaskItemEntity().getWorkerList().get(pos).equals("baseModel")) {
                    if(Long.parseLong(taskAndGT.getTaskItemEntity().getUpdateTime().get(0))<1572003600000L){
                        lastWorker = "10" + taskAndGT.getTaskItemEntity().getClassId();
                    }else{
                        lastWorker = "20" + taskAndGT.getTaskItemEntity().getClassId();
                    }
                    // lastWorker = "10" + taskAndGT.getTaskItemEntity().getClassId();
                }else{
                    lastWorker = taskAndGT.getTaskItemEntity().getWorkerList().get(pos);
                }

                if(timeStamp.equals(taskItemEntity.getLastUpdateTime())){
                    taskAndGT.setAcc(acc);
                    taskAndGT.setRec(rec);
                    taskAndGT.setMeanIoU(meanIoU);
                }

                pos++;
            }
            taskAndGT.setDifficult(difficult);
            accCount+=taskAndGT.getAcc();
            recCount+=taskAndGT.getRec();
            IoUCount+=taskAndGT.getMeanIoU();
        }
        JSONObject jsonObject = new JSONObject();
        improveMap.forEach((worker,improve)->{
            // System.out.println("worker="+worker);
            // System.out.println("size="+improve.size());
            jsonObject.put(worker,improve);
        });

        try {
            dataUtil.writeDataToFile(jsonObject.toJSONString(),"D:/fastwashdata/improve4.json");
        } catch (Exception e) {
            e.printStackTrace();
        }


//        System.out.println(jsonObject.toJSONString());
//        System.out.println("ACC=" + accCount/taskAndGTS.size());
//        System.out.println("REC=" +recCount/taskAndGTS.size());
//        System.out.println("IOU=" +IoUCount/taskAndGTS.size());
//        System.out.println("FSCORE=" + 2.0/((1.0/(accCount/taskAndGTS.size()))+(1.0/(recCount/taskAndGTS.size()))));
    }

    private static void makeTask(List<TaskAndGT> taskAndGTS){
        JSONArray jsonArray = new JSONArray();
        for(TaskAndGT taskAndGT:taskAndGTS) {
            JSONObject jsonObject = new JSONObject();
            JSONArray workerList = new JSONArray();
            int pos = 0;
            for(String worker: taskAndGT.getTaskItemEntity().getWorkerList()){
                JSONObject w = new JSONObject();

                if (worker.equals("baseModel")) {
                    if(Long.parseLong(taskAndGT.getTaskItemEntity().getUpdateTime().get(0))<1572003600000L){
                        w.put("workerId","10" + taskAndGT.getTaskItemEntity().getClassId());
                    }else{
                        w.put("workerId","20" + taskAndGT.getTaskItemEntity().getClassId());
                    }
                    //w.put("workerId","10" + taskAndGT.getTaskItemEntity().getClassId());
                }else{
                    w.put("workerId", worker);
                }
                w.put("quality",taskAndGT.getTaskItemEntity().getQuality().get(pos).getQuality());
                if (worker.equals("baseModel")){
                    w.put("change",false);
                }else{
                    if(Math.abs(taskAndGT.getTaskItemEntity().getQuality().get(pos).getQuality()-taskAndGT.getTaskItemEntity().getQuality().get(pos-1).getQuality())<0.000000000001){
                        w.put("change",false);
                    }
                    else{
                        w.put("change",true);
                    }
                }
                // 难度给下一个人
                w.put("difficult",taskAndGT.getDifficultList().get(pos));
                workerList.add(w);
                pos++;
            }
            jsonObject.put(taskAndGT.getTaskItemEntity().getId(),workerList);
            jsonArray.add(jsonObject);
        }
        try {
            dataUtil.writeDataToFile(jsonArray.toJSONString(),"D:/fastwashdata/task4.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void computeDifficult(List<TaskAndGT> taskAndGTS){
        JSONObject jsonObject = ReadJsonFile.ReadFile("D:/fastwashdata/weight_file_zc_compare2.json");
        int pos = 0;
        for(TaskAndGT taskAndGT:taskAndGTS){
            int[][] matrix = taskAndGT.getMultiTask();
            List<Annotation> annotations = new ArrayList<>();
            boolean add =true;
            int iterSize = taskAndGT.getMultiTask().length-1;
            double[][] greatProb = new double[iterSize][2];
            double last = 0.0;
            for(int it = 1;it<=iterSize;it++){
                // 代表第几个子任务
                int start = pos;
                int right = taskAndGT.getMultiTaskRight()-1;
                for(int x = taskAndGT.getMultiTaskRight()-1;x>=0;x--){
                    if(taskAndGT.getMultiTask()[it][x]==0){
                        right--;
                    }else{
                        break;
                    }
                }
                double first = 1.0;
                double second = 1.0;
                //迭代寻找下一个任务
                for(int j = 0;j<=right;j++){
                    JSONArray iter = jsonObject.getJSONArray(String.valueOf(start));
                    // pos位置的第迭代行
                    JSONObject res = iter.getJSONObject(it-1);
                    //如果工人回答0
                    if(taskAndGT.getMultiTask()[it-1][j]==0){
                        first = first* Double.parseDouble(res.get("0").toString());
                    }else{
                        first = first* Double.parseDouble(res.get("1").toString());
                    }
                    //如果工人回答1
                    if(taskAndGT.getMultiTask()[it][j]==0){
                        second = second* Double.parseDouble(res.get("0").toString());
                    }else{
                        second = second* Double.parseDouble(res.get("1").toString());
                    }
                    start++;
                }
                double left = 1.0;
                double r = 1.0;
                if(taskAndGT.getDifficultList() ==null){
                    List<Double> difficult = new ArrayList<>();
                    taskAndGT.setDifficultList(difficult);
                }
                if(it==1){
                    greatProb[it-1][0]=first*left;
                    greatProb[it-1][1]=second*r;
                    // 第一个人用
                    taskAndGT.getDifficultList().add(1-greatProb[it-1][0]);
                    // 第二个人用
                    taskAndGT.getDifficultList().add(1-greatProb[it-1][1]);
                }else{
                    greatProb[it-1][0]=first*left;
                    greatProb[it-1][1]=second*r;
                    taskAndGT.getDifficultList().add(1-greatProb[it-1][1]);
                }
                // taskAndGT.setDifficult(1-greatProb[it-1][0]);
                // System.out.println(taskAndGT.getDifficult());
                // System.out.println(pos);
            }
            pos = pos + taskAndGT.getMultiTaskRight();
        }
    }

//    private JSONArray makeTask(List<TaskAndGT> taskAndGTS){
//        JSONArray jsonArray = new JSONArray();
//        for(TaskAndGT taskAndGT:taskAndGTS) {
//            JSONObject jsonObject = new JSONObject();
//            JSONArray workerList = new JSONArray();
//            int pos = 0;
//            for(String worker: taskAndGT.getTaskItemEntity().getWorkerList()){
//                JSONObject w = new JSONObject();
//
//                if (worker.equals("baseModel")) {
//                    w.put("workerId","10" + taskAndGT.getTaskItemEntity().getClassId());
//                }else{
//                    w.put("workerId", worker);
//                }
//                // w.put("quality",taskAndGT.getTaskItemEntity().getQuality().get(pos).getQuality());
//                if (worker.equals("baseModel")){
//                    w.put("change",false);
//                }else{
//                    if(!taskAndGT.getTaskItemEntity().getChange().get(pos)){
//                        w.put("change",false);
//                    }
//                    else{
//                        w.put("change",true);
//                    }
//                }
//                // 难度给下一个人
//                w.put("difficult",taskAndGT.getDifficultList().get(pos));
//                workerList.add(w);
//                pos++;
//            }
//            jsonObject.put(taskAndGT.getTaskItemEntity().getId(),workerList);
//            jsonArray.add(jsonObject);
//        }
//        return jsonArray;
//    }
}
