// File: c:/ddc/Java/Knight/Gridp5.java
// Date: Sun Nov 17 16:22:21 2024
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
public class Gridp5 { 
    // static Date date = new Date();
    // static Random random = new Random(date.getTime());
    static Random random = new Random(777); // repeatable results
    // static final int lx = 5; 
    // static final int ly = 10;

    // static final int lx = 6; 
    // static final int ly = 12;
    // static final int ly = 25;
    // static final int ly = 30;
    static final int ly = 20000;
    // static final int ly = 200; // -
    // static final int ly = 100; // -
    // static final int ly = 70; // +
    // static final int ly = 50; // +

    // static final int lx = 15;
    static final int lx = 20;
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
    // static final int ly = 2000;

    // static final int ly = 20000;
    static int midpointy = ly/2;

    static GNP5 [][] grid = new GNP5[lx][ly];
    static int moveCnt = 0; 
    static Object os = new Object();
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
    // static int numBarriers = 2; // 4, 6, ..
    // static int numBarriers = 4; // 4, 6, ..
    // static int numBarriers = 6; // 4, 6, ..
    // static int numBarriers = 8; // 4, 6, ..
    static int numBarriers = 10; // 4, 6, ..
    static int barrierDistance = ly/(numBarriers+1);

    static boolean done = false; // for terminating when a solution is found

    static long startTime = 0; // System.currentTimeMillis();
    static long endTimeG = 0;

    static Hashtable<GNP5,String> locations = new Hashtable<GNP5,String>();

