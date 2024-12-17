// File: c:/ddc/Java/Knight/Gridpp.java
// Date: Sun Fri Dec 06 11:07:24 2024
// (C) OntoOO/ Dennis de Champeaux

/*
  This code is parametrized regarding::
     Number of barriers
     The sizes of the grid
     Search direction
     Trace output generated
     Successor operations ordering scrambled or not
     Hampering or easing the successor operations/nodes
        (hampering is adding moves in the wrong direction)
     Restore or not the search space after a recursive call
*/



import java.io.*;
import java.util.*;

/*  Two stack variant 
     This version uses the GridB version of the algorithm.

*/

/*
Y
         North
     West      East
         South  
0-0                  X
 */
public class Gridpp {
    // static Date date = new Date();
    // static Random random = new Random(date.getTime());
    static Random random = new Random(777); // repeatable results
    // static Random random = new Random(771); // repeatable results
    // static final int lx = 5;
    // static final int lx = 6; 
    // static final int lx = 10; 
    // static final int lx = 15; 
    static final int lx = 20; // paper
    // static final int lx = 25; 
    // static final int lx = 30; 
    // static final int lx = 35; 
    // static final int lx = 40; 
    // static final int lx = 50; 
    // static final int lx = 80; 
    // static final int lx = 100; 
    // static final int lx = 120; 
    // static final int lx = 140; 
    // static final int lx = 200; 

    // static final int ly = lx;

    // static final int ly = 12;
    // static final int ly = 20;
    // static final int ly = 25;
    // static final int ly = 30;

    // static final int ly = 80;
    // static final int ly = 100;
    // static final int ly = 150;
    // static final int ly = 200;
    // static final int ly = 250;
    // static final int ly = 300;
    // static final int ly = 400;
    // static final int ly = 800;
    // static final int ly = 1600;
    // static final int ly = 1800;
    // static final int ly = 2000;
    // static final int ly = 3200;
    // static final int ly = 6400;
    // static final int ly = 12800;
    
    // static final int ly = 1800; 
    // static final int ly = 2000; 
    // static final int ly = 2100; 

    static final int ly = 20000; // paper

    static GNpp [][] grid = new GNpp[lx][ly];
    static int moveCnt = 0; 
    static int solutionCnt = 0; 
    static int fCnt = 1;
    static int bCnt = 1;
    //    static int fPathLng = 0;
    //    static int bPathLng = 0;
    // static int fdepth = 0;
    // static int bdepth = 0;

    /*
    static int bx = ly/3;
    static int barrier1 = bx - 1;
    static int barrier2 = ly - bx;
    // */
    static int numBarriers = 2; // 4, 6, ..
    // static int numBarriers = 4; // 4, 6, ..
    // static int numBarriers = 6; // 4, 6, ..
    // static int numBarriers = 8; // 4, 6, ..
    // static int numBarriers = 10; // 4, 6, .. // paper
    static int barrierDistance = ly/(numBarriers+1);

    // select one
    static boolean bidirection = true;
    // static boolean bidirection = false;

    static Hashtable<GNpp,String> locations = new Hashtable<GNpp,String>();

    static boolean donef = false; 
    static boolean doneb = false;

    static Stack<SItempp> fStack = new Stack<SItempp>();
    static Stack<SItempp> bStack = new Stack<SItempp>();
    static boolean flip = true;


    public static void main(String[] args) {
	if ( !Gridpp.bidirection ) doneb = true;
	System.out.println("bidirection " + bidirection +
			   " lx " + lx +
			   " ly " + ly); //  + " barrier1 " + barrier1 + 
	                                 // " barrier2 " + barrier2);
	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GNpp();
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

	for ( int i = 0; i < lx; i++ ) 
	    for ( int j = 0; j < ly; j++ ) {
		GNpp gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }
	/*
	// check new barriers
	int barrierX = 2* barrierDistance;
	for ( int k = 0; k < lx; k++ ) {
	    System.out.print(k + " ");
	    GNpp gnj = grid[k][barrierX]; // or ...
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
	// showd(); showv(); 
	// System.exit(0);



	GNpp startState = grid[0][0]; startState.fPathLng = 0; 
	startState.direction = 1; startState.pos = 1; startState.visited = "+";
	Gridpp.locations.put(startState, "+");
	GNpp goalState = grid[lx-1][ly-1]; goalState.bPathLng = 0;
	// GNpp goalState = grid[0][ly-1]; goalState.bPathLng = 0;
	goalState.direction = -1; goalState.pos = 1; goalState.visited = "-";
	Gridpp.locations.put(goalState, "-");
	showg(startState); showg(goalState); 
	System.out.println("---");
        // showd(); showv(); 
	// System.exit(0);
	// create 2 Sitems
	SItempp startItem = new SItempp(startState, true);
	Gridpp.fStack.push(startItem);
	SItempp goalItem = new SItempp(goalState, false);
	Gridpp.bStack.push(goalItem);
	Thread forward = new Thread(new Runnable() {
		public void run() {
		    move(fStack, true); } } );
	Thread backward = new Thread(new Runnable() {
		public void run() {
		    move(bStack, false); } } );
	    
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
	// show();
	System.out.println();
	show();  // pos numbers	
	showd(); // f & b
	showv(); // + & -
	// */
	System.out.println("\nGridpp.fCnt " + Gridpp.fCnt + 
		   " Gridpp.bCnt " + Gridpp.bCnt);


        } // end main

