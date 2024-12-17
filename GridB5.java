// File: c:/ddc/Java/Knight/GridB5.java
// Date: Sun Nov 17 11:37:24 2024
// (C) OntoOO/ Dennis de Champeaux

/*
  This code is parametrized regarding::
     Number of barriers
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
     This version uses the GridB version of the algorithm.

*/

/*
Y
         North
     West      East
         South  
0-0                  X
 */
public class GridB5 {
    // static Date date = new Date();
    // static Random random = new Random(date.getTime());
    static Random random = new Random(777); // repeatable results
    // static Random random = new Random(771); // repeatable results
    // static final int lx = 5;
    // static final int lx = 6; 
    // static final int lx = 10; 
    // static final int lx = 15; 
    static final int lx = 20; 
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
    // static final int ly = 1800;
    // static final int ly = 2000;
    // static final int ly = 3200;
    // static final int ly = 6400;
    // static final int ly = 12800;
    
    // static final int ly = 1800; 
    // static final int ly = 2000; 
    // static final int ly = 2100; 

    static final int ly = 20000; 

    static GNB5 [][] grid = new GNB5[lx][ly];
    static int moveCnt = 0; 
    static int solutionCnt = 0; 
    static int fCnt = 1;
    static int bCnt = 1;
    //    static int fPathLng = 0;
    //    static int bPathLng = 0;
    // static int fdepth = 0;
    // static int bdepth = 0;

    /*
    static int bx = ly/3;
    static int barrier1 = bx - 1;
    static int barrier2 = ly - bx;
    // */
    // static int numBarriers = 2; // 4, 6, ..
    // static int numBarriers = 4; // 4, 6, ..
    // static int numBarriers = 6; // 4, 6, ..
    // static int numBarriers = 8; // 4, 6, ..
    static int numBarriers = 10; // 4, 6, ..
    static int barrierDistance = ly/(numBarriers+1);

    // select one
    static boolean bidirection = true;
    // static boolean bidirection = false;

    static Hashtable<GNB5,String> locations = new Hashtable<GNB5,String>();

    static boolean donef = false; 
    static boolean doneb = ( !bidirection ? true : false ); 

    static Stack<SItemB5> fStack = new Stack<SItemB5>();
    static Stack<SItemB5> bStack = new Stack<SItemB5>();
    static boolean flip = true;