    public static void main(String[] args) {
	System.out.println(" lx " + lx +
			   " ly " + ly); // + " barrier1 " + barrier1 + 
	                                 //" barrier2 " + barrier2);
	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GNP5();
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

	// /*
	// make barriers
	int yb = barrierDistance;
	for ( int n = 0; n < numBarriers/2; n++ ) {
	    for ( int k = 0; k < lx-1; k++ ) {
		grid[k][yb].south = null; 
		grid[k][yb-1].north = null; 
	    }
	    yb = yb + barrierDistance;
	    for ( int k = 1; k < lx; k++ ) {
		grid[k][yb].north = null; 
		grid[k][yb+1].south = null; 
	    }
	}
	// */

	for ( int i = 0; i < lx; i++ ) 
	    for ( int j = 0; j < ly; j++ ) {
		GNP5 gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }
	/*
	// check new barriers
	int barrierX = 2* barrierDistance;
	for ( int k = 0; k < lx; k++ ) {
	    System.out.print(k + " ");
	    GNB5 gnj = grid[k][barrierX]; // or ...
	    System.out.print("gnj " + gnj.id);
	    // System.out.print(" north " + gnj.north.id);
	    System.out.print(" north " + (null== gnj.north ? null : gnj.north.id));
	    // System.out.println(" south " + gnj.south.id);
	    System.out.println(" south " + (null== gnj.south ? null : gnj.south.id));
	    // System.out.print(" west " + gnj.west.id);
	    System.out.print(" west " + (null== gnj.west ? null : gnj.west.id));
	    // System.out.println(" east " + gnj.east.id);
	    System.out.println(" east " + (null== gnj.east ? null : gnj.east.id));
	    System.out.println();
	}
	// */

	GNP5 startState = grid[0][0]; startState.fPathLng = 0; 
	startState.direction = 1; startState.pos = 1; startState.visited = "+";
	Gridp5.locations.put(startState, "+");
	GNP5 goalState = grid[lx-1][ly-1]; goalState.bPathLng = 0;
	// GNP5 goalState = grid[0][ly-1]; goalState.bPathLng = 0;
	goalState.direction = -1; goalState.pos = 1; goalState.visited = "-";
	Gridp5.locations.put(goalState, "-");
	// showg(startState); showg(goalState); 
	System.out.println();
	// System.exit(0);
	// boolean forwardRunDone = false;
	// boolean backwardRunDone = false;
	// 	Object os = new Object();
	//    synchronized ( os ) {
	Nodegp5 fNode = new Nodegp5(true, startState); // forward thread
	Nodegp5 bNode = new Nodegp5(false, goalState); // backward thread
	// Thread currThread = Thread.currentThread();
	Thread forward = new Thread(new Runnable() {
		public void run() { 
		    fNode.move(fNode.gn); 
		} } );
	Thread backward = new Thread(new Runnable() {
		public void run() { 
		    bNode.move(bNode.gn); 
		} } );
	startTime = System.currentTimeMillis();
	// backward.start(); // to change the order
	forward.start();
	backward.start();
	try { // wait for them to terminate
	    forward.join();
	    backward.join();
	} catch (InterruptedException e) {}
	
	long endTime = System.currentTimeMillis();
	System.out.println("\ntiming " + (endTime-startTime));
	System.out.println("timingG " + (endTimeG-startTime));
	System.out.println("solutionCnt " + solutionCnt);
	System.out.println("moveCnt " + moveCnt);

	/*
	show();
	showd();
	showv();
	// */
	// show();
	// showd();

	System.out.println("Gridp5.fCnt " + Gridp5.fCnt + 
			   " Gridp5.bCnt " + Gridp5.bCnt);

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

    static public void showg(GNP5 gn) {
	System.out.println(gn.id);
	System.out.println("direction " + gn.direction);
	int numMoves = gn.getNumMoves();
	System.out.println("numMoves " + numMoves);
	GNP5 [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    System.out.print(k + " ");
	    GNP5 gnk = moves[k];
	    System.out.print(gnk.id + " ");
	}
	System.out.println();
    }

    static void findMoves(GNP5 gn, int dr) {
	// set numMovesand puts in moves candidate moves
	gn.numMoves = 0;
	GNP5 gnn = gn.north;
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
    static void scramble(int numMoves, GNP5 [] moves) { 
	// change order of the candidate moves in a GN
	// System.out.println("scramble numMoves " + numMoves);
	for (int i = 0; i < numMoves; i++) {
	    int a = random.nextInt(numMoves);
	    int b = random.nextInt(numMoves);
	    // System.out.println("scramble a b " + a + " " + b);
	    GNP5 t = moves[a]; moves[a] = moves[b]; moves[b] = t;
	}
    } // end scramble()
    // */
} // end Gridp5

class Nodegp5 { 
    protected GNP5 gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected GNP5 [] moves = new GNP5[4];
    Nodegp5(boolean b, GNP5 gnp) {
	Gridp5.moveCnt++;
	gn = gnp;
	moveForward = b;
	// Choose one of the three || IGNORE - the b-parameter decides it
        // moveForward = true; // unidirectional search
	// moveForward = false; // unidirectional search
        // moveForward = (Gridp5.fPathLng <= Gridp5.bPathLng); // bidirectional search +
	// moveForward = (Gridp5.fCnt <= Gridp5.bCnt); // bidirectional search

	// findMoves sets numMoves and puts in moves candidate moves
	// forward & backward eased
	// if ( moveForward ) Gridp5.findMoves(gn, 1); else Gridp5.findMoves(gn, -1);
	// forward & backward hampered
	if ( moveForward ) Gridp5.findMoves(gn, 2); else Gridp5.findMoves(gn, -2);

	// System.out.println("Nodegp5 Gridp5.moveCnt " + Gridp5.moveCnt + " " + b);
    }
    public void move(GNP5 gn) {
	if ( Gridp5.done ) return; // terminate
	/*
	if ( moveForward ) {
	    if ( Gridp5.midpointy <= gn.y ) return;
	} else {
	    if ( gn.y <= Gridp5.midpointy) return;
	}
	// */
	if ( moveForward ) Gridp5.depthf++; else Gridp5.depthb++;
	boolean trace = false;
	numMoves = gn.getNumMoves();
	if (trace) System.out.println("\nGridp5.moveCnt " + Gridp5.moveCnt);
	if (trace) System.out.println("gn.id " + gn.id);
	if (trace) System.out.println("gn.pos " + gn.pos);
	if (trace) System.out.println("numMoves " + numMoves);
	if (trace) System.out.println("moveForward " + moveForward);
	// if (trace) System.out.println("Gridp5.depth " + Gridp5.depth);
	if (trace) System.out.println("Gridp5.fCnt " + Gridp5.fCnt + 
				      " Gridp5.bCnt " + Gridp5.bCnt);
	if (trace) { Gridp5.show(); Gridp5.showd(); }
	// trace = false;

	GNP5 [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    GNP5 gnk = moves[k];
	    String direction = Gridp5.locations.get(gnk);
	    if ( null != direction ) {
		if  ( moveForward ) {
		    if  (  direction.equals("+") ) continue; }
		else if  (  direction.equals("-") ) continue;
				System.out.println("SOLUTION " + 
				   (moveForward ? "forward" : "backward"));
		Gridp5.endTimeG = System.currentTimeMillis();
		/*
		  System.out.println();
		  System.out.println("***********SOLUTION");
		  System.out.println("Gridp5.moveCnt " + Gridp5.moveCnt);
		  System.out.println("gn id " + gn.id + " pos " + gn.pos);
		  System.out.println("gnk id " + gnk.id + " pos " + gnk.pos);
		  System.out.println("fCnt " + Gridp5.fCnt + " bCnt " + Gridp5.bCnt); 
		  // System.out.println("Gridp5.fCnt + Gridp5.bCnt " + 
		  //	     (Gridp5.fCnt + Gridp5.bCnt));
		  // Gridp5.show(); 
		  // Gridp5.showd(); 
		  // Gridp5.showv(); 
		  // Gridp5.closef();
		  System.exit(0);
		  // */
		synchronized(Gridp5.os) { Gridp5.solutionCnt++; }
		Gridp5.done = true; // terminate when a solution is found
		/*
		  System.out.println("----------- moveCnt " + Gridp5.moveCnt);
		  System.out.println("move f FOUND SOLUTION # " + Gridp5.solutionCnt);
		  // print the paths
		  System.out.println("move other side: " + gnk.id);
		  GNP5 z = gnk.parent; 
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
		  // if ( 1000 <  Gridp5.moveCnt ) System.exit(0);
		  // */
		return;
	    }
	    // direction = null
	    if ( block(gnk, moveForward) ) continue;
	    /*
	    // don't enter the other territory:
	    if ( moveForward ) {
		if ( Gridp5.midpointy +2 < gnk.y ) continue;
	    } else {
		if ( gnk.y < Gridp5.midpointy ) continue;
	    }
	    */
	    if (trace) System.out.println("move f k gnk " + k + " " + gnk.id);
	    if (trace) System.out.println("gnk.id " + gnk.id);
	    if (trace) System.out.println("gnk.direction " + gnk.direction);
	    if (trace) System.out.println("gnk.pos " + gnk.pos);

	    // can go there
	    synchronized(gnk) {
		// System.out.println("move f GO DEEPER " + gnk.id);
		if ( moveForward ) Gridp5.fCnt++; else Gridp5.bCnt++;
		if ( moveForward ) Gridp5.locations.put(gnk, "+");
		else Gridp5.locations.put(gnk, "-");
		gnk.pos = ( moveForward ? Gridp5.fCnt : Gridp5.bCnt );
		// gnk.fPathLng = gn.fPathLng+1;
		gnk.direction = ( moveForward ? 1 : -1 );
		gnk.parent = gn;
		gnk.visited = ( moveForward ? "+" : "-" );
		// Gridp5.fPathLng++;
	    }
	    (new Nodegp5(moveForward, gnk)).move(gnk);
	    if ( Gridp5.done ) return;
	    // do (NOT) restore
	    gnk.pos = 0;
	    gnk.parent = null;
	    gnk.direction = 0;
	    if ( moveForward ) {
		gnk.fPathLng = gn.fPathLng-1;
		Gridp5.fCnt--;
	    } else {
		gnk.bPathLng = gn.bPathLng-1;
		Gridp5.bCnt--;
	    }
	    // */
	}


	// System.out.println("move BACKTRACK gn depth " + Gridp5.depth + 
	//			   " gn.id " + gn.id + " gn.pos " + gn.pos);
	if ( moveForward ) Gridp5.depthf--; else Gridp5.depthb--;
	// return;
    } // end move
    // Check whether to block a move to gnk
    boolean block(GNP5 gnk, boolean forward) {
	GNP5 [] gnkMoves = gnk.getMoves();
	int numMoves = gnk.numMoves;
	for (int k = 0; k < numMoves; k++) {
	    GNP5 gnbk = gnkMoves[k];
	    if ( 0 == gnbk.pos ) return false; // found a move
	    if ( (forward && gnbk.direction == -1) ||
		 (!forward && gnbk.direction == 1)) return false; // found a solution
	}
	return true; // block the move
    } // end block

} // end Nodeg

class GNP5 {
    protected int x = 0;
    protected int y = 0;
    protected String id = "";
    protected int pos = 0;
    protected GNP5 north = null; 
    protected GNP5 east = null; 
    protected GNP5 south = null; 
    protected GNP5 west = null;
    public void setNorth(GNP5 n) { north = n; }
    public void setEast(GNP5 n) { east = n; }
    public void setSouth(GNP5 n) { south = n; }
    public void setWest(GNP5 n) { west = n; }
    protected GNP5 parent = null;
    public void setParent(GNP5 n) { parent = n; }
    protected int direction = 0; // forward = 1; backward = -1
    protected int pathLength = -1;
    public void setPathLength(int x) { pathLength = x; }
    protected GNP5 [] moves = new GNP5[4];
    public GNP5 [] getMoves() { return moves; }
    protected int numMoves = -1;
    public int getNumMoves() { return numMoves; }
    protected int nextMove = 0;
    public int getNextMove() { int n = nextMove; nextMove++; return n; }
    protected int fPathLng = 0;
    protected int bPathLng = 0;
    protected String visited = " ";

} // end GNP5