    static public void move(Stack<SItempp> stack, boolean moveForward) {
	// boolean moveForward = item.moveForward;
	while ( true) {
	    if (stack.empty() ) { // synchronize
		if (moveForward) { Gridpp.donef = true; return; }
		else { Gridpp.doneb = true; return; }
	    }
	    if ( Gridpp.donef || Gridpp.doneb ) return;

	    Gridpp.moveCnt++; // synchronize
	    // System.out.println("moveCnt " + Gridpp.moveCnt);
	    // System.out.println("moveCnt " + Gridpp.moveCnt);
	    // System.out.println("move direction " + Gridpp.flip);
	    // Gridpp.show(); Gridpp.showd();  // Gridpp.showv();


	    if ( moveForward ) { // forward
		SItempp fItem = Gridpp.fStack.peek();
		GNpp gn = fItem.gn;
		int fNumMoves = fItem.numMoves;
		int fNumExplored = fItem.numExplored;
		// fItem.show();
		boolean found = false;
		GNpp gnk = null;
		while ( fNumExplored < fNumMoves ) {
		    gnk = fItem.moves[fNumExplored];  
		    fItem.numExplored++; fNumExplored++;
		    if ( 1 == gnk.direction ) continue;
		    found = true;
		    break;
		}
		if ( !found ) {
		    /* Do (NOT) restore:
		    Gridpp.fCnt--;
		    gn.pos = 0;
		    gn.parent = null;
		    gn.direction = 0;
		    gn.visited = " ";
		    // */
		    Gridpp.fStack.pop();
		    if ( Gridpp.fStack.isEmpty() ) { donef = true; continue; }
		    // fItem = Gridpp.fStack.peek();
		    // fItem.backTrack = gn;
		    // Gridpp.showd(); 
		    continue;
		}
		String direction = Gridpp.locations.get(gnk);
		if ( null == direction ) {
		    // go down and continue
		    Gridpp.locations.put(gnk, "+");
		    Gridpp.fCnt++;
		    gnk.pos = Gridpp.fCnt;
		    // gnk.fPathLng = gn.fPathLng+1;
		    gnk.direction = 1;
		    gnk.parent = gn;
		    gnk.visited = "+";
		    // Gridpp.fPathLng++;
		    SItempp sgnk = new SItempp(gnk, true);
		    Gridpp.fStack.push(sgnk);
		    continue;
		}
		if ( direction.equals("+") ) continue; // visited earlier
		// direction.equals("-")
		// a solution
		// Gridpp.show(); 
		// Gridpp.showd(); 

		Gridpp.solutionCnt++;
		// for termination
		{ Gridpp.donef = true;  Gridpp.doneb = true; } 
		/*
		show();
		showd();
		showv();
		//*/
		Gridpp.fStack.pop(); // backtrack
		if ( Gridpp.fStack.isEmpty() ) { donef = true; continue; }
		continue;
	    } else { // backward
		// if ( Gridpp.bStack.isEmpty() ) { doneb = true; continue; }
		SItempp bItem = Gridpp.bStack.peek();
		GNpp gn = bItem.gn;
		int bNumMoves = bItem.numMoves;
		int bNumExplored = bItem.numExplored;
		// bItem.show();
		boolean found = false;
		GNpp gnk = null;
		while ( bNumExplored < bNumMoves ) {
		    gnk = bItem.moves[bNumExplored];  
		    bItem.numExplored++; bNumExplored++;
		    if ( -1 == gnk.direction ) continue;
		    found = true;
		    break;
		}
		if ( !found ) {
		    /* Do (NOT) restore:
		    Gridpp.bCnt--;
		    gn.pos = 0;
		    gn.parent = null;
		    gn.direction = 0;
		    gn.visited = " ";
		    // */
		    Gridpp.bStack.pop();
		    if ( Gridpp.bStack.isEmpty() ) { doneb = true; continue; }
		    // bItem = Gridpp.bStack.peek();
		    // bItem.backTrack = gn;
		    // Gridpp.showd(); 
		    continue;
		}
		String direction = Gridpp.locations.get(gnk);
		if ( null == direction ) {
		    // go down and continue
		    Gridpp.locations.put(gnk, "-");
		    Gridpp.bCnt++;
		    gnk.pos = Gridpp.bCnt;
		    // gnk.bPathLng = gn.bPathLng+1;
		    gnk.direction = -1;
		    gnk.parent = gn;
		    gnk.visited = "-";
		    // Gridpp.fPathLng++;
		    SItempp sgnk = new SItempp(gnk, false);
		    Gridpp.bStack.push(sgnk);
		    continue;
		}
		if ( direction.equals("-") ) continue; // visited earlier
		// direction.equals("+")
		// a solution
		// Gridpp.show(); 
		// Gridpp.showd(); 
		/*
		System.out.println("gnk.x " + gnk.x + " .y " + gnk.y + " pos " +
				   gnk.pos);
		System.out.println("fCnt " + Gridpp.fCnt + " bCnt " + Gridpp.bCnt);
		*/
		Gridpp.solutionCnt++;
		{ donef = true; doneb = true; } // for termination
		/*
		show();
		showd();
		showv();
		//*/
		Gridpp.bStack.pop(); // backtrack
		if ( Gridpp.bStack.isEmpty() ) { donef = true; continue; }
		continue;
	    }
	} // end while

    } // end move

