import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Creates the framework for the main graph (the graph with all actors as vertices and their shared moves as edges)
 * Handles reading in the input from all 3 files as well
 */
public class KevinBacon {
    Graph<String, Set<String>> mainGraph;   //Map for the main graph of actor nodes and sets of shared movie edges
    HashMap<Integer, String> actorMap;      //Map for actor ID -> actor Name
    HashMap<Integer, String> movieMap;      //Map for movie ID -> movie Name
    HashMap<String, List<String>> actorsInMovieMap; //Map for movie Name -> list of Actor Name's in that movie
    BufferedReader input;

    /**
     * Construct a KevinBacon object
     */
    public KevinBacon(){
        mainGraph = new AdjacencyMapGraph<>();
    }


    /**
     * Populate the actorID -> actorName map
     */
    public void setActorMap(){
        //read in actorID -> actorName
        actorMap = new HashMap<>();
        try{
            input = new BufferedReader(new FileReader("bacon/actors.txt"));
            String line;
            //add actorID -> actorName in map
            while((line = input.readLine()) != null){
                String[] lineArray = line.split("\\|");
                int id = Integer.parseInt(lineArray[0]);
                String name = lineArray[1];
                actorMap.put(id,name);
                //add actorNames as vertices to mainGraph
                mainGraph.insertVertex(name);
            }
        } catch (IOException e){
            System.err.println("IO Error while reading in actor file\n"+e.getMessage());
        } finally {
            //close the input file regardless
            try{
                input.close();
                System.out.println("Actor file closed");
            } catch(IOException e){
                System.err.println("Cannot close actor file\n"+e.getMessage());
            }
        }

    }

    /**
     * Populate the movieID -> movieName map
     */
    public void setMovieMap(){
        //read in movieID -> movieName
        movieMap = new HashMap<>();
        try{
            input = new BufferedReader(new FileReader("bacon/movies.txt"));
            String line;
            //add movieID -> movieName in map
            while((line = input.readLine()) != null){
                String[] lineArray = line.split("\\|");
                int id = Integer.parseInt(lineArray[0]);
                String name = lineArray[1];
                movieMap.put(id,name);
            }
        } catch (IOException e){
            System.err.println("IO Error while reading in movies file\n"+e.getMessage());
        }
        finally {
            //close the input file regardless
            try{
                input.close();
                System.out.println("Movie file closed");
            } catch(IOException e){
                System.err.println("Cannot close movie file\n"+e.getMessage());
            }
        }
    }

    /**
     * Populate the movieName -> List of actorNames in that movie map
     */
    public void setActorsInMovieMap(){
        //read in movieID -> actorID
        actorsInMovieMap = new HashMap<>();
        try{
            input = new BufferedReader(new FileReader("bacon/movie-actors.txt"));
            String line;
            //add movieID -> movieName in map
            while((line = input.readLine()) != null){
                String[] lineArray = line.split("\\|");
                // convert to movieName -> set of actorNames in movie
                int movieID = Integer.parseInt(lineArray[0]);
                int actorID = Integer.parseInt(lineArray[1]);
                String movieName = movieMap.get(movieID);
                String actorName = actorMap.get(actorID);
                if(!actorsInMovieMap.containsKey(movieName)){
                    actorsInMovieMap.put(movieName, new ArrayList<String>());
                }
                actorsInMovieMap.get(movieName).add(actorName);
            }
        } catch (IOException e){
            System.err.println("IO Error while reading in movie-actor file\n"+e.getMessage());
        } finally {
            //close the input file regardless
            try{
                input.close();
                System.out.println("Movie-actor file closed");
            } catch(IOException e){
                System.err.println("Cannot close movie-actor file\n"+e.getMessage());
            }
        }

    }

