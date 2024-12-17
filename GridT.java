// File: c:/ddc/Java/Knight/GridT.java
// Date: Fri Apr 22 19:12:42 2022, Sun Nov 13 09:23:01 2022
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
public class GridT {
    // static Date date = new Date();
    // static Random random = new Random(date.getTime());
    static Random random = new Random(777); // repeatable results
    // static final int lx = 6; 
    // static final int ly = 10; 
    // static final int ly = 12;

    // static final int lx = 5;
    // static final int lx = 10;
    // static final int lx = 15;
    // static final int lx = 20;
    // static final int lx = 40;
    // static final int lx = 60;
    // static final int lx = 80;
    // static final int lx = 100;

    static final int lx = 600;
    // static final int ly = 1000; 
    static final int ly = 2000; 

    static GNT [][] grid = new GNT[lx][ly];
    static int moveCnt = 0; 
    static int solutionCnt = 0; 
    static int fCnt = 1;
    static int bCnt = 1;
    static int fPathLng = 0;
    static int bPathLng = 0;
    static int depth = 0;
    static boolean done = false;

    static Hashtable<GNT,String> locations = new Hashtable<GNT,String>();
    static boolean moveForward = false;

    public static void main(String[] args) {
	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GNT();
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
		GNT gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }
	GNT startState = grid[0][0]; startState.fPathLng = 0; 
	startState.direction = 1; startState.pos = 1;
	startState.visited = "+"; locations.put(startState, "+");
	GNT goalState = grid[lx-1][ly-1]; goalState.bPathLng = 0;
	goalState.direction = -1; goalState.pos = 1;
	goalState.visited = "-"; locations.put(goalState, "-"); 
	showg(startState); showg(goalState); 
	// System.exit(0);
	NodegT initNode = new NodegT(startState, goalState);
	long startTime = System.currentTimeMillis();
	initNode.move();
	long endTime = System.currentTimeMillis();
	System.out.println("timing " + (endTime-startTime));
	System.out.println("solutionCnt " + solutionCnt);
	System.out.println("moveCnt " + moveCnt);
	// show();
	// showd();

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
    static public void showg(GNT gn) {
	System.out.println(gn.id);
	System.out.println("direction " + gn.direction);
	int numMoves = gn.getNumMoves();
	System.out.println("numMoves " + numMoves);
	GNT [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    System.out.print(k + " ");
	    GNT gnk = moves[k];
	    System.out.print(gnk.id + " ");
	}
	System.out.println();
    }
    static void findMoves(GNT gn, int dr) {
	// set numMovesand puts in moves candidate moves
	gn.numMoves = 0;
	GNT gnn = gn.north;
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
    static void scramble(int numMoves, GNT [] moves) { 
	// change order of the candidate moves in a GN
	// System.out.println("scramble numMoves " + numMoves);
	for (int i = 0; i < numMoves; i++) {
	    int a = random.nextInt(numMoves);
	    int b = random.nextInt(numMoves);
	    // System.out.println("scramble a b " + a + " " + b);
	    GNT t = moves[a]; moves[a] = moves[b]; moves[b] = t;
	}
    } // end scramble()
    // */
} // end GridT

class NodegT { 
    private GNT fs, bs, gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected GNT [] moves = new GNT[4];
    NodegT(GNT ssx, GNT bsx) {
	GridT.moveCnt++;
	// System.out.println("NodegT GridT.moveCnt " + GridT.moveCnt);
	// System.out.println("NodegT ssx.pos " + ssx.pos);
	// System.out.println("NodegT bsx.pos " + bsx.pos);
	fs = ssx; bs = bsx;
	// if ( 3 < GridT.moveCnt ) System.exit(0);
	// Choose one of the three
	// moveForward = true; // unidirectional search
	// moveForward = false; // unidirectional search
        // moveForward = (GridT.fPathLng <= GridT.bPathLng); // bidirectional search
	// or:
	// /*
	  GridT.moveForward = !GridT.moveForward;
	  moveForward = GridT.moveForward; // bidirectional search
	// */
	// findMoves sets numMoves and puts in moves candidate moves
	// both directions eased
	if ( moveForward ) GridT.findMoves(fs, 1); else GridT.findMoves(bs, -1);
	// both directions hamperd
	// if ( moveForward ) GridT.findMoves(fs, 2); else GridT.findMoves(bs, -2);
    }
    public void move() {
	if ( GridT.done) return;
	// System.out.println("move moveForward " + moveForward);
	// GridT.show();  
	GridT.depth++;
	gn = ( moveForward ? fs : bs );
	// System.out.println("move gn.pos " + gn.pos);
	numMoves = gn.getNumMoves();
	// System.out.println("move numMoves " + numMoves);
	// GridT.showg(gn);
	GNT [] moves = gn.getMoves();
	if ( moveForward ) {
	    for (int k = 0; k < numMoves; k++) {
		GNT gnk = moves[k];
		String direction = GridT.locations.get(gnk);
		if ( null == direction ) { // not visited
		    if ( block(gnk, moveForward) ) {
			continue;
		    }
		    // System.out.println("move f GO DEEPER " + gnk.id);
		    GridT.locations.put(gnk, "+");
		    GridT.fCnt++;
		    gnk.pos = GridT.fCnt;
		    // gnk.fPathLng = gn.fPathLng+1;
		    gnk.direction = 1; 
		    gnk.parent = gn;
		    gnk.visited = "+"; 
		    // GridT.fPathLng++;
		    (new NodegT(gnk, bs)).move();
		    // System.out.println("move f back from recursion " + GridT.depth);
		    if ( GridT.done ) { 
			GridT.depth--;
			return;
		    }
		    // GridT.locations.remove(gnk);
		    /*  do (NOT) restore
		    gnk.pos = 0;
		    gnk.parent = null;
		    gnk.direction = 0;
		    // gnk.fPathLng = gn.fPathLng-1;
		    GridT.fCnt--;
		    // */
		    // GridT.fPathLng--;
		    continue;
		}
		if ( direction.equals("+") ) continue; // visited earlier
		// a solution
		/*
		    System.out.println();
		    System.out.println("Solution GridT.moveCnt " + GridT.moveCnt);
		    System.out.println("f id " + gn.id + " pos " + gn.pos);
		    System.out.println("f id " + gnk.id + " pos " + gnk.pos);

		    // GridT.show(); 
		    // GridT.showd(); 
		    // System.exit(0);
		    // */
		    GridT.solutionCnt++;
		    // GridT.done = true;
		    // GridT.showd();
		    /*
		    System.out.println("----------- moveCnt " + GridT.moveCnt);
		    System.out.println("move f FOUND SOLUTION # " + GridT.solutionCnt);
		    // print the paths
		    System.out.println("move other side: " + gnk.id);
		    GN z = gnk.parent; 
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
		    // if ( 1000 <  GridT.moveCnt ) System.exit(0);
		    // */
		    return;
	    }
	} else { // move backward
	    for (int k = 0; k < numMoves; k++) {
		GNT gnk = moves[k];
		// System.out.println("move b k gnk " + k + " " + gnk.id);
		String direction = GridT.locations.get(gnk);
		if ( null == direction ) { // not visited
		    if ( block(gnk, moveForward) ) {
			continue;
		    }
		    // System.out.println("move b GO DEEPER " + gnk.id);
		    GridT.locations.put(gnk, "-");
		    GridT.bCnt++;
		    gnk.pos = GridT.bCnt;
		    // gnk.bPathLng = gn.bPathLng+1;
		    gnk.direction = -1; 
		    gnk.parent = gn;
		    gnk.visited = "-"; 
		    // GridT.bPathLng++;
		    (new NodegT(fs, gnk)).move();
		    // System.out.println("move f back from recursion " + GridT.depth);
		    if ( GridT.done ) { 
			GridT.depth--;
			return;
		    }
		    // GridT.locations.remove(gnk);
		    /*  do (NOT) restore
		    gnk.pos = 0;
		    gnk.parent = null;
		    gnk.direction = 0;
		    // gnk.bPathLng = gn.bPathLng-1;
		    GridT.bCnt--;
		    // */
		    // GridT.bPathLng--;
		    continue;
		}
		if ( direction.equals("-") ) continue; // visited earlier
		// a solution
		    /*
		    System.out.println();
		    System.out.println("Solution GridT.moveCnt " + GridT.moveCnt);
		    System.out.println("b id " + gn.id + " pos " + gn.pos);
		    System.out.println("b id " + gnk.id + " pos " + gnk.pos);
		    */
		    // GridT.show(); 
		    // GridT.showd(); 
		    // System.exit(0);
		    // */
		    GridT.solutionCnt++;
		    // GridT.done = true;
		    // GridT.showd(); 
		    /*
		    System.out.println("------------------------" );
		    System.out.println("move b FOUND SOLUTION # " + GridT.solutionCnt);
		    // /*
		    // print the paths
		    System.out.println("move other side: " + gnk.id);
		    GN z = gnk.parent; 
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
		    // System.exit(0);
		    // */
		    return;
		}

	}
	// System.out.println("move BACKTRACK gn depth " + gn.id + " " + GridT.depth);
	GridT.depth--;
	// return;
    } // end move