    static public void show1(int i, int j) {
	// System.out.println("i j " + i + " " + j + " " + grid[i][j].id);
	int n = grid[i][j].pos;
	// System.out.print( (n < 10 ? " " + n : n) + " ");
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
	    // for ( int i = midpointy-1; i <= midpointy-1; i++ ) 
	    //         showd1(j, i);
	    System.out.println();
	}} // end showm
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

    static public void showg(GNpp gn) {
	System.out.println(gn.id);
	System.out.println("direction " + gn.direction);
	int numMoves = gn.getNumMoves();
	System.out.println("numMoves " + numMoves);
	GNpp [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    System.out.print(k + " ");
	    GNpp gnk = moves[k];
	    System.out.print(gnk.id + " ");
	}
	System.out.println();
    }

    static void findMoves(GNpp gn, int dr) {
	// set numMovesand puts in moves candidate moves
	gn.numMoves = 0;
	GNpp gnn = gn.north;
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
    static void scramble(int numMoves, GNpp [] moves) { 
	// change order of the candidate moves in a GN
	// System.out.println("scramble numMoves " + numMoves);
	for (int i = 0; i < numMoves; i++) {
	    int a = random.nextInt(numMoves);
	    int b = random.nextInt(numMoves);
	    // System.out.println("scramble a b " + a + " " + b);
	    GNpp t = moves[a]; moves[a] = moves[b]; moves[b] = t;
	}
    } // end scramble()
    // */
} // end Gridpp

class SItempp {
    protected GNpp gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected GNpp [] moves = new GNpp[4];
    protected int numExplored = 0;
    // protected boolean backTrack = false;
    SItempp(GNpp gnx, boolean b) {
	gn = gnx; moveForward = b;
	// sets moves & nummoves 
	// forward and backward easied:
	// if ( moveForward ) Gridpp.findMoves(gn, 1); else Gridpp.findMoves(gn, -1);
	// forward hampered and backward easied:
	// if ( moveForward ) Gridpp.findMoves(gn, 2); else Gridpp.findMoves(gn, -1);
	// forward and backward hampered:
	if ( moveForward ) Gridpp.findMoves(gn, 2); else Gridpp.findMoves(gn, -2);
	numMoves = gn.numMoves;
	moves = gn.moves;
    }
    /*
    public void show() { 
	System.out.println("SItempp moveForward " +  moveForward);
	System.out.println("numMoves " + numMoves + " numExplored " + numExplored);
    }
    */
} // end SItempp

class GNpp {
    protected int x = 0;
    protected int y = 0;
    protected String id = "";
    protected int pos = 0;
    protected GNpp north = null; 
    protected GNpp east = null; 
    protected GNpp south = null; 
    protected GNpp west = null;
    public void setNorth(GNpp n) { north = n; }
    public void setEast(GNpp n) { east = n; }
    public void setSouth(GNpp n) { south = n; }
    public void setWest(GNpp n) { west = n; }
    protected GNpp parent = null;
    public void setParent(GNpp n) { parent = n; }
    protected int direction = 0; // forward = 1; backward = -1
    protected int pathLength = -1;
    public void setPathLength(int x) { pathLength = x; }
    protected GNpp [] moves = new GNpp[4];
    public GNpp [] getMoves() { return moves; }
    protected int numMoves = -1;
    public int getNumMoves() { return numMoves; }
    protected int nextMove = 0;
    public int getNextMove() { int n = nextMove; nextMove++; return n; }
    protected int fPathLng = 0;
    protected int bPathLng = 0;
    protected String visited = " ";

} // end GNpp
