import java.util.*;

public class GraphLibBacon extends GraphLib{

    /**
     * Create the BFS tree from the main graph centered at some root vertex
     * @param g         Main graph
     * @param source    Root node to BFS from
     * @param <V>       Generic type of Vertex
     * @param <E>       Generic type of Edge
     * @return          BFS tree from root node as a directed graph
     */
    public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source){
        //create bfs graph centered at source
        Graph<V,E> bfsGraph = new AdjacencyMapGraph<>();
        //create queue for BFS and set to hold visited nodes
        Queue<V> queue = new LinkedList<>();
        Set<V> visited = new HashSet<>();
        //add source to queue and mark as visited
        queue.add(source);
        visited.add(source);
        //insert a vertex into the bfs graph with source
        bfsGraph.insertVertex(source);
        while(!queue.isEmpty()){
            V curr = queue.remove();
//            visited.add(curr);
            //for each unvisited neighbor of the popped vertex:
            for(V neighbor: g.outNeighbors(curr)){
                if(!visited.contains(neighbor)){
                    //enqueue and mark vertex as visited
                    queue.add(neighbor);
                    visited.add(neighbor);
                    //add this vertex to the bfs graph
                    bfsGraph.insertVertex(neighbor);
                    //insert a directed edge from this vertex pointing to it in neighbor (neighbor closest to central vertex) into BFS graph
                    //label this edge as the set of movies shared by these two vertices from the original graph
                    bfsGraph.insertDirected(neighbor,curr,g.getLabel(curr,neighbor));
                }
            }
        }
        return bfsGraph;
    }

    /**
     * Returns the vertices en route from the parameter to the root
     * @param tree  BFS tree from root vertex
     * @param v     The starting point of the path (going from v to root)
     * @param <V>   Generic type for Vertex
     * @param <E>   Generic type for Edge
     * @return      List of vertices in order from v to root
     */
    public static <V,E> List<V> getPath(Graph<V,E> tree, V v){
        List<V> pathList = new ArrayList<>();
        Iterable<V> parent;
        //ensure that the chosen destination is connected to the source
        try{
            //if chosen destination has no out neighbors in the connected tree, then it's not connected to source
            parent = tree.outNeighbors(v);
        } catch (NullPointerException e){
            System.out.println("Vertex \""+v+"\" isn't connected to the chosen center.");
            return pathList;
        }
        pathList.add(v);
        //from the destination node, continually add each nodes predecessor until no more predecessors (which means we are at the source)
        while(parent.iterator().hasNext()){
            V prev = parent.iterator().next();
            pathList.add(prev);
            parent = tree.outNeighbors(prev);
        }
        return pathList;
    }

    /**
     * Find vertices in the main graph but not the BFS tree (not connected to root of BFS tree)
     * @param graph     The main graph (all actors and movies)
     * @param subgraph  BFS tree from some specified root
     * @param <V>       Generic type for Vertex
     * @param <E>       Generic type for Edges
     * @return          Set of all vertices in the main graph but not the subgraph
     */
    public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph){
        //list to hold all vertices in the subgraph (bfs tree)
        ArrayList<V> subVertices = new ArrayList<>();
        for(V sub: subgraph.vertices()){
            subVertices.add(sub);
        }
        //set to hold vertices missing from bfs tree that are in the original graph of all actors and movies
        Set<V> missing = new HashSet<>();
        //for each vertex in the original graph
        for(V v: graph.vertices()){
            //add it to the set if it's not in the ArrayList holding all vertices from the bfs tree
            if(!subVertices.contains(v)) missing.add(v);
        }
        return missing;
    }

    /**
     * Gets the average separation of each node in BFS tree from root
     * @param tree  BFS tree
     * @param root  root vertex of BFS tree
     * @param <V>   Generic type for Vertex
     * @param <E>   Generic type for Edge
     * @return      Average separation of all nodes in the tree from the root (
     */
    public static <V,E> double averageSeparation(Graph<V,E> tree, V root){
        //use helper function to get total distances in the graph
        double total = separationHelper(tree,root,0);
        //avg = total distance from source of all vertices in graph/ num of vertices in graph -1 (to account for source node because it's not realistic to count it)
        //if there's only one node in the tree, let's say for arguments sake that it has 0 separation
        if((tree.numVertices()-1)<=0) return 0;
        return total/(tree.numVertices()-1);
    }

    /**
     * Helper to get total separation in the BFS tree
     * @param tree  BFS tree
     * @param root  Root vertex of BFS tree
     * @param depth current depth of the vertex being track
     * @param <V>   Generic type for Vertex
     * @param <E>   Generic type for Edge
     * @return      total separation of all nodes in the BFS tree from the root as a double
     */
    public static <V,E> double separationHelper(Graph<V,E> tree, V root, int depth){
        double total = 0;
        //get iterable of all children of current vertex
        Iterable<V> children = tree.inNeighbors(root);
        //increase total by the depth of the current node in the tree (its distance from source node)
        total += depth;
        //for each child vertex...
        for(V child: children){
            //recursively call the helper on each child to get the distance/height of each of its children, and so on...
            total += separationHelper(tree,child, depth+1);
        }
        return total;
    }

    /**
     * Get the separations of each vertex from the node of the BFS tree
     * @param tree  BFS tree
     * @param root  Root of the BFS tree
     * @param <V>   Generic Type of the Vertex
     * @param <E>   Generic Type of the Edge
     * @return      List of CompareName objects, each with an actor name and their separation from the root
     */
    public static <V,E> ArrayList<CompareNames> getSeparations(Graph<V,E> tree, V root){
        //use accumulator to add CompareNames objects to the list
        ArrayList<CompareNames> list = new ArrayList<>();
        getSeparationsHelper(tree, root, list, 0);
        return list;
    }

    /**
     * Helper to get the separation of each node from root (recursive DFS)
     * @param tree  BFS tree
     * @param curr  Current node
     * @param list  List of CompareNames objects
     * @param depth int to track depth
     * @param <V>   Generic Type of the Vertex
     * @param <E>   Generic Type of the Edge
     */
    public static <V,E> void getSeparationsHelper(Graph<V,E> tree, V curr, ArrayList<CompareNames> list, int depth){
        //add a new CompareNames object to the list with the current vertex's name and depth
        list.add(new CompareNames(curr, depth));
        //recur on children of current node that are 1 depth level further from the root
        for(V v: tree.inNeighbors(curr)){
            getSeparationsHelper(tree, v, list, depth+1);
        }
    }

    /**
     * EC attempt to get betweenness centrality of a given nodes in the given BFS tree
     * This is a measure of how often a node occurs on a path from each node to the root of the BFS tree
     *
     * @param tree  BFS tree
     * @param v     Vertex to get centrality of
     * @param <V>   Generic Type of the Vertex
     * @param <E>   Generic Type of the Edge
     */
    public static <V,E> int betweennessCentrality(Graph<V,E> tree, V v){
        int amt = 0;
        Iterable<V> iter = tree.vertices();
        //for each vertex in the tree, see if v is on its path to the root
        for(V vert: iter){
            List<V> path = GraphLibBacon.getPath(tree, vert);
            if(path.contains(v)) amt++;
        }
        return amt-1; //minus 1 for its own path
    }

}
