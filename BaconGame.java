import java.util.*;

/**
 * UI for the game
 * Play the game in the main method here!
 */
public class BaconGame {
    private Scanner scan;   //scanner to read user input
    private KevinBacon kb;  //KevinBacon object so we can make the main graph
    private Graph<String, Set<String>> graph;   //Graph to hold the main graph
    private Graph<String, Set<String>> bfsGraph;    //Graph to hold the BFS tree
    String currentCenter;   //the current root actor (center of universe)
    private List<CompareNames<String>> separations;     //list to hold all the actors in non-decreasing separation order (having to run this operation again and again is quite expensive)

    /**
     * Creates the BaconGame UI object
     */
    public BaconGame() {
        //instantiate all our instance variables
        scan = new Scanner(System.in);
        kb = new KevinBacon();
        graph = new AdjacencyMapGraph<>();
        bfsGraph = new AdjacencyMapGraph<>();
        currentCenter = "Kevin Bacon";
        separations = new ArrayList<>();

    }

    /**
     * Plays the game!
     */
    public void play() {
        kb.createGraph();
        graph = kb.getMainGraph();
        bfsGraph = GraphLibBacon.bfs(graph, "Kevin Bacon");
        //PLEASE USE ALL LOWERCASE WHEN PLAYING THE GAME, please!
        System.out.println("Commands:\n c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n d <low> <high>: list actors sorted by degree, with degree between low and high\n i: list actors with infinite separation from the current center\n p <name>: find path from <name> to current center of the universe\n s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n u <name>: make <name> the center of the universe\n b <name>: get betweenness centrality of <name> from current center\n q: quit game");
        System.out.println("\n" + currentCenter + " is now the center of the acting universe, connected to " + bfsGraph.numVertices() + "/9235 actors with average separation " + GraphLibBacon.averageSeparation(bfsGraph, "Kevin Bacon"));
        while (true) {
            System.out.println("\nKevin Bacon game >");
            String line = scan.nextLine();
            char c = line.charAt(0);
            //quit the game
            if (c == 'q') break;
                //make <name> the center of the universe
            else if (c == 'u') {
                //get the input name
                String fullName = "";
                String[] arr = line.split(" ");
                for (int i = 1; i < arr.length; i++) {
                    if (i != arr.length - 1) fullName += arr[i] + " ";
                    else fullName += arr[i];
                }
                currentCenter = fullName;
                //set bfsGraph to the tree centered from full name and print a message
                try {
                    bfsGraph = GraphLibBacon.bfs(graph, currentCenter);
                } catch (NullPointerException e) {
                    //if we mistype or pick a node that doesn't exist, say so and continue the game
                    System.out.println("\"" + fullName + "\" doesn't exist in the graph");
                    continue;
                }
                System.out.println(fullName + " is now the center of the acting universe, connected to " + bfsGraph.numVertices() + "/9235 actors with average separation " + GraphLibBacon.averageSeparation(bfsGraph, currentCenter));
            }
            //list actors with infinite separation from current center (the missing vertices of this graph)
            else if (c == 'i') {
                System.out.println(GraphLibBacon.missingVertices(graph, bfsGraph).toString());
            }
            //get path
            else if (c == 'p') {
                //get the name of actor to make path from
                List<String> list;
                String fullName = "";
                String[] arr = line.split(" ");
                for (int i = 1; i < arr.length; i++) {
                    if (i != arr.length - 1) fullName += arr[i] + " ";
                    else fullName += arr[i];
                }
                //get the path from that actor to the root
                list = GraphLibBacon.getPath(bfsGraph, fullName);
                //if there's no path, move on
                if (list.size() == 0) continue;
                //print their number (distance from root)
                System.out.println(fullName + "'s number is " + (list.size() - 1));
                //print out the shared movies between each actor on the path
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < list.size() - 1; i++) {
                    String from = list.get(i);
                    String to = list.get(i + 1);
                    sb.append(from + " appeared in " + bfsGraph.getLabel(from, to).toString() + " with " + to + "\n");
                }
                System.out.println(sb.toString());
            }
            //list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation
            else if (c == 'c') {
                int num;
                try {
                    num = Integer.parseInt(line.split(" ")[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Please input a single number");
                    continue;
                }
                if (separations.size() == 0) {
                    //create a list of CompareNames objects so we can sort every name(vertex) by its average separation
                    for (String name : graph.vertices()) {
                        double separation = GraphLibBacon.averageSeparation(GraphLibBacon.bfs(graph, name), name);
                        if(separation>0) separations.add(new CompareNames(name, separation));
                    }
                    //sort the list according to ascending average separation
                    separations.sort((o1, o2) -> Double.compare(o1.getMetric(), o2.getMetric()));
                }
//                System.out.println(list.toString());
                StringBuilder sb = new StringBuilder();
                if (num == 0) System.out.println("You picked nothing");
                    //if input is a positive number, we get n entries from the front (the n entries with the lowest separation)
                else if (num > 0) {
                    for (int i = 0; i < num; i++) {
                        sb.append(separations.get(i).toString() + "\n");
                    }
                }
                //if input is negative number, we get n entries from the back (the n entries with the highest separation)
                else {
                    num = num * -1;
                    for (int i = separations.size() - 1; i > separations.size() - num - 1; i--) {
                        sb.append(separations.get(i).toString() + "\n");
                    }
                }
                System.out.println(sb.toString());
            }
            //list actors sorted by degree, with degree between low and high
            else if (c == 'd') {
                //get low and high amounts from input
                double low = Double.parseDouble(line.split(" ")[1]);
                double high = Double.parseDouble(line.split(" ")[2]);
                ArrayList<CompareNames> list = new ArrayList<>();
                //add all the actors with degrees between low and high to a list as CompareNames objects and their metrics as their degree
                for (String name : graph.vertices()) {
                    int degree = graph.inDegree(name);
//                    System.out.println(name+" "+degree);
                    if (degree >= low && degree <= high) list.add(new CompareNames(name, degree));
                }
                //if there are no actors with degrees between low and high, state so and move on
                if (list.size() == 0) {
                    System.out.println("There are no actors with degrees between " + low + " and " + high);
                    continue;
                }
                //otherwise, print all the actors with degrees between low and high in non-decreasing degree order
                list.sort((o1, o2) -> Double.compare(o1.getMetric(), o2.getMetric()));
                System.out.println("Actors with degrees between " + low + " and " + high + " (inclusive), in non-decreasing order: ");
                for (CompareNames cn : list) {
                    System.out.println(cn);
                }
            }
            //list actors sorted by non-infinite separation from the current center, with separation between low and high
            else if (c == 's') {
                //get low and high amounts from input
                double low = Double.parseDouble(line.split(" ")[1]);
                double high = Double.parseDouble(line.split(" ")[2]);
                ArrayList<CompareNames> list = GraphLibBacon.getSeparations(bfsGraph, currentCenter);
//                System.out.println(list.toString());
                //add all the actors with degrees between low and high to a list as CompareNames objects and their metrics as their separation
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getMetric() < low || list.get(i).getMetric() > high) {
                        list.remove(i);
                        i--;
                    }
                }
                //if there are no actors with separations between low and high, state so and move on
                if (list.size() == 0) {
                    System.out.println("There are no actors with separation from " + currentCenter + " between " + low + " and " + high);
                    continue;
                }
                //otherwise, print all the actors between low and high in non-decreasing separation order
                list.sort((o1, o2) -> Double.compare(o1.getMetric(), o2.getMetric()));
                System.out.println("Actors with separation from " + currentCenter + " between " + low + " and " + high + " (inclusive), in non-decreasing order: ");
                for (CompareNames cn : list) {
                    System.out.println(cn);
                }
            }
            //get betweenness centrality
            else if(c == 'b'){
                //get the name of actor to find betweenness centrality from
                String fullName = "";
                String[] arr = line.split(" ");
                for (int i = 1; i < arr.length; i++) {
                    if (i != arr.length - 1) fullName += arr[i] + " ";
                    else fullName += arr[i];
                }
                //get their betweenness centrality
                int centrality = GraphLibBacon.betweennessCentrality(bfsGraph,fullName);
                if(centrality == -1){   //if they aren't connected (centrality is -1), state so and continue
                    System.out.println(fullName+" isn't connected to "+currentCenter);
                    continue;
                }
                System.out.println(fullName+"'s betweenness centrality in "+ currentCenter+" 's centered tree is "+GraphLibBacon.betweennessCentrality(bfsGraph,fullName));
            }
            //some invlaid command character
            else{
                System.out.println("Invalid input");
            }
        }
    }

    public static void main(String[] args) {
        BaconGame game = new BaconGame();
        game.play();
    }
}
