// File: c:/ddc/Java/Knight/Gridp4.java
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
// Two thread parallel version of 
// This is a two barrier version 
public class Gridp4 { 
    // static Date date = new Date();
    // static Random random = new Random(date.getTime());
    static Random random = new Random(777); // repeatable results

    // static final int lx = 6; 
    // static final int ly = 10;
    // static final int ly = 12;
    // static final int ly = 25;
    // static final int ly = 30;

    // static final int lx = 5; 
    static final int lx = 10;
    // static final int lx = 15;
    // static final int lx = 20;
    // static final int lx = 40;
    // static final int lx = 60;
    // static final int lx = 80;
    // static final int lx = 100;
    // static final int lx = 600;

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

    // static final int ly = 1600;
    // static final int ly = 1800;
    static final int ly = 2000;


    static GNP4 [][] grid = new GNP4[lx][ly];
    static int moveCnt = 0; 
    static Object os = new Object();
    static int solutionCnt = 0; 
    static int fCnt = 1;
    static int bCnt = 1;
    static int fPathLng = 0;
    static int bPathLng = 0;
    static int depthf = 0;
    static int depthb = 0;

    static int bx = ly/3;
    static int barrier1 = bx - 1;
    static int barrier2 = ly - bx;

    static boolean done = false; // for terminating when a solution is found

    // static Hashtable<GNP4,String> locations = new Hashtable<GNP4,String>();

