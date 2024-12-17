
// File: c:/ddc/Java/Knight/Knight5.java
// Date: Thu Sep 12 20:12:42 2024
// (C) OntoOO/ Dennis de Champeaux

/* Reimplementation of Knight4 using encoding of Knight3 */

// Create cyclic knight tours on 8x8 or 6x6 boards
import java.io.*;
import java.util.*;

public class Knight5 {
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
    static Tile5 [] board = new Tile5[lng];
    static final int numMoves = 8;
    static final int maxTilesSet = side*side;
    static final int halfTilesSet = maxTilesSet/2;
    static int [] candidateMoves = new int[] { 
	-twoside2 - 1, -twoside2 + 1,
	-side2 - 2, -side2 + 2,
	side2 - 2, side2 + 2,
	twoside2 - 1, twoside2 + 1};

    static private String show1(Tile5 tk) {
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
    static int moveCnt = 0; 
    // static int failureCnt = 0;
    // static int numSetTiles = 2;
    static int tilesCnt = 3;
    static int fCnt = 2;
    static int bCnt = 3;

    static long startTime = 0;
    static long startTime0 = 0;

   // static boolean forward = false;
   // static boolean forward = true;

    static boolean done = false;

    // select one
    // static boolean bidirection = true;
    // static boolean bidirection = false;

    // The depthBi parame determines the depth of the forward search 
    // after which the backward search is triggered
    static int depthBi = 25;
    // ===================================================
    public static void main(String[] args) {
	if ( 6 != side && 8 != side ) {
	    System.out.println("Adjust chokeParam first!!"); System.exit(0); }
	// Set up the board:
	System.out.println("side:" + side);
	System.out.println("side2:" + side2);
	System.out.println("startState1:" + startState1);
	System.out.println("startState:" + startState);
	System.out.println("goalState:" + goalState);
	// initialize board
	for ( int i = 0; i < lng; i++ ) board[i] = new Tile5(i);
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
		Tile5 t = board[idx];
		for ( int k = 0; k < numMoves; k++ ) {
		    Tile5 nk = board[idx + candidateMoves[k]];
		    int posk = nk.getPos();
		    // if ( 0 == posk ) t.setNeighbor(nk, k);
		    if ( -1 != posk ) t.addTile(nk);
		} 
	    }
	}
	/*
	// calculate the free neighbors
	for ( int i = 0; i < side; i++ ) {
	    int row = twoside2 + i * side2;
	    for ( int j = 0; j < 8 ; j++ ) {
		int column = edge + j;
		int idx = row + column;
		Tile5 t = board[idx];
		for ( int k = 0; k < numMoves; k++ ) {
		    Tile5 nk = t.getNeighbor(k);
		    if ( null == nk ) continue;
		    t.incrementFreeCnt();
		}
	    }
	}
	*/
	
	Node5 startNode = new Node5(startState);
	Node5 goalNode = new Node5(goalState);


	long startTime = System.currentTimeMillis();
	// ... get the ball rolling
	// System.out.println("bidirection: " + bidirection); gone
	// System.out.println("forward: " + forward);
	// System.out.println("fGoalState " + fGoalState);
	// System.out.println("bGoalState " + bGoalState);
	show();
	// System.exit(0);

	// ==================== Exec LOOP ============================== 

	// Start the forward loop
	// By changing its start code an uni directional backward search can be done
	// Otherwise the parameter depthBi specifies a forward search followed 
	// by a backward search. When the backward search terminates the 
	// forward search creates another configuration and invokes the backward 
	// search again.
	// goforward(0,  startItem, goalItem );	
	goforward(0,  startNode, goalNode );	

