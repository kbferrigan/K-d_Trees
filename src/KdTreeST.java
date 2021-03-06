import java.util.*;

public class KdTreeST<Value> {

    private class Node {
        private Point2D p; // the point
        private Value val; // the symbol table maps the point to this value
        private RectHV rect; // the axis-aligned rectangle corresponding to this node
        private Node lb; // the left/bottom subtree
        private Node rt; // the right/top subtree
        private boolean xCord; // the x/y coordinate status; x = true, y = false
        
        public Node(){
            
        }
        public Node(Point2D p, Value val, RectHV rect, Node lb, Node rt, boolean xCord){
            this.p = p;
            this.val = val;
            this.rect = rect;
            this.lb = lb;
            this.rt = rt;
            this.xCord = xCord;
        }
    }

    Node topNode;
    int nodeAmnt;
    double xmin;
    double ymin;
    double xmax;
    double ymax;

    // construct an empty set of points
    public KdTreeST(){
        topNode = null;
        nodeAmnt = 0;
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        if (topNode == null) return true;
        else return false;
    }

    // number of points
    public int size() { return nodeAmnt; }

    // associate the value val with point p
    public void put(Point2D p, Value val){ 
        if (p == null || val == null) throw new IllegalArgumentException("NULL");
        xmin = 0.0;
        ymin = 0.0;
        xmax = 1.0;
        ymax = 1.0;
        topNode = put(topNode, p, val, true);
    }

    private Node put(Node n, Point2D p, Value val, boolean xCord){
        if (n == null){ // If there is no node, add a node
            nodeAmnt++;
            RectHV rect = new RectHV(xmin, ymin, xmax, ymax);
            return new Node(p, val, rect, null, null, xCord);
        }
        if (p.equals(n.p)){ // If the point equals the point of the node, return its value
            n.val = val;
            return n;
        }
        double comp; // comparable value
        if (n.xCord) comp = Double.compare(p.x(), n.p.x()); // x-coordinate traversal
        else comp = Double.compare(p.y(), n.p.y()); // y-coordinate traversal
        if (comp < 0){ // If the point is less than the point of the node, go left
            if (n.xCord) xmax = n.p.x();
            else ymax = n.p.y();
            n.lb = put(n.lb, p, val, !n.xCord);
        }
        else if (comp >= 0){ // If the point is greater than the point of the node, go right
            if (n.xCord) xmin = n.p.x();
            else ymin = n.p.y();
            n.rt = put(n.rt, p, val, !n.xCord);
        }
        return n;
    }
    
    // value associated with point p
    public Value get(Point2D p){
        if (p == null) throw new IllegalArgumentException("NULL");
        return get(topNode, p);
    }

    private Value get(Node n, Point2D p){
        if (n == null) return null; // Exit if the tree is empty or the end of the node
        if (p.equals(n.p)) return n.val; // If the point equals the point of the node, return its value
        double comp; // comparable value
        if (n.xCord) comp = Double.compare(p.x(), n.p.x()); // x-coordinate
        else comp = Double.compare(p.y(), n.p.y()); // y-coordinate
        if (comp < 0) return get(n.lb, p); // If the point is less than the point of the node, return left
        else if (comp >= 0) return get(n.rt, p); // If the point is greater than the point of the node, return right
        return n.val;
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D p){
        if (p == null) throw new IllegalArgumentException("NULL");
        return get(p) != null;
    }

    // all points in the symbol table helper
    private ArrayList<Point2D> dive(Node tempnode, ArrayList<Point2D> allPoints){
        while (tempnode != null){
            allPoints.add(tempnode.p);
            if (tempnode.lb != null){
                allPoints=dive(tempnode.lb,allPoints);
            }
            if (tempnode.rt != null){
                allPoints = dive(tempnode.rt,allPoints);
            }
        }
        return allPoints;
    }

    // all points in the symbol table
    public Iterable<Point2D> points(){
        ArrayList<Point2D> allPoints = null;
        Node tempNode = topNode;
        allPoints = dive(tempNode,allPoints);
        return allPoints;
    }