    public static void main(String[] args) {
	System.out.println(" lx " + lx +
			   " ly " + ly + " barrier1 " + barrier1 + 
			   " barrier2 " + barrier2);
	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GNP4();
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

	// make barriers
	for ( int k = 0; k < lx-1; k++ ) {
	    grid[k][barrier1].south = null; 
	    grid[k][barrier1-1].north = null; 
	}
	// grid[lx-1][barrier1].east = null;

	for ( int k = 1; k < lx; k++ ) {
	    grid[k][barrier2].north = null; 
	    grid[k][barrier2+1].south = null; 
	}
	// grid[0][barrier2].west = null;

	/*
	// check the new barriers
	for ( int k = 0; k < lx; k++ ) {
	    System.out.print(k + " ");
	    GN4 gnj = grid[k][barrier2]; // or barrier1
	    System.out.print("north " + gnj.north);
	    System.out.println(" south " + gnj.south);
	    System.out.print(" west " + gnj.west);
	    System.out.println(" east " + gnj.east);
	    System.out.println();
	}
	// */

	for ( int i = 0; i < lx; i++ ) 
	    for ( int j = 0; j < ly; j++ ) {
		GNP4 gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }


	GNP4 startState = grid[0][0]; startState.fPathLng = 0; 
	startState.direction = 1; startState.pos = 1; startState.visited = "+";
	GNP4 goalState = grid[lx-1][ly-1]; goalState.bPathLng = 0;
	// GNP4 goalState = grid[0][ly-1]; goalState.bPathLng = 0;
	goalState.direction = -1; goalState.pos = 1; goalState.visited = "-";
	showg(startState); showg(goalState); 
	System.out.println();
	// System.exit(0);
	// boolean forwardRunDone = false;
	// boolean backwardRunDone = false;
	// 	Object os = new Object();
	//    synchronized ( os ) {
	Nodegp4 fNode = new Nodegp4(true, startState); // forward thread
	Nodegp4 bNode = new Nodegp4(false, goalState); // backward thread
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
	showd();
	showv();
	// */
	// show();
	// showd();

	System.out.println("Gridp4.fCnt " + Gridp4.fCnt + 
			   " Gridp4.bCnt " + Gridp4.bCnt);

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

    static public void showg(GNP4 gn) {
	System.out.println(gn.id);
	System.out.println("direction " + gn.direction);
	int numMoves = gn.getNumMoves();
	System.out.println("numMoves " + numMoves);
	GNP4 [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    System.out.print(k + " ");
	    GNP4 gnk = moves[k];
	    System.out.print(gnk.id + " ");
	}
	System.out.println();
    }

    static void findMoves(GNP4 gn, int dr) {
	// set numMovesand puts in moves candidate moves
	gn.numMoves = 0;
	GNP4 gnn = gn.north;
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
    static void scramble(int numMoves, GNP4 [] moves) { 
	// change order of the candidate moves in a GN
	// System.out.println("scramble numMoves " + numMoves);
	for (int i = 0; i < numMoves; i++) {
	    int a = random.nextInt(numMoves);
	    int b = random.nextInt(numMoves);
	    // System.out.println("scramble a b " + a + " " + b);
	    GNP4 t = moves[a]; moves[a] = moves[b]; moves[b] = t;
	}
    } // end scramble()
    // */
} // end Gridp4

class Nodegp4 { 
    protected GNP4 gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected GNP4 [] moves = new GNP4[4];
    Nodegp4(boolean b, GNP4 gnp) {
	Gridp4.moveCnt++;
	gn = gnp;
	moveForward = b;
	// Choose one of the three
        // moveForward = true; // unidirectional search
	// moveForward = false; // unidirectional search
        // moveForward = (Gridp4.fPathLng <= Gridp4.bPathLng); // bidirectional search +
	// moveForward = (Gridp4.fCnt <= Gridp4.bCnt); // bidirectional search

	// findMoves sets numMoves and puts in moves candidate moves
	// forward & backward eased
	if ( moveForward ) Gridp4.findMoves(gn, 1); else Gridp4.findMoves(gn, -1);
	// forward & backward hampered
	// if ( moveForward ) Gridp4.findMoves(gn, 2); else Gridp4.findMoves(gn, -2);

	// System.out.println("Nodegp4 Gridp4.moveCnt " + Gridp4.moveCnt + " " + b);
    }
    public void move(GNP4 gn) {
	if ( Gridp4.done ) return; // terminate
	/*
	if ( moveForward ) {
	    if ( Gridp4.midpointy <= gn.y ) return;
	} else {
	    if ( gn.y <= Gridp4.midpointy) return;
	}
	*/
	if ( moveForward ) Gridp4.depthf++; else Gridp4.depthb++;
	boolean trace = false;
	numMoves = gn.getNumMoves();
	if (trace) System.out.println("\nGridp4.moveCnt " + Gridp4.moveCnt);
	if (trace) System.out.println("gn.id " + gn.id);
	if (trace) System.out.println("gn.pos " + gn.pos);
	if (trace) System.out.println("numMoves " + numMoves);
	if (trace) System.out.println("moveForward " + moveForward);
	// if (trace) System.out.println("Gridp4.depth " + Gridp4.depth);
	if (trace) System.out.println("Gridp4.fCnt " + Gridp4.fCnt + 
				      " Gridp4.bCnt " + Gridp4.bCnt);
	if (trace) { Gridp4.show(); Gridp4.showd(); }
	// trace = false;

	GNP4 [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    GNP4 gnk = moves[k];
	    /*
	    // don't enter the other territory:
	    if ( moveForward ) {
		if ( Gridp4.midpointy +2 < gnk.y ) continue;
	    } else {
		if ( gnk.y < Gridp4.midpointy ) continue;
	    }
	    */
	    if (trace) System.out.println("move f k gnk " + k + " " + gnk.id);
	    if (trace) System.out.println("gnk.id " + gnk.id);
	    if (trace) System.out.println("gnk.direction " + gnk.direction);
	    if (trace) System.out.println("gnk.pos " + gnk.pos);
	    /* See Gridp5 for using the hash-table locations (not available in 
	       this version) for recognizing a solution.  It finds grid locs that 
	       were restored to empty after a backtrack (when restoration is 
	       activated). */
	    if ( ( moveForward && -1 == gnk.direction ) ||
		 ( !moveForward && 1 == gnk.direction ) ) { // a solution
		// System.out.println("SOLUTION " + 
		//		   (moveForward ? "forward" : "backward"));
		/*
		  System.out.println();
		  System.out.println("***********SOLUTION");
		  System.out.println("Gridp4.moveCnt " + Gridp4.moveCnt);
		  System.out.println("f id " + gnk.id + " pos " + gnk.pos);
		  System.out.println("Gridp4.fCnt + Gridp4.bCnt " + 
		  (Gridp4.fCnt + Gridp4.bCnt));
		  Gridp4.show(); 
		  Gridp4.showd(); 
		  // Gridp4.showv(); 
		  // Gridp4.closef();
		  System.exit(0);
		  // */
		synchronized(Gridp4.os) { Gridp4.solutionCnt++; }
		Gridp4.done = true; // terminate when a solution is found
		/*
		  System.out.println("----------- moveCnt " + Gridp4.moveCnt);
		  System.out.println("move f FOUND SOLUTION # " + Gridp4.solutionCnt);
		  // print the paths
		  System.out.println("move other side: " + gnk.id);
		  GNP4 z = gnk.parent; 
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
		  // if ( 1000 <  Gridp4.moveCnt ) System.exit(0);
		  // */
		return;
	    }
	    if ( 0 == gnk.direction ) { // can go there
		synchronized(gnk) {
		    // System.out.println("move f GO DEEPER " + gnk.id);
		    if ( moveForward ) Gridp4.fCnt++; else Gridp4.bCnt++;
		    gnk.pos = ( moveForward ? Gridp4.fCnt : Gridp4.bCnt );
		    // gnk.fPathLng = gn.fPathLng+1;
		    gnk.direction = ( moveForward ? 1 : -1 );
		    gnk.parent = gn;
		    gnk.visited = ( moveForward ? "+" : "-" );
		    // Gridp4.fPathLng++;
		}
		(new Nodegp4(moveForward, gnk)).move(gnk);
		if ( Gridp4.done ) return;
		// do (NOT) restore
		   gnk.pos = 0;
		   gnk.parent = null;
		   gnk.direction = 0;
		   gnk.fPathLng = gn.fPathLng-1;
		   Gridp4.fCnt--;
		   // */
		// Gridp4.fPathLng--;
		continue;
	    } else { 
		    // visited earlier
		    // System.out.println("move f visited earlier");
		continue;
		// System.exit(0);
	    }
	} 


	// System.out.println("move BACKTRACK gn depth " + Gridp4.depth + 
	//			   " gn.id " + gn.id + " gn.pos " + gn.pos);
	if ( moveForward ) Gridp4.depthf--; else Gridp4.depthb--;
	// return;
    } // end move
} // end Nodeg

class GNP4 {
    protected int x = 0;
    protected int y = 0;
    protected String id = "";
    protected int pos = 0;
    protected GNP4 north = null; 
    protected GNP4 east = null; 
    protected GNP4 south = null; 
    protected GNP4 west = null;
    public void setNorth(GNP4 n) { north = n; }
    public void setEast(GNP4 n) { east = n; }
    public void setSouth(GNP4 n) { south = n; }
    public void setWest(GNP4 n) { west = n; }
    protected GNP4 parent = null;
    public void setParent(GNP4 n) { parent = n; }
    protected int direction = 0; // forward = 1; backward = -1
    protected int pathLength = -1;
    public void setPathLength(int x) { pathLength = x; }
    protected GNP4 [] moves = new GNP4[4];
    public GNP4 [] getMoves() { return moves; }
    protected int numMoves = -1;
    public int getNumMoves() { return numMoves; }
    protected int nextMove = 0;
    public int getNextMove() { int n = nextMove; nextMove++; return n; }
    protected int fPathLng = 0;
    protected int bPathLng = 0;
    protected String visited = " ";

} // end GNP4
