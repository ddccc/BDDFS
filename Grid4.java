// File: c:/ddc/Java/Knight/Grid4.java
// Date: Sun Aug 28 09:17:20 2022, Sun Nov 13 09:40:48 2022
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
/*
    This is a two barrier version 
*/
public class Grid4 {
    // static Date date = new Date();
    // static Random random = new Random(date.getTime());
    static Random random = new Random(777); // repeatable results
    // static Random random = new Random(771); // repeatable results
    static final int lx = 5;
    // static final int lx = 6; 
    // static final int lx = 10; 
    // static final int lx = 15; 
    // static final int lx = 20; 
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

    static final int ly = 12;
    // static final int ly = 20;
    // static final int ly = 25;
    // static final int ly = 30;

    // static final int ly = 100;
    // static final int ly = 150;
    // static final int ly = 200;
    // static final int ly = 250;
    // static final int ly = 300;
    // static final int ly = 400;
    // static final int ly = 800;
    // static final int ly = 1600;
    // static final int ly = 2000;
    // static final int ly = 3200;
    // static final int ly = 6400;
    // static final int ly = 12800;
    
    // static final int ly = 2100; // 
    // static final int ly = 1800; 


    static GN4 [][] grid = new GN4[lx][ly];
    static int moveCnt = 0; 
    static int solutionCnt = 0; 
    static int fCnt = 1;
    static int bCnt = 1;
    static int depth = 0;

    static int bx = ly/3;
    static int barrier1 = bx - 1;
    static int barrier2 = ly - bx;


    // select one
    static boolean bidirection = true;
    // static boolean bidirection = false;

    static Hashtable<GN4,String> locations = new Hashtable<GN4,String>();


    static boolean done = false; // for terminating when a solution is found
    static boolean moveForward = false; 


    public static void main(String[] args) {
	System.out.println("bidirection " + bidirection +
			   " lx " + lx +
			   " ly " + ly + " barrier1 " + barrier1 + 
			   " barrier2 " + barrier2);
	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GN4();
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

	for ( int i = 0; i < lx; i++ ) 
	    for ( int j = 0; j < ly; j++ ) {
		GN4 gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }
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
	/*
	int x = barrier1; int y = barrier1-1; 
	System.out.print("barrier1 " +  barrier1 + " " + (barrier1-1) +
			 " barrier2 " +  barrier2 + " " + (barrier2+1));
	System.out.println();
	*/

	GN4 startState = grid[0][0]; startState.fPathLng = 0; 
	startState.direction = 1; startState.pos = 1; startState.visited = "+";
	locations.put(startState, "+");
	GN4 goalState = grid[lx-1][ly-1]; goalState.bPathLng = 0;
	// GN4 goalState = grid[0][ly-1]; goalState.bPathLng = 0;
	goalState.direction = -1; goalState.pos = 1; goalState.visited = "-";
	locations.put(goalState, "-");
	// showg(startState); showg(goalState); 
	// System.out.println();
	// showd(); showv(); 
	// System.exit(0);

	Nodeg4 initNode = new Nodeg4(startState, goalState);
	long startTime = System.currentTimeMillis();
	initNode.move();

	long endTime = System.currentTimeMillis();
	System.out.println("\ntiming " + (endTime-startTime));
	System.out.println("solutionCnt " + solutionCnt);
	System.out.println("moveCnt " + moveCnt);
	// /*
	// show();
	System.out.println();
	show();  // pos numbers
	showd(); // f & b
	showv(); // + & -
	// System.out.println("\nGrid4.fCnt " + Grid4.fCnt + 
	//	   " Grid4.bCnt " + Grid4.bCnt);
	// */

    } // end main


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

    static public void showg(GN4 gn) {
	System.out.println(gn.id);
	System.out.println("direction " + gn.direction);
	int numMoves = gn.getNumMoves();
	System.out.println("numMoves " + numMoves);
	GN4 [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    System.out.print(k + " ");
	    GN4 gnk = moves[k];
	    System.out.print(gnk.id + " ");
	}
	System.out.println();
    }

