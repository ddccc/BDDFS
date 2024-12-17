// File: c:/ddc/Java/Knight/Knight4.java
// Date:Thu Sep 15 18:43:38 2022/ Thu Nov 28 19:29:37 2024
// (C) OntoOO/ Dennis de Champeaux

/* +++++++++++++++==
  This is a uni-directional version of Knight3 using the Grid4 type version
  of the algorithm - not using java recursion, but using f & b simulated 
  recursion.  It showed that the latter is too slow.
 */


// Create cyclic knight tours on 8x8 or 6x6 boards
import java.io.*;
import java.util.*;

public class Knight4 {
    // The board is a linear array with all sides having an edge of size 2.

    // static final int side = 8; 
    // the numbers shown in red ar for a 8x8 board   
    static final int side = 6;    
    static final int chokeParam = ( 6 == side ? 33 : 60 ); 
    static final int edge = 2;
    static final int side2 = side + 2 * edge; // 12
    static final int lng = side2 * side2; 
    static final int twoside2 = 2 * side2; // 24
    static final int startState1 = twoside2 + edge; // 26
    static final int startState = 3 * side2 + edge + 2; // 40
    static final int goalState = 4 * side2 + edge + 1; // 51
    static Tile4 [] board = new Tile4[lng];
    static final int numMoves = 8;
    static final int maxTilesSet = side*side;
    static final int halfTilesSet = maxTilesSet/2;
    static int [] candidateMoves = new int[] { 
	-twoside2 - 1, -twoside2 + 1,
	-side2 - 2, -side2 + 2,
	side2 - 2, side2 + 2,
	twoside2 - 1, twoside2 + 1};

    static private String show1(Tile4 tk) {
	int k = tk.getPos();
	// int k = tk.getFreeCnt();
	return ( k < 0 ? "xxx" :
	 ( k < 10 ? "  " + k : " " + k ) );
    }
    static public void show() {
	int cnt = 0;
	for (int i = 0; i < lng; i++) {
	    System.out.print(show1(board[i]));
	    cnt++;
	    if ( cnt == side2 ) {
		cnt = 0;
		System.out.println();
	    }
	}
	System.out.println();
    }
    static final int maxDepth = side * side; 
    static int solutionCnt = 0; 
    // static int failureCnt = 0;
    // static int numSetTiles = 2;
    static int tilesCnt = 3;
    static int fCnt = 2;
    static int bCnt = 3;

    // static boolean donef = true; 
    static boolean donef = false; // set true for uni-directional forward search
    // static boolean doneb = true; 
    static boolean doneb = false; // set true for uni-directional backward search
    static int moveCnt = 0; 

    // static boolean forward = false;
    static boolean forward = true;

    // select one
    static boolean bidirection = true;
    // static boolean bidirection = false;

    //    static Stack<SItemK4> fStack = new Stack<SItemK4>();
    static Stack<MySuper> fStack = new Stack<MySuper>();
    static Stack<SItemK4> bStack = new Stack<SItemK4>();
    // static boolean flip = false;
    static int fGoalState = 0;
    static int bGoalState = 0;

    static long startTime = 0;
    static long startTime0 = 0;

    // The depthBi parame determines the depth of the forward search 
    // after which the backward search is triggered
    static int depthBi = 3; // ++++
    static boolean again = true;

