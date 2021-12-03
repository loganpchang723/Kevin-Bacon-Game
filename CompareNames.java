import java.util.Objects;

/**
 * A class that creates objects for when we want to compare vertices (actors) according to different metrics
 * Make them Comparable for easy sorting
 * @param <V> Generic type for the Vertex
 */
public class CompareNames <V> implements Comparable<CompareNames>{
    private V name;         //name of the actor
    private double metric;  //some tangible metric (like separation)

    /**
     * Construct a CompareNames object that takes in name and metric parameters
     * @param name      Name of the actor(vertex)
     * @param metric    Metric of the actor(vertex)
     */
    public CompareNames(V name, double metric){
        this.name = name;
        this.metric = metric;
    }

    //standard getters
    public V getName() { return name; }
    public double getMetric() { return metric; }

    /**
     * compareTo method so we can easily sort our objects
     * @param o Another CompareNames object
     * @return  -1 if the current object's metric is less than the others, 0 if equal, else 1
     */
    @Override
    public int compareTo(CompareNames o) {
        return Double.compare(this.metric, o.getMetric());
    }

    /**
     * Standard toString to give name and metric
     * @return  String of name+" "+metric
     */
    @Override
    public String toString() {
        return name+" "+metric;
    }



}