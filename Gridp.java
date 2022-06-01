// File: c:/ddc/Java/Knight/Gridp.java
// Date: Sun May 29 16:21:22 2022
// (C) OntoOO/ Dennis de Champeaux
import java.io.*;
import java.util.*;
/*
Y
         North
     West      East
         South  
0-0                  X
 */
// Two thread parallel version of Grid3
public class Gridp { 
    // static Date date = new Date();
    // static Random random = new Random(date.getTime());
    static Random random = new Random(777); // repeatable results
    // static final int lx = 6; 
    // static final int ly = 10;

    // static final int lx = 15;
    // static final int ly = lx;
    static final int lx = 100;   
    static final int ly = 2000;


    static GNP [][] grid = new GNP[lx][ly];
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

    public static void main(String[] args) {
	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GNP();
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
	// make barrier
	System.out.println("lx " + lx + " ly " + ly);
	System.out.println("midpointx " + midpointx + " midpointy " + midpointy);

	for ( int k = 0; k < lx; k++ )
	    if ( k != midpointx ) {
		grid[k][midpointy].north = null; 
		grid[k][midpointy].south = null; 
	    }
	grid[midpointx][midpointy].east = null;
	grid[midpointx][midpointy].west = null;

	for ( int i = 0; i < lx; i++ ) 
	    for ( int j = 0; j < ly; j++ ) {
		GNP gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }
	/*
	// check the new barrier
	for ( int k = 0; k < lx; k++ ) {
	    System.out.print(k + " ");
	    GNP gnj = grid[k][midpointy];
	    System.out.print("north " + gnj.north);
	    System.out.println(" south " + gnj.south);
	    System.out.print(" west " + gnj.west);
	    System.out.println(" east " + gnj.east);
	    System.out.println();
	}
	*/


	GNP startState = grid[0][0]; startState.fPathLng = 0; 
	startState.direction = 1; startState.pos = 1; startState.visited = "+";
	GNP goalState = grid[lx-1][ly-1]; goalState.bPathLng = 0;
	// GNP goalState = grid[0][ly-1]; goalState.bPathLng = 0;
	goalState.direction = -1; goalState.pos = 1; goalState.visited = "-";
	showg(startState); showg(goalState); 
	System.out.println();
	// System.exit(0);
	// boolean forwardRunDone = false;
	// boolean backwardRunDone = false;
	// 	Object os = new Object();
	//    synchronized ( os ) {
	Nodegp fNode = new Nodegp(true, startState); // forward thread
	Nodegp bNode = new Nodegp(false, goalState); // backward thread
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
	// /*
	/*
	show();
	showd();
	showv();
	// */

	System.out.println("Gridp.fCnt " + Gridp.fCnt + 
			   " Gridp.bCnt " + Gridp.bCnt);

    } // end main