    public static void main(String[] args) {
	// Set up the board:
	System.out.println("side:" + side);
	System.out.println("side2:" + side2);
	System.out.println("startState1:" + startState1);
	System.out.println("startState:" + startState);
	System.out.println("goalState:" + goalState);
	// initialize board
	for ( int i = 0; i < lng; i++ ) board[i] = new Tile4(i);
	// clear the board
	for ( int i = 0; i < side; i++ ) {
	    int row = twoside2 + i * side2;
	    for ( int j = 0; j < side ; j++ ) {
		int column = edge + j;
		int sum = row + column;
		board[sum].setPos(0);
	    }
	}
	// init 3 starting positions
	board[startState1].setPos(1);
	// board[startState1].setDirForward();
	board[startState].setPos(2);
	// board[startState].setDirForward();
	board[goalState].setPos(3);

	// board[goalState].setDirBackward();
	
	// set the neighbors of the tiles
	for ( int i = 0; i < side; i++ ) {
	    int row = twoside2 + i * side2;
	    for ( int j = 0; j < side ; j++ ) {
		int column = edge + j;
		int idx = row + column;
		Tile4 t = board[idx];
		for ( int k = 0; k < numMoves; k++ ) {
		    Tile4 nk = board[idx + candidateMoves[k]];
		    int posk = nk.getPos();
		    if ( -1 != posk ) { 
			// t.setNeighbor(nk, k);
			t.addTile(nk);
		    } } }
	}
	/*
	// calculate the free neighbors
	for ( int i = 0; i < side; i++ ) {
	    int row = twoside2 + i * side2;
	    for ( int j = 0; j < 8 ; j++ ) {
		int column = edge + j;
		int idx = row + column;
		Tile4 t = board[idx];
		for ( int k = 0; k < numMoves; k++ ) {
		    Tile4 nk = t.getNeighbor(k);
		    if ( null == nk ) continue;
		    t.incrementFreeCnt();
		}
	    }
	}
	*/
	/* for testing
	Tile4 ss = board[startState];
	System.out.println("startState:" + startState + " " + ss.getPos());
	Tile4 gs = board[goalState];
	System.out.println("goalState:" + goalState + " " + gs.getPos());
	Tile4 t34 = board[34];
	Vector<Tile4> v34 = t34.getNeighbortTiles();
	int i34 = v34.size();
	for (int j = 0; j < i34; j++) {
	    Tile4 tj = v34.elementAt(j);
	    System.out.println("j " + j + " tj.index " + tj.getBoardIndex() + 
			       " pos " + tj.getPos());
	}
	// */
	/*
	for ( int i = 40; i < 40+side2; i++ ) {
	    Tile4 ti = board[i];
	    System.out.println("i " + i + " pos " + ti.getPos() +
			       " FreeCnt " + ti.getFreeCnt());
	}
	// */
	// System.exit(0);
	// Knight4.fGoalState = startState;
	// Knight4.bGoalState = goalState;

	SItemK4 startItem = new SItemK4(startState, true);
	fStack.push(startItem);
	SItemK4 goalItem = new SItemK4(goalState, false);
	bStack.push(goalItem);

	long startTime = System.currentTimeMillis();
	// ... get the ball rolling
	System.out.println("donef: " + donef);
	System.out.println("doneb: " + doneb);	
	System.out.println("bidirection: " + bidirection);
	System.out.println("forward: " + forward);
	System.out.println("fGoalState " + fGoalState);
	System.out.println("bGoalState " + bGoalState);
	show();
	// System.exit(0);

	// boolean forward = false;
	boolean forward = true;

	while (again) {
	    if ( fStack.empty() || bStack.empty() ) { again = false; continue; }
	    moveCnt++;
	    /*
	    System.out.println("------- moveCnt " + moveCnt);
	    System.out.println("fCnt " + fCnt);
	    // show();
	    if ( 5000 <  moveCnt) { show();
		System.out.println("------- moveCnt " + moveCnt);
		System.exit(0);
	    }
	    // */
	    // if ( 27 == moveCnt) { System.out.println("AA"); System.exit(0); }
	    // forward = !forward;
	    if (forward) {
		MySuper ms = fStack.peek(); 
		SItemK4 nodef = null;
		Undo undo = null;
		if (ms instanceof Undo) {
		    // System.out.println("******* Undo");
		    // show();
		    undo = (Undo) ms; 
		    undo.doUndo(forward);
		    fStack.pop();
		    ms = fStack.peek();  // ms instanceof SItemK4
		}
		nodef = (SItemK4) ms;
		Tile4 tilef = nodef.tgn;
		// System.out.println("tilef " + tilef.pos);
		if ( tilesCnt + 1 == maxTilesSet ) { // check for solution
		    /*
		    System.out.println("tilesCnt + 1 == maxTilesSet");
		    System.out.println("------- moveCnt " + moveCnt);
		    System.out.println("tilef " + tilef.pos);
		    show();
		    // */
		    MySuper msb = bStack.peek(); 
		    SItemK4 nodeb = (SItemK4) msb;
		    // Tile4 tileb = nodeb.tgn;
		    
		    // System.out.println("tileb " + tileb.pos);
		    /* [find the zeroTile reached by nodef]
		       find the zeroTile reached by nodeb if any
		       if found -> bingo
		       else { fStack.pop();  continue; }
		    */
		    // System.out.println("nodeb.gn " + nodeb.gn);
		    int zeroIndex = findZeroTile(nodeb.gn);
		    // System.out.println("zeroIndex " + zeroIndex);
		    if ( 0 == zeroIndex ) { // not found
			 fStack.pop();  continue;
		    }
		    solutionCnt++;
		    if ( 0 == solutionCnt%1000 ) {
			System.out.println("solutionCnt " + solutionCnt);
		    }
		    fStack.pop();  continue;
		    // System.exit(0);
		}
		
		// System.out.println("***** moveCnt " + moveCnt);
		// show(); System.exit(0);

		int nextIndex = nodef.nextIndex;
		// System.out.println("nextIndex " + nextIndex + " fCnt " + fCnt);
		// System.out.println("tilef.pos " + tilef.pos);
		// System.out.println("nodef.gn " + nodef.gn);
		Tile4 nextTile1 = null;
		boolean found = false;
		while ( nextIndex < nodef.size ) {
		    nextTile1 = nodef.nextTiles.elementAt(nextIndex); 
		    if ( 0 != nextTile1.getPos() ) { // skip
			nodef.nextIndex++; nextIndex++; continue; }
		    int boardIndex = nextTile1.getBoardIndex();
		    // System.out.println("boardIndex " + boardIndex);
		    int numFreeCells = numFreeCells(boardIndex);
		    if ( 0 == numFreeCells ) {
			// no point in going there now thus ignore
			nodef.nextIndex++; nextIndex++; continue; }
		
		    boolean found2 = false;
		    if ( Knight4.tilesCnt <= Knight4.chokeParam ) {
			// if ( Knight3.tilesCnt <= 33 ) { //6x6
			// if ( Knight3.tilesCnt <= 60 ) { //8x8
			//			boolean found = false;
			for ( int z = 0; z < Knight4.numMoves; z++ ) {
			    // System.out.println("z " + z);
			    // int idxz = getNeighbor(nextIdx1, z);
			    int idxz = getNeighbor(boardIndex, z);
			    // System.out.println("idxz " + idxz);
			    if ( 0 != Knight4.board[idxz].getPos() ) continue; // no worry
			    int numFreeCellsz = numFreeCells(idxz);
			    if ( numFreeCellsz <= 1 ) { found2 = true; break; }
			}
		    }
		    if ( found2 ) { 
			// System.out.println("||||||found2"); show(); // System.exit(0);
			nodef.nextIndex++;
			nextIndex++;
			continue;
		    }
		    found = true; break;
			   
		    // if ( 27 == moveCnt) { 
		    // System.out.println("CC"); System.exit(0); }
		}
		if ( !found) {
		    // System.out.println("!found/ backtrack");
		    // show();
		    fStack.pop();
		    continue;
		    // System.exit(0);
		}
		/*
		if ( 28 <= moveCnt ) {
		    System.out.println("found");
		    System.out.println("nextTile1 " + nextTile1.getBoardIndex());
		    show();
		    System.exit(0);
		}
		*/
		nodef.nextIndex++;
		nextIndex++;
		Knight4.tilesCnt++;
		Knight4.fCnt = Knight4.fCnt + 2;
		nextTile1.setPos(Knight4.fCnt);
		Undo undox = new Undo(nextTile1);
		SItemK4 nextItem = new SItemK4(nextTile1.getBoardIndex(), true); 
		fStack.push(undox);
		fStack.push(nextItem);
		continue;
		// push item that will undo
		/*
		  Knight4.tilesCnt--;
		  Knight4.fCnt = Knight4.fCnt - 2;
		  nextTile1.setPos(0);
		*/
	    } else { // backward
		continue;
	    }
	    // if (forward) move(Knight4.fStack, Knight4.bStack);
	    // else move(Knight4.bStack, Knight4.fStack);
	} // end of while again loop

	// ==================== Exec LOOP ============================== 

	// Start the forward loop
	// By changing its start code an uni directional backward search can be done
	// Otherwise the parameter depthBi specifies a forward search followed 
	// by a backward search. When the backward search terminates the 
	// forward search creates another configuration and invokes the backward 
	// search again.
	// goforward(0,  startItem, goalItem );	
	// initNode.move();
	long endTime = System.currentTimeMillis();
	System.out.println("\nsolutionCnt " + solutionCnt);
	System.out.println("Duration " + (endTime-startTime));
	// show();
    } // end main

