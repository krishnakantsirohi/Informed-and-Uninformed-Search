/*
* Krishnakant Singh Sirohi
* 1001668969
* kss8969
* */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/*
* Node class to store the current city, its parent and the cumulative path cost.
* */
class Node{
    float pathCost;
    float hueristicCost;
    String city;
    Node parent;
    Node(String city, Node parent, float pathCost)
    {
        this.pathCost = pathCost;
        this.parent = parent;
        this.city = city;
    }
    Node(String city, Node parent, float pathCost, float hueristicCost)
    {
        this.pathCost = pathCost;
        this.parent = parent;
        this.city = city;
        this.hueristicCost = hueristicCost;
    }
}
/*
* Comparator class used for sorting the fringe in descending order on the basis of their cumulative path cost.
* */

class nodeComparator implements Comparator<Node>{
    public int compare(Node n1,Node n2)
    {
        if (n1.pathCost>n2.pathCost) return 1;
        else if (n1.pathCost<n2.pathCost) return -1;
        else return 0;
    }
}
/*
* Comparator class used for sorting the fringe in decreasing order on the basis of sum of cumulative path cost and the straight line distance
* from current node to goal.
* */
class hueristicComparator implements Comparator<Node>{

    @Override
    public int compare(Node n1, Node n2) {
        if (n1.hueristicCost>n2.hueristicCost) return 1;
        else  if (n1.hueristicCost<n2.hueristicCost) return -1;
        return 0;
    }
}

public class find_route {
    /*
    * child is used to store the information provided in the input file. Every city in the input is a key for which the value is an Arraylist of
    * of Arrays. For eg.
    * 1. Bremen: {(Hamburg, 116), (Hannover, 132), (Dortmund, 234)}
    * 2. Lueback: {(Hamburg, 63)}
    * */
    Hashtable<String, ArrayList<String[]>> child = new Hashtable<String, ArrayList<String[]>>();
    /*
    * hueristics is used to store the information provided in the hueristics file. Every element is a pair of city name and
    * the straight line distance from current city to the destination city.
    * */
    Hashtable<String, Float> hueristics = new Hashtable<String,Float>();
    /*
    * route is used to store the shortest cumulative path from source to destination.
    * */
    Hashtable<String, Object[]> route = new Hashtable<String, Object[]>();
    /*
    * nodes is used to store the number of nodes expanded while finding the shortest path from route to destination.
    * */
    int nodes=0;

    private void findRoute(String inputFile, String source, String destination) throws IOException
    {
        // parse the input file and store it in the child hashtable initialized above.
        parseInputFile(inputFile);
        // visited set stores the nodes which has already been expanded.
        HashSet<String> visited = new HashSet<String>();
        // fringe stores the nodes on the basis of their cumulative path cost.
        PriorityQueue<Node> fringe = new PriorityQueue<Node>(1,new nodeComparator());
        // add the source to fringe with parent as null and cumulative path cost0.
        fringe.add(new Node(source,null,0));
        // Explores all the nodes until either the goal test is passed or the fringe is empty.
        while (!fringe.isEmpty())
        {
            // picks the node with the least cumulative path cost.
            Node current = fringe.poll();
            nodes++;
            // Insert the node in route as it has already been visited. The least cost check has already been implemented in the function.
            insertInRoute(current);
            if (current.city.equals(destination))
                break;
            if (visited.contains(current.city))
                continue;
            /*
             * If the goal test is not passed then, parse all the children of the current city and store it in the fringe if it has not
             * already been visited.
            */
            ArrayList<String[]> children = child.get(current.city);
            Iterator<String[]> it = children.iterator();
            while (it.hasNext()){
                String[] s = it.next();
                Node n = new Node(s[0],current,current.pathCost+Float.parseFloat(s[1]));
                fringe.add(n);

            }
            // Add the current node to visited set as all it's child nodes has already been visited.
            visited.add(current.city);
        }
        // prints the elements of the route.
        traceRoute(destination);
    }

