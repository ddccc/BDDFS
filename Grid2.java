// File: c:/ddc/Java/Knight/Grid2.java
// Date: Fri Apr 22 19:12:42 2022, Sat Nov 12 20:56:31 2022
// (C) OntoOO/ Dennis de Champeaux

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

import java.io.*;
import java.util.*;
/*
Y
         North
     West      East
         South  
0-0                  X
 */
public class Grid2 {
    // static Date date = new Date();
    // static Random random = new Random(date.getTime());
    static Random random = new Random(777); // repeatable results
    // static final int lx = 5; 
    static final int lx = 6; 
    // static final int lx = 10;
    // static final int lx = 15; 
    // static final int lx = 20; 
    // static final int lx = 30; 
    // static final int lx = 40; // -
    // static final int lx = 50; // +
    // static final int lx = 60; // +
    // static final int lx = 70; // -
    // static final int lx = 80; // +
    // static final int lx = 90; 
    // static final int lx = 100; 
    // static final int lx = 200; 
    // static final int lx = 2000; 

    // static final int ly = 10; 
    // static final int ly = 12; 
    // static final int ly = 24; // +
    // static final int ly = 25; // +  - 6x25
    // static final int ly = 30; // +
    // static final int ly = 35; // - 10x35 - 6x35
    // static final int ly = 40; // +
    // static final int ly = 45; 
    // static final int ly = 50; 
    // static final int ly = 55; 
    // static final int ly = 60; 
    // static final int ly = 65; 
    // static final int ly = 70; 
    // static final int ly = 75; 
    // static final int ly = 80; 
    // static final int ly = 85; 
    // static final int ly = 90; 
    // static final int ly = 95; 
    static final int ly = 100; 
    // static final int ly = 500; 
    // static final int ly = 1800; 
    // static final int ly = 2000;
 
    static GN2 [][] grid = new GN2[lx][ly];
    static int moveCnt = 0; 
    static int solutionCnt = 0; 
    static int fCnt = 1;
    static int bCnt = 1;
    static int fPathLng = 0;
    static int bPathLng = 0;
    static int depth = 0;

    static int midpointy = ly/2;

    // select one
    static boolean bidirection = true;
    // static boolean bidirection = false;

    static Hashtable<GN2,String> locations = new Hashtable<GN2,String>();

    static boolean flip = true;
    static boolean done = false;

    public static void main(String[] args) {
	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GN2();
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
		GN2 gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }


	GN2 startState = grid[0][0]; startState.fPathLng = 0; startState.pos = 1;
	startState.direction = 1;  startState.visited = "+";
	locations.put(startState, "+");

	GN2 goalState = grid[lx-1][ly-1]; goalState.bPathLng = 0; goalState.pos = 1;
	goalState.direction = -1; goalState.visited = "-";
	locations.put(goalState, "-");
	// showg(startState); showg(goalState);
	System.out.println("lx " + lx + " ly " + ly);

	Nodeg2 initNode = new Nodeg2(startState, goalState);
	long startTime = System.currentTimeMillis();
	initNode.move();
	long endTime = System.currentTimeMillis();
	System.out.println("timing " + (endTime-startTime));
	System.out.println("solutionCnt " + solutionCnt);
	System.out.println("moveCnt " + moveCnt);
	// show();