    // =================================================================
    // =================== forward & backward============================
    static void move(Stack<SItemK4> xStack, Stack<SItemK4> yStack) {
	if ( xStack.empty() || yStack.empty() ) { Knight4.again = false; return; }
	moveCnt++;
	SItemK4 nodex = xStack.pop(); 
	Tile4 tilex = nodex.tgn; 
	if (tilesCnt + 1 == maxTilesSet ) {
	    // check here for a solution
	    SItemK4 nodey = yStack.peek();
	    Tile4 tiley = nodey.tgn;
	    int nextIndex = nodey.nextIndex;
	    while ( nextIndex < nodey.size ) {
		Tile4 nextTile1 = nodey.nextTiles.elementAt(nextIndex); 
		if ( 0 != nextTile1.getPos() ) { // skip
		    nodey.nextIndex++;
		    nextIndex++;
		    continue;
		}
		// tile = nextTile1; break; 
	    }
	} // end of solution check
	int nextIndex = nodex.nextIndex;
	while ( nextIndex < nodex.size ) {
	    Tile4 nextTile1 = nodex.nextTiles.elementAt(nextIndex); 
	    if ( 0 != nextTile1.getPos() ) { // skip
		nodex.nextIndex++;
		nextIndex++;
		continue;
	    }
	    int boardIndex = nextTile1.getBoardIndex();
	    int numFreeCells = numFreeCells(boardIndex);
	    if ( 0 == numFreeCells ) {
		// no point in going there now thus ignore
		nodex.nextIndex++;
		nextIndex++;
		continue;
	    }
	    nodex.nextIndex++;
	    nextIndex++;
	    Knight4.tilesCnt++;
	    Knight4.fCnt = Knight4.fCnt + 2;
	    nextTile1.setPos(Knight4.fCnt);
	    SItemK4 nextItem = new SItemK4(nextTile1.getBoardIndex(), true); //++
	    // show();
	    // goforward(depth, nextItem, nodeb);
	    Knight4.tilesCnt--;
	    Knight4.fCnt = Knight4.fCnt - 2;
	    nextTile1.setPos(0);
	    continue;
	}
    } // end move
  