	// initNode.move();
	long endTime = System.currentTimeMillis();
	System.out.println("\nsolutionCnt " + solutionCnt);
	System.out.println("Duration " + (endTime-startTime));
	// show();
    } // end main
    // System.exit(0);

    // =================================================================
    // =================== foward & backward============================
    // static void goforward(int depth, SItemK4 nodef,  SItemK4 nodeb) {
    static void goforward(int depth, Node5 fNode, Node5 bNode) {
	if ( done) return;
	moveCnt++; 
	/*
	System.out.println("goforward moveCnt " + moveCnt);
	System.out.println("goforward depth " + depth);
	show();
	*/

	// for unidirectional backward search replace with:
	// if ( true ) {
	if ( depthBi == depth ) { 
	    // System.out.println("goforward suspended");
 	    // show();
	    // System.exit(0);
	    gobackward(fNode, bNode);
	    // System.exit(0);
	    // reset indices !!!! for the next backward run fNode
	    // nodeb.nextIndex = 0;
	    bNode.nextIndex = 0;
	    return;
	} else 	depth++;

	Tile5 tile = fNode.tgn;
	int nextIndex = fNode.nextIndex;
	while ( nextIndex < fNode.size ) {
	    Tile5 nextTile1 = fNode.nextTiles.elementAt(nextIndex); 
	    if ( 0 != nextTile1.getPos() ) { // skip
		fNode.nextIndex++;
		nextIndex++;
		continue;
	    }
	    int boardIndex = nextTile1.getBoardIndex();
	    int numFreeCells = numFreeCells(boardIndex);
	    // check whether it is safe to move to nextIndex
	    if ( 0 == numFreeCells ) {
		// no point in going there now thus ignore
		fNode.nextIndex++;
		nextIndex++;
		continue;
	    }
	    // check whether a cell becomes unreachable
	    // +++++++++++++++++++++++
	    if ( Knight5.tilesCnt <= Knight5.chokeParam ) { //  33 6x6
		// if ( Knight3.tilesCnt <= 60 ) { // 60  8x8
		boolean found = false;
		for ( int z = 0; z < Knight5.numMoves; z++ ) {
		    int idxz = getNeighbor(boardIndex, z);
		    if ( 0 != Knight5.board[idxz].getPos() ) continue; // no worry
		    int numFreeCellsz = numFreeCells(idxz);
		    if ( numFreeCellsz <= 1 ) { found = true; break; }
		}
		// if ( found ) continue;
		if ( found ) {
		    fNode.nextIndex++;
		    nextIndex++;
		    continue;
		}	
	    }
	    fNode.nextIndex++;
	    nextIndex++;
	    Knight5.tilesCnt++;
	    Knight5.fCnt = Knight5.fCnt + 2;
	    nextTile1.setPos(Knight5.fCnt);
	    Node5 nextItem = new Node5(nextTile1.getBoardIndex());
	    goforward(depth, nextItem, bNode);
	    Knight5.tilesCnt--;
	    Knight5.fCnt = Knight5.fCnt - 2;
	    nextTile1.setPos(0);
	    continue;
	}
    } // end goforward

    // =================================================================
    static void gobackward(Node5 fNode, Node5 bNode) {
	if ( done ) return;
	moveCnt++; 
	
	Tile5 tilef = fNode.tgn;
	// System.out.println("**** gobackward tilef pos " + tilef.pos);
	Tile5 tileb = bNode.tgn;
	// System.out.println("**** gobackward tileb pos " + tileb.pos);
	if (Knight5.tilesCnt + 1 == maxTilesSet ) {
	    // check here for a solution
	    Tile5 tile = null;
	    int nextIndex = bNode.nextIndex;
	    while ( nextIndex < bNode.size ) {
		Tile5 nextTile1 = bNode.nextTiles.elementAt(nextIndex); 
		if ( 0 != nextTile1.getPos() ) { // skip
		    bNode.nextIndex++;
		    nextIndex++;
		    continue;
		}
		tile = nextTile1; break; 
	    }
	    if ( null != tile )  {
		solutionCnt++;
		// if (500 == solutionCnt ) done = true;
		/*
		System.out.println("**** gobackward solution !!! " + s
		                   olutionCnt);
		System.out.println("**** moveCnt " + moveCnt);
		show();
		// System.exit(0);
		// */
		// /*
		if ( 0 == Knight5.solutionCnt%1000 ) {
		    System.out.println("*** Solution b " + Knight5.solutionCnt);
		    // display the solution
		    // show(); 
		}
		// */
	    }
	    return;
	}
	// Check whether forward head is now choked; return if so.
	// if ( true ) {
	int boardIndexf = tilef.getBoardIndex();
	int numFreeCellsf = numFreeCells(boardIndexf);
	if ( 0 == numFreeCellsf ) { 
	    return;
	}

	Tile5 tile = bNode.tgn;
	int nextIndex = bNode.nextIndex;
	while ( nextIndex < bNode.size ) {
	    Tile5 nextTile1 = bNode.nextTiles.elementAt(nextIndex); 
	    if ( 0 != nextTile1.getPos() ) { // skip
		bNode.nextIndex++;
		nextIndex++;
		continue;
	    }

	    int boardIndex = nextTile1.getBoardIndex();
	    int numFreeCells = numFreeCells(boardIndex);
	    if ( 0 == numFreeCells ) { // can't get there
		bNode.nextIndex++;
		nextIndex++;
		continue;
	    }
	    if ( 18 <= tilesCnt ) { // handicap
	    if ( Knight5.tilesCnt <= Knight5.chokeParam ) { //  33 6x6
		// if ( Knight3.tilesCnt <= 60 ) { // 60  8x8
		boolean found = false;
		for ( int z = 0; z < Knight5.numMoves; z++ ) {
		    int idxz = getNeighbor(boardIndex, z);
		    // System.out.println("gobackward idxz " +  idxz  );
		    if ( 0 != Knight5.board[idxz].getPos() ) continue; // no worry
		    int numFreeCellsz = numFreeCells(idxz);
		    if ( numFreeCellsz <= 1 ) { found = true; break; }
		}
		// if ( found ) continue;
		if ( found ) {
		    bNode.nextIndex++;
		    nextIndex++;
		    continue;
		}	
	    }
	    }


	    bNode.nextIndex++;
	    nextIndex++;
	    Knight5.tilesCnt++;
	    Knight5.bCnt = Knight5.bCnt + 2;
	    nextTile1.setPos(Knight5.bCnt);
	    Node5 nextItem = new Node5(nextTile1.getBoardIndex());
	    // show();
	    gobackward(fNode, nextItem);
	    // now undo
	    Knight5.tilesCnt--;
	    Knight5.bCnt = Knight5.bCnt - 2;
	    nextTile1.setPos(0);
	    continue;
	} // end while
	// return here if no more options
    } // end gobackward

    static int getNeighbor(int idx, int k) { return idx + Knight5.candidateMoves[k]; }
    static int numFreeCells(int nextIdx) {
	int cnt = 0;
	for ( int k = 0; k < Knight5.numMoves; k++ ) {
	    int idxk = getNeighbor(nextIdx, k);
	    if ( 0 == Knight5.board[idxk].getPos() ) cnt++;
	}
	// System.out.println("numFreeCells  nextIdx " + nextIdx + " cnt " + cnt);
	return cnt;
    } // end numFreeCells    

} // end of Knight5

