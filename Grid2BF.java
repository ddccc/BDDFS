// File: c:/ddc/Java/Knight/Grid2BF.java
// Date: Fri May 19 20:41:40 2023
// (C) OntoOO/ Dennis de Champeaux

/*
BF version
  This code is parametrized regarding::
     The sizes of the grid
     Search direction
     Trace output generated
     Successor operations ordering scrambled or not
     Hampering or easing the successor operations/nodes
        (hampering is adding moves in the wrong direction)
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
public class Grid2BF {
    // static Date date = new Date();
    // static Random random = new Random(date.getTime());
    static Random random = new Random(777); // repeatable results
    // static final int lx = 5; 
    // static final int lx = 6; 
     static final int lx = 10; 
    // static final int lx = 15; 
    // static final int lx = 20; 
    // static final int lx = 30; 
    // static final int lx = 40; 
    // static final int lx = 50; 
    // static final int lx = 60; 
    // static final int lx = 70; 
    // static final int lx = 80; 
    // static final int lx = 90; 
    // static final int lx = 100; 
    // static final int lx = 200; 

    // static final int ly = 10; 
    // static final int ly = 12; 
    // static final int ly = 20;
    // static final int ly = 25;
    // static final int ly = 30;
    // static final int ly = 35;
    // static final int ly = 40;
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
    // static final int ly = 100; 
    // static final int ly = 500; 
    // static final int ly = 1800; 
    static final int ly = 2000;
 
    static GN2BF [][] grid = new GN2BF[lx][ly];
    static int moveCnt = 0; 
    static int solutionCnt = 0; 
    static int fCnt = 1;
    static int bCnt = 1;
    static int fPathLng = 0;
    static int bPathLng = 0;
    static int depth = 0;

    // select one
    static boolean bidirection = true;
    //static boolean bidirection = false;

    static Hashtable<GN2BF,String> locations = new Hashtable<GN2BF,String>();

    static boolean flip = true;
    static boolean done = false;

    static Queue<GN2BF> fqueue = new LinkedList<GN2BF>();
    static Queue<GN2BF> bqueue = new LinkedList<GN2BF>();
    // offer(.) poll()
    public static void main(String[] args) {
	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GN2BF();
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
		GN2BF gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }
	GN2BF startState = grid[0][0]; startState.fPathLng = 0; startState.pos = 1;
	startState.direction = 1;  startState.visited = "+";
	locations.put(startState, "+");
	Grid2BF.fqueue.offer(startState);
	GN2BF goalState = grid[lx-1][ly-1]; goalState.bPathLng = 0; goalState.pos = 1;
	goalState.direction = -1; goalState.visited = "-";
	locations.put(goalState, "-");
	Grid2BF.bqueue.offer(goalState);
	showg(startState); showg(goalState);
	System.out.println("lx " + lx + " ly " + ly + " bidirection " + bidirection);
	// System.exit(0);
	boolean moveForward = true;
	long startTime = System.currentTimeMillis();
	while ( true ) {
	    if ( Grid2BF.done ) break;
	    // if ( 59 < Grid2BF.moveCnt ) System.exit(0);
	    // showd();
	    // show();
	    // System.out.println();
	    // System.out.println("Grid2BF.moveCnt " + Grid2BF.moveCnt);
	    Grid2BF.moveCnt++;
	    if ( Grid2BF.bidirection ) moveForward = !moveForward;
	    if ( moveForward ) {
		GN2BF fs = Grid2BF.fqueue.poll();
		if ( null == fs ) { Grid2BF.done = true; continue; }
		String direction = Grid2BF.locations.get(fs);
		// direction is +
		// findMoves sets numMoves and puts in moves candidate moves
		Grid2BF.findMoves(fs, 1);
		// forward hampered
		// Grid2BF.findMoves(fs, 2);
		// both hampered
		// Grid2BF.findMoves(fs, 2);
		for (int k = 0; k < fs.numMoves; k++) {
		    GN2BF gnk = fs.moves[k];
		    direction = Grid2BF.locations.get(gnk);
		    if ( null == direction ) {
			Grid2BF.locations.put(gnk, "+");
			Grid2BF.fCnt++;
			gnk.pos = Grid2BF.fCnt;
			// gnk.fPathLng = gn.fPathLng+1;
			gnk.direction = 1; 
			gnk.parent = fs;
			gnk.visited = "+"; 
			// Grid2BF.fPathLng++;
			Grid2BF.fqueue.offer(gnk);
			continue;
		    }
		    if ( direction.equals("+") ) continue; // visited already
		    if ( direction.equals("-") ) { // goal
			/*
			System.out.println("Goal");
			// System.out.println("Grid2BF.moveCnt " + Grid2BF.moveCnt);
			System.out.println("f id " + fs.id + " pos " + fs.pos);
			Grid2BF.show(); 
			// Grid2BF.showd(); 
			// */
			Grid2BF.solutionCnt++;
			// System.out.println("Goal cnt: " + Grid2BF.solutionCnt);
			Grid2BF.done = true; // terminate with a solution
			continue;
		    }
		}
	    } else { // backward
		GN2BF bs = Grid2BF.bqueue.poll();
		if ( null == bs ) { Grid2BF.done = true; continue; }
		String direction = Grid2BF.locations.get(bs);
		// direction is -
		// findMoves sets numMoves and puts in moves candidate moves
		Grid2BF.findMoves(bs, -1);
		// forward hampered
		// Grid2BF.findMoves(fs, 2);
		// both hampered
		// Grid2BF.findMoves(fs, 2);
		for (int k = 0; k < bs.numMoves; k++) {
		    GN2BF gnk = bs.moves[k];
		    direction = Grid2BF.locations.get(gnk);
		    if ( null == direction ) {
			Grid2BF.locations.put(gnk, "-");
			Grid2BF.bCnt++;
			gnk.pos = Grid2BF.bCnt;
			// gnk.fPathLng = gn.fPathLng+1;
			gnk.direction = -1; 
			gnk.parent = bs;
			gnk.visited = "-"; 
			// Grid2BF.fPathLng++;
			Grid2BF.bqueue.offer(gnk);
			continue;
		    }
		    if ( direction.equals("-") ) continue; // visited already
		    if ( direction.equals("+") ) { // goal
			/*
			System.out.println("Goal");
			// System.out.println("Grid2BF.moveCnt " + Grid2BF.moveCnt);
			System.out.println("b id " + bs.id + " pos " + bs.pos);
			Grid2BF.show(); 
			// Grid2BF.showd(); 
			// */
			Grid2BF.solutionCnt++;
			Grid2BF.done = true; // terminate with a solution
			continue;
		    }
		}
	    }

	}

	// Nodeg2BF initNode = new Nodeg2BF();
	// initNode.move();
	long endTime = System.currentTimeMillis();
	System.out.println("timing " + (endTime-startTime));
	System.out.println("solutionCnt " + solutionCnt);
	System.out.println("moveCnt " + moveCnt);
	/*
	// System.out.println("depth " + depth);
	showd();
	show();
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
    static public void showg(GN2BF gn) {
	System.out.println(gn.id);
	System.out.println("direction " + gn.direction);
	int numMoves = gn.getNumMoves();
	System.out.println("numMoves " + numMoves);
	GN2BF [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    System.out.print(k + " ");
	    GN2BF gnk = moves[k];
	    System.out.print(gnk.id + " ");
	}
	System.out.println();
    } // end showg

    static void findMoves(GN2BF gn, int dr) {
	// set numMovesand puts in moves candidate moves
	gn.numMoves = 0;
	GN2BF gnn = gn.north;
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
    static void scramble(int numMoves, GN2BF [] moves) { 
	// change order of the candidate moves in a GN
	// System.out.println("scramble numMoves " + numMoves);
	for (int i = 0; i < numMoves; i++) {
	    int a = random.nextInt(numMoves);
	    int b = random.nextInt(numMoves);
	    // System.out.println("scramble a b " + a + " " + b);
	    GN2BF t = moves[a]; moves[a] = moves[b]; moves[b] = t;
	}
    } // end scramble()
    // */
} // end Grid2BF



class GN2BF {
    protected int x = 0;
    protected int y = 0;
    protected String id = "";
    protected int pos = 0;
    protected GN2BF north = null; 
    protected GN2BF east = null; 
    protected GN2BF south = null; 
    protected GN2BF west = null;
    public void setNorth(GN2BF n) { north = n; }
    public void setEast(GN2BF n) { east = n; }
    public void setSouth(GN2BF n) { south = n; }
    public void setWest(GN2BF n) { west = n; }
    protected GN2BF parent = null;
    public void setParent(GN2BF n) { parent = n; }
    protected int direction = 0; // forward = 1; backward = -1
    protected int pathLength = -1;
    public void setPathLength(int x) { pathLength = x; }
    protected GN2BF [] moves = new GN2BF[4];
    public GN2BF [] getMoves() { return moves; }
    protected int numMoves = -1;
    public int getNumMoves() { return numMoves; }
    protected int nextMove = 0;
    public int getNextMove() { int n = nextMove; nextMove++; return n; }
    protected int fPathLng = 0;
    protected int bPathLng = 0;
    protected String visited = " ";
} // end GN2BF
