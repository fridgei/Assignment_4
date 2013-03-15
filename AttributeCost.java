import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class AttributeCost extends Thread implements Comparable {
    File path;
    int relationSize = 0;
    int distinctValues = 0;

    public AttributeCost(File dataPath, int size, int distinct) {
        this.path = dataPath;
        this.relationSize = size;
        this.distinctValues = distinct;
    }

    public AttributeCost(File dataPath) {
        this.path = dataPath;
    }

    public int getRelationSize() {
        return this.relationSize;
    }

    public int getDistinctValues() {
        return this.distinctValues;
    }

    public void run() {
        BufferedReader br = null;
        String line;
        Set<String> valueSet = new HashSet<String>();
        try {
            br = new BufferedReader(new FileReader(this.path));
            while((line = br.readLine()) != null) {
                valueSet.add(line);
                this.relationSize++;
            }
            this.distinctValues = valueSet.size();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(br != null) {
                    br.close();
                }
            } catch (IOException cex) {
                cex.printStackTrace();
            }
        }
    }

    public char getPrefix() {
        return this.path.getName().charAt(0);
    }

    public char getRelationName() {
        return this.path.getName().charAt(1);
    }

    public String toString() {
        return this.path.getName() + " has " + this.distinctValues + " distinct values and " + this.relationSize + " total values";
    }

    public int compareTo(Object o) {
        AttributeCost other = (AttributeCost) o;
        char myFirst = this.path.getName().charAt(0);
        char otherFirst = other.path.getName().charAt(0);
        if(myFirst > otherFirst) {
            return 1;
        } else if(myFirst < otherFirst) {
            return -1;
        }
        return 0;
    }
}