        static void goforward(int depth, SItemK4 nodef,  SItemK4 nodeb) {
	moveCnt++; 

	// if ( 20751730 <=  moveCnt) System.exit(0);

	Tile4 tile = nodef.tgn;


	int nextIndex = nodef.nextIndex;

	// for unidirectional backward search replace with:
	// if ( true ) {
	if ( depthBi == depth ) { 
 	    // show();
	    gobackward(depth, nodef, nodeb);

	    // reset indices !!!! for the next backward run
	    nodeb.nextIndex = 0;
	    return;
	} else {
	    depth++;
	}
	while ( nextIndex < nodef.size ) {
	    Tile4 nextTile1 = nodef.nextTiles.elementAt(nextIndex); 
	    if ( 0 != nextTile1.getPos() ) { // skip
		nodef.nextIndex++;
		nextIndex++;
		continue;
	    }
	    int boardIndex = nextTile1.getBoardIndex();
	    int numFreeCells = numFreeCells(boardIndex);
	    if ( 0 == numFreeCells ) {
		// no point in going there now thus ignore
		nodef.nextIndex++;
		nextIndex++;
		continue;
	    }
	    nodef.nextIndex++;
	    nextIndex++;
	    Knight4.tilesCnt++;
	    Knight4.fCnt = Knight4.fCnt + 2;
	    nextTile1.setPos(Knight4.fCnt);
	    SItemK4 nextItem = new SItemK4(nextTile1.getBoardIndex(), true);
	    // show();
	    goforward(depth, nextItem, nodeb);
	    Knight4.tilesCnt--;
	    Knight4.fCnt = Knight4.fCnt - 2;
	    nextTile1.setPos(0);
	    continue;
	}
    } // end goforward

