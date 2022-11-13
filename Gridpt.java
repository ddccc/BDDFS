// File: c:/ddc/Java/Knight/Gridpt.java
// Date: Sat Nov 05 13:00:36 2022 Sun Nov 13 09:31:16 2022
// (C) OntoOO/ Dennis de Champeaux
import java.io.*;
import java.util.*;

/*
  This code is parametrized regarding::
     The sizes of the grid
     Search direction
     Trace output generated
     Successor operations ordering scrambled or not
     Hampering or easing the successor operations/nodes
        (hampering is adding moves in the wrong direction)
     Restore or not the search space after a recursive call
 */
/*
Y
         North
     West      East
         South  
0-0                  X
 */
// Two thread parallel version of Grid3
public class Gridpt { 
    // static Date date = new Date();
    // static Random random = new Random(date.getTime());
    static Random random = new Random(777); // repeatable results
    // static final int lx = 6; 
    // static final int ly = 12;

    // static final int lx = 6; 
    // static final int ly = 30;

    // static final int lx = 5; 
    // static final int lx = 10;
    // static final int lx = 15;
    // static final int lx = 20;
    // static final int lx = 40;
    // static final int lx = 60;
    // static final int lx = 80;    
    // static final int lx = 100;    
    static final int lx = 600;    


    // static final int ly = lx;

    // static final int lx = 100;   
    // static final int lx = 200;
    // static final int lx = 300;
    // static final int lx = 400;
    // static final int lx = 500;
    // static final int lx = 600;
    // static final int lx = 700;
    // static final int lx = 800;
    // static final int lx = 900;
    // static final int lx = 1000;
    static final int ly = 2000;

    // static final int lx = 400;   
    // static final int ly = 400;


    static GNPt [][] grid = new GNPt[lx][ly];
    static int moveCnt = 0; 
    static Object os = new Object();
    static int solutionCnt = 0; 
    static int fCnt = 1;
    static int bCnt = 1;
    static int fPathLng = 0;
    static int bPathLng = 0;
    static int depthf = 0;
    static int depthb = 0;

    static int midpointx = lx/2;
    static int midpointy = ly/2;

    static boolean done = false; // for terminating when a solution is found

    // static Hashtable<GNPt,String> locations = new Hashtable<GNPt,String>();