    // Check whether to block a move to gnk
    boolean block(GNT gnk, boolean forward) {
	GNT [] gnkMoves = gnk.getMoves();
	int numMoves = gnk.numMoves;
	for (int k = 0; k < numMoves; k++) {
	    GNT gnbk = gnkMoves[k];
	    if ( 0 == gnbk.pos ) return false; // found a move
	    if ( (forward && gnbk.direction == -1) ||
		 (!forward && gnbk.direction == 1)) return false; // found a solution
	}
	return true; // block the move
    } // end block
} // end NodegT

class GNT {
    protected int x = 0;
    protected int y = 0;
    protected String id = "";
    protected int pos = 0;
    protected GNT north = null; 
    protected GNT east = null; 
    protected GNT south = null; 
    protected GNT west = null;
    public void setNorth(GNT n) { north = n; }
    public void setEast(GNT n) { east = n; }
    public void setSouth(GNT n) { south = n; }
    public void setWest(GNT n) { west = n; }
    protected GNT parent = null;
    public void setParent(GNT n) { parent = n; }
    protected int direction = 0; // forward = 1; backward = -1
    protected int pathLength = -1;
    public void setPathLength(int x) { pathLength = x; }
    protected GNT [] moves = new GNT[4];
    public GNT [] getMoves() { return moves; }
    protected int numMoves = -1;
    public int getNumMoves() { return numMoves; }
    protected int nextMove = 0;
    public int getNextMove() { int n = nextMove; nextMove++; return n; }
    protected int fPathLng = 0;
    protected int bPathLng = 0;
    protected String visited = " ";
} // end GNT