    private void findRoute(String inputFile, String source, String destination, String h_file) throws IOException
    {
        // parse the hueristics file and store it in the hueristics hashtable initialized above.
        parseHueristicFile(h_file);
        // parse the input file and store it in the child hashtable initialized above.
        parseInputFile(inputFile);
        // visited set stores the nodes which has already been expanded.
        HashSet<String> visited = new HashSet<String>();
        // fringe stores the nodes on the basis of their cumulative path cost.
        PriorityQueue<Node> fringe = new PriorityQueue<Node>(1,new hueristicComparator());
        // add the source to fringe with parent as null and cumulative path cost0.
        fringe.add(new Node(source,null,0,0));
        // Explores all the nodes until either the goal test is passed or the fringe is empty.
        while (!fringe.isEmpty())
        {
            // picks the node with the least cumulative path cost.
            Node current = fringe.poll();
            nodes++;
            // Insert the node in route as it has already been visited. The least cost check has already been implemented in the function.
            insertInRoute(current);
            if (current.city.equals(destination)) break;
            if (visited.contains(current.city))
                continue;
            /*
             * If the goal test is not passed then, parse all the children of the current city and store it in the fringe if it has not
             * already been visited.
             */
            ArrayList<String[]> children = child.get(current.city);
            Iterator<String[]> itr = children.iterator();
            while (itr.hasNext()){
                String[] str = itr.next();
                Node node = new Node(str[0],current, current.pathCost+Float.parseFloat(str[1]),current.pathCost+Float.parseFloat(str[1])+hueristics.get(str[0]));
                fringe.add(node);
            }
            // Add the current node to visited set as all it's child nodes has already been visited.
            visited.add(current.city);
        }
        // prints the elements of the route.
        traceRoute(destination);
    }

    public void insertInRoute(Node node)
    {
        if (!route.containsKey(node.city) || (Float) route.get(node.city)[1]>node.pathCost){
            Object[] value = {node.parent!=null ? node.parent.city : null, node.pathCost};
            route.put(node.city,value);
        }
    }

    // parse the input file and store the information in child hashtable.
    private void parseInputFile(String inputFile) throws IOException
    {
        File file = new File(inputFile);
        BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
        String line;
        while (!(line=br.readLine()).equals("END OF INPUT"))
        {
            String start = line.split(" ")[0];
            String end = line.split(" ")[1];
            String dist = line.split(" ")[2];
            cacheMap(start, end,dist);
            cacheMap(end, start, dist);
        }
    }

    // parse the hueristics file and store the information in hueristics hastable.
    private void parseHueristicFile(String h_file) throws IOException
    {
        File file = new File(h_file);
        Scanner sc = new Scanner(new FileReader(file.getPath()));
        String line;
        while (!(line = sc.nextLine()).equals("END OF INPUT"))
        {
            hueristics.put(line.split(" ")[0].toString(),Float.parseFloat(line.split(" ")[1].toString()));
        }
    }

    // Function stores the input file information in child hashtable.
    private void cacheMap(String start, String end, String dist)
    {
        String[] entry = {end, dist};
        if (child.containsKey(start))
            child.get(start).add(entry);
        else {
            ArrayList<String[]> temp = new ArrayList<String[]>();
            temp.add(entry);
            child.put(start,temp);
        }
    }

    // takes destination city as an argument if it exists in the route then it prints the output route else the goal test is failed
    // distance is infinity. If it passes the goal test then it reads the information and iterate through the hashtable entries.
    private void traceRoute(String dest)
    {
        String totdis = "infinity";
        // Since the route stores the information in reverse order therefore storing it in the stack therefore making it easier to trace back the path.
        Stack<String> finalRoute = new Stack<String>();
        if (route.containsKey(dest)){
            totdis = route.get(dest)[1]+" km";
            String parent = (String) route.get(dest)[0];
            while (parent!=null){
                float dist = (Float) route.get(dest)[1] - (Float)route.get(parent)[1];
                finalRoute.push(parent+" to "+dest+ ", "+dist+" km");
                dest=parent;
                parent = (String)route.get(dest)[0];
            }
        }
        StringBuffer sb = new StringBuffer();
        sb.append("nodes expanded: "+nodes+"\n\n");
        sb.append("distance: "+totdis+"\n");
        sb.append("route:\n");
        if (finalRoute.isEmpty()) sb.append("none");
        else {
            while (!finalRoute.isEmpty()){
                sb.append(finalRoute.pop());
                sb.append("\n");
            }
        }
        System.out.println(sb.toString());
    }

    public static void main(String[] args) throws IOException
    {
        find_route findRoute = new find_route();
        // finds the shortest path without using hueristics file.
        if (args.length==3 && (args[0]!=null || args[1]!=null || args[2]!=null))
        {
            findRoute.findRoute(args[0],args[1],args[2]);
        }
        // finds the shortest path using hueristics file.
        else if (args.length==4 && (args[0]!=null || args[1]!=null || args[2]!=null || args[3]!=null))
        {
            findRoute.findRoute(args[0],args[1],args[2],args[3]);
        }
        else{
            System.out.println("Invalid number of arguments!");
        }
    }
}