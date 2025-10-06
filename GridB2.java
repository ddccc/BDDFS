// File: c:/ddc/Java/Knight/GridB2.java
// Date: Sun Aug 24 11:37:21 2025
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
/*  Two stack variant - sequential
    This version uses the Grid4 & GridBt version of the algorithm.

*/
/*
Y
         North
     West      East
         South  
0-0                  X
 */
public class GridB2 {
    // static Date date = new Date();
    // static Random random = new Random(date.getTime());
    static Random random = new Random(777); // repeatable results
    // static Random random = new Random(771); // repeatable results
    // static final int lx = 5;
    // static final int lx = 6; 
    // static final int lx = 10; 
    // static final int lx = 15; 
    // static final int lx = 20; 
    // static final int lx = 25; 
    // static final int lx = 30; 
    // static final int lx = 35; 
    // static final int lx = 40; 
    // static final int lx = 50; 
    // static final int lx = 60; 
    // static final int lx = 80; 
    // static final int lx = 100; 
    // static final int lx = 120; 
    // static final int lx = 140; 
    // static final int lx = 200; 
    static final int lx = 600; 

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
    static final int ly = 2000;
    // static final int ly = 3200;
    // static final int ly = 6400;
    // static final int ly = 12800;
    
    // static final int ly = 1800; 
    // static final int ly = 2000; 
    // static final int ly = 2100; // 


    static GNB2 [][] grid = new GNB2[lx][ly];
    static int moveCnt = 0; 
    static int solutionCnt = 0; 
    static int fCnt = 1;
    static int bCnt = 1;
    //    static int fPathLng = 0;
    //    static int bPathLng = 0;
    // static int fdepth = 0;
    // static int bdepth = 0;

    // select one
    static boolean bidirection = true;
    // static boolean bidirection = false;

    static Hashtable<GNB2,String> locations = new Hashtable<GNB2,String>();

    static boolean donef = false; 
    static boolean doneb = ( !bidirection ? true : false ); 

    static Stack<SItemB2> fStack = new Stack<SItemB2>();
    static Stack<SItemB2> bStack = new Stack<SItemB2>();
    static boolean flip = true;


