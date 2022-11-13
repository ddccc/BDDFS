// File: c:/ddc/Java/Knight/GridB.java
// Date: Thu Sep 22 19:54:19 2022
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
/*  Two stack variant 
     This version uses the Grid4 version of the algorithm.

*/
/*
Y
         North
     West      East
         South  
0-0                  X
 */
public class GridBt {
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


    static GNBt [][] grid = new GNBt[lx][ly];
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

    static Hashtable<GNBt,String> locations = new Hashtable<GNBt,String>();

    static boolean donef = false; 
    static boolean doneb = ( !bidirection ? true : false ); 

    static Stack<SItemB> fStack = new Stack<SItemB>();
    static Stack<SItemB> bStack = new Stack<SItemB>();
    static boolean flip = true;


    public static void main(String[] args) {
	if ( !GridBt.bidirection ) doneb = true;
	System.out.println("bidirection " + bidirection +
			   " lx " + lx +
			   " ly " + ly); //  + " barrier1 " + barrier1 + 
	                                 // " barrier2 " + barrier2);

	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GNBt();
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
		GNBt gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }

	GNBt startState = grid[0][0]; startState.fPathLng = 0; 
	startState.direction = 1; startState.pos = 1; startState.visited = "+";
	GridBt.locations.put(startState, "+");
	GNBt goalState = grid[lx-1][ly-1]; goalState.bPathLng = 0;
	// GNBt goalState = grid[0][ly-1]; goalState.bPathLng = 0;
	goalState.direction = -1; goalState.pos = 1; goalState.visited = "-";
	GridBt.locations.put(goalState, "-");
	showg(startState); showg(goalState); 
	System.out.println("---");
	// showd(); showv(); 
	// System.exit(0);

	// create 2 Sitems
	SItemB startItem = new SItemB(startState, true);
	GridBt.fStack.push(startItem);
	SItemB goalItem = new SItemB(goalState, false);
	GridBt.bStack.push(goalItem);

	long startTime = System.currentTimeMillis();

