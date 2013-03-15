import java.io.*;

public class CostTest {
   public static void main(String[] args) {
      File folder = new File("/scratch/CS440Assignment4");
      File [] files = folder.listFiles();
      AttributeCost[] costTasks = new AttributeCost[files.length];
      try {
          for(int i = 0; i < files.length; i++) {
              costTasks[i] = new AttributeCost(files[i]);
              costTasks[i].start();
          }
          for(AttributeCost task:costTasks) {
              task.join();
              System.out.println(task);
        }
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
      Relation[] rels = DbUtils.createRelations(costTasks);
   }
}