    public static void main(String[] args) {
	if ( !GridB2.bidirection ) doneb = true;
	System.out.println("bidirection " + bidirection +
			   " lx " + lx +
			   " ly " + ly); //  + " barrier1 " + barrier1 + 
	                                 // " barrier2 " + barrier2);

	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GNB2();
	// <<<
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
	// >>>
	for ( int i = 0; i < lx; i++ ) 
	    for ( int j = 0; j < ly; j++ ) {
		GNB2 gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }

	GNB2 startState = grid[0][0]; startState.fPathLng = 0; 
	startState.direction = 1; startState.pos = 1; startState.visited = "+";
	GridB2.locations.put(startState, "+");
	GNB2 goalState = grid[lx-1][ly-1]; goalState.bPathLng = 0;
	// GNB2 goalState = grid[0][ly-1]; goalState.bPathLng = 0;
	goalState.direction = -1; goalState.pos = 1; goalState.visited = "-";
	GridB2.locations.put(goalState, "-");
	// showg(startState); showg(goalState); 
	System.out.println("---");
	// showd(); showv(); 
	// System.exit(0);

	// create 2 Sitems
	SItemB2 startItem = new SItemB2(startState, true);
	GridB2.fStack.push(startItem);
	SItemB2 goalItem = new SItemB2(goalState, false);
	GridB2.bStack.push(goalItem);

	long startTime = System.currentTimeMillis();

	while ( !donef || !doneb ) { // execution loop
	    GridB2.moveCnt++;
	    if ( GridB2.bidirection ) // select bi or uni direction
	     	   GridB2.flip = !GridB2.flip;
	    boolean moveForward = GridB2.flip;
	    // System.out.println("moveCnt " + GridB2.moveCnt);
	    // System.out.println("move direction " + GridB2.flip);
	    // GridB2.show(); GridB2.showd();  // GridB2.showv();

	    if ( GridB2.flip ) { // forward
		if ( GridB2.fStack.isEmpty() ) { donef = true; continue; }
		SItemB2 fItem = GridB2.fStack.peek();
		GNB2 gn = fItem.gn;
		int fNumMoves = fItem.numMoves;
		int fNumExplored = fItem.numExplored;
		// fItem.show();
		GNB2 gnk = null;
		while ( fNumExplored < fNumMoves ) {
		    GNB2 gnkx = fItem.moves[fNumExplored];  
		    fItem.numExplored++; fNumExplored++;
		    // if ( block(gnkx, moveForward) ) { continue; }
		    if ( 1 == gnkx.direction ) continue;
		    gnk = gnkx;
		    break;
		}
		if ( null == gnk ) {
		    /* Do (NOT) restore:
		    GridB2.fCnt--;
		    gn.pos = 0;
		    gn.parent = null;
		    gn.direction = 0;
		    gn.visited = " ";
		    // */
		    GridB2.fStack.pop();
		    if ( GridB2.fStack.isEmpty() ) { donef = true; continue; }
		    // fItem = GridB2.fStack.peek();
		    // fItem.backTrack = gn;
		    // GridB2.showd(); 
		    continue;
		}
		String direction = GridB2.locations.get(gnk);
		if ( null == direction ) {
		    if ( block(gnk, true) ) {
			continue;
		    }
		    // go down and continue
		    GridB2.locations.put(gnk, "+");
		    GridB2.fCnt++;
		    gnk.pos = GridB2.fCnt;
		    // gnk.fPathLng = gn.fPathLng+1;
		    gnk.direction = 1;
		    gnk.parent = gn;
		    gnk.visited = "+";
		    // GridB2.fPathLng++;
		    SItemB2 sgnk = new SItemB2(gnk, true);
		    GridB2.fStack.push(sgnk);
		    continue;
		}
		if ( direction.equals("+") ) continue; // visited earlier
		// direction.equals("-")
		// a solution
		// GridB2.show(); 
		// GridB2.showd(); 
		GridB2.solutionCnt++;
		{ donef = true; doneb = true; } // for termination
		/*
		  show here 		      
		*/
		GridB2.fStack.pop(); // backtrack
		if ( GridB2.fStack.isEmpty() ) { donef = true; continue; }
		continue;
	    } else { // backward
		if ( GridB2.bStack.isEmpty() ) { doneb = true; continue; }
		SItemB2 bItem = GridB2.bStack.peek();
		GNB2 gn = bItem.gn;
		int bNumMoves = bItem.numMoves;
		int bNumExplored = bItem.numExplored;
		// bItem.show();
		GNB2 gnk = null;
		while ( bNumExplored < bNumMoves ) {
		    GNB2 gnkx = bItem.moves[bNumExplored];  
		    bItem.numExplored++; bNumExplored++;
		    // if ( block(gnkx, moveForward) ) { continue; }
		    if ( -1 == gnkx.direction ) continue;
		    gnk = gnkx;
		    break;
		}
		if ( null == gnk ) {
		    /* Do (NOT) restore:
		    GridB2.bCnt--;
		    gn.pos = 0;
		    gn.parent = null;
		    gn.direction = 0;
		    gn.visited = " ";
		    // */
		    GridB2.bStack.pop();
		    if ( GridB2.bStack.isEmpty() ) { doneb = true; continue; }
		    // bItem = GridB2.bStack.peek();
		    // bItem.backTrack = gn;
		    // GridB2.showd(); 
		    continue;
		}
		String direction = GridB2.locations.get(gnk);
		if ( null == direction ) {
		    if ( block(gnk, false) ) {
			continue;
		    }
		    // go down and continue
		    GridB2.locations.put(gnk, "-");
		    GridB2.bCnt++;
		    gnk.pos = GridB2.bCnt;
		    // gnk.bPathLng = gn.bPathLng+1;
		    gnk.direction = -1;
		    gnk.parent = gn;
		    gnk.visited = "-";
		    // GridB2.fPathLng++;
		    SItemB2 sgnk = new SItemB2(gnk, false);
		    GridB2.bStack.push(sgnk);
		    continue;
		}
		if ( direction.equals("-") ) continue; // visited earlier
		// direction.equals("+")
		// a solution
		// GridB2.show(); 
		// GridB2.showd(); 
		GridB2.solutionCnt++;
		{ donef = true; doneb = true; } // for termination
		/*
		  show here 		      
		*/
		GridB2.bStack.pop(); // backtrack
		if ( GridB2.bStack.isEmpty() ) { donef = true; continue; }
		continue;
	    }
	} // end while


	long endTime = System.currentTimeMillis();
	System.out.println("\ntiming " + (endTime-startTime));
	System.out.println("solutionCnt " + solutionCnt);
	System.out.println("moveCnt " + moveCnt);
	/*
	System.out.println();
	show();  // pos numbers	
	showd(); // f & b
	showv(); // + & -
	// System.out.println("\nGridB2.fCnt " + GridB2.fCnt + 
	//	   " GridB2.bCnt " + GridB2.bCnt);
	// */

    } // end main