    /**
     * Create the main graph from the movieName -> List of actorNames in that movie map
     */
    public void createGraph(){
        if(actorMap == null) setActorMap();
        if(movieMap == null) setMovieMap();
        if(actorsInMovieMap == null) setActorsInMovieMap();
        ArrayList<String> actorNames;
        //create Map of List<actor1, actor2> -> set of movieNames
        HashMap<ArrayList<String>, Set<String>> edgeMap = new HashMap<>();
        //for each movieName
        for(String movieName: actorsInMovieMap.keySet()){
            List<String> actors = actorsInMovieMap.get(movieName);
            //loop through all pairs of actor names
//            System.out.println(movieName);
//            System.out.println(actors.toString());
            for(int i = 0; i<actors.size()-1; i++){
                //create List of actor names in lexicographic order
                for(int j = i+1; j<actors.size(); j++){
                    String name1 = actors.get(i);
                    String name2 = actors.get(j);
                    if(name1.compareTo(name2)>0){
                        String temp = name1;
                        name1 = name2;
                        name2 = temp;
                    }
                    actorNames = new ArrayList<>();
                    actorNames.add(name1); actorNames.add(name2);
//                    System.out.println(actorNames.toString());
                    //if List not in map, add it to map with empty set
                    if(!edgeMap.containsKey(actorNames)){
                        edgeMap.put(actorNames, new HashSet<>());
                    }
                    //regardless, add movieName to List's set in Map
                    edgeMap.get(actorNames).add(movieName);
                }
            }
        }
        //for each pair of actors, put in an undirected edge between them that holds their shared movies as edge label in mainGraph
        for(ArrayList<String> actorPairs: edgeMap.keySet()){
            Set<String> commonMovies = edgeMap.get(actorPairs);
            mainGraph.insertUndirected(actorPairs.get(0), actorPairs.get(1), commonMovies);
        }
    }

    //basic getters for all the maps
    public Graph<String, Set<String>> getMainGraph() {
        return mainGraph;
    }
    public HashMap<Integer, String> getActorMap() { return actorMap; }
    public HashMap<Integer, String> getMovieMap() { return movieMap; }
    public HashMap<String, List<String>> getActorsInMovieMap() {
        return actorsInMovieMap;
    }

    //TESTING DRIVER
    public static void main(String[] args){
        KevinBacon kb = new KevinBacon();
        kb.createGraph();
        System.out.println("FINAL GRAPH: \n"+kb.getMainGraph());
//        System.out.println("BFS GRAPH FROM KEVIN BACON: \n");
//        System.out.println(GraphLibBacon.bfs(kb.getMainGraph(), "Kevin Bacon").toString());
//        System.out.println("Path from Dartmouth(Earl thereof) to Kevin Bacon: "+GraphLibBacon.getPath(GraphLibBacon.bfs(kb.getMainGraph(), "Kevin Bacon"),"Dartmouth (Earl thereof)").toString());
////        System.out.println("Path from LeVar Burton to Kevin Bacon: "+GraphLibBacon.getPath(GraphLibBacon.bfs(kb.getMainGraph(), "Kevin Bacon"),"LeVar Burton").toString());
////        System.out.println("Path from Buster Keaton Keaton to Kevin Bacon: "+GraphLibBacon.getPath(GraphLibBacon.bfs(kb.getMainGraph(), "Kevin Bacon"),"Buster Keaton").toString());
//        System.out.println("Missing from Kevin Bacon Graph: "+GraphLibBacon.missingVertices(kb.getMainGraph(), GraphLibBacon.bfs(kb.getMainGraph(), "Kevin Bacon")).toString());
//        System.out.println("Average Separation from Kevin Bacon: "+GraphLibBacon.averageSeparation(GraphLibBacon.bfs(kb.getMainGraph(), "Kevin Bacon"), "Kevin Bacon"));
//        System.out.println("Average Separation from Nobody: "+GraphLibBacon.averageSeparation(GraphLibBacon.bfs(kb.getMainGraph(), "Nobody"), "Nobody"));
//        System.out.println("Path from Nobody to Kevin Bacon: "+GraphLibBacon.getPath(GraphLibBacon.bfs(kb.getMainGraph(), "Kevin Bacon"),"Nobody").toString());
    }
}