    public static void main(String[] args) {
	if ( !GridB5.bidirection ) doneb = true;
	System.out.println("bidirection " + bidirection +
			   " lx " + lx +
			   " ly " + ly); //  + " barrier1 " + barrier1 + 
	                                 // " barrier2 " + barrier2);
	for ( int i = 0; i < lx; i++ )
	    for ( int j = 0; j < ly; j++ ) grid[i][j] = new GNB5();
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
	int yb = barrierDistance;
	for ( int n = 0; n < numBarriers/2; n++ ) {
	    for ( int k = 0; k < lx-1; k++ ) {
		grid[k][yb].south = null; 
		grid[k][yb-1].north = null; 
	    }
	    yb = yb + barrierDistance;
	    for ( int k = 1; k < lx; k++ ) {
		grid[k][yb].north = null; 
		grid[k][yb+1].south = null; 
	    }
	}

	for ( int i = 0; i < lx; i++ ) 
	    for ( int j = 0; j < ly; j++ ) {
		GNB5 gnij = grid[i][j];
		gnij.x = i; gnij.y = j;
		gnij.id = i + "-" + j;
		findMoves(gnij, 0);
	    }
	/*
	// check new barriers
	int barrierX = 2* barrierDistance;
	for ( int k = 0; k < lx; k++ ) {
	    System.out.print(k + " ");
	    GNB5 gnj = grid[k][barrierX]; // or ...
	    System.out.print("gnj " + gnj.id);
	    // System.out.print(" north " + gnj.north.id);
	    System.out.print(" north " + (null== gnj.north ? null : gnj.north.id));
	    // System.out.println(" south " + gnj.south.id);
	    System.out.println(" south " + (null== gnj.south ? null : gnj.south.id));
	    // System.out.print(" west " + gnj.west.id);
	    System.out.print(" west " + (null== gnj.west ? null : gnj.west.id));
	    // System.out.println(" east " + gnj.east.id);
	    System.out.println(" east " + (null== gnj.east ? null : gnj.east.id));
	    System.out.println();
	}
	// */
	// showd(); showv(); 
	// System.exit(0);



	GNB5 startState = grid[0][0]; startState.fPathLng = 0; 
	startState.direction = 1; startState.pos = 1; startState.visited = "+";
	GridB5.locations.put(startState, "+");
	GNB5 goalState = grid[lx-1][ly-1]; goalState.bPathLng = 0;
	// GNB5 goalState = grid[0][ly-1]; goalState.bPathLng = 0;
	goalState.direction = -1; goalState.pos = 1; goalState.visited = "-";
	GridB5.locations.put(goalState, "-");
	showg(startState); showg(goalState); 
	System.out.println("---");
        // showd(); showv(); 
	// System.exit(0);

	// create 2 Sitems
	SItemB5 startItem = new SItemB5(startState, true);
	GridB5.fStack.push(startItem);
	SItemB5 goalItem = new SItemB5(goalState, false);
	GridB5.bStack.push(goalItem);

	long startTime = System.currentTimeMillis();

	while ( !donef || !doneb ) { // execution loop
	    GridB5.moveCnt++;
	    // System.out.println("moveCnt " + GridB5.moveCnt);
	    if ( GridB5.bidirection ) // select bi or uni direction
	     	   GridB5.flip = !GridB5.flip;
	    // System.out.println("moveCnt " + GridB5.moveCnt);
	    // System.out.println("move direction " + GridB5.flip);
	    // GridB5.show(); GridB5.showd();  // GridB5.showv();

	    if ( GridB5.flip ) { // forward
		if ( GridB5.fStack.isEmpty() ) { donef = true; continue; }
		SItemB5 fItem = GridB5.fStack.peek();
		GNB5 gn = fItem.gn;
		int fNumMoves = fItem.numMoves;
		int fNumExplored = fItem.numExplored;
		// fItem.show();
		boolean found = false;
		GNB5 gnk = null;
		while ( fNumExplored < fNumMoves ) {
		    gnk = fItem.moves[fNumExplored];  
		    fItem.numExplored++; fNumExplored++;
		    if ( 1 == gnk.direction ) continue;
		    found = true;
		    break;
		}
		if ( !found ) {
		    // /* Do (NOT) restore:
		    GridB5.fCnt--;
		    gn.pos = 0;
		    gn.parent = null;
		    gn.direction = 0;
		    gn.visited = " ";
		    // */
		    GridB5.fStack.pop();
		    if ( GridB5.fStack.isEmpty() ) { donef = true; continue; }
		    // fItem = GridB5.fStack.peek();
		    // fItem.backTrack = gn;
		    // GridB5.showd(); 
		    continue;
		}
		String direction = GridB5.locations.get(gnk);
		if ( null == direction ) {
		    // go down and continue
		    GridB5.locations.put(gnk, "+");
		    GridB5.fCnt++;
		    gnk.pos = GridB5.fCnt;
		    // gnk.fPathLng = gn.fPathLng+1;
		    gnk.direction = 1;
		    gnk.parent = gn;
		    gnk.visited = "+";
		    // GridB5.fPathLng++;
		    SItemB5 sgnk = new SItemB5(gnk, true);
		    GridB5.fStack.push(sgnk);
		    continue;
		}
		if ( direction.equals("+") ) continue; // visited earlier
		// direction.equals("-")
		// a solution
		// GridB5.show(); 
		// GridB5.showd(); 

		GridB5.solutionCnt++;
		{ donef = true; doneb = true; } // for termination
		/*
		show();
		showd();
		showv();
		//*/
		GridB5.fStack.pop(); // backtrack
		if ( GridB5.fStack.isEmpty() ) { donef = true; continue; }
		continue;
	    } else { // backward
		if ( GridB5.bStack.isEmpty() ) { doneb = true; continue; }
		SItemB5 bItem = GridB5.bStack.peek();
		GNB5 gn = bItem.gn;
		int bNumMoves = bItem.numMoves;
		int bNumExplored = bItem.numExplored;
		// bItem.show();
		boolean found = false;
		GNB5 gnk = null;
		while ( bNumExplored < bNumMoves ) {
		    gnk = bItem.moves[bNumExplored];  
		    bItem.numExplored++; bNumExplored++;
		    if ( -1 == gnk.direction ) continue;
		    found = true;
		    break;
		}
		if ( !found ) {
		    // /* Do (NOT) restore:
		    GridB5.bCnt--;
		    gn.pos = 0;
		    gn.parent = null;
		    gn.direction = 0;
		    gn.visited = " ";
		    // */
		    GridB5.bStack.pop();
		    if ( GridB5.bStack.isEmpty() ) { doneb = true; continue; }
		    // bItem = GridB5.bStack.peek();
		    // bItem.backTrack = gn;
		    // GridB5.showd(); 
		    continue;
		}
		String direction = GridB5.locations.get(gnk);
		if ( null == direction ) {
		    // go down and continue
		    GridB5.locations.put(gnk, "-");
		    GridB5.bCnt++;
		    gnk.pos = GridB5.bCnt;
		    // gnk.bPathLng = gn.bPathLng+1;
		    gnk.direction = -1;
		    gnk.parent = gn;
		    gnk.visited = "-";
		    // GridB5.fPathLng++;
		    SItemB5 sgnk = new SItemB5(gnk, false);
		    GridB5.bStack.push(sgnk);
		    continue;
		}
		if ( direction.equals("-") ) continue; // visited earlier
		// direction.equals("+")
		// a solution
		// GridB5.show(); 
		// GridB5.showd(); 
		/*
		System.out.println("gnk.x " + gnk.x + " .y " + gnk.y + " pos " +
				   gnk.pos);
		System.out.println("fCnt " + GridB5.fCnt + " bCnt " + GridB5.bCnt);
		*/
		GridB5.solutionCnt++;
		{ donef = true; doneb = true; } // for termination
		/*
		show();
		showd();
		showv();
		//*/
		GridB5.bStack.pop(); // backtrack
		if ( GridB5.bStack.isEmpty() ) { donef = true; continue; }
		continue;
		
	    }
	} // end while


	long endTime = System.currentTimeMillis();
	System.out.println("\ntiming " + (endTime-startTime));
	System.out.println("solutionCnt " + solutionCnt);
	System.out.println("moveCnt " + moveCnt);
	/*
	// show();
	System.out.println();
	show();  // pos numbers	
	showd(); // f & b
	showv(); // + & -
	// System.out.println("\nGridB5.fCnt " + GridB5.fCnt + 
	//	   " GridB5.bCnt " + GridB5.bCnt);
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

    static public void showg(GNB5 gn) {
	System.out.println(gn.id);
	System.out.println("direction " + gn.direction);
	int numMoves = gn.getNumMoves();
	System.out.println("numMoves " + numMoves);
	GNB5 [] moves = gn.getMoves();
	for (int k = 0; k < numMoves; k++) {
	    System.out.print(k + " ");
	    GNB5 gnk = moves[k];
	    System.out.print(gnk.id + " ");
	}
	System.out.println();
    }

    static void findMoves(GNB5 gn, int dr) {
	// set numMovesand puts in moves candidate moves
	gn.numMoves = 0;
	GNB5 gnn = gn.north;
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
    static void scramble(int numMoves, GNB5 [] moves) { 
	// change order of the candidate moves in a GN
	// System.out.println("scramble numMoves " + numMoves);
	for (int i = 0; i < numMoves; i++) {
	    int a = random.nextInt(numMoves);
	    int b = random.nextInt(numMoves);
	    // System.out.println("scramble a b " + a + " " + b);
	    GNB5 t = moves[a]; moves[a] = moves[b]; moves[b] = t;
	}
    } // end scramble()
    // */
} // end GridB5

class SItemB5 {
    protected GNB5 gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected GNB5 [] moves = new GNB5[4];
    protected int numExplored = 0;
    // protected boolean backTrack = false;
    SItemB5(GNB5 gnx, boolean b) {
	gn = gnx; moveForward = b;
	// sets moves & nummoves 
	// forward and backward easied:
	// if ( moveForward ) GridB5.findMoves(gn, 1); else GridB5.findMoves(gn, -1);
	// forward hampered and backward easied:
	// if ( moveForward ) GridB5.findMoves(gn, 2); else GridB5.findMoves(gn, -1);
	// forward and backward hampered:
	if ( moveForward ) GridB5.findMoves(gn, 2); else GridB5.findMoves(gn, -2);
	numMoves = gn.numMoves;
	moves = gn.moves;
    }
    /*
    public void show() { 
	System.out.println("SItemB5 moveForward " +  moveForward);
	System.out.println("numMoves " + numMoves + " numExplored " + numExplored);
    }
    */
} // end SItemB5

class GNB5 {
    protected int x = 0;
    protected int y = 0;
    protected String id = "";
    protected int pos = 0;
    protected GNB5 north = null; 
    protected GNB5 east = null; 
    protected GNB5 south = null; 
    protected GNB5 west = null;
    public void setNorth(GNB5 n) { north = n; }
    public void setEast(GNB5 n) { east = n; }
    public void setSouth(GNB5 n) { south = n; }
    public void setWest(GNB5 n) { west = n; }
    protected GNB5 parent = null;
    public void setParent(GNB5 n) { parent = n; }
    protected int direction = 0; // forward = 1; backward = -1
    protected int pathLength = -1;
    public void setPathLength(int x) { pathLength = x; }
    protected GNB5 [] moves = new GNB5[4];
    public GNB5 [] getMoves() { return moves; }
    protected int numMoves = -1;
    public int getNumMoves() { return numMoves; }
    protected int nextMove = 0;
    public int getNextMove() { int n = nextMove; nextMove++; return n; }
    protected int fPathLng = 0;
    protected int bPathLng = 0;
    protected String visited = " ";

} // end GNB5
