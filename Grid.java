// File: c:/ddc/Java/Knight/Grid.java
// Date: Fri Apr 22 19:12:42 2022
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
public class Grid {
    static Date date = new Date();
    static Random random = new Random(date.getTime());
    // static final int lx = 6; 
    static final int lx = 6; 
    // static final int ly = 10; 
    static final int ly = 10; 
    static GN [][] grid = new GN[lx][ly];
    static int moveCnt = 0; 
    static int solutionCnt = 0; 
    static int fCnt = 1;
    static int bCnt = 1;
    static int fPathLng = 0;
    static int bPathLng = 0;
    static int depth = 0;

    public static void main(String[] args) {
	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GN();
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
		GN gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }
	GN startState = grid[0][0]; startState.fPathLng = 0; 
	startState.direction = 1;
	GN goalState = grid[lx-1][ly-1]; goalState.bPathLng = 0;
	goalState.direction = -1;
	showg(startState); showg(goalState); 
	// System.exit(0);
	Nodeg initNode = new Nodeg(startState, goalState);
	long startTime = System.currentTimeMillis();
	initNode.move();
	long endTime = System.currentTimeMillis();
	System.out.println("timing " + (endTime-startTime));
	System.out.println("solutionCnt " + solutionCnt);
	System.out.println("moveCnt " + moveCnt);

    } // end main

    static public void show1(int i, int j) {
	// System.out.println("i j " + i + " " + j + " " + grid[i][j].id);
	int n = grid[i][j].pos;
	System.out.print( (n < 10 ? " " + n : n) + " ");
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
	if ( 0 == n ) System.out.print("  ");
	else if ( 1 == n ) System.out.print(" f"); 
	else System.out.print(" b"); 
    } //
    static public void showd() {
	for ( int j = 0; j < lx; j++ ) {
	    for ( int i = 0; i < ly; i++ ) showd1(j, i);
	    System.out.println();
	}
    } // end showd


    static public void showg(GN gn) {
	System.out.println(gn.id);
	System.out.println("direction " + gn.direction);
	int numMoves = gn.getNumMoves();
	System.out.println("numMoves " + numMoves);
	GN [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    System.out.print(k + " ");
	    GN gnk = moves[k];
	    System.out.print(gnk.id + " ");
	}
	System.out.println();
    }
    static void findMoves(GN gn, int dr) {
	// set numMovesand puts in moves candidate moves
	gn.numMoves = 0;
	GN gnn = gn.north;
	if ( null != gnn && dr != -1 ) { gn.moves[gn.numMoves] = gnn; gn.numMoves++; }
	gnn = gn.east;
	if ( null != gnn ) { gn.moves[gn.numMoves] = gnn; gn.numMoves++; }
	gnn = gn.south;
	if ( null != gnn && dr != 1 ) { gn.moves[gn.numMoves] = gnn; gn.numMoves++; }
	gnn = gn.west;
	if ( null != gnn ) { gn.moves[gn.numMoves] = gnn; gn.numMoves++; }
	// scramble(gn.numMoves, gn.moves); // optional
    } // end findMoves
    /*
    static void scramble(int numMoves, GN [] moves) { 
	// change order of the candidate moves in a GN
	// System.out.println("scramble numMoves " + numMoves);
	for (int i = 0; i < numMoves; i++) {
	    int a = random.nextInt(numMoves);
	    int b = random.nextInt(numMoves);
	    // System.out.println("scramble a b " + a + " " + b);
	    GN t = moves[a]; moves[a] = moves[b]; moves[b] = t;
	}
    } // end scramble()
    */
} // end Grid