    static public void show1(int i, int j) {
	// System.out.println("i j " + i + " " + j + " " + grid[i][j].id);
	int n = grid[i][j].pos;
	System.out.print( (n < 10 ? " " + n : n) + " ");
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

    static public void showg(GNP gn) {
	System.out.println(gn.id);
	System.out.println("direction " + gn.direction);
	int numMoves = gn.getNumMoves();
	System.out.println("numMoves " + numMoves);
	GNP [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    System.out.print(k + " ");
	    GNP gnk = moves[k];
	    System.out.print(gnk.id + " ");
	}
	System.out.println();
    }

    static void findMoves(GNP gn, int dr) {
	// set numMovesand puts in moves candidate moves
	gn.numMoves = 0;
	GNP gnn = gn.north;
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
	// scramble(gn.numMoves, gn.moves); // optional
    } // end findMoves
    // /*
    static void scramble(int numMoves, GNP [] moves) { 
	// change order of the candidate moves in a GN
	// System.out.println("scramble numMoves " + numMoves);
	for (int i = 0; i < numMoves; i++) {
	    int a = random.nextInt(numMoves);
	    int b = random.nextInt(numMoves);
	    // System.out.println("scramble a b " + a + " " + b);
	    GNP t = moves[a]; moves[a] = moves[b]; moves[b] = t;
	}
    } // end scramble()
    // */
} // end Gridp

class Nodegp { 
    protected GNP gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected GNP [] moves = new GNP[4];
    Nodegp(boolean b, GNP gnp) {
	Gridp.moveCnt++;
	gn = gnp;
	moveForward = b;
	// Choose one of the three
        // moveForward = true; // unidirectional search
	// moveForward = false; // unidirectional search
        // moveForward = (Gridp.fPathLng <= Gridp.bPathLng); // bidirectional search +
	// moveForward = (Gridp.fCnt <= Gridp.bCnt); // bidirectional search

	// findMoves sets numMoves and puts in moves candidate moves
	if ( moveForward ) Gridp.findMoves(gn, 1); else Gridp.findMoves(gn, -1);
	// System.out.println("Nodegp Gridp.moveCnt " + Gridp.moveCnt + " " + b);
    }
    public void move(GNP gn) {
	if ( Gridp.done ) return; // terminate
	if ( moveForward ) Gridp.depthf++; else Gridp.depthb++;
	boolean trace = false;
	numMoves = gn.getNumMoves();
	if (trace) System.out.println("\nGridp.moveCnt " + Gridp.moveCnt);
	if (trace) System.out.println("gn.id " + gn.id);
	if (trace) System.out.println("gn.pos " + gn.pos);
	if (trace) System.out.println("numMoves " + numMoves);
	if (trace) System.out.println("moveForward " + moveForward);
	// if (trace) System.out.println("Gridp.depth " + Gridp.depth);
	if (trace) System.out.println("Gridp.fCnt " + Gridp.fCnt + 
				      " Gridp.bCnt " + Gridp.bCnt);
	if (trace) { Gridp.show(); Gridp.showd(); }

	GNP [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    GNP gnk = moves[k];
	    if (trace) System.out.println("move f k gnk " + k + " " + gnk.id);
	    if (trace) System.out.println("gnk.id " + gnk.id);
	    if (trace) System.out.println("gnk.direction " + gnk.direction);
	    if (trace) System.out.println("gnk.pos " + gnk.pos);
	    if ( ( moveForward && -1 == gnk.direction ) ||
		 ( !moveForward && 1 == gnk.direction ) ) { // a solution
		/*
		  System.out.println();
		  System.out.println("***********SOLUTION");
		  System.out.println("Gridp.moveCnt " + Gridp.moveCnt);
		  System.out.println("f id " + gnk.id + " pos " + gnk.pos);
		  System.out.println("Gridp.fCnt + Gridp.bCnt " + 
		  (Gridp.fCnt + Gridp.bCnt));
		  Gridp.show(); 
		  Gridp.showd(); 
		  Gridp.showv(); 
		  // Gridp.closef();
		  System.exit(0);
		  // */
		synchronized(Gridp.os) { Gridp.solutionCnt++; }
		Gridp.done = true; // terminate when a solution is found
		/*
		  System.out.println("----------- moveCnt " + Gridp.moveCnt);
		  System.out.println("move f FOUND SOLUTION # " + Gridp.solutionCnt);
		  // print the paths
		  System.out.println("move other side: " + gnk.id);
		  GNP z = gnk.parent; 
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
		  // if ( 1000 <  Gridp.moveCnt ) System.exit(0);
		  // */
		return;
	    }
	    if ( 0 == gnk.direction ) { // can go there
	    synchronized(gnk) {
		// System.out.println("move f GO DEEPER " + gnk.id);
		if ( moveForward ) Gridp.fCnt++; else Gridp.bCnt++;
		gnk.pos = ( moveForward ? Gridp.fCnt : Gridp.bCnt );
		// gnk.fPathLng = gn.fPathLng+1;
		gnk.direction = ( moveForward ? 1 : -1 );
		gnk.parent = gn;
		gnk.visited = ( moveForward ? "+" : "-" );
		// Gridp.fPathLng++;
	    }
		(new Nodegp( moveForward, gnk)).move(gnk);
		    /* // do (NOT) restore
		    gnk.pos = 0;
		    gnk.parent = null;
		    gnk.direction = 0;
		    gnk.fPathLng = gn.fPathLng-1;
		    Gridp.fCnt--;
		    // */
		    // Gridp.fPathLng--;
		    continue;
		} else { 
		    // visited earlier
		    // System.out.println("move f visited earlier");
		    continue;
		    // System.exit(0);
		}
	    } 


	// System.out.println("move BACKTRACK gn depth " + Gridp.depth + 
	//			   " gn.id " + gn.id + " gn.pos " + gn.pos);
	if ( moveForward ) Gridp.depthf--; else Gridp.depthb--;
	// return;
    } // end move
} // end Nodeg

class GNP {
    protected int x = 0;
    protected int y = 0;
    protected String id = "";
    protected int pos = 0;
    protected GNP north = null; 
    protected GNP east = null; 
    protected GNP south = null; 
    protected GNP west = null;
    public void setNorth(GNP n) { north = n; }
    public void setEast(GNP n) { east = n; }
    public void setSouth(GNP n) { south = n; }
    public void setWest(GNP n) { west = n; }
    protected GNP parent = null;
    public void setParent(GNP n) { parent = n; }
    protected int direction = 0; // forward = 1; backward = -1
    protected int pathLength = -1;
    public void setPathLength(int x) { pathLength = x; }
    protected GNP [] moves = new GNP[4];
    public GNP [] getMoves() { return moves; }
    protected int numMoves = -1;
    public int getNumMoves() { return numMoves; }
    protected int nextMove = 0;
    public int getNextMove() { int n = nextMove; nextMove++; return n; }
    protected int fPathLng = 0;
    protected int bPathLng = 0;
    protected String visited = " ";

} // end GNP