    public static void main(String[] args) {
	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GNPt();
	// Rectangular grid on a torus
	for ( int i = 0; i < lx; i++ ) {
	    if ( 0 == i ) {
		for ( int j = 0; j < ly; j++ ) {
		    grid[0][j].setEast(grid[1][j]);
		}
		for ( int j = 0; j < ly; j++ ) {
		    grid[0][j].setWest(grid[lx-1][j]);
		}
		for ( int j = 0; j < ly-1; j++ ) {
		    grid[0][j].setNorth(grid[0][j+1]);
		}
		grid[0][ly-1].setNorth(grid[0][0]);
		for ( int j = 1; j < ly; j++ ) {
		    grid[0][j].setSouth(grid[0][j-1]);
		}
		grid[0][0].setSouth(grid[0][ly-1]);
	    } else { // 0 < i
		if ( i == lx-1 ) {
		    for ( int j = 0; j < ly; j++ )
			grid[i][j].setEast(grid[0][j]);
		    for ( int j = 0; j < ly; j++ ) 
			grid[i][j].setWest(grid[i-1][j]);
		    for ( int j = 0; j < ly-1; j++ ) 
			grid[i][j].setNorth(grid[i][j+1]);
		    grid[i][ly-1].setNorth(grid[i][0]);
		    for ( int j = 1; j < ly; j++ ) 
			grid[i][j].setSouth(grid[i][j-1]);
		    grid[i][0].setSouth(grid[i][ly-1]);

		} else { // 0 < i < lx-1
		    for ( int j = 0; j < ly; j++ ) {
			grid[i][j].setWest(grid[i-1][j]);
			grid[i][j].setEast(grid[i+1][j]);
		    }
		    for ( int j = 0; j < ly-1; j++ ) 
			grid[i][j].setNorth(grid[i][j+1]);
		    for ( int j = 1; j < ly; j++ ) 
			grid[i][j].setSouth(grid[i][j-1]);
		    grid[i][ly-1].setNorth(grid[i][0]);
		    grid[i][0].setSouth(grid[i][ly-1]);
		}	
	    }
	}

	for ( int i = 0; i < lx; i++ ) 
	    for ( int j = 0; j < ly; j++ ) {
		GNPt gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }

	GNPt startState = grid[0][0]; startState.fPathLng = 0; 
	startState.direction = 1; startState.pos = 1; startState.visited = "+";
	GNPt goalState = grid[lx-1][ly-1]; goalState.bPathLng = 0;
	// GNPt goalState = grid[0][ly-1]; goalState.bPathLng = 0;
	goalState.direction = -1; goalState.pos = 1; goalState.visited = "-";
	showg(startState); showg(goalState); 
	System.out.println();
	// System.exit(0);
	// boolean forwardRunDone = false;
	// boolean backwardRunDone = false;
	// 	Object os = new Object();
	//    synchronized ( os ) {
	Nodegpt fNode = new Nodegpt(true, startState); // forward thread
	Nodegpt bNode = new Nodegpt(false, goalState); // backward thread
	// Thread currThread = Thread.currentThread();
	Thread forward = new Thread(new Runnable() {
		public void run() { 
		    fNode.move(fNode.gn); 
		} } );
	Thread backward = new Thread(new Runnable() {
		public void run() { 
		    bNode.move(bNode.gn); 
		} } );
	long startTime = System.currentTimeMillis();
	// backward.start(); // to change the order
	forward.start();
	backward.start();
	try { // wait for them to terminate
	    forward.join();
	    backward.join();
	} catch (InterruptedException e) {}
	
	long endTime = System.currentTimeMillis();
	System.out.println("\ntiming " + (endTime-startTime));
	System.out.println("solutionCnt " + solutionCnt);
	System.out.println("moveCnt " + moveCnt);
	/*
	show();	
	System.out.println();
	showd();
	System.out.println();
	showv();
	// */
	// show();
	// showd();

	System.out.println("Gridpt.fCnt " + Gridpt.fCnt + 
			   " Gridpt.bCnt " + Gridpt.bCnt);

    } // end main

    static public void show1(int i, int j) {
	// System.out.println("i j " + i + " " + j + " " + grid[i][j].id);
	int n = grid[i][j].pos;
	if ( 0 == n ) System.out.print("   "); else
	    System.out.print( (n < 10 ? "  " + n : " " + n));
    } // show1
    static public void show() {
	for ( int j = 0; j < lx; j++ ) {
	    for ( int i = 0; i < ly; i++ ) show1(j, i);
	    System.out.println();
	}
    } // end show

    static public void showm() {
	for ( int j = 0; j < lx; j++ ) {
	    for ( int i = midpointy-1; i <= midpointy-1; i++ ) 
		showd1(j, i);
	    System.out.println();
	}
    } // end showm
    static public void showd1(int i, int j) {
	// System.out.println("i j " + i + " " + j + " " + grid[i][j].id);
	int n = grid[i][j].direction;
	if ( 0 == n ) System.out.print("   ");
	else if ( 1 == n ) System.out.print("  f"); 
	else System.out.print("  b"); 
    } // showd1
    static public void showd() {
	for ( int j = 0; j < lx; j++ ) {
	    for ( int i = 0; i < ly; i++ ) showd1(j, i);
	    System.out.println();
	}
    } // end showd

    static public void showv1(int i, int j) {

	String s = grid[i][j].visited;
	// System.out.print("  " + s);
	// /*
	if ( s.equals(" ") ) System.out.print("   ");
	else if ( s.equals("+") ) System.out.print("  +"); 
	else System.out.print("  -"); 
	     // */
    } // showv1
    static public void showv() {
	for ( int j = 0; j < lx; j++ ) {
	    for ( int i = 0; i < ly; i++ ) showv1(j, i);
	    System.out.println();
	}
    } // end showv

    static public void showg(GNPt gn) {
	System.out.println(gn.id);
	System.out.println("direction " + gn.direction);
	int numMoves = gn.getNumMoves();
	System.out.println("numMoves " + numMoves);
	GNPt [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    System.out.print(k + " ");
	    GNPt gnk = moves[k];
	    System.out.print(gnk.id + " ");
	}
	System.out.println();
    }