	/*
	System.out.println("depth " + depth);
	showd();
	showv();
	// */
    } // end main

    static public void show1(int i, int j) {
	// System.out.println("i j " + i + " " + j + " " + grid[i][j].id);
	int n = grid[i][j].pos;
	if ( 0 == n ) System.out.print("   "); else
	    System.out.print( (n < 10 ? "  " + n : " " + n));
    } //
    static public void show() {
	for ( int j = 0; j < lx; j++ ) {
	    for ( int i = 0; i < ly; i++ ) show1(j, i);
	    System.out.println();
	}
    } // end show

    static public void showd1(int i, int j) {
	// System.out.println("i j " + i + " " + j + " " + grid[i][j].id);
	int n = grid[i][j].direction;
	if ( 0 == n ) System.out.print("   ");
	else if ( 1 == n ) System.out.print("  f"); 
	else System.out.print("  b"); 
    } //
    static public void showd() {
	for ( int j = 0; j < lx; j++ ) {
	    for ( int i = 0; i < ly; i++ ) showd1(j, i);
	    System.out.println();
	}
    } // end showd
    static public void showv1(int i, int j) {
	String s = grid[i][j].visited;
	if ( s.equals(" ") ) System.out.print("   ");
	else if ( s.equals("+") ) System.out.print("  +"); 
	else if ( s.equals("-") ) System.out.print("  -"); 
	else System.out.print("   "); 
    } // showv1
    static public void showv() {
	for ( int j = 0; j < lx; j++ ) {
	    for ( int i = 0; i < ly; i++ ) showv1(j, i);
	    System.out.println();
	}
    } // end showv
    static public void showg(GN2 gn) {
	System.out.println(gn.id);
	System.out.println("direction " + gn.direction);
	int numMoves = gn.getNumMoves();
	System.out.println("numMoves " + numMoves);
	GN2 [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    System.out.print(k + " ");
	    GN2 gnk = moves[k];
	    System.out.print(gnk.id + " ");
	}
	System.out.println();
    } // end showg

    static void findMoves(GN2 gn, int dr) {
	// set numMovesand puts in moves candidate moves
	gn.numMoves = 0;
	GN2 gnn = gn.north;
	if ( null != gnn && dr != -1 ) { gn.moves[gn.numMoves] = gnn; gn.numMoves++; }
	gnn = gn.east;
	if ( null != gnn ) { gn.moves[gn.numMoves] = gnn; gn.numMoves++; }
	gnn = gn.south;
	if ( null != gnn && dr != 1 ) { gn.moves[gn.numMoves] = gnn; gn.numMoves++; }
	gnn = gn.west;
	if ( null != gnn ) { gn.moves[gn.numMoves] = gnn; gn.numMoves++; }
	scramble(gn.numMoves, gn.moves); // optional
    } // end findMoves
    // /*
    static void scramble(int numMoves, GN2 [] moves) { 
	// change order of the candidate moves in a GN
	// System.out.println("scramble numMoves " + numMoves);
	for (int i = 0; i < numMoves; i++) {
	    int a = random.nextInt(numMoves);
	    int b = random.nextInt(numMoves);
	    // System.out.println("scramble a b " + a + " " + b);
	    GN2 t = moves[a]; moves[a] = moves[b]; moves[b] = t;
	}
    } // end scramble()
    // */
} // end Grid2