class Nodeg { 
    private GN fs, bs, gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected GN [] moves = new GN[4];
    Nodeg(GN ssx, GN bsx) {
	Grid.moveCnt++;
	fs = ssx; bs = bsx;
	// Select one of the three
        // moveForward = true; // unidirectional search
	// moveForward = false; // unidirectional search
        moveForward = (Grid.fPathLng <= Grid.bPathLng); // bidirectional search

	// findMoves sets numMoves and puts in moves candidate moves
	if ( moveForward ) Grid.findMoves(fs, 1); else Grid.findMoves(bs, -1);
    }
    public void move() {
	Grid.depth++;
	gn = ( moveForward ? fs : bs );
	numMoves = gn.getNumMoves();
	GN [] moves = gn.getMoves();
	if ( moveForward ) {
	    for (int k = 0; k < numMoves; k++) {
		GN gnk = moves[k];
		if ( -1 == gnk.direction ) { // a solution
		    /*
		    System.out.println();
		    System.out.println("Grid.moveCnt " + Grid.moveCnt);
		    System.out.println("f id " + gnk.id);
		    Grid.show(); 
		    Grid.showd(); 
		    */
		    Grid.solutionCnt++;
		    /*
		    System.out.println("----------- moveCnt " + Grid.moveCnt);
		    System.out.println("move f FOUND SOLUTION # " + Grid.solutionCnt);
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
		    // */
		    return;
		}
		if ( 0 == gnk.direction ) { // can go there
		    // System.out.println("move GO DEEPER " + gnk.id);
		    Grid.fCnt++;
		    gnk.pos = Grid.fCnt;
		    gnk.fPathLng = gn.fPathLng+1;
		    gnk.direction = 1;
		    gnk.parent = gn;
		    Grid.fPathLng++;
		    (new Nodeg(gnk, bs)).move();
		    // /*
		    gnk.pos = 0;
		    gnk.parent = null;
		    gnk.direction = 0;
		    gnk.fPathLng = gn.fPathLng-1;
		    Grid.fCnt--;
		    // */
		    Grid.fPathLng--;
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
		GN gnk = moves[k];
		if ( 1 == gnk.direction ) { // a solution
		    /*
		    System.out.println();
		    System.out.println("Grid.moveCnt " + Grid.moveCnt);
		    System.out.println("b id " + gnk.id);
		    Grid.show(); 
		    Grid.showd(); 
		    */
		    Grid.solutionCnt++;
		    /*
		    System.out.println("------------------------" );
		    System.out.println("move b FOUND SOLUTION # " + Grid.solutionCnt);
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
		if ( 0 == gnk.direction ) { // can go there
		    // System.out.println("move GO DEEPER " + gnk.id);
		    Grid.bCnt++;
		    gnk.pos = Grid.bCnt;
		    gnk.bPathLng = gn.bPathLng+1;
		    gnk.direction = -1;
		    gnk.parent = gn;
		    Grid.bPathLng++;
		    (new Nodeg(fs, gnk)).move();
		    // /*
		    gnk.pos = 0;
		    gnk.parent = null;
		    gnk.direction = 0;
		    gnk.bPathLng = gnk.bPathLng-1;
		    Grid.bCnt--;
		    // */
		    Grid.bPathLng--;
		    continue;
		} else { 
		    // visited earlier
		    // System.out.println("move b visited earlier");
		    continue;
		    // System.exit(0);
		}
	    } 
	}
	// System.out.println("move BACKTRACK gn depth " + gn.id + " " + Grid.depth);
	Grid.depth--;
	// return;
    } // end move
} // end Nodeg

class GN {
    protected int x = 0;
    protected int y = 0;
    protected String id = "";
    protected int pos = 0;
    protected GN north = null; 
    protected GN east = null; 
    protected GN south = null; 
    protected GN west = null;
    public void setNorth(GN n) { north = n; }
    public void setEast(GN n) { east = n; }
    public void setSouth(GN n) { south = n; }
    public void setWest(GN n) { west = n; }
    protected GN parent = null;
    public void setParent(GN n) { parent = n; }
    protected int direction = 0; // forward = 1; backward = -1
    protected int pathLength = -1;
    public void setPathLength(int x) { pathLength = x; }
    protected GN [] moves = new GN[4];
    public GN [] getMoves() { return moves; }
    protected int numMoves = -1;
    public int getNumMoves() { return numMoves; }
    protected int nextMove = 0;
    public int getNextMove() { int n = nextMove; nextMove++; return n; }
    protected int fPathLng = 0;
    protected int bPathLng = 0;
} // end GN
