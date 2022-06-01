// File: c:/ddc/Java/Knight/Grid3.java
// Date: Mon May 16 09:03:27 2022
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
public class Grid3 {
    // static Date date = new Date();
    // static Random random = new Random(date.getTime());
    static Random random = new Random(777); // repeatable results
    // static Random random = new Random(771); // repeatable results
    static final int lx = 6; 
    static final int ly = 10;

    // static final int ly = lx;
    // static final int lx = 50;   
    // static final int ly = 2000;


    static GN3 [][] grid = new GN3[lx][ly];
    static int moveCnt = 0; 
    static int solutionCnt = 0; 
    static int fCnt = 1;
    static int bCnt = 1;
    static int fPathLng = 0;
    static int bPathLng = 0;
    static int depth = 0;

    static int midpointx = lx/2;
    static int midpointy = ly/2;

    static boolean done = false; // for terminating when a solution is found

    public static void main(String[] args) {
	// try{
	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GN3();
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
		GN3 gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }
	/*
	// check the new barrier
	for ( int k = 0; k < lx; k++ ) {
	    System.out.print(k + " ");
	    GN3 gnj = grid[k][midpointy];
	    System.out.print("north " + gnj.north);
	    System.out.println(" south " + gnj.south);
	    System.out.print(" west " + gnj.west);
	    System.out.println(" east " + gnj.east);
	    System.out.println();
	}
	*/


	GN3 startState = grid[0][0]; startState.fPathLng = 0; 
	startState.direction = 1; startState.pos = 1; startState.visited = "+";
	GN3 goalState = grid[lx-1][ly-1]; goalState.bPathLng = 0;
	// GN3 goalState = grid[0][ly-1]; goalState.bPathLng = 0;
	goalState.direction = -1; goalState.pos = 1; goalState.visited = "-";
	showg(startState); showg(goalState); 
	System.out.println();
	// System.exit(0);	
	Nodeg2 initNode = new Nodeg2(startState, goalState);
	long startTime = System.currentTimeMillis();
	initNode.move();
	long endTime = System.currentTimeMillis();
	System.out.println("\ntiming " + (endTime-startTime));
	System.out.println("solutionCnt " + solutionCnt);
	System.out.println("moveCnt " + moveCnt);
	// /*
	/*
	show();
	showd();
	showv();
	System.out.println("Grid3.fCnt " + Grid3.fCnt + 
			   " Grid3.bCnt " + Grid3.bCnt);
	*/

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

    static public void showg(GN3 gn) {
	System.out.println(gn.id);
	System.out.println("direction " + gn.direction);
	int numMoves = gn.getNumMoves();
	System.out.println("numMoves " + numMoves);
	GN3 [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    System.out.print(k + " ");
	    GN3 gnk = moves[k];
	    System.out.print(gnk.id + " ");
	}
	System.out.println();
    }

