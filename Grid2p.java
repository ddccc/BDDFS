// File: c:/ddc/Java/Knight/Grid2p.java
// Date: Sun May 29 16:21:22 2022 Sun Nov 13 09:44:35 2022
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
// Two thread parallel version of Grid2
public class Grid2p { 
    // static Date date = new Date();
    // static Random random = new Random(date.getTime());
    static Random random = new Random(777); // repeatable results

    // static final int lx = 6; 
    // static final int ly = 12;
    // static final int ly = 25;
    // // static final int ly = 30;

    // static final int lx = 5; 
    // static final int lx = 10;
    // static final int lx = 15;
    // static final int lx = 20;
    // static final int lx = 40;
    // static final int lx = 60;
    // static final int lx = 80;
    // static final int lx = 100;
    static final int lx = 600;

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
 
    // static final int ly = lx;
    // static final int ly = 1600;
    // static final int ly = 1800;
    static final int ly = 2000;


    static GN2p4 [][] grid = new GN2p4[lx][ly];
    static int moveCnt = 0; 
    static Object os = new Object();
    static Object os2 = new Object();
    static int solutionCnt = 0; 
    static int fCnt = 1;
    static int bCnt = 1;
    static int fPathLng = 0;
    static int bPathLng = 0;
    static int depthf = 0;
    static int depthb = 0;
    /*
    static int bx = ly/3;
    static int barrier1 = bx - 1;
    static int barrier2 = ly - bx;
    */
    static int midpointy = ly/2;

    static boolean done = false; // for terminating when a solution is found

    static Hashtable<GN2p4,String> locations = new Hashtable<GN2p4,String>();