class Node5 {
    protected int gn;
    protected Vector<Tile5> nextTiles = null;
    protected int size = 0;
    protected int nextIndex = 0;
    Tile5 tgn = null;
    protected int [] moves = new int[Knight3.numMoves];
    protected int zeroCnt = 0;
    Node5(int idx) { // idx board location
	gn = idx;
	tgn = Knight5.board[gn];
	nextTiles = tgn.getNeighbortTiles();
	size = nextTiles.size();
    } // end  Node5 constructor

    /* // not used ....
    int getNeighbor(int idx, int k) { return idx + Knight3.candidateMoves[k]; }
    void findMoves(int idx) {
	// set zeroCnt and puts in moves candidate moves`>
	zeroCnt = 0;
	// System.out.println("findMoves(idx) " + idx);
	for ( int k = 0; k < Knight3.numMoves; k++ ) {
	    int idxk = getNeighbor(idx, k);
	    // System.out.println("idxk " + idxk + " board[idxk] " + board[idxk]);
	    if ( 0 == Knight3.board[idxk].getPos() ) { // candidate loc
		moves[zeroCnt] = idxk;
		zeroCnt++;
	    }
	}
    } // end findMoves
    */
} // end Node5

class Tile5 {
    public Tile5(int bi) { boardIndex = bi; }
    private int boardIndex;
    public int getBoardIndex() { return boardIndex; }
    public int pos = -1;
    public void setPos(int z) { pos = z; }
    public int getPos() { return pos; }
    private Tile5 [] neighbors = new Tile5[] { 
	null, null, null, null,
	null, null, null, null
    };
    private Vector<Tile5> neighborTiles = new Vector<Tile5>();
    public void addTile(Tile5 t) { neighborTiles.add(t); }
    public Vector<Tile5> getNeighbortTiles() { return neighborTiles; }

} // end Tile5