    static void findMoves(GNPt gn, int dr) {
	// set numMovesand puts in moves candidate moves
	gn.numMoves = 0;
	GNPt gnn = gn.north;
	if ( null != gnn && dr != -1 ) { 
	    gn.moves[gn.numMoves] = gnn; gn.numMoves++; }
	gnn = gn.south;
	if ( null != gnn && dr != 1 ) { 
	    gn.moves[gn.numMoves] = gnn; gn.numMoves++; }
	gnn = gn.east;
	if ( null != gnn ) { 
	    gn.moves[gn.numMoves] = gnn; gn.numMoves++; }
	gnn = gn.west;
	if ( null != gnn ) { 
	    gn.moves[gn.numMoves] = gnn; gn.numMoves++; }
	scramble(gn.numMoves, gn.moves); // optional
    } // end findMoves
    // /*
    static void scramble(int numMoves, GNPt [] moves) { 
	// change order of the candidate moves in a GN
	// System.out.println("scramble numMoves " + numMoves);
	for (int i = 0; i < numMoves; i++) {
	    int a = random.nextInt(numMoves);
	    int b = random.nextInt(numMoves);
	    // System.out.println("scramble a b " + a + " " + b);
	    GNPt t = moves[a]; moves[a] = moves[b]; moves[b] = t;
	}
    } // end scramble()
    // */
} // end Gridpt