    private ArrayList getpointsinrange(ArrayList aryL, Node n, RectHV rect){//get X points
        if (n == null) throw new IllegalArgumentException("invalid node");
        Node tempnode = n;
        if (n.xCord){//x level
            if (rect.xmax()<tempnode.p.x()){//if not inside x axis yet go left
                if (tempnode.lb == null){
                    return aryL;
                }
                tempnode = tempnode.lb;
                getpointsinrange(aryL,tempnode,rect);
            }
            else if (rect.xmin()>tempnode.p.x()){//if not inside x axis yet go right
                if (tempnode.rt == null){
                    return aryL;
                }
                tempnode = tempnode.rt;
                getpointsinrange(aryL,tempnode,rect);
            }
            else{//inside x axis
                //keep going & check children
                if (rect.contains(n.p)){
                    aryL.add(n.p);
                }
                if (tempnode.rt != null && tempnode.lb != null){
                    aryL=getpointsinrange(aryL,tempnode.lb,rect);
                    aryL=getpointsinrange(aryL,tempnode.rt,rect);
                }
                else if (tempnode.rt != null){
                    aryL=getpointsinrange(aryL,tempnode.rt,rect);
                }
                else if (tempnode.lb != null){
                    aryL=getpointsinrange(aryL,tempnode.lb,rect);
                }
            }
        }

        else if(!n.xCord){//y level
            if (rect.ymax()<tempnode.p.y()){//if above inside y axis yet go left
                if (tempnode.lb == null){
                    return aryL;
                }
                tempnode = tempnode.lb;
                getpointsinrange(aryL,tempnode,rect);
            }
            else if (rect.ymin()>tempnode.p.y()){//if not inside y axis yet go right
                if (tempnode.rt == null){
                    return aryL;
                }
                tempnode = tempnode.rt;
                getpointsinrange(aryL,tempnode,rect);
            }
            else {//inside y axis
                //keep going & check children
                if (rect.contains(n.p)){
                    aryL.add(n.p);
                }
                if (tempnode.rt != null && tempnode.lb != null){
                    aryL=getpointsinrange(aryL,tempnode.lb,rect);
                    aryL=getpointsinrange(aryL,tempnode.rt,rect);
                }
                else if (tempnode.rt != null){
                    aryL=getpointsinrange(aryL,tempnode.rt,rect);
                }
                else if (tempnode.lb != null){
                    aryL=getpointsinrange(aryL,tempnode.lb,rect);
                }
            }
        }
        return aryL;
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect){
        if (rect == null) throw new IllegalArgumentException("No Rectangle Found!");
        ArrayList inRange = new ArrayList();
        inRange = getpointsinrange(inRange,topNode,rect);
        return inRange;
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p){
        if (isEmpty() || p == null) throw new IllegalArgumentException("The Symbol Table is Empty!");
        return nearest(topNode, p, topNode.p, true);
    }

    private Point2D nearest(Node n, Point2D p, Point2D q, boolean xCord){
        Point2D nearest = q; // adjusted point for recursive comparison
        if (n == null) return nearest; // If we are at the last node, we are already at the nearest point
        if (n.p.distanceSquaredTo(p) < nearest.distanceSquaredTo(p)) nearest = n.p; // Set the nearest point to the node's point
        if (n.rect.distanceSquaredTo(p) < nearest.distanceSquaredTo(p)){ // If the nearest point is closer than the next point, we just return nearest. Otherwise, we need to find the nearest
            double comp; // comparable value
            if (n.xCord) comp = Double.compare(p.x(), n.p.x()); // x-coordinate
            else comp = Double.compare(p.y(), n.p.y()); // y-coordinate
            if (comp < 0){ // If the point is less than the point of the node, go left
                nearest = nearest(n.lb, p, nearest, xCord);
                nearest = nearest(n.rt, p, nearest, xCord);
            }
            else if (comp >= 0){ // If the point is greater than the point of the node, go right
                nearest = nearest(n.rt, p, nearest, xCord);
                nearest = nearest(n.lb, p, nearest, xCord);
            }
        }
        return nearest; // We found the nearest neighbor of point p
    }

    // unit testing (required)
    public static void main(String[] args){
        PointST s = new PointST();
        Object o = new Object();
        Point2D p = new Point2D(5,3);
        RectHV r = new RectHV(0,0,20,20);

        System.out.println(s.isEmpty());
        System.out.println(s.size());
        s.put(p,o);
        System.out.println(s.isEmpty());
        System.out.println(s.size());
        System.out.println(s.get(p));
        System.out.println(s.contains(p));
        System.out.println(s.points());
        System.out.println(s.range(r));
        System.out.println(s.nearest(p));
    }
}