    static void findMoves(GN3 gn, int dr) {
	// set numMovesand puts in moves candidate moves
	gn.numMoves = 0;
	GN3 gnn = gn.north;
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
    static void scramble(int numMoves, GN3 [] moves) { 
	// change order of the candidate moves in a GN
	// System.out.println("scramble numMoves " + numMoves);
	for (int i = 0; i < numMoves; i++) {
	    int a = random.nextInt(numMoves);
	    int b = random.nextInt(numMoves);
	    // System.out.println("scramble a b " + a + " " + b);
	    GN3 t = moves[a]; moves[a] = moves[b]; moves[b] = t;
	}
    } // end scramble()
    // */
} // end Grid3

class Nodeg2 { 
    private GN3 fs, bs, gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected GN3 [] moves = new GN3[4];
    Nodeg2(GN3 ssx, GN3 bsx) {
	Grid3.moveCnt++;
	fs = ssx; bs = bsx;
	// Choose one of the three
        // moveForward = true; // unidirectional search
	// moveForward = false; // unidirectional search
        // moveForward = (Grid3.fPathLng <= Grid3.bPathLng); // bidirectional search +
	moveForward = (Grid3.fCnt <= Grid3.bCnt); // bidirectional search

	// findMoves sets numMoves and puts in moves candidate moves
	if ( moveForward ) Grid3.findMoves(fs, 1); else Grid3.findMoves(bs, -1);
    }
    public void move() {
	if ( Grid3.done ) return; // terminate
	Grid3.depth++;
	gn = ( moveForward ? fs : bs );
	boolean trace = false;
	numMoves = gn.getNumMoves();
	if (trace) System.out.println("\nGrid3.moveCnt " + Grid3.moveCnt);
	if (trace) System.out.println("gn.id " + gn.id);
	if (trace) System.out.println("gn.pos " + gn.pos);
	if (trace) System.out.println("numMoves " + numMoves);
	if (trace) System.out.println("moveForward " + moveForward);
	if (trace) System.out.println("Grid3.depth " + Grid3.depth);
	if (trace) System.out.println("Grid3.fCnt " + Grid3.fCnt + 
				      " Grid3.bCnt " + Grid3.bCnt);
	if (trace) { Grid3.show(); Grid3.showd(); }


	GN3 [] moves = gn.getMoves();
	if ( moveForward ) {
	    for (int k = 0; k < numMoves; k++) {
		GN3 gnk = moves[k];
		if (trace) System.out.println("move f k gnk " + k + " " + gnk.id);
		if (trace) System.out.println("gnk.id " + gnk.id);
		if (trace) System.out.println("gnk.direction " + gnk.direction);
		if (trace) System.out.println("gnk.pos " + gnk.pos);
		if ( -1 == gnk.direction ) { // a solution
		    /*
		    System.out.println();
		    System.out.println("***********SOLUTION");
		    System.out.println("Grid3.moveCnt " + Grid3.moveCnt);
		    System.out.println("f id " + gnk.id + " pos " + gnk.pos);
		    System.out.println("Grid3.fCnt + Grid3.bCnt " + 
				       (Grid3.fCnt + Grid3.bCnt));
		    Grid3.show(); 
		    Grid3.showd(); 
		    Grid3.showv(); 
		    // Grid3.closef();
		    System.exit(0);
		    // */
		    Grid3.solutionCnt++;
		    // Grid3.done = true; // terminate when a solution is found
		    /*
		    System.out.println("----------- moveCnt " + Grid3.moveCnt);
		    System.out.println("move f FOUND SOLUTION # " + Grid3.solutionCnt);
		    // print the paths
		    System.out.println("move other side: " + gnk.id);
		    GN3 z = gnk.parent; 
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
		    // if ( 1000 <  Grid3.moveCnt ) System.exit(0);
		    // */
		    return;
		}
		if ( 0 == gnk.direction ) { // can go there
		    // System.out.println("move f GO DEEPER " + gnk.id);
		    Grid3.fCnt++;
		    gnk.pos = Grid3.fCnt;
		    // gnk.fPathLng = gn.fPathLng+1;
		    gnk.direction = 1;
		    gnk.parent = gn;
		    gnk.visited = "+";
		    // Grid3.fPathLng++;
		    (new Nodeg2(gnk, bs)).move();
		    /* // do (NOT) restore
		    gnk.pos = 0;
		    gnk.parent = null;
		    gnk.direction = 0;
		    gnk.fPathLng = gn.fPathLng-1;
		    Grid3.fCnt--;
		    // */
		    // Grid3.fPathLng--;
		    continue;
		} else { 
		    // visited earlier
		    // System.out.println("move f visited earlier");
		    continue;
		    // System.exit(0);
		}
	    } 
	} else { // move backward
	    for (int k = 0; k < numMoves; k++) {
		GN3 gnk = moves[k];
		if (trace)  System.out.println("move b k gnk " + k + " " + gnk.id);
		if (trace) System.out.println("gnk.id " + gnk.id);
		if (trace) System.out.println("gnk.direction " + gnk.direction);
		if (trace) System.out.println("gnk.pos " + gnk.pos);
		if ( 1 == gnk.direction ) { // a solution
		    /*
		    System.out.println();
		    System.out.println("************** SOLUTION");
		    System.out.println("Grid3.moveCnt " + Grid3.moveCnt);
		    System.out.println("b id " + gnk.id + " pos " + gnk.pos);
		    System.out.println("Grid3.fCnt + Grid3.bCnt " + 
				       (Grid3.fCnt + Grid3.bCnt));
		    Grid3.show(); 
		    Grid3.showd(); 
		    Grid3.showv(); 
		    System.exit(0);
		    // */
		    Grid3.solutionCnt++;
		    Grid3.done = true; // terminate when a solution is found
		    /*
		    System.out.println("------------------------" );
		    System.out.println("move b FOUND SOLUTION # " + Grid3.solutionCnt);
		    // /*
		    // print the paths
		    System.out.println("move other side: " + gnk.id);
		    GN3 z = gnk.parent; 
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
		    return;
		}
		if ( 0 == gnk.direction ) { // can go there
		    Grid3.bCnt++;
		    gnk.pos = Grid3.bCnt;
		    gnk.direction = -1;
		    gnk.parent = gn;
		    gnk.visited = "-";
		    // Grid3.bPathLng++;
		    (new Nodeg2(fs, gnk)).move();
		    /* // do (NOT) restore
		    gnk.pos = 0;
		    gnk.parent = null;
		    gnk.direction = 0;
		    gnk.bPathLng = gnk.bPathLng-1;
		    Grid3.bCnt--;
		    // */
		    // Grid3.bPathLng--;
		    continue;
		} else { 
		    // visited earlier
		    // System.out.println("move b visited earlier");
		    continue;
		    // System.exit(0);
		}
	    } 
	}
	// System.out.println("move BACKTRACK gn depth " + Grid3.depth + 
	//			   " gn.id " + gn.id + " gn.pos " + gn.pos);
	Grid3.depth--;

	// return;
    } // end move
} // end Nodeg

class GN3 {
    protected int x = 0;
    protected int y = 0;
    protected String id = "";
    protected int pos = 0;
    protected GN3 north = null; 
    protected GN3 east = null; 
    protected GN3 south = null; 
    protected GN3 west = null;
    public void setNorth(GN3 n) { north = n; }
    public void setEast(GN3 n) { east = n; }
    public void setSouth(GN3 n) { south = n; }
    public void setWest(GN3 n) { west = n; }
    protected GN3 parent = null;
    public void setParent(GN3 n) { parent = n; }
    protected int direction = 0; // forward = 1; backward = -1
    protected int pathLength = -1;
    public void setPathLength(int x) { pathLength = x; }
    protected GN3 [] moves = new GN3[4];
    public GN3 [] getMoves() { return moves; }
    protected int numMoves = -1;
    public int getNumMoves() { return numMoves; }
    protected int nextMove = 0;
    public int getNextMove() { int n = nextMove; nextMove++; return n; }
    protected int fPathLng = 0;
    protected int bPathLng = 0;
    protected String visited = " ";

} // end GN3
