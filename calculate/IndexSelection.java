import java.io.*;
import java.util.Arrays;
import java.lang.Integer;
import java.util.ArrayList;

public class IndexSelection {

    public static void main(String[] args) {
        Index[] test = IndexSelection.WeightIndexed("/scratch/CS440Assignment4");
        ArrayList<Relation> relations = IndexSelection.buildRelations(test);
        Relation best = IndexSelection.relationToIndex(relations);
        System.out.println(best.getRelationId());
    }




    public static CostTable() {
        // Relation S
        String sxPath = "/scratch/CS440Assignment4/SX.dat";
        String sxPath = "/scratch/CS440Assignment4/SY.dat";
        // Relation R
        String sxPath = "/scratch/CS440Assignment4/RX.dat";
        // Relation U -- disregard the file name its really relation U
        String sxPath = "/scratch/CS440Assignment4/TY.dat";



    public static Index[] WeightIndexed(String path) {
        BufferedReader br;
        Process p;
        File folder = new File(path);
        File [] files = folder.listFiles();
        Index[] possibleIndexes = new Index[files.length];
        Arrays.sort(files);
        Runtime r = Runtime.getRuntime();
        try {
            for(int i =0; i < files.length; i++) {
                p = new ProcessBuilder("/bin/bash", "-c", "sort -n " +
                    files[i].getPath() + " | uniq | wc -l").start();
                p.waitFor();
                br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                possibleIndexes[i] = new Index(files[i], Integer.parseInt(br.readLine()));
            }
        } catch (Exception e) {
            System.out.println("Fucked up");
        }
        return possibleIndexes;
    }

    public static ArrayList<Relation> buildRelations(Index[] indexes) {
        ArrayList<Relation> indexedRelations = new ArrayList<Relation>();
        Arrays.sort(indexes);
        ArrayList<Index> idxAccumulator = new ArrayList<Index>();
        for(int i = 0; i < indexes.length; i++) {
            idxAccumulator.add(indexes[i]);
            while(i + 1 < indexes.length &&
                  indexes[i + 1].getPrefix() == idxAccumulator.get(idxAccumulator.size() - 1).getPrefix()) {
                idxAccumulator.add(indexes[++i]);
            }
            indexedRelations.add(new Relation(idxAccumulator));
            idxAccumulator = new ArrayList<Index>();
        }
        return indexedRelations;
    }

    public static Relation relationToIndex(ArrayList<Relation> relations) {
        Relation max = null;
        for(Relation relation:relations) {
            if(max == null || max.getMaxDistinct() < relation.getMaxDistinct()) {
                max = relation;
            }
        }
        return max;
    }
}

class microSort extends Thread {

    public CostSizeComputation(String[] paths,