class Nodeg2 { 
    private GN2 fs, bs, gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected GN2 [] moves = new GN2[4];
    Nodeg2(GN2 ssx, GN2 bsx) {
	if ( Grid2.done) return;
	Grid2.moveCnt++;
	fs = ssx; bs = bsx;
           // Choose one of the four
	   // moveForward = true; // unidirectional search
	   // moveForward = false; // unidirectional search
           // bidirectional search:
	         // activate fPathLng & bPathLng first
	         //  moveForward = (Grid2.fPathLng <= Grid2.bPathLng); 
	// /* alternate direction
	     if ( Grid2.bidirection ) Grid2.flip = !Grid2.flip;
	     if ( Grid2.flip ) moveForward = true; 
	          else moveForward = false;
	     // */

	// findMoves sets numMoves and puts in moves candidate moves
	    // forward and backward not hampered = eased:
	    // if ( moveForward ) Grid2.findMoves(fs, 1); else Grid2.findMoves(bs, -1);
	    // forward hampered:
	     // if ( moveForward ) Grid2.findMoves(fs, 2); else Grid2.findMoves(bs, -1);
	    // both hampered:
	    if ( moveForward ) Grid2.findMoves(fs, 2); else Grid2.findMoves(bs, -2);
    }
    public void move() {
	Grid2.depth++;
	/*
	System.out.println("\n ------------------------" +
			   "\n moveCnt " + Grid2.moveCnt + 
			   " moveForward " +  moveForward +
			   " depth " + Grid2.depth + 
			   " fs.pos " + fs.pos +
			   " bs.pos " + bs.pos  );
	// System.out.println("move fs.id " + fs.id + " bs.id " + bs.id);
	// */	

	// if ( 4004 < Grid2.moveCnt) {  System.exit(0); 	}

	gn = ( moveForward ? fs : bs );
	gn.visitCnt++;

	// *********************************************
    	// Do not activate for unidirectional search
	// Fails here also for bi-directional
	// It works in Grid3 but prevends a solution here
	// *********************************************
	/*
	if ( moveForward ) {
	    if ( Grid2.midpointy + 1 <= gn.y ) return;
	} else {
	    if ( gn.y <= Grid2.midpointy - 1 ) return;
	}
	// */
	numMoves = gn.getNumMoves();
	GN2 [] moves = gn.getMoves();
	if ( moveForward ) {
	    for (int k = 0; k < numMoves; k++) {
		GN2 gnk = moves[k];
		// The block prevents a solution here also
		// if ( Grid2.midpointy + 2 <= gnk.y ) continue; // fails
		String direction = Grid2.locations.get(gnk);
		if ( null == direction ) { // not visited
		    if ( block(gnk, moveForward) ) continue;
		    // System.out.println("move f GO DEEPER " + gnk.id);
		    Grid2.locations.put(gnk, "+");
		    Grid2.fCnt++;
		    gnk.pos = Grid2.fCnt;
		    gnk.fPathLng = gn.fPathLng+1;
		    gnk.direction = 1; 
		    gnk.parent = gn;
		    gnk.visited = "+"; 
		    Grid2.fPathLng++;
		    (new Nodeg2(gnk, bs)).move();
		    // System.out.println("move f back from recursion " + Grid2.depth);
		    // Grid2.show();
		    // /*
		    if ( Grid2.done ) { 
			Grid2.depth--;
			return;
		    }
		    // */
		    // 
		    // /*  do (NOT) restore
		    Grid2.locations.remove(gnk);
		    gnk.pos = 0;
		    gnk.parent = null;
		    gnk.direction = 0;
		    gnk.fPathLng = 0;
		    Grid2.fCnt--;
		    Grid2.fPathLng--;
		    // */
		    // Grid2.show();
		    continue;
		}
		if ( direction.equals("+") ) continue; // visited earlier
		// a solution
		/*
		    System.out.println();
		    System.out.println("Grid2.moveCnt " + Grid2.moveCnt);
		    System.out.println("f id " + gn.id + " pos " + gn.pos);
		    System.out.println("f id " + gnk.id + " pos " + gnk.pos);
		    Grid2.show(); 
		    Grid2.showd(); 
		    // */
		    Grid2.solutionCnt++;
		    { Grid2.done = true; break; } // terminate with 1 solution
		    /*
		    System.out.println("----------- moveCnt " + Grid2.moveCnt);
		    System.out.println("move f FOUND SOLUTION # " + Grid2.solutionCnt);
		    // print the paths
		    System.out.println("move other side: " + gnk.id);
		    GN2 z = gnk.parent; 
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
		    // */
		    // return; // activate for more than 1 solution
		    // Grid2.depth--;
		    // return;
	    } // end for loop
	} else { // move backward
	    for (int k = 0; k < numMoves; k++) {
		GN2 gnk = moves[k];
		// The block prevents a solution here also
		// if ( gnk.y <= Grid2.midpointy - 2 ) continue; // block
		// System.out.println("move b k gnk " + k + " " + gnk.id);
		String direction = Grid2.locations.get(gnk);
		// System.out.println("move AAA direction " + direction);
		if ( null == direction ) { // not visited
		    if ( block(gnk, moveForward) ) continue;
		    // System.out.println("move f GO DEEPER " + gnk.id);
		    Grid2.locations.put(gnk, "-");
		    Grid2.bCnt++;
		    gnk.pos = Grid2.bCnt;
		    gnk.bPathLng = gn.bPathLng+1;
		    gnk.direction = -1; 
		    gnk.parent = gn;
		    gnk.visited = "-"; 
		    Grid2.bPathLng++;
		    (new Nodeg2(fs, gnk)).move();
		    // System.out.println("move b back from recursion " + Grid2.depth);
		    // Grid2.show();
		    // /*
		    if ( Grid2.done ) { 
			Grid2.depth--;
			return;
		    }

		    // */
		    // 
		    // /* do (NOT) restore
		    Grid2.locations.remove(gnk);
		    gnk.pos = 0;
		    gnk.parent = null;
		    gnk.direction = 0;
		    gnk.bPathLng = 0;
		    Grid2.bCnt--;
		    Grid2.bPathLng--;
		    // */
		    // Grid2.show();
		    continue;
		}
		if ( direction.equals("-") ) continue; // visited earlier
		// a solution
		    /*
		    System.out.println();
		    System.out.println("Grid2.moveCnt " + Grid2.moveCnt);
		    System.out.println("b id " + gn.id + " pos " + gn.pos);
		    System.out.println("b id " + gnk.id + " pos " + gnk.pos);
		    Grid2.show(); 
		    Grid2.showd(); 
		    // */
		    Grid2.solutionCnt++;
		    { Grid2.done = true; break; } // terminate with 1 solution
		    /*
		    System.out.println("------------------------" );
		    System.out.println("move b FOUND SOLUTION # " + Grid2.solutionCnt);
		    // /*
		    // print the paths
		    System.out.println("move other side: " + gnk.id);
		    GN2 z = gnk.parent; 
		    while (true) {
			if ( null == z ) break;
			System.out.println("move f z " + z.id);
			z = z.parent;
		    }
		    z = gn; 
		    while (true) {
			if ( null == z ) break;
			System.out.println("move b z " + z.id);
			z = z.parent;
		    }
		    // */
		    // return; // restore for more than one solution
		    // Grid2.depth--;
		    // return;
	    } // end for loop
	}
	// System.out.println("move BACKTRACK gn " + gn.id + " depth " + Grid2.depth
	//	     + (moveForward ? " f" : " b") );
	Grid2.depth--;
	// System.out.println("move BACKTRACK gn " + gn.id + " depth " + Grid2.depth);
	// 	Grid2.show();
	// return;
    } // end move

