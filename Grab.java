// File: c:/ddc/Java/Knight/Grabjava
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
public class Grab {
    // static Date date = new Date();
    // static Random random = new Random(date.getTime());
    static Random random = new Random(777); // repeatable results
    // static final int lx = 6; 
    static final int lx = 600; 
    // static final int ly = 10; 
    static final int ly = 2000; 
    static GNB [][] grid = new GNB[lx][ly];
    static int moveCnt = 0; 
    static int solutionCnt = 0; 
    static int fCnt = 1;
    static int bCnt = 1;
    static int fLevel = 1;
    static int bLevel = ly-1;
    static int depth = 0;
    // Select one of the two below:
    static boolean modeBidirection = true;
    static boolean moveForward = false;
    // static boolean modeBidirection = false;
    // static boolean moveForward = true;


    public static void main(String[] args) {
	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GNB();
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
		GNB gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }
	GNB startState = grid[0][0]; 
	startState.direction = 1;
	startState.pos = 1;
	GNB goalState = grid[lx-1][ly-1]; 
	goalState.direction = -1;
	goalState.pos = 1;
	showg(startState); showg(goalState); 
	// showd();
	// System.exit(0);
	Nodeg initNode = new Nodeg(startState, goalState);
	long startTime = System.currentTimeMillis();
	initNode.move();
	long endTime = System.currentTimeMillis();
	System.out.println("timing " + (endTime-startTime));
	System.out.println("solutionCnt " + solutionCnt);
	System.out.println("moveCnt " + moveCnt);
	System.out.println("# tiles      " + (lx*ly));
	System.out.println("# set tiles  " + (fCnt+bCnt));
	System.out.println("# free tiles " + (lx*ly - (fCnt+bCnt)));
	// show();
	// showd();

    } // end main

    static public void show1(int i, int j) {
	// System.out.println("i j " + i + " " + j + " " + grid[i][j].id);
	int n = grid[i][j].pos;
	if ( 0 == n ) System.out.print("   "); else
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
	if ( 0 == n ) System.out.print("   ");
	else if ( 1 == n ) System.out.print(" f "); 
	else System.out.print(" b "); 
    } //
    static public void showd() {
	for ( int j = 0; j < lx; j++ ) {
	    for ( int i = 0; i < ly; i++ ) showd1(j, i);
	    System.out.println();
	}
    } // end showd


    static public void showg(GNB gn) {
	System.out.println(gn.id);
	System.out.println("direction " + gn.direction);
	int numMoves = gn.getNumMoves();
	System.out.println("numMoves " + numMoves);
	GNB [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    System.out.print(k + " ");
	    GNB gnk = moves[k];
	    System.out.print(gnk.id + " ");
	}
	System.out.println();
    }
    static void findMoves(GNB gn, int dr) {
	// set numMovesand puts in moves candidate moves
	gn.numMoves = 0;
	GNB gnn = gn.north;
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
    static void scramble(int numMoves, GNB [] moves) { 
	// change order of the candidate moves in a GNB
	// System.out.println("scramble numMoves " + numMoves);
	for (int i = 0; i < numMoves; i++) {
	    int a = random.nextInt(numMoves);
	    int b = random.nextInt(numMoves);
	    // System.out.println("scramble a b " + a + " " + b);
	    GNB t = moves[a]; moves[a] = moves[b]; moves[b] = t;
	}
    } // end scramble()
    // */
} // end Grab

class Nodeg { 
    private GNB fs, bs, gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected GNB [] moves = new GNB[4];
    Nodeg(GNB ssx, GNB bsx) {
	Grab.moveCnt++;
	fs = ssx; bs = bsx;
    }
    public void move() {
	Grab.depth++;
	// Select one of the next three
	// moveForward = true; // unidirectional search
	// moveForward = false; // unidirectional search
	// bidirectional search
	if ( Grab.modeBidirection ) Grab.moveForward = !Grab.moveForward; else
	    Grab.moveForward = true; // or set to false
	moveForward = Grab.moveForward;

	// findMoves sets numMoves and puts in moves candidate moves
	if ( moveForward ) Grab.findMoves(fs, 1); else Grab.findMoves(bs, -1);

	gn = ( moveForward ? fs : bs );
	numMoves = gn.getNumMoves();
	GNB [] moves = gn.getMoves();

	if ( moveForward ) {
	    for (int k = 0; k < numMoves; k++) {
		GNB gnk = moves[k];
		if (Grab.bLevel <= Grab.fLevel) { // possible solution
		    if ( -1 == gnk.direction ) {
			// System.out.println("move f SOLUTION");
			 Grab.solutionCnt++;
			 // Grab.show(); 
			 // Grab.showd(); 
			 // System.exit(0);
			 continue;
		    }
		    if ( 1 == gnk.direction ) continue;
		    if ( Grab.fLevel <  gnk.y ) continue; // because ...
		    gnk.direction = 1;
		    Grab.fCnt++;
		    gnk.pos = Grab.fCnt;
		    (new Nodeg(gnk, bs)).move();
		    // System.exit(0);
		    continue;
		}
		if ( 0 != gnk.direction ) continue;
		gnk.direction = 1;
		Grab.fCnt++;
		if ( Grab.fLevel < gnk.y ) Grab.fLevel = gnk.y;
		gnk.pos = Grab.fCnt;
		(new Nodeg(gnk, bs)).move();
		/*
		  gnk.pos = 0;
		  gnk.direction = 0;
		  Grab.fCnt--;
	        // */
	    }
	} else { // move backward
	    for (int k = 0; k < numMoves; k++) {
		GNB gnk = moves[k];
		if (Grab.bLevel <= Grab.fLevel) { // possible solution
		    if ( 1 == gnk.direction ) {
			// System.out.println("move b SOLUTION");
			Grab.solutionCnt++;
			// Grab.show(); 
			// Grab.showd(); 
			// System.exit(0);
			 continue;
		    }
		    if ( -1 == gnk.direction ) continue;
		    if ( gnk.y < Grab.bLevel ) continue; // because ...
		    gnk.direction = -1;
		    Grab.bCnt++;
		    gnk.pos = Grab.bCnt;
		    (new Nodeg(fs, gnk)).move();
		    continue;
		}
		if ( 0 != gnk.direction ) continue;
		gnk.direction = -1;
		Grab.bCnt++;
		if ( gnk.y < Grab.bLevel ) Grab.bLevel = gnk.y;
		gnk.pos = Grab.bCnt;
		(new Nodeg(fs, gnk)).move();
		/*
		  gnk.pos = 0;
		  gnk.direction = 0;
		  Grab.bCnt--;
	        // */
	    } 
	}
	Grab.depth--;
    } // end move
} // end Nodeg

class GNB {
    protected int x = 0;
    protected int y = 0;
    protected String id = "";
    protected int pos = 0;
    protected GNB north = null; 
    protected GNB east = null; 
    protected GNB south = null; 
    protected GNB west = null;
    public void setNorth(GNB n) { north = n; }
    public void setEast(GNB n) { east = n; }
    public void setSouth(GNB n) { south = n; }
    public void setWest(GNB n) { west = n; }
    protected int direction = 0; // forward = 1; backward = -1
    protected GNB [] moves = new GNB[4];
    public GNB [] getMoves() { return moves; }
    protected int numMoves = -1;
    public int getNumMoves() { return numMoves; }
    protected int nextMove = 0;
    public int getNextMove() { int n = nextMove; nextMove++; return n; }

} // end GNB