	while ( !donef || !doneb ) { // execution loop
	    GridBt.moveCnt++;
	    if ( GridBt.bidirection ) // select bi or uni direction
	     	   GridBt.flip = !GridBt.flip;
	    // System.out.println("moveCnt " + GridBt.moveCnt);
	    // System.out.println("move direction " + GridBt.flip);
	    // GridBt.show(); GridBt.showd();  // GridBt.showv();

	    if ( GridBt.flip ) { // forward
		if ( GridBt.fStack.isEmpty() ) { donef = true; continue; }
		SItemB fItem = GridBt.fStack.peek();
		GNBt gn = fItem.gn;
		int fNumMoves = fItem.numMoves;
		int fNumExplored = fItem.numExplored;
		// fItem.show();
		boolean found = false;
		GNBt gnk = null;
		while ( fNumExplored < fNumMoves ) {
		    gnk = fItem.moves[fNumExplored];  
		    fItem.numExplored++; fNumExplored++;
		    if ( 1 == gnk.direction ) continue;
		    found = true;
		    break;
		}
		if ( !found ) {
		    /* Do (NOT) restore:
		    GridBt.fCnt--;
		    gn.pos = 0;
		    gn.parent = null;
		    gn.direction = 0;
		    gn.visited = " ";
		    // */
		    GridBt.fStack.pop();
		    if ( GridBt.fStack.isEmpty() ) { donef = true; continue; }
		    // fItem = GridBt.fStack.peek();
		    // fItem.backTrack = gn;
		    // GridBt.showd(); 
		    continue;
		}
		String direction = GridBt.locations.get(gnk);
		if ( null == direction ) {
		    // go down and continue
		    GridBt.locations.put(gnk, "+");
		    GridBt.fCnt++;
		    gnk.pos = GridBt.fCnt;
		    // gnk.fPathLng = gn.fPathLng+1;
		    gnk.direction = 1;
		    gnk.parent = gn;
		    gnk.visited = "+";
		    // GridBt.fPathLng++;
		    SItemB sgnk = new SItemB(gnk, true);
		    GridBt.fStack.push(sgnk);
		    continue;
		}
		if ( direction.equals("+") ) continue; // visited earlier
		// direction.equals("-")
		// a solution
		// GridBt.show(); 
		// GridBt.showd(); 
		GridBt.solutionCnt++;
		// { donef = true; doneb = true; } // for termination
		/*
		  show here 		      
		*/
		GridBt.fStack.pop(); // backtrack
		if ( GridBt.fStack.isEmpty() ) { donef = true; continue; }
		continue;
	    } else { // backward
		if ( GridBt.bStack.isEmpty() ) { doneb = true; continue; }
		SItemB bItem = GridBt.bStack.peek();
		GNBt gn = bItem.gn;
		int bNumMoves = bItem.numMoves;
		int bNumExplored = bItem.numExplored;
		// bItem.show();
		boolean found = false;
		GNBt gnk = null;
		while ( bNumExplored < bNumMoves ) {
		    gnk = bItem.moves[bNumExplored];  
		    bItem.numExplored++; bNumExplored++;
		    if ( -1 == gnk.direction ) continue;
		    found = true;
		    break;
		}
		if ( !found ) {
		    /* Do (NOT) restore:
		    GridBt.bCnt--;
		    gn.pos = 0;
		    gn.parent = null;
		    gn.direction = 0;
		    gn.visited = " ";
		    // */
		    GridBt.bStack.pop();
		    if ( GridBt.bStack.isEmpty() ) { doneb = true; continue; }
		    // bItem = GridBt.bStack.peek();
		    // bItem.backTrack = gn;
		    // GridBt.showd(); 
		    continue;
		}
		String direction = GridBt.locations.get(gnk);
		if ( null == direction ) {
		    // go down and continue
		    GridBt.locations.put(gnk, "-");
		    GridBt.bCnt++;
		    gnk.pos = GridBt.bCnt;
		    // gnk.bPathLng = gn.bPathLng+1;
		    gnk.direction = -1;
		    gnk.parent = gn;
		    gnk.visited = "-";
		    // GridBt.fPathLng++;
		    SItemB sgnk = new SItemB(gnk, false);
		    GridBt.bStack.push(sgnk);
		    continue;
		}
		if ( direction.equals("-") ) continue; // visited earlier
		// direction.equals("+")
		// a solution
		// GridBt.show(); 
		// GridBt.showd(); 
		GridBt.solutionCnt++;
		// { donef = true; doneb = true; } // for termination
		/*
		  show here 		      
		*/
		GridBt.bStack.pop(); // backtrack
		if ( GridBt.bStack.isEmpty() ) { donef = true; continue; }
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
	// System.out.println("\nGridBt.fCnt " + GridBt.fCnt + 
	//	   " GridBt.bCnt " + GridBt.bCnt);
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

    static public void showg(GNBt gn) {
	System.out.println(gn.id);
	System.out.println("direction " + gn.direction);
	int numMoves = gn.getNumMoves();
	System.out.println("numMoves " + numMoves);
	GNBt [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    System.out.print(k + " ");
	    GNBt gnk = moves[k];
	    System.out.print(gnk.id + " ");
	}
	System.out.println();
    }

    static void findMoves(GNBt gn, int dr) {
	// set numMovesand puts in moves candidate moves
	gn.numMoves = 0;
	GNBt gnn = gn.north;
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
    static void scramble(int numMoves, GNBt [] moves) { 
	// change order of the candidate moves in a GN
	// System.out.println("scramble numMoves " + numMoves);
	for (int i = 0; i < numMoves; i++) {
	    int a = random.nextInt(numMoves);
	    int b = random.nextInt(numMoves);
	    // System.out.println("scramble a b " + a + " " + b);
	    GNBt t = moves[a]; moves[a] = moves[b]; moves[b] = t;
	}
    } // end scramble()
    // */
} // end GridBt

class SItemB {
    protected GNBt gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected GNBt [] moves = new GNBt[4];
    protected int numExplored = 0;
    // protected boolean backTrack = false;
    SItemB(GNBt gnx, boolean b) {
	gn = gnx; moveForward = b;
	// sets moves & nummoves 
	// forward and backward easied:
	if ( moveForward ) GridBt.findMoves(gn, 1); else GridBt.findMoves(gn, -1);
	// forward hampered and backward easied:
	// if ( moveForward ) GridBt.findMoves(gn, 2); else GridBt.findMoves(gn, -1);
	// forward and backward hampered:
	// if ( moveForward ) GridBt.findMoves(gn, 2); else GridBt.findMoves(gn, -2);
	numMoves = gn.numMoves;
	moves = gn.moves;
    }
    /*
    public void show() { 
	System.out.println("SItemB moveForward " +  moveForward);
	System.out.println("numMoves " + numMoves + " numExplored " + numExplored);
    }
    */
} // end SItemB

class GNBt {
    protected int x = 0;
    protected int y = 0;
    protected String id = "";
    protected int pos = 0;
    protected GNBt north = null; 
    protected GNBt east = null; 
    protected GNBt south = null; 
    protected GNBt west = null;
    public void setNorth(GNBt n) { north = n; }
    public void setEast(GNBt n) { east = n; }
    public void setSouth(GNBt n) { south = n; }
    public void setWest(GNBt n) { west = n; }
    protected GNBt parent = null;
    public void setParent(GNBt n) { parent = n; }
    protected int direction = 0; // forward = 1; backward = -1
    protected int pathLength = -1;
    public void setPathLength(int x) { pathLength = x; }
    protected GNBt [] moves = new GNBt[4];
    public GNBt [] getMoves() { return moves; }
    protected int numMoves = -1;
    public int getNumMoves() { return numMoves; }
    protected int nextMove = 0;
    public int getNextMove() { int n = nextMove; nextMove++; return n; }
    protected int fPathLng = 0;
    protected int bPathLng = 0;
    protected String visited = " ";

} // end GNBt