    // =================================================================
    static void gobackward(int depth, SItemK4 nodef, SItemK4 nodeb) {
	moveCnt++; 
	
	// System.out.println("backward moveCnt " + moveCnt);
	Tile4 tilef = nodef.tgn;
	// System.out.println("**** gobackward tilef pos " + tilef.pos);
	Tile4 tileb = nodeb.tgn;
	// System.out.println("**** gobackward tileb pos " + tileb.pos);
	// System.out.println("**** gobackward tilesCnt " + tilesCnt);
	// show();
	if (Knight4.tilesCnt + 1 == maxTilesSet ) {
	    // check here for a solution
	    /* 
	    System.out.println("Knight4.tilesCnt + 1 == maxTilesSet");
	    System.out.println("backward moveCnt " + moveCnt);
	    System.out.println("**** gobackward tilesCnt " + tilesCnt);
	    System.out.println("**** gobackward tilef pos " + tilef.pos);
	    System.out.println("**** gobackward tileb pos " + tileb.pos);
	    System.out.println("**** gobackward bCnt " + bCnt);
	    // */
	    Tile4 tile = nodeb.tgn;
	    int nextIndex = nodeb.nextIndex;
	    while ( nextIndex < nodeb.size ) {
		Tile4 nextTile1 = nodeb.nextTiles.elementAt(nextIndex); 
		if ( 0 != nextTile1.getPos() ) { // skip
		    nodeb.nextIndex++;
		    nextIndex++;
		    continue;
		}
		tile = nextTile1; break; 
	    }
	    if ( null != tile )  {
		solutionCnt++;
		/*
		System.out.println("**** gobackward solution !!! " + solutionCnt);
		System.out.println("**** moveCnt " + moveCnt);
		show();
		// System.exit(0);
		// */
		if ( 0 == Knight4.solutionCnt%1000 ) {
		    System.out.println("*** Solution b " + Knight4.solutionCnt);
		    // display the solution
		    // show(); 
		}
	    }
	    return;
	}
	// Check whether forward head is now choked; return if so.
	int boardIndexf = tilef.getBoardIndex();
	int numFreeCellsf = numFreeCells(boardIndexf);
	if ( 0 == numFreeCellsf ) { 
	    return;
	}
	Tile4 tile = nodeb.tgn;
	int nextIndex = nodeb.nextIndex;
	while ( nextIndex < nodeb.size ) {
	    Tile4 nextTile1 = nodeb.nextTiles.elementAt(nextIndex); 
	    if ( 0 != nextTile1.getPos() ) { // skip
		nodeb.nextIndex++;
		nextIndex++;
		continue;
	    }
	    int boardIndex = nextTile1.getBoardIndex();
	    int numFreeCells = numFreeCells(boardIndex);
	    if ( 0 == numFreeCells ) { // can't get there
		nodeb.nextIndex++;
		nextIndex++;
		continue;
	    }
	    nodeb.nextIndex++;
	    nextIndex++;
	    Knight4.tilesCnt++;
	    Knight4.bCnt = Knight4.bCnt + 2;
	    nextTile1.setPos(Knight4.bCnt);
	    SItemK4 nextItem = new SItemK4(nextTile1.getBoardIndex(), false);
	    // show();
	    gobackward(depth, nodef, nextItem);
	    // now undo
	    Knight4.tilesCnt--;
	    Knight4.bCnt = Knight4.bCnt - 2;
	    nextTile1.setPos(0);
	    continue;
	} // end while
	// return here if no more options
    } // end gobackward