class Nodegpt { 
    protected GNPt gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected GNPt [] moves = new GNPt[4];
    Nodegpt(boolean b, GNPt gnp) {
	Gridpt.moveCnt++;
	gn = gnp;
	moveForward = b;
	// Choose one of the three
        // moveForward = true; // unidirectional search
	// moveForward = false; // unidirectional search
        // moveForward = (Gridpt.fPathLng <= Gridpt.bPathLng); // bidirectional search +
	// moveForward = (Gridpt.fCnt <= Gridpt.bCnt); // bidirectional search

	// findMoves sets numMoves and puts in moves candidate moves
	// forward & backward eased
	if ( moveForward ) Gridpt.findMoves(gn, 1); else Gridpt.findMoves(gn, -1);
	// forward & backward hampered
	// if ( moveForward ) Gridpt.findMoves(gn, 2); else Gridpt.findMoves(gn, -2);

	// System.out.println("Nodegpt Gridpt.moveCnt " + Gridpt.moveCnt + " " + b);
    }
    public void move(GNPt gn) {
	if ( Gridpt.done ) return; // terminate
	/*
	if ( moveForward ) {
	    if ( Gridpt.midpointy <= gn.y ) return;
	} else {
	    if ( gn.y <= Gridpt.midpointy) return;
	}
	*/
	if ( moveForward ) Gridpt.depthf++; else Gridpt.depthb++;
	boolean trace = false;
	numMoves = gn.getNumMoves();
	if (trace) System.out.println("\nGridpt.moveCnt " + Gridpt.moveCnt);
	if (trace) System.out.println("gn.id " + gn.id);
	if (trace) System.out.println("gn.pos " + gn.pos);
	if (trace) System.out.println("numMoves " + numMoves);
	if (trace) System.out.println("moveForward " + moveForward);
	// if (trace) System.out.println("Gridpt.depth " + Gridpt.depth);
	if (trace) System.out.println("Gridpt.fCnt " + Gridpt.fCnt + 
				      " Gridpt.bCnt " + Gridpt.bCnt);
	if (trace) { Gridpt.show(); Gridpt.showd(); }
	// trace = false;

	GNPt [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    GNPt gnk = moves[k];
	    /*
	    // don't enter the other territory:
	    if ( moveForward ) {
		if ( Gridpt.midpointy +2 < gnk.y ) continue;
	    } else {
		if ( gnk.y < Gridpt.midpointy ) continue;
	    }
	    */
	    if (trace) System.out.println("move f k gnk " + k + " " + gnk.id);
	    if (trace) System.out.println("gnk.id " + gnk.id);
	    if (trace) System.out.println("gnk.direction " + gnk.direction);
	    if (trace) System.out.println("gnk.pos " + gnk.pos);
	    if ( ( moveForward && -1 == gnk.direction ) ||
		 ( !moveForward && 1 == gnk.direction ) ) { // a solution
		// System.out.println("SOLUTION " + 
		//		   (moveForward ? "forward" : "backward"));
		/*
		  System.out.println();
		  System.out.println("***********SOLUTION");
		  System.out.println("Gridpt.moveCnt " + Gridpt.moveCnt);
		  System.out.println("f id " + gnk.id + " pos " + gnk.pos);
		  System.out.println("Gridpt.fCnt + Gridpt.bCnt " + 
		  (Gridpt.fCnt + Gridpt.bCnt));
		  Gridpt.show(); 
		  Gridpt.showd(); 
		  // Gridpt.showv(); 
		  // Gridpt.closef();
		  System.exit(0);
		  // */
		synchronized(Gridpt.os) { Gridpt.solutionCnt++; }
		// Gridpt.done = true; // terminate when a solution is found
		/*
		  System.out.println("----------- moveCnt " + Gridpt.moveCnt);
		  System.out.println("move f FOUND SOLUTION # " + Gridpt.solutionCnt);
		  // print the paths
		  System.out.println("move other side: " + gnk.id);
		  GNPt z = gnk.parent; 
		  while (true) {
		  if ( null == z ) break;
		  System.out.println("move b z  " + z.id);
		  z = z.parent;
		  }
		  z = gn; 
		  System.out.println("move this side: " + z.id);
		  while (true) {
		  if ( null == z ) break;
		  System.out.println("move f z " + z.id);
		  z = z.parent;
		  }
		  System.exit(0);
		  // if ( 1000 <  Gridpt.moveCnt ) System.exit(0);
		  // */
		return;
	    }
	    if ( 0 == gnk.direction ) { // can go there
		synchronized(gnk) {
		    // System.out.println("move f GO DEEPER " + gnk.id);
		    if ( moveForward ) Gridpt.fCnt++; else Gridpt.bCnt++;
		    gnk.pos = ( moveForward ? Gridpt.fCnt : Gridpt.bCnt );
		    // gnk.fPathLng = gn.fPathLng+1;
		    gnk.direction = ( moveForward ? 1 : -1 );
		    gnk.parent = gn;
		    gnk.visited = ( moveForward ? "+" : "-" );
		    // Gridpt.fPathLng++;
		}
		(new Nodegpt( moveForward, gnk)).move(gnk);
		if ( Gridpt.done ) return;
		/* // do (NOT) restore
		   gnk.pos = 0;
		   gnk.parent = null;
		   gnk.direction = 0;
		   gnk.fPathLng = gn.fPathLng-1;
		   Gridpt.fCnt--;
		   // */
		// Gridpt.fPathLng--;
		continue;
	    } else { 
		    // visited earlier
		    // System.out.println("move f visited earlier");
		continue;
		// System.exit(0);
	    }
	} 


	// System.out.println("move BACKTRACK gn depth " + Gridpt.depth + 
	//			   " gn.id " + gn.id + " gn.pos " + gn.pos);
	if ( moveForward ) Gridpt.depthf--; else Gridpt.depthb--;
	// return;
    } // end move
} // end Nodeg

class GNPt {
    protected int x = 0;
    protected int y = 0;
    protected String id = "";
    protected int pos = 0;
    protected GNPt north = null; 
    protected GNPt east = null; 
    protected GNPt south = null; 
    protected GNPt west = null;
    public void setNorth(GNPt n) { north = n; }
    public void setEast(GNPt n) { east = n; }
    public void setSouth(GNPt n) { south = n; }
    public void setWest(GNPt n) { west = n; }
    protected GNPt parent = null;
    public void setParent(GNPt n) { parent = n; }
    protected int direction = 0; // forward = 1; backward = -1
    protected int pathLength = -1;
    public void setPathLength(int x) { pathLength = x; }
    protected GNPt [] moves = new GNPt[4];
    public GNPt [] getMoves() { return moves; }
    protected int numMoves = -1;
    public int getNumMoves() { return numMoves; }
    protected int nextMove = 0;
    public int getNextMove() { int n = nextMove; nextMove++; return n; }
    protected int fPathLng = 0;
    protected int bPathLng = 0;
    protected String visited = " ";

} // end GNPt
