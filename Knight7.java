// File: c:/ddc/Java/Knight/Knight7.java
// Date: Thu Oct 24 16:52:57 2024
// (C) OntoOO/ Dennis de Champeaux

// Create cyclic knight tours on 8x8 or 6x6 boards
// Works in forward and backward mode - slower than Knight3
// Does not work with both threads running
import java.io.*;
import java.util.*;

public class Knight7 {
    // The board is a linear array with all sides having an edge of size 2.

    // static final int side = 8;    
    static final int side = 6;    
    static final int chokeParam = ( 6 == side ? 33 : 60 ); 
    static final int edge = 2;
    static final int side2 = side + 2 * edge; // 12
    static final int lng = side2 * side2; 
    static final int twoside2 = 2 * side2; // 24
    static final int startState1 = twoside2 + edge; // 26
    static final int startState = 3 * side2 + edge + 2; // 40
    static final int goalState = 4 * side2 + edge + 1; // 51
    static Tile7 [] board = new Tile7[lng];
    static final int numMoves = 8;
    static final int maxTilesSet = side*side;
    static final int targetTilesSet = maxTilesSet-1; // one before last jump
    static int [] candidateMoves = new int[] { 
	-twoside2 - 1, -twoside2 + 1,
	-side2 - 2, -side2 + 2,
	side2 - 2, side2 + 2,
	twoside2 - 1, twoside2 + 1};