    static int getNeighbor(int idx, int k) { return idx + Knight4.candidateMoves[k]; }
    static int numFreeCells(int nextIdx) {
	int cnt = 0;
	for ( int k = 0; k < Knight4.numMoves; k++ ) {
	    int idxk = getNeighbor(nextIdx, k);
	    if ( 0 == Knight4.board[idxk].getPos() ) cnt++;
	}
	// System.out.println("numFreeCells  nextIdx " + nextIdx + " cnt " + cnt);
	return cnt;
    } // end numFreeCells  
  
    static int findZeroTile(int target) {
	int out = 0;
	for ( int k = 0; k < Knight3.numMoves; k++ ) {
	    int targetNeighbor = getNeighbor(target, k);
	    if ( 0 == Knight4.board[targetNeighbor].getPos() ) { 
		out = targetNeighbor;
		break;
	    }
	}
	return out;
    } // end findZeroTile
    static boolean hasFreeCell(int nextIdx) {
	boolean out = false;
	for ( int k = 0; k < Knight4.numMoves; k++ ) {
	    int idxk = getNeighbor(nextIdx, k);
	    if ( 0 == Knight4.board[idxk].getPos() ) { out = true; break; }
	}
	return out;
    } // end hasFreeCell
 

    /*
    static private int moverCnt = 0;
    static private void check() {
	boolean[] num = new boolean[maxDepth+1];
	for (int i = 0; i < maxDepth; i++) num[i] = false;
	for (int i = 0; i < lng; i++) {
	    Tile4 t = board[i];
	    int tpos = t.getPos();
	    if ( tpos <= 0 ) continue;
	    // System.out.println("tpos " + tpos);
	    if ( maxDepth < tpos || num[tpos] ) {
		System.out.println("found duplicate: " + tpos);
		show();
		System.out.println("moverCnt: " + moverCnt);
		System.exit(0);
	    } else num[tpos] = true;
	}
    } // end check
    */
} // end of Knight4

class MySuper {
    MySuper(){};
}

class SItemK4 extends MySuper {
    protected int gn;
    protected boolean moveForward = false;
    protected int numMoves = 0;
    protected int [] moves = new int[Knight4.numMoves];
    protected int zeroCnt = 0;
    protected int k = 0; // index for moved
    // protected int numExplored = 0;
    // protected boolean backTrack = false;
    // protected int moveCnt = 0;
    Tile4 tgn = null;
    protected int size = 0;
    protected int nextIndex = 0;
    protected Vector<Tile4> nextTiles = null;
    SItemK4(int gnx, boolean b) {
	gn = gnx; moveForward = b;
	tgn = Knight4.board[gn];
	nextTiles = tgn.getNeighbortTiles();
	size = nextTiles.size();
	// sets moves & zeroCnt 
	// findMoves(gn); 
    }
} // end SItemK4

class Undo extends MySuper {
    protected Tile4 nextTile1 = null;
    Undo(Tile4 t) { nextTile1  = t; }
    void doUndo(boolean forward) {
	Knight4.tilesCnt--;
	if ( forward ) Knight4.fCnt = Knight4.fCnt - 2; 
	else Knight4.bCnt = Knight4.bCnt - 2;
	nextTile1.setPos(0);
    }
} // end Undo

class Tile4 {
    public Tile4(int bi) { boardIndex = bi; }
    private int boardIndex;
    public int getBoardIndex() { return boardIndex; }
    public int pos = -1;
    public void setPos(int z) { pos = z; }
    public int getPos() { return pos; }
    private Tile4 [] neighbors = new Tile4[] { 
	null, null, null, null,
	null, null, null, null
    };
    private Vector<Tile4> neighborTiles = new Vector<Tile4>();
    public void addTile(Tile4 t) { neighborTiles.add(t); }
    public Vector<Tile4> getNeighbortTiles() { return neighborTiles; }

} // end Tile4