    static boolean block(GNB2 gnk, boolean forward) {
	GNB2 [] gnkMoves = gnk.getMoves();
	int numMoves = gnk.numMoves;
	for (int k = 0; k < numMoves; k++) {
	    GNB2 gnbk = gnkMoves[k];
	    if ( 0 == gnbk.pos ) return false; // found a move
	    if ( (forward && gnbk.direction == -1) ||
		 (!forward && gnbk.direction == 1)) return false; // found a solution
	}
	return true; // block the move
    } // end block

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

    static public void showg(GNB2 gn) {
	System.out.println(gn.id);
	System.out.println("direction " + gn.direction);
	int numMoves = gn.getNumMoves();
	System.out.println("numMoves " + numMoves);
	GNB2 [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    System.out.print(k + " ");
	    GNB2 gnk = moves[k];
	    System.out.print(gnk.id + " ");
	}
	System.out.println();
    }

    static void findMoves(GNB2 gn, int dr) {
	// set numMovesand puts in moves candidate moves
	gn.numMoves = 0;
	GNB2 gnn = gn.north;
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
    static void scramble(int numMoves, GNB2 [] moves) { 
	// change order of the candidate moves in a GN
	// System.out.println("scramble numMoves " + numMoves);
	for (int i = 0; i < numMoves; i++) {
	    int a = random.nextInt(numMoves);
	    int b = random.nextInt(numMoves);
	    // System.out.println("scramble a b " + a + " " + b);
	    GNB2 t = moves[a]; moves[a] = moves[b]; moves[b] = t;
	}
    } // end scramble()
    // */
} // end GridB2

class SItemB2 {
    protected GNB2 gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected GNB2 [] moves = new GNB2[4];
    protected int numExplored = 0;
    // protected boolean backTrack = false;
    SItemB2(GNB2 gnx, boolean b) {
	gn = gnx; moveForward = b;
	// sets moves & nummoves 
	// forward and backward easied:
	// if ( moveForward ) GridB2.findMoves(gn, 1); else GridB2.findMoves(gn, -1);
	// forward hampered and backward easied:
	// if ( moveForward ) GridB2.findMoves(gn, 2); else GridB2.findMoves(gn, -1);
	// forward and backward hampered:
	if ( moveForward ) GridB2.findMoves(gn, 2); else GridB2.findMoves(gn, -2);
	numMoves = gn.numMoves;
	moves = gn.moves;
    }
    /*
    public void show() { 
	System.out.println("SItemB2 moveForward " +  moveForward);
	System.out.println("numMoves " + numMoves + " numExplored " + numExplored);
    }
    */
} // end SItemB2

class GNB2 {
    protected int x = 0;
    protected int y = 0;
    protected String id = "";
    protected int pos = 0;
    protected GNB2 north = null; 
    protected GNB2 east = null; 
    protected GNB2 south = null; 
    protected GNB2 west = null;
    public void setNorth(GNB2 n) { north = n; }
    public void setEast(GNB2 n) { east = n; }
    public void setSouth(GNB2 n) { south = n; }
    public void setWest(GNB2 n) { west = n; }
    protected GNB2 parent = null;
    public void setParent(GNB2 n) { parent = n; }
    protected int direction = 0; // forward = 1; backward = -1
    protected int pathLength = -1;
    public void setPathLength(int x) { pathLength = x; }
    protected GNB2 [] moves = new GNB2[4];
    public GNB2 [] getMoves() { return moves; }
    protected int numMoves = -1;
    public int getNumMoves() { return numMoves; }
    protected int nextMove = 0;
    public int getNextMove() { int n = nextMove; nextMove++; return n; }
    protected int fPathLng = 0;
    protected int bPathLng = 0;
    protected String visited = " ";

} // end GNB2