    static private String show1(Tile7 tk) {
	int k = tk.getPos();
	// int k = tk.getFreeCnt();
	return ( k < 0 ? " * " :
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

    static int solutionCnt = 0; 
    static int moveCnt = 0; 
    static int tilesCnt = 3;
    static int fCnt = 2;
    static int bCnt = 3;

    static long startTime = 0;
    // static boolean moveForward = false;
    static boolean done = false;

    // static Object os = new Object();
    static Object oTilesCnt = new Object();
    static Object oMoveCnt = new Object();
    static Object oTileX = new Object();
    static Object oSolutionCnt = new Object();

    public static void main(String[] args) {
	if ( 6 != side && 8 != side ) {
	    System.out.println("Adjust chokeParam first!!"); System.exit(0); }
	// Set up the board:
	System.out.println("side:" + side);
	System.out.println("side2:" + side2);
	System.out.println("startState:" + startState);
	System.out.println("goalState:" + goalState);
	System.out.println("targetTilesSet " + targetTilesSet);

	// initialize board
	for ( int i = 0; i < lng; i++ ) {
	    board[i] = new Tile7(i); 
	}

	// clear the board
	for ( int i = 0; i < side; i++ ) {
	    int row = twoside2 + i * side2;
	    for ( int j = 0; j < side ; j++ ) {
		int column = edge + j;
		int sum = row + column;
		board[sum].setPos(0);
	    }
	}

	// set the neighbors of the tiles
	for ( int i = 0; i < side; i++ ) {
	    int row = twoside2 + i * side2;
	    for ( int j = 0; j < side ; j++ ) {
		int column = edge + j;
		int idx = row + column;
		Tile7 t = board[idx];
		for ( int k = 0; k < numMoves; k++ ) {
		    Tile7 nk = board[idx + candidateMoves[k]];
		    int posk = nk.getPos();
		    if ( 0 == posk ) {
			t.setNeighbor(nk, k);
		    }
		}
	    }
	}

	// Set  neighbors indices in Tile entry
	// The array has length 9; first element (in 0) has the number
	// of indices in 1, 2, ... upto 8.
	for ( int i = 0; i < side; i++ ) {
	    int row = twoside2 + i * side2;
	    for ( int j = 0; j < 8 ; j++ ) {
		int column = edge + j;
		int idx = row + column;
		Tile7 t = board[idx];
		int cnt = 0;
		for ( int k = 0; k < numMoves; k++ ) {
		    Tile7 tk = t.getNeighbor(k);
		    if ( null == tk ) continue;
		    cnt++;
		    t.theNeighbors[cnt] = tk.getBoardIndex();
		}
		t.theNeighbors[0] = cnt;
	    }
	}

	// init 3 starting positions
	board[startState1].setPos(1);
	// board[startState1].setDirForward();
	board[startState].setPos(2);
	// board[startState].setDirForward();
	board[goalState].setPos(3);
	// board[goalState].setDirBackward();

	/* // not used
	// calculate the # free neighbors
	for ( int i = 0; i < side; i++ ) {
	    int row = twoside2 + i * side2;
	    for ( int j = 0; j < 8 ; j++ ) {
		int column = edge + j;
		int idx = row + column;
		Tile7 t = board[idx];
		for ( int k = 0; k < numMoves; k++ ) {
		    Tile7 nk = t.getNeighbor(k);
		    if ( null == nk ) continue;
		    if ( 0 == nk.getPos() ) t.incrementFreeCnt();
		}
	    }
	} 
	// */
	/*
	// Example linked list NOT used
	Tile7 t34 = board[34];
	System.out.println("\nt34.getPos() " + t34.getPos());
	System.out.println("t34 # free neighbors : " + t34.getFreeCnt());
	LinkedList<Tile7> l34 = t34.getNextTiles();
	int siz = l34.size();
	System.out.println("Neighbor size " + siz + "\nNeighbors:");
	for (int i = 0; i<siz; i++) {
	    Tile7 t34i = l34.get(i);
	    System.out.println("i " + i + "  getBoardIndex() " +
			       t34i.getBoardIndex() +
			       " t34i.getPos() " + t34i.getPos() +
			       " t34i.getFreeCnt() " + t34i.getFreeCnt());
	}
	// */
	/*
	show();

	System.exit(0);
	// */
	Node7 fNode = new Node7(true, startState); // forward thread
	Node7 bNode = new Node7(false, goalState); // backward thread

	Thread forward = new Thread(new Runnable() {
		public void run() { 
		    fNode.move(fNode.state); 
		} } );
	Thread backward = new Thread(new Runnable() {
		public void run() { 
		    bNode.move(bNode.state); 
		} } );
	// for testing unidirectional search 
	startTime = System.currentTimeMillis();
	// fNode.move(fNode.state); 

	// ... get the ball rolling
	// forward.start();
	backward.start();
	// /*
	try { // wait for them to terminate
	    forward.join();
	    backward.join();
	} catch (InterruptedException e) {}
	// */
	long endTime = System.currentTimeMillis();
	System.out.println("\ntiming " + (endTime-startTime));
	System.out.println("moveCnt " + moveCnt);
	System.out.println("solutionCnt " + solutionCnt);
	/*
	System.out.println("solution # solutionCntF " + Knight7.solutionCntF +
					   " backward " + Knight7.solutionCntB);
	/*
	show(board);
	*/

    } // end main

} // end of Knight7

class Node7 {
    protected boolean moveForward = false;
    protected int state = 0; // forward or backward
    protected int [] moves = new int[Knight7.numMoves];
    protected int zeroCnt = 0;
    // protected LinkedList<Tile7> ll;
    protected int llSize;
    // protected LinkedList<Tile7> lli;

    Node7(boolean b, int statex) {
	if ( Knight7.done ) return;
	moveForward = b;
	state = statex;
	findMoves(moves, state);
	synchronized(Knight7.oMoveCnt) { Knight7.moveCnt++; }
	// System.out.println("moveCnt " + Knight7.moveCnt);
	// if ( 36 == Knight7.tilesCnt) { 
	// if ( Knight7.moveCnt <= 3) {
	    // System.out.println("moveCnt " + Knight7.moveCnt);
	    // Knight7.show();
	    // System.exit(0);
	//}
    } // end Node7 constructor
    void move(int state) {
	/*
	System.out.println("\nmoveCnt " + Knight7.moveCnt);
	System.out.println("state " + state);
	// System.out.println("zeroCnt " + zeroCnt);
	Tile7 tile = Knight7.board[state]; // ????????? delete
	System.out.println("pos " + tile.getPos());
	System.out.println("tilesCnt " + Knight7.tilesCnt);
	System.out.println("fCnt " + Knight7.fCnt + " bCnt " + Knight7.bCnt);
	Knight7.show(); 
	// if ( 21 <= Knight7.moveCnt ) System.exit(0);
	// */

	// ***************************
	// add synchronized +++++++++++++++++++++++++++=
	// ***************************

	synchronized(Knight7.oTilesCnt) {
	    if ( Knight7.tilesCnt == Knight7.maxTilesSet ) {

		int [] neighbors = getAllNeighbors(state );
		boolean found = false;
		int goalTile = (moveForward ? Knight7.bCnt : Knight7.fCnt);
		for ( int j = 1; j <= neighbors[0] ; j++ ) {
		    if ( goalTile == Knight7.board[neighbors[j]].getPos() ) {
			found = true; break;
		    }
		}
		if ( found ) {
		    synchronized(Knight7.oSolutionCnt) {
			Knight7.solutionCnt++; }
		    // System.out.println("solutionCnt " + Knight7.solutionCnt);
		    if ( 0 == Knight7.solutionCnt%1000) {
			long diff = System.currentTimeMillis() - Knight7.startTime;
			long delta = diff/ Knight7.solutionCnt;
			System.out.println("solutionCnt " + Knight7.solutionCnt + 
					   " delta " + delta);
		    }
		}
		// if ( 100 < Knight7.solutionCnt ) Knight3.done = true;
		/* // display the solution
		   System.out.println("\nmove moveCnt " + Knight7.moveCnt);
		   System.out.println("move tilesCnt " + Knight7.tilesCnt);
		   System.out.println("move goalTile " + goalTile);
		   Knight7.show();
		   // System.out.println("move goal state!!!!");
		   if ( 0 < Knight7.solutionCnt ) System.exit(0);
		   // */
		return;
	    }
	}

    	synchronized(Knight7.oTilesCnt) {
	    if ( Knight7.tilesCnt == Knight7.targetTilesSet ) {
		int zeroTile = findZeroTile(state); // fetch unique tile
		Knight7.tilesCnt++;
		if ( moveForward ) {
		    Knight7.fCnt = Knight7.fCnt + 2;
		    Knight7.board[zeroTile].setPos(Knight7.fCnt);
		} else {
		    Knight7.bCnt = Knight7.bCnt + 2;
		    Knight7.board[zeroTile].setPos(Knight7.bCnt);
		}
		Node7 node7 =  new Node7(moveForward, zeroTile);
		node7.move(node7.state); // state??
		Knight7.tilesCnt--;
		if ( moveForward ) {
		    Knight7.fCnt = Knight7.fCnt - 2;
		    Knight7.board[zeroTile].setPos(0);
		} else {
		    Knight7.bCnt = Knight7.bCnt + 2;
		    Knight7.board[zeroTile].setPos(0);
		}
		return;
	    }
	}
	// go deeper
  	for ( int k = 0; k < zeroCnt; k++ ) {
	    if ( Knight7.done ) return;
	    int nextIdx = moves[k];
	    // check whether it is safe to move to nextIdx
	    boolean hasFreeCell = hasFreeCell(nextIdx); 
	    if ( !hasFreeCell ) continue;
	    // check whether a cell will become unreachable
	    if ( Knight7.tilesCnt <= Knight7.chokeParam ) {
		// if ( Knight3.tilesCnt <= 33 ) { //6x6
		// if ( Knight3.tilesCnt <= 60 ) { //8x8
		boolean found = false;
		for ( int z = 0; z < Knight7.numMoves; z++ ) {
		    int idxz = getNeighbor(nextIdx, z);
		    if ( 0 != Knight7.board[idxz].getPos() ) continue; // no worry
		    int numFreeCellsz = numFreeCells(idxz);
		    if ( numFreeCellsz <= 1 ) { found = true; break; }
		}
		if ( found ) continue;
	    }
	    synchronized(Knight7.oTilesCnt) { Knight7.tilesCnt++; }
	    if ( moveForward ) {
		Knight7.fCnt = Knight7.fCnt + 2;
		Knight7.board[nextIdx].setPos(Knight7.fCnt);
	    } else {
		Knight7.bCnt = Knight7.bCnt + 2;
		Knight7.board[nextIdx].setPos(Knight7.bCnt);
	    }
	    Node7 node7 = new Node7(moveForward, nextIdx);
	    node7.move(node7.state); // state??
	    synchronized(Knight7.oTilesCnt) { Knight7.tilesCnt--; }
	    Knight7.board[nextIdx].setPos(0);
	    if ( moveForward ) Knight7.fCnt = Knight7.fCnt - 2;
	    else Knight7.bCnt = Knight7.bCnt - 2;
	} // end for loop
	return;

	/*
	System.out.println("EXIT moveCnt " + Knight7.moveCnt);
	System.exit(0);
	*/
    } // end move

 
    int numFreeCells(int nextIdx) {
	int cnt = 0;
	for ( int k = 0; k < Knight7.numMoves; k++ ) {
	    int idxk = getNeighbor(nextIdx, k);
	    if ( 0 == Knight7.board[idxk].getPos() ) cnt++;
	}
	// System.out.println("numFreeCells  nextIdx " + nextIdx + 
        // " cnt " + cnt);
	return cnt;
    } // end numFreeCells

    int getNeighbor(int idx, int k) { 
	    return idx + Knight7.candidateMoves[k]; }
    void findMoves(int [] moves, int idx) {
	// set zeroCnt and puts in moves candidate moves
	zeroCnt = 0;
	// System.out.println("findMoves(idx) " + idx);
	for ( int k = 0; k < Knight7.numMoves; k++ ) {
	    int idxk = getNeighbor(idx, k);
	    // System.out.println("idxk " + idxk + " board[idxk] " + board[idxk]);
	    if ( 0 == Knight7.board[idxk].getPos() ) { // candidate loc
		moves[zeroCnt] = idxk;
		zeroCnt++;
	    }
        } 
    } // end findMoves

    // check against the next one
    int findZeroTile(int target) {
	int out = 0;
	for ( int k = 0; k < Knight7.numMoves; k++ ) {
	    int targetNeighbor = getNeighbor(target, k);
	    if ( 0 == Knight7.board[targetNeighbor].getPos() ) { 
		out = targetNeighbor;
		break;
	    }
	}
	return out;
    } // end findZeroTile

    int [] getAllNeighbors(int targetTile) {
	Tile7 t = Knight7.board[targetTile];
	return t.theNeighbors;
    } // end getAllNeighbors

    boolean hasFreeCell(int nextIdx) {
	boolean out = false;
	for ( int k = 0; k < Knight7.numMoves; k++ ) {
	    int idxk = getNeighbor(nextIdx, k);
	    if ( 0 == Knight7.board[idxk].getPos() ) { out = true; break; }
	}
	return out;
    }
} // end Node7

class Tile7 {
    public Tile7(int bi) { boardIndex = bi; }
    private int boardIndex;
    public int getBoardIndex() { return boardIndex; }
    private int pos = -1;
    public void setPos(int z) { pos = z; }
    public int getPos() { return pos; }
    private Tile7 [] neighbors = new Tile7[] { 
	null, null, null, null,
	null, null, null, null
    };
    public int [] theNeighbors = new int [9];
    public Tile7 getNeighbor(int i) { return neighbors[i]; }
    public void setNeighbor(Tile7 t, int i) { neighbors[i] = t; }

    /*
    private int freeCnt = 0;
    public int getFreeCnt() { return freeCnt; }
    public void setFreeCnt(int z) { freeCnt = z; }
    public void incrementFreeCnt() { freeCnt++; }
    public void decrementFreeCnt() { freeCnt--; }
    // */
    /*
    public void addToLL(Tile7 t7) { nextTiles.add(t7); }
    private LinkedList<Tile7> nextTiles = new LinkedList<Tile7>();
    public LinkedList<Tile7> getNextTiles() { return nextTiles; }
    */
    /*
    private int direction = -1;
    public int getDirection() { return direction; }
    public void unSetDirection() { direction = -1; }
    public void setDirForward() { direction = 0; }
    public boolean isDirectionForward() { return (0 == direction); }
    public void setDirBackward() { direction = 1; }
    public boolean isDirectionBackward() { return (1 == direction); }
    */
} // end Tile7