    public static void main(String[] args) {
	System.out.println(" lx " + lx +
			   " ly " + ly ); 
	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GN2p4();
	for ( int i = 0; i < lx; i++ ) {
	    if ( 0 == i ) {
		for ( int j = 0; j < ly; j++ ) {
		    grid[0][j].setEast(grid[1][j]);
		}
		for ( int j = 0; j < ly-1; j++ ) {
		    grid[0][j].setNorth(grid[0][j+1]);
		}
		for ( int j = 1; j < ly; j++ ) {
		    grid[0][j].setSouth(grid[0][j-1]);
		}
	    } else { // 0 < i
		if ( i == lx-1 ) {
		    for ( int j = 0; j < ly; j++ ) 
			grid[i][j].setWest(grid[i-1][j]);
		    for ( int j = 0; j < ly-1; j++ ) 
			grid[i][j].setNorth(grid[i][j+1]);
		    for ( int j = 1; j < ly; j++ ) 
			grid[i][j].setSouth(grid[i][j-1]);
		} else { // 0 < i < lx-1
		    for ( int j = 0; j < ly; j++ ) {
			grid[i][j].setWest(grid[i-1][j]);
			grid[i][j].setEast(grid[i+1][j]);
		    }
		    for ( int j = 0; j < ly-1; j++ ) 
			grid[i][j].setNorth(grid[i][j+1]);
		    for ( int j = 1; j < ly; j++ ) 
			grid[i][j].setSouth(grid[i][j-1]);
		}	
	    }
	}

	for ( int i = 0; i < lx; i++ ) 
	    for ( int j = 0; j < ly; j++ ) {
		GN2p4 gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }

	GN2p4 startState = grid[0][0]; startState.fPathLng = 0; 
	startState.direction = 1; startState.pos = 1; startState.visited = "+";
	GN2p4 goalState = grid[lx-1][ly-1]; goalState.bPathLng = 0;
	// GNP4 goalState = grid[0][ly-1]; goalState.bPathLng = 0;
	goalState.direction = -1; goalState.pos = 1; goalState.visited = "-";
	// showg(startState); showg(goalState); 
	System.out.println();
	// System.exit(0);
	// boolean forwardRunDone = false;
	// boolean backwardRunDone = false;
	// 	Object os = new Object();
	//    synchronized ( os ) {
	Nodeg2p4 fNode = new Nodeg2p4(true, startState); // forward thread
	Nodeg2p4 bNode = new Nodeg2p4(false, goalState); // backward thread
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


	// show();
	/*
	// showd();
	// showv();
	System.out.println("Grid2p.fCnt " + Grid2p.fCnt + 
			   " Grid2p.bCnt " + Grid2p.bCnt);
	// */


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
    /*
    static public void showm() {
	for ( int j = 0; j < lx; j++ ) {
	    for ( int i = midpointy-1; i <= midpointy-1; i++ ) 
		showd1(j, i);
	    System.out.println();
	}
    } // end showm
    */
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

    static public void showg(GN2p4 gn) {
	System.out.println(gn.id);
	System.out.println("direction " + gn.direction);
	int numMoves = gn.getNumMoves();
	System.out.println("numMoves " + numMoves);
	GN2p4 [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    System.out.print(k + " ");
	    GN2p4 gnk = moves[k];
	    System.out.print(gnk.id + " ");
	}
	System.out.println();
    }

    static void findMoves(GN2p4 gn, int dr) {
	// set numMovesand puts in moves candidate moves
	gn.numMoves = 0;
	GN2p4 gnn = gn.north;
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
    static void scramble(int numMoves, GN2p4 [] moves) { 
	// change order of the candidate moves in a GN
	// System.out.println("scramble numMoves " + numMoves);
	for (int i = 0; i < numMoves; i++) {
	    int a = random.nextInt(numMoves);
	    int b = random.nextInt(numMoves);
	    // System.out.println("scramble a b " + a + " " + b);
	    GN2p4 t = moves[a]; moves[a] = moves[b]; moves[b] = t;
	}
    } // end scramble()
    // */
} // end Grid2p

class Nodeg2p4 { 
    protected GN2p4 gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected GN2p4 [] moves = new GN2p4[4];
    Nodeg2p4(boolean b, GN2p4 gnp) {
	Grid2p.moveCnt++;
	gn = gnp;
	moveForward = b;
	/*
	    System.out.println("\n-------------------------------------");
	    System.out.println("moveCnt " + Grid2p.moveCnt);
	    System.out.println("pos " + gn.pos + " fCnt " +  Grid2p.fCnt);
	    Grid2p.show();
	    if (50 < Grid2p.moveCnt ) System.exit(0);
	*/

	// Choose one of the three
        // moveForward = true; // unidirectional search
	// moveForward = false; // unidirectional search
        // moveForward = (Grid2p.fPathLng <= Grid2p.bPathLng); // bidirectional search +
	// moveForward = (Grid2p.fCnt <= Grid2p.bCnt); // bidirectional search

	// findMoves sets numMoves and puts in moves candidate moves
	// forward & backward eased
	// if ( moveForward ) Grid2p.findMoves(gn, 1); else Grid2p.findMoves(gn, -1);
	// forward & backward hampered
	// normal paths:
        if ( moveForward ) Grid2p.findMoves(gn, 2);  else Grid2p.findMoves(gn, -2);
    } // end Nodeg2p4

    public void move(GN2p4 gn) {
	if ( Grid2p.done ) return; // terminate
	if ( moveForward ) Grid2p.depthf++; else Grid2p.depthb++;
	// boolean trace = true;
	boolean trace = false;
	numMoves = gn.getNumMoves();
	if (trace) System.out.println("\n-------------------------------------");
	if (trace) System.out.println("\nGrid2p.moveCnt " + Grid2p.moveCnt);
	if (trace) System.out.println("gn.id " + gn.id);
	if (trace) System.out.println("gn.pos " + gn.pos);
	if (trace) System.out.println("numMoves " + numMoves);
	if (trace) System.out.println("moveForward " + moveForward);
	// if (trace) System.out.println("Grid2p.depth " + Grid2p.depth);
	if (trace) System.out.println("Grid2p.fCnt " + Grid2p.fCnt + 
				      " Grid2p.bCnt " + Grid2p.bCnt);
	if (trace) { Grid2p.show(); } // Grid2p.showd(); }
	// trace = false;

	GN2p4 [] moves = gn.getMoves();

	for (int k = 0; k < numMoves; k++) {
	    GN2p4 gnk = moves[k];

	    if (trace) System.out.println("move f k gnk " + k + " " + gnk.id);
	    if (trace) System.out.println("gnk.id " + gnk.id);
	    if (trace) System.out.println("gnk.direction " + gnk.direction);
	    if (trace) System.out.println("gnk.pos " + gnk.pos);
	    /* See Gridp5 for using the hash-table locations (available in this
	       version) for recognizing a solution.  It finds grid locs that 
	       were restored to empty after a backtrack (when restoration is 
	       activated). */
	
	    // synnchronized(gnk) {
	    if ( ( moveForward && -1 == gnk.direction ) ||
		 ( !moveForward && 1 == gnk.direction ) ) { // a solution
		// System.out.println("SOLUTION " + 
		//		   (moveForward ? "forward" : "backward"));
		/*
		  System.out.println();
		  System.out.println("***********SOLUTION");
		  System.out.println("Grid2p.moveCnt " + Grid2p.moveCnt);
		  System.out.println("f id " + gnk.id + " pos " + gnk.pos);
		  System.out.println("Grid2p.fCnt + Grid2p.bCnt " + 
		  (Grid2p.fCnt + Grid2p.bCnt));
		*/
		// Grid2p.show(); 
		// System.out.println();
		  /*
		  Grid2p.showd(); 
		  // Grid2p.showv(); 
		  // Grid2p.closef();
		  System.exit(0);
		  // */
		// synchronized(Grid2p.os) { Grid2p.solutionCnt++; }
		Grid2p.solutionCnt++; 
		Grid2p.done = true; // terminate when a solution is found
		/*
		  System.out.println("----------- moveCnt " + Grid2p.moveCnt);
		  System.out.println("move f FOUND SOLUTION # " + Grid2p.solutionCnt);
		  // print the paths
		  System.out.println("move other side: " + gnk.id);
		  GN2p4 z = gnk.parent; 
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
		  // if ( 1000 <  Grid2p.moveCnt ) System.exit(0);
		  // */
		return;
	    }
	    //    } // end synchronized
      boolean ok = false;
      // synchronized(gnk) {
	    if ( 0 == gnk.direction ) { // can go there
		ok = true;
		    // System.out.println("move f GO DEEPER " + gnk.id);
		    // fCnt & bCnt is different in this version !!
		    if ( moveForward ) {
			Grid2p.fCnt++; Grid2p.locations.put(gnk, "+");
		    } else {
			Grid2p.bCnt++; Grid2p.locations.put(gnk, "-");
		    }
		    gnk.pos = ( moveForward ? Grid2p.fCnt : Grid2p.bCnt );
		    gnk.direction = ( moveForward ? 1 : -1 );
		    gnk.parent = gn;
		    gnk.visited = ( moveForward ? "+" : "-" );
		    // Grid2p.fPathLng++;
	    }
	    // } // end synchronized
      if (ok) {
		// System.out.println("before recursion");
		// Grid2p.show();
		// System.exit(0);
      
		Nodeg2p4 n4 = (new Nodeg2p4(moveForward, gnk));
		n4.move(gnk);
		// (new Nodeg2p4(moveForward, gnk)).move(gnk);
		if ( Grid2p.done ) return;
		/* // do (NOT) restore
		Grid2p.locations.remove(gnk);
		gnk.pos = 0;
		gnk.parent = null;
		gnk.direction = 0;
		if ( moveForward ) {
		    gnk.fPathLng = gn.fPathLng-1;
		    Grid2p.fCnt--;
		}
		else {
		    gnk.bPathLng = gn.bPathLng-1;
		    Grid2p.bCnt--;
		}
		   // */
		// Grid2p.fPathLng--;
		continue;
      }

		    // visited earlier
		    // System.out.println("move f visited earlier");
      // continue;
		// System.exit(0);
	
	} // end for-loop

	// System.out.println("move BACKTRACK gn depth " + Grid2p.depth + 
	//			   " gn.id " + gn.id + " gn.pos " + gn.pos);
	if ( moveForward ) Grid2p.depthf--; else Grid2p.depthb--;
	// return;
    } // end move
} // end Nodeg2p4

class GN2p4 {
    protected int x = 0;
    protected int y = 0;
    protected String id = "";
    protected int pos = 0;
    protected GN2p4 north = null; 
    protected GN2p4 east = null; 
    protected GN2p4 south = null; 
    protected GN2p4 west = null;
    public void setNorth(GN2p4 n) { north = n; }
    public void setEast(GN2p4 n) { east = n; }
    public void setSouth(GN2p4 n) { south = n; }
    public void setWest(GN2p4 n) { west = n; }
    protected GN2p4 parent = null;
    public void setParent(GN2p4 n) { parent = n; }
    protected int direction = 0; // forward = 1; backward = -1
    protected int pathLength = -1;
    public void setPathLength(int x) { pathLength = x; }
    protected GN2p4 [] moves = new GN2p4[4];
    public GN2p4 [] getMoves() { return moves; }
    protected int numMoves = -1;
    public int getNumMoves() { return numMoves; }
    protected int nextMove = 0;
    public int getNextMove() { int n = nextMove; nextMove++; return n; }
    protected int fPathLng = 0;
    protected int bPathLng = 0;
    protected String visited = " ";

} // end GN2p4
