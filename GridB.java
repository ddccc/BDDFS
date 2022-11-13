// File: c:/ddc/Java/Knight/GridB.java
// Date: Thu Sep 22 19:54:19 2022, Sat Nov 12 21:04:16 2022
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
public class GridB {
    // static Date date = new Date();
    // static Random random = new Random(date.getTime());
    static Random random = new Random(777); // repeatable results
    // static Random random = new Random(771); // repeatable results
    // static final int lx = 5;
    static final int lx = 6; 
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

    // static final int ly = 80;
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

    // /*
    static int bx = ly/3;
    static int barrier1 = bx - 1;
    static int barrier2 = ly - bx;
    // */

    // select one
    static boolean bidirection = true;
    // static boolean bidirection = false;

    static Hashtable<GNB2,String> locations = new Hashtable<GNB2,String>();

    static boolean donef = false; 
    static boolean doneb = ( !bidirection ? true : false ); 

    static Stack<SItemB> fStack = new Stack<SItemB>();
    static Stack<SItemB> bStack = new Stack<SItemB>();
    static boolean flip = true;


    public static void main(String[] args) {
	if ( !GridB.bidirection ) doneb = true;
	System.out.println("bidirection " + bidirection +
			   " lx " + lx +
			   " ly " + ly); //  + " barrier1 " + barrier1 + 
	                                 // " barrier2 " + barrier2);
	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GNB2();
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
	///*
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
		GNB2 gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }
	// */
	/*
	// check the new barriers
	for ( int k = 0; k < lx; k++ ) {
	    System.out.print(k + " ");
	    GNB2 gnj = grid[k][barrier2]; // or barrier1
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


	GNB2 startState = grid[0][0]; startState.fPathLng = 0; 
	startState.direction = 1; startState.pos = 1; startState.visited = "+";
	GridB.locations.put(startState, "+");
	GNB2 goalState = grid[lx-1][ly-1]; goalState.bPathLng = 0;
	// GNB2 goalState = grid[0][ly-1]; goalState.bPathLng = 0;
	goalState.direction = -1; goalState.pos = 1; goalState.visited = "-";
	GridB.locations.put(goalState, "-");
	showg(startState); showg(goalState); 
	System.out.println("---");
	// showd(); showv(); 
	// System.exit(0);

	// create 2 Sitems
	SItemB startItem = new SItemB(startState, true);
	GridB.fStack.push(startItem);
	SItemB goalItem = new SItemB(goalState, false);
	GridB.bStack.push(goalItem);

	long startTime = System.currentTimeMillis();

	while ( !donef || !doneb ) { // execution loop
	    GridB.moveCnt++;
	    // System.out.println("moveCnt " + GridB.moveCnt);
	    // if ( 5000 < GridB.solutionCnt ) { 
	    //    donef = true; doneb = true; continue; }
	    if ( GridB.bidirection ) // select bi or uni direction
	     	   GridB.flip = !GridB.flip;
	    // System.out.println("moveCnt " + GridB.moveCnt);
	    // System.out.println("move direction " + GridB.flip);
	    // GridB.show(); GridB.showd();  // GridB.showv();

	    if ( GridB.flip ) { // forward
		if ( GridB.fStack.isEmpty() ) { donef = true; continue; }
		SItemB fItem = GridB.fStack.peek();
		GNB2 gn = fItem.gn;
		int fNumMoves = fItem.numMoves;
		int fNumExplored = fItem.numExplored;
		// fItem.show();
		boolean found = false;
		GNB2 gnk = null;
		while ( fNumExplored < fNumMoves ) {
		    gnk = fItem.moves[fNumExplored];  
		    fItem.numExplored++; fNumExplored++;
		    if ( 1 == gnk.direction ) continue;
		    found = true;
		    break;
		}
		if ( !found ) {
		    // /* Do (NOT) restore:
		    GridB.fCnt--;
		    gn.pos = 0;
		    gn.parent = null;
		    gn.direction = 0;
		    gn.visited = " ";
		    // */
		    GridB.fStack.pop();
		    if ( GridB.fStack.isEmpty() ) { donef = true; continue; }
		    // fItem = GridB.fStack.peek();
		    // fItem.backTrack = gn;
		    // GridB.showd(); 
		    continue;
		}
		String direction = GridB.locations.get(gnk);
		if ( null == direction ) {
		    // go down and continue
		    GridB.locations.put(gnk, "+");
		    GridB.fCnt++;
		    gnk.pos = GridB.fCnt;
		    // gnk.fPathLng = gn.fPathLng+1;
		    gnk.direction = 1;
		    gnk.parent = gn;
		    gnk.visited = "+";
		    // GridB.fPathLng++;
		    SItemB sgnk = new SItemB(gnk, true);
		    GridB.fStack.push(sgnk);
		    continue;
		}
		if ( direction.equals("+") ) continue; // visited earlier
		// direction.equals("-")
		// a solution
		// GridB.show(); 
		// GridB.showd(); 
		GridB.solutionCnt++;
		{ donef = true; doneb = true; } // for termination
		/*
		  show here 		      
		*/
		GridB.fStack.pop(); // backtrack
		if ( GridB.fStack.isEmpty() ) { donef = true; continue; }
		continue;
	    } else { // backward
		if ( GridB.bStack.isEmpty() ) { doneb = true; continue; }
		SItemB bItem = GridB.bStack.peek();
		GNB2 gn = bItem.gn;
		int bNumMoves = bItem.numMoves;
		int bNumExplored = bItem.numExplored;
		// bItem.show();
		boolean found = false;
		GNB2 gnk = null;
		while ( bNumExplored < bNumMoves ) {
		    gnk = bItem.moves[bNumExplored];  
		    bItem.numExplored++; bNumExplored++;
		    if ( -1 == gnk.direction ) continue;
		    found = true;
		    break;
		}
		if ( !found ) {
		    // /* Do (NOT) restore:
		    GridB.bCnt--;
		    gn.pos = 0;
		    gn.parent = null;
		    gn.direction = 0;
		    gn.visited = " ";
		    // */
		    GridB.bStack.pop();
		    if ( GridB.bStack.isEmpty() ) { doneb = true; continue; }
		    // bItem = GridB.bStack.peek();
		    // bItem.backTrack = gn;
		    // GridB.showd(); 
		    continue;
		}
		String direction = GridB.locations.get(gnk);
		if ( null == direction ) {
		    // go down and continue
		    GridB.locations.put(gnk, "-");
		    GridB.bCnt++;
		    gnk.pos = GridB.bCnt;
		    // gnk.bPathLng = gn.bPathLng+1;
		    gnk.direction = -1;
		    gnk.parent = gn;
		    gnk.visited = "-";
		    // GridB.fPathLng++;
		    SItemB sgnk = new SItemB(gnk, false);
		    GridB.bStack.push(sgnk);
		    continue;
		}
		if ( direction.equals("-") ) continue; // visited earlier
		// direction.equals("+")
		// a solution
		// GridB.show(); 
		// GridB.showd(); 
		GridB.solutionCnt++;
		{ donef = true; doneb = true; } // for termination
		/*
		  show here 		      
		*/
		GridB.bStack.pop(); // backtrack
		if ( GridB.bStack.isEmpty() ) { donef = true; continue; }
		continue;
		
	    }
	} // end while


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
	// System.out.println("\nGridB.fCnt " + GridB.fCnt + 
	//	   " GridB.bCnt " + GridB.bCnt);
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
} // end GridB

class SItemB {
    protected GNB2 gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected GNB2 [] moves = new GNB2[4];
    protected int numExplored = 0;
    // protected boolean backTrack = false;
    SItemB(GNB2 gnx, boolean b) {
	gn = gnx; moveForward = b;
	// sets moves & nummoves 
	// forward and backward easied:
	if ( moveForward ) GridB.findMoves(gn, 1); else GridB.findMoves(gn, -1);
	// forward hampered and backward easied:
	// if ( moveForward ) GridB.findMoves(gn, 2); else GridB.findMoves(gn, -1);
	// forward and backward hampered:
	// if ( moveForward ) GridB.findMoves(gn, 2); else GridB.findMoves(gn, -2);
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