    static void findMoves(GN4 gn, int dr) {
	// set numMovesand puts in moves candidate moves
	gn.numMoves = 0;
	GN4 gnn = gn.north;
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
    static void scramble(int numMoves, GN4 [] moves) { 
	// change order of the candidate moves in a GN
	// System.out.println("scramble numMoves " + numMoves);
	for (int i = 0; i < numMoves; i++) {
	    int a = random.nextInt(numMoves);
	    int b = random.nextInt(numMoves);
	    // System.out.println("scramble a b " + a + " " + b);
	    GN4 t = moves[a]; moves[a] = moves[b]; moves[b] = t;
	}
    } // end scramble()
    // */
} // end Grid4

class Nodeg4 { 
    private GN4 fs, bs, gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected GN4 [] moves = new GN4[4];
    Nodeg4(GN4 ssx, GN4 bsx) {
	Grid4.depth++;
	Grid4.moveCnt++;
	fs = ssx; bs = bsx;
	// Choose one of the four
        // moveForward = true; // unidirectional search
	// moveForward = false; // unidirectional search
        // moveForward = (Grid4.fPathLng <= Grid4.bPathLng); // bidirectional search +
	// moveForward = (Grid4.fCnt <= Grid4.bCnt); // bidirectional search
	// OR for bidirectional:
	Grid4.moveForward = !Grid4.moveForward;
	moveForward = Grid4.moveForward; // bidirectional search

	// System.out.println("\nGrid4.moveCnt " + Grid4.moveCnt);
	// System.out.println("moveForward " + moveForward);
	// Grid4.show();
	// if ( 13 < Grid4.moveCnt ) System.exit(0);

	// findMoves sets numMoves and puts in moves candidate moves
	// both directions easied
	if ( moveForward ) Grid4.findMoves(fs, 1); else Grid4.findMoves(bs, -1);
	// both directions hampered
	// if ( moveForward ) Grid4.findMoves(fs, 2); else Grid4.findMoves(bs, -2);
    }
    public void move() {
	if ( Grid4.done ) {
	    Grid4.depth--;
	    return; // terminate
	}
	gn = ( moveForward ? fs : bs );
	/* // do not activate for uni-directional search
	if ( moveForward ) {
	    if ( Grid4.midpointy + 1 <= gn.y ) return;
	} else {
	    if ( gn.y <= Grid4.midpointy -1) return;
	}
	*/
	boolean trace = false;
	numMoves = gn.getNumMoves();
	if (trace) System.out.println("\nGrid4.moveCnt " + Grid4.moveCnt);
	if (trace) System.out.println("gn.id " + gn.id);
	if (trace) System.out.println("gn.pos " + gn.pos);
	if (trace) System.out.println("numMoves " + numMoves);
	if (trace) System.out.println("moveForward " + moveForward);
	if (trace) System.out.println("Grid4.depth " + Grid4.depth);
	if (trace) System.out.println("Grid4.fCnt " + Grid4.fCnt + 
				      " Grid4.bCnt " + Grid4.bCnt);
	// trace = false;
	if (trace) { Grid4.show(); Grid4.showd(); }

	GN4 [] moves = gn.getMoves();
	if ( moveForward ) {
	    for (int k = 0; k < numMoves; k++) {
		GN4 gnk = moves[k];
		if (trace) System.out.println("move f k gnk " + k + " " + gnk.id);
		if (trace) System.out.println("gnk.id " + gnk.id);
		if (trace) System.out.println("gnk.direction " + gnk.direction);
		if (trace) System.out.println("gnk.pos " + gnk.pos);
		String direction = Grid4.locations.get(gnk);
		if ( null == direction ) { // not visited
		    // System.out.println("move f GO DEEPER " + gnk.id);
		    Grid4.locations.put(gnk, "+");
		    Grid4.fCnt++;
		    gnk.pos = Grid4.fCnt;
		    // gnk.fPathLng = gn.fPathLng+1;
		    gnk.direction = 1; 
		    gnk.parent = gn;
		    gnk.visited = "+"; 
		    // Grid4.fPathLng++;
		    (new Nodeg4(gnk, bs)).move();
		    // System.out.println("move f back from recursion " + Grid4.depth);
		    if ( Grid4.done ) { 
			Grid4.depth--;
			return;
		    }
		    // Grid4.locations.remove(gnk);
		    /* // do (NOT) restore
		    gnk.pos = 0;
		    gnk.parent = null;
		    gnk.direction = 0;
		    // gnk.fPathLng = gn.fPathLng-1;
		    Grid4.fCnt--;
		    // */
		    // Grid4.fPathLng--;
		    continue;
		}
		if ( direction.equals("+") ) continue; // visited earlier

		// a solution
		    /*
		    System.out.println();
		    System.out.println("***********SOLUTION");
		    System.out.println("Grid4.moveCnt " + Grid4.moveCnt);
		    System.out.println("f id " + gnk.id + " pos " + gnk.pos);
		    System.out.println("Grid4.fCnt + Grid4.bCnt " + 
				       (Grid4.fCnt + Grid4.bCnt));
		    Grid4.show(); 
		    Grid4.showd(); 
		    Grid4.showv(); 
		    // Grid4.closef();
		    System.exit(0);
		    // */
		    // Grid4.show(); 
		    // Grid4.showd(); 
		    Grid4.solutionCnt++;
		    Grid4.done = true; // terminate when a solution is found
		    /*
		    System.out.println("----------- moveCnt " + Grid4.moveCnt);
		    System.out.println("move f FOUND SOLUTION # " + Grid4.solutionCnt);
		    // print the paths
		    System.out.println("move other side: " + gnk.id);
		    GN4 z = gnk.parent; 
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
		    // if ( 1000 <  Grid4.moveCnt ) System.exit(0);
		    // */
		    Grid4.depth--;
		    return;
	    }
	} else { // move backward
	    for (int k = 0; k < numMoves; k++) {
		GN4 gnk = moves[k];
		if (trace)  System.out.println("move b k gnk " + k + " " + gnk.id);
		if (trace) System.out.println("gnk.id " + gnk.id);
		if (trace) System.out.println("gnk.direction " + gnk.direction);
		if (trace) System.out.println("gnk.pos " + gnk.pos);
		String direction = Grid4.locations.get(gnk);
		if ( null == direction ) { // not visited
		    // System.out.println("move f GO DEEPER " + gnk.id);
		    Grid4.locations.put(gnk, "-");
		    Grid4.bCnt++;
		    gnk.pos = Grid4.bCnt;
		    // gnk.bPathLng = gn.bPathLng+1;
		    gnk.direction = -1; 
		    gnk.parent = gn;
		    gnk.visited = "-"; 
		    // Grid4.bPathLng++;
		    (new Nodeg4(fs, gnk)).move();
		    // System.out.println("move b back from recursion " + Grid4.depth);
		    if ( Grid4.done ) { 
			Grid4.depth--;
			return;
		    }

		    // Grid4.locations.remove(gnk);
		    /* // do (NOT) restore
		    gnk.pos = 0;
		    gnk.parent = null;
		    gnk.direction = 0;
		    // gnk.bPathLng = gn.bPathLng-1;
		    Grid4.bCnt--;
		    // */
		    // Grid4.fPathLng--;
		    continue;
		}
		if ( direction.equals("-") ) continue; // visited earlier

		// a solution
		    /*
		    System.out.println();
		    System.out.println("************** SOLUTION");
		    System.out.println("Grid4.moveCnt " + Grid4.moveCnt);
		    System.out.println("b id " + gnk.id + " pos " + gnk.pos);
		    System.out.println("Grid4.fCnt + Grid4.bCnt " + 
				       (Grid4.fCnt + Grid4.bCnt));
		    Grid4.show(); 
		    Grid4.showd(); 
		    Grid4.showv(); 
		    // System.exit(0);
		    // */
		    Grid4.solutionCnt++;
		    Grid4.done = true; // terminate when a solution is found
		    /*
		    System.out.println("------------------------" );
		    System.out.println("move b FOUND SOLUTION # " + Grid4.solutionCnt);
		    // /*
		    // print the paths
		    System.out.println("move other side: " + gnk.id);
		    GN4 z = gnk.parent; 
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
		    System.exit(0);
		    // */
		    Grid4.depth--;
		    return;
	    }
	}
	// System.out.println("move BACKTRACK gn depth " + Grid4.depth + 
	//			   " gn.id " + gn.id + " gn.pos " + gn.pos);
	Grid4.depth--;

	// return;
    } // end move
} // end Nodeg4


class GN4 {
    protected int x = 0;
    protected int y = 0;
    protected String id = "";
    protected int pos = 0;
    protected GN4 north = null; 
    protected GN4 east = null; 
    protected GN4 south = null; 
    protected GN4 west = null;
    public void setNorth(GN4 n) { north = n; }
    public void setEast(GN4 n) { east = n; }
    public void setSouth(GN4 n) { south = n; }
    public void setWest(GN4 n) { west = n; }
    protected GN4 parent = null;
    public void setParent(GN4 n) { parent = n; }
    protected int direction = 0; // forward = 1; backward = -1
    protected int pathLength = -1;
    public void setPathLength(int x) { pathLength = x; }
    protected GN4 [] moves = new GN4[4];
    public GN4 [] getMoves() { return moves; }
    protected int numMoves = -1;
    public int getNumMoves() { return numMoves; }
    protected int nextMove = 0;
    public int getNextMove() { int n = nextMove; nextMove++; return n; }
    protected int fPathLng = 0;
    protected int bPathLng = 0;
    protected String visited = " ";

} // end GN4