    // Check whether to block a move to gnk
    boolean block(GN2 gnk, boolean forward) {
	GN2 [] gnkMoves = gnk.getMoves();
	int numMoves = gnk.numMoves;
	for (int k = 0; k < numMoves; k++) {
	    GN2 gnbk = gnkMoves[k];
	    if ( 0 == gnbk.pos ) return false; // found a move
	    if ( (forward && gnbk.direction == -1) ||
		 (!forward && gnbk.direction == 1)) return false; // found a solution
	}
	return true; // block the move
    } // end block

} // end Nodeg2

class GN2 { // grid element
    protected int x = 0;
    protected int y = 0;
    protected String id = "";
    protected int pos = 0;
    protected GN2 north = null; 
    protected GN2 east = null; 
    protected GN2 south = null; 
    protected GN2 west = null;
    public void setNorth(GN2 n) { north = n; }
    public void setEast(GN2 n) { east = n; }
    public void setSouth(GN2 n) { south = n; }
    public void setWest(GN2 n) { west = n; }
    protected GN2 parent = null;
    public void setParent(GN2 n) { parent = n; }
    protected int direction = 0; // forward = 1; backward = -1
    protected int pathLength = -1;
    public void setPathLength(int x) { pathLength = x; }
    protected GN2 [] moves = new GN2[4];
    public GN2 [] getMoves() { return moves; }
    protected int numMoves = -1;
    public int getNumMoves() { return numMoves; }
    protected int nextMove = 0;
    public int getNextMove() { int n = nextMove; nextMove++; return n; }
    protected int fPathLng = 0;
    protected int bPathLng = 0;
    protected String visited = " ";
    protected int visitCnt = 0;
} // end GN2
