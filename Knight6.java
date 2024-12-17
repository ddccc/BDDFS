// File: c:/ddc/Java/Knight/Knight6.java
// Date: Thu Oct 24 16:52:57 2024
// (C) OntoOO/ Dennis de Champeaux

// Create cyclic knight tours on 8x8 or 6x6 boards
// Can use one or two threads.  Slower than Knight3 

import java.io.*;
import java.util.*;

public class Knight6 {
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
    static final int numMoves = 8;
    static final int maxTilesSet = side*side;
    static final int targetTilesSet = maxTilesSet-1; // one before last jump
    static int [] candidateMoves = new int[] { 
	-twoside2 - 1, -twoside2 + 1,
	-side2 - 2, -side2 + 2,
	side2 - 2, side2 + 2,
	twoside2 - 1, twoside2 + 1};

    static int fCnt = 2;
    static int bCnt = 3;
    static Tile6 [] boardF = new Tile6[lng];
    static Tile6 [] boardB = new Tile6[lng];


    static int solutionCntF = 0; 
    static int solutionCntB = 0; 
    static int moveCnt = 0; 
    static int tilesCnt = 3;

    static long startTime = 0;
    static long startTime0 = 0;
    // static boolean moveForward = false;
    static boolean done = false;
    // static Object os = new Object();
    static Object oTilesCnt = new Object();
    static Object oMoveCnt = new Object();
    // static Object oSolutionCnt = new Object();

    static private String show1(Tile6 tk) {
	int k = tk.getPos();
	return ( k < 0 ? " * " :
	 ( k < 10 ? "  " + k : " " + k ) );
    }
    static public void show(Tile6 [] board) {
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


    public static void main(String[] args) {
	if ( 6 != side && 8 != side ) {
	    System.out.println("Adjust chokeParam first!!"); System.exit(0); }
	// Set up the board:
	System.out.println("side:" + side);
	System.out.println("side2:" + side2);
	System.out.println("startState:" + startState);
	System.out.println("goalState:" + goalState);
	System.out.println("targetTilesSet " + targetTilesSet);

	// initialize boards
	for ( int i = 0; i < lng; i++ ) {
	    boardF[i] = new Tile6(i); boardB[i] = new Tile6(i);
	}
	// clear the boards
	for ( int i = 0; i < side; i++ ) {
	    int row = twoside2 + i * side2;
	    for ( int j = 0; j < side ; j++ ) {
		int column = edge + j;
		int sum = row + column;
		boardF[sum].setPos(0);
		boardB[sum].setPos(0);

	    }
	}
	// init 3 starting positions
	boardF[startState1].setPos(1);
	// board[startState1].setDirForward();
	boardF[startState].setPos(2);
	// board[startState].setDirForward();
	boardF[goalState].setPos(3);
	// board[goalState].setDirBackward();

	// init 3 starting positions
	boardB[startState1].setPos(1);
	// board[startState1].setDirForward();
	boardB[startState].setPos(2);
	// board[startState].setDirForward();
	boardB[goalState].setPos(3);
	// board[goalState].setDirBackward();

	
	// set the neighbors of the tiles
	// forward
	for ( int i = 0; i < side; i++ ) {
	    int row = twoside2 + i * side2;
	    for ( int j = 0; j < side ; j++ ) {
		int column = edge + j;
		int idx = row + column;
		Tile6 t = boardF[idx];
		for ( int k = 0; k < numMoves; k++ ) {
		    Tile6 nk = boardF[idx + candidateMoves[k]];
		    int posk = nk.getPos();
		    if ( 0 == posk ) t.setNeighbor(nk, k);
		}
	    }
	}
	// backward
	for ( int i = 0; i < side; i++ ) {
	    int row = twoside2 + i * side2;
	    for ( int j = 0; j < side ; j++ ) {
		int column = edge + j;
		int idx = row + column;
		Tile6 t = boardB[idx];
		for ( int k = 0; k < numMoves; k++ ) {
		    Tile6 nk = boardB[idx + candidateMoves[k]];
		    int posk = nk.getPos();
		    if ( 0 == posk ) t.setNeighbor(nk, k);
		}
	    }
	}

	// calculate the free neighbors
	// forward
	for ( int i = 0; i < side; i++ ) {
	    int row = twoside2 + i * side2;
	    for ( int j = 0; j < 8 ; j++ ) {
		int column = edge + j;
		int idx = row + column;
		Tile6 t = boardF[idx];
		for ( int k = 0; k < numMoves; k++ ) {
		    Tile6 nk = t.getNeighbor(k);
		    if ( null == nk ) continue;
		    t.incrementFreeCnt();
		}
	    }
	}
	// backward
	for ( int i = 0; i < side; i++ ) {
	    int row = twoside2 + i * side2;
	    for ( int j = 0; j < 8 ; j++ ) {
		int column = edge + j;
		int idx = row + column;
		Tile6 t = boardB[idx];
		for ( int k = 0; k < numMoves; k++ ) {
		    Tile6 nk = t.getNeighbor(k);
		    if ( null == nk ) continue;
		    t.incrementFreeCnt();
		}
	    }
	}
	/*
	show(boardF);
	show(boardB);
	System.exit(0);
	// */

	Node6 fNode = new Node6(true, boardF, startState); // forward thread
	Node6 bNode = new Node6(false, boardB, goalState); // backward thread

	Thread forward = new Thread(new Runnable() {
		public void run() { 
		    fNode.moveF(boardF, fNode.state); 
		} } );
	Thread backward = new Thread(new Runnable() {
		public void run() { 
		    bNode.moveB(boardB, bNode.state); 
		} } );

	long startTime = System.currentTimeMillis();
	// ... get the ball rolling
	// forward.start();
	backward.start();
	try { // wait for them to terminate
	    forward.join();
	    backward.join();
	} catch (InterruptedException e) {}
	long endTime = System.currentTimeMillis();
	System.out.println("\ntiming " + (endTime-startTime));
	// System.out.println("solutionCnt " + (solutionCntF + solutionCntB));
	System.out.println("solution # forward " + Knight6.solutionCntF +
					   " backward " + Knight6.solutionCntB);
	System.out.println("moveCnt " + moveCnt);
	/*
	show(boardF);
	show(boardB);
	*/

    } // end main
    /*    
    static private int moverCnt = 0;
    static private void check() {
	boolean[] num = new boolean[maxDepth+1];
	for (int i = 0; i < maxDepth; i++) num[i] = false;
	for (int i = 0; i < lng; i++) {
	    Tile6 t = board[i];
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
} // end of Knight6

class Node6 {
    protected boolean moveForward = false;
    protected int state = 0; // forward or backward
    protected int [] moves = new int[Knight6.numMoves];
    protected int zeroCnt = 0;
    Node6(boolean b, Tile6 [] board, int statex) {
	if ( Knight6.done ) return;
	moveForward = b;
	state = statex;
	findMoves(board, moves, state);
	// System.out.println("\nmoveCnt " + Knight6.moveCnt);
	/*
	if ( 70 == (Knight6.fCnt + Knight6.bCnt) ) {
	    System.out.println("Knight6.fCnt + Knight6.bCnt " + 
			       Knight6.fCnt + Knight6.bCnt);
	    Knight6.show(Knight6.boardF); Knight6.show(Knight6.boardB);
	    System.exit(0);
	}
	*/
	/*
	if ( 71 == (Knight6.fCnt + Knight6.bCnt) ) {
	    System.out.println("Knight6.fCnt + Knight6.bCnt " + 
			       Knight6.fCnt + Knight6.bCnt);
	    Knight6.show(Knight6.boardF); Knight6.show(Knight6.boardB);
	    System.exit(0);
	*/
	/*
	if (300000 < Knight6.moveCnt) {
	    System.out.println("\nmoveCnt " + Knight6.moveCnt);
	    Knight6.show(Knight6.boardF); Knight6.show(Knight6.boardB);
	    // System.exit(0);
	    Knight6.done = true;
	    return;
	}
	*/
	// System.out.println("moveCnt " + Knight6.moveCnt);

    } // end Node6 constructor
    void moveF(Tile6 [] board, int state) {
	synchronized(Knight6.oMoveCnt) { Knight6.moveCnt++; }
	/*
	System.out.println("\nmoveCnt " + Knight6.moveCnt);
	System.out.println("state " + state);
	System.out.println("zeroCnt " + zeroCnt);
	Tile6 tile = Knight6.board[state];
	System.out.println("pos " + tile.getPos());
	System.out.println("tilesCnt " + Knight6.tilesCnt);
	System.out.println("fCnt " + Knight6.fCnt + " bCnt " + Knight6.bCnt);

	// Knight6.show();
	*/	
	if ( Knight6.tilesCnt == Knight6.maxTilesSet ) {
	    // if ( 71 == ( Knight6.fCnt + Knight6.bCnt ) ) {
	    // check it 
	    // System.out.println("\nmoveCnt " + Knight6.moveCnt);
	    // System.out.println("check for solution");
	    // Knight6.show();
	    // System.out.println("board[state].pos " + board[state].getPos());
	    for ( int k = 0; k < Knight6.numMoves; k++ ) {
		int idxk = getNeighbor(state, k);
		int pos = board[idxk].getPos();
		int target = Knight6.bCnt;
		if ( pos == target ) { 
		    Knight6.solutionCntF++; 
		    if ( 20000 <= Knight6.solutionCntF ) {
			System.out.println("\nFound F solution !! " +  
					   Knight6.solutionCntF);
			System.out.println("fCnt " + Knight6.fCnt +
					   " bCnt " + Knight6.bCnt);
			Knight6.show(board);
			System.out.println("solution # forward " + Knight6.solutionCntF +
					   " backward " + Knight6.solutionCntB);
			System.exit(0);
		    }
		    // System.out.println("\nFound solution !!");
		    // Knight6.show(board);
		    // System.exit(0);
		    break;
		}
		// else System.out.println("No solution !!");
	    }
	    return;
	}
    
	if ( Knight6.tilesCnt == Knight6.targetTilesSet ) {
	    // if ( 70 == ( Knight6.fCnt + Knight6.bCnt ) ) {
	    // close to a potential solution
	    // find the zero tile, set it and go down.
	    // System.out.println("\nmoveCnt " + Knight6.moveCnt);
	    int idxk = moves[0];
	    // idxk has the last zero tile, set it and proceed
	    Knight6.tilesCnt++;
	    Knight6.fCnt = Knight6.fCnt + 2;
	    board[idxk].setPos(Knight6.fCnt);
	    new Node6(moveForward, board, idxk).moveF(board, idxk);
	    Knight6.tilesCnt--;
	    board[idxk].setPos(0);
	    Knight6.fCnt = Knight6.fCnt - 2;
	    return;
	}
	if ( 0 == zeroCnt ) { 
	    System.out.println("\n0 == zeroCnt forward " + moveForward);
	    System.out.println("tilesCnt " + Knight6.tilesCnt);
	    System.out.println("fCnt " + Knight6.fCnt + " bCnt " + Knight6.bCnt);
	    // Knight6.show();
	    // System.exit(0);
	    return;
	}
	// go deeper
    	for ( int k = 0; k < zeroCnt; k++ ) {
	    if ( Knight6.done ) return;
	    int nextIdx = moves[k];
	    // check whether it is safe to move to nextIdx
	    int numFreeCells = numFreeCells(board, nextIdx);
	    if ( 0 < numFreeCells ) { // go down
		// check whether a cell becomes unreachable
		if ( Knight6.tilesCnt <= Knight6.chokeParam ) { //6x6
		// if ( Knight6.tilesCnt <= 60 ) { //8x8
		    boolean found = false;
		    for ( int z = 0; z < Knight6.numMoves; z++ ) {
			int idxz = getNeighbor(nextIdx, z);
			if ( 0 != board[idxz].getPos() ) continue; // no worry
			int numFreeCellsz = numFreeCells(board, idxz);
			if ( numFreeCellsz <= 1 ) { found = true; break; }
		    }
		    if ( found ) continue;
		}
		synchronized(Knight6.oTilesCnt) {
		    synchronized(Knight6.boardB[nextIdx]) {
			if ( 0 == Knight6.boardB[nextIdx].getPos() ) {
			    Knight6.tilesCnt++;
			    Knight6.fCnt = Knight6.fCnt + 2;
			    board[nextIdx].setPos(Knight6.fCnt);
			    Knight6.boardB[nextIdx].setPos(Knight6.fCnt); 
			}
			else continue;
		    }
		}
		new Node6(moveForward, board, nextIdx).moveF(board, nextIdx);
		synchronized(Knight6.oTilesCnt) {
		    synchronized(Knight6.boardB[nextIdx]) {
			Knight6.tilesCnt--;
			board[nextIdx].setPos(0);
			Knight6.boardB[nextIdx].setPos(0);
			Knight6.fCnt = Knight6.fCnt - 2;
		    } 
		}
	    }
	} // end for loop

	/*
	System.out.println("EXIT moveCnt " + Knight6.moveCnt);
	System.exit(0);
	*/
    } // end moveF

    void moveB(Tile6 [] board, int state) {
	synchronized(Knight6.oMoveCnt) { Knight6.moveCnt++; }
	/*
	System.out.println("\nmoveCnt " + Knight6.moveCnt);
	System.out.println("state " + state);
	System.out.println("zeroCnt " + zeroCnt);
	Tile6 tile = Knight6.board[state];
	System.out.println("pos " + tile.getPos());
	System.out.println("tilesCnt " + Knight6.tilesCnt);
	System.out.println("fCnt " + Knight6.fCnt + " bCnt " + Knight6.bCnt);

	// Knight6.show();
	*/	
	if ( Knight6.tilesCnt == Knight6.maxTilesSet ) {
	    //if ( 71 == ( Knight6.fCnt + Knight6.bCnt ) ) {
	    // check it 
	    // System.out.println("\nmoveCnt " + Knight6.moveCnt);
	    // System.out.println("check for solution");
	    // Knight6.show();
	    // System.out.println("board[state].pos " + board[state].getPos());
	    for ( int k = 0; k < Knight6.numMoves; k++ ) {
		int idxk = getNeighbor(state, k);
		int pos = board[idxk].getPos();
		int target = Knight6.fCnt;
		if ( pos == target ) { 
		    Knight6.solutionCntB++;
		    if ( 20000 <= Knight6.solutionCntB ) {
			System.out.println("\nFound B solution !! " +  
					   Knight6.solutionCntB);
			System.out.println("fCnt " + Knight6.fCnt +
					   " bCnt " + Knight6.bCnt);
			Knight6.show(board);
			System.out.println("solution # forward " + Knight6.solutionCntF +
					   " backward " + Knight6.solutionCntB);
			System.exit(0);
		    }
		    break;
		}
		// else System.out.println("No solution !!");
	    }
	    return;
	}

	if ( Knight6.tilesCnt == Knight6.targetTilesSet ) {
	// if ( 70 == ( Knight6.fCnt + Knight6.bCnt ) ) {
	    // close to a potential solution
	    // find the zero tile, set it and go down.
	    // System.out.println("\nmoveCnt " + Knight6.moveCnt);
	    int idxk = moves[0];
	    // idxk has the last zero tile, set it and proceed
	    Knight6.tilesCnt++;
	    Knight6.bCnt = Knight6.bCnt + 2;
	    board[idxk].setPos(Knight6.bCnt);
	    new Node6(moveForward, board, idxk).moveB(board, idxk);
	    Knight6.tilesCnt--;
	    board[idxk].setPos(0);
	    Knight6.bCnt = Knight6.bCnt - 2;
	    return;
	}
	if ( 0 == zeroCnt ) { 
	    System.out.println("\n0 == zeroCnt forward " + moveForward);
	    System.out.println("tilesCnt " + Knight6.tilesCnt);
	    System.out.println("fCnt " + Knight6.fCnt + " bCnt " + Knight6.bCnt);
	    // Knight6.show();
	    // System.exit(0);
	    return;
	}
	// go deeper
    	for ( int k = 0; k < zeroCnt; k++ ) {
	    if ( Knight6.done ) return;
	    int nextIdx = moves[k];
	    // check whether it is safe to move to nextIdx
	    int numFreeCells = numFreeCells(board, nextIdx);
	    if ( 0 < numFreeCells ) { // go down
		// check whether a cell becomes unreachable
		if ( Knight6.tilesCnt <= Knight6.chokeParam ) { //6x6
		// if ( Knight6.tilesCnt <= 60 ) { //8x8
		    boolean found = false;
		    for ( int z = 0; z < Knight6.numMoves; z++ ) {
			int idxz = getNeighbor(nextIdx, z);
			if ( 0 != board[idxz].getPos() ) continue; // no worry
			int numFreeCellsz = numFreeCells(board, idxz);
			if ( numFreeCellsz <= 1 ) { found = true; break; }
		    }
		    if ( found ) continue;
		}
		synchronized(Knight6.oTilesCnt) {
		    synchronized(Knight6.boardF[nextIdx]) {
			if ( 0 == Knight6.boardF[nextIdx].getPos() ) {
			    Knight6.tilesCnt++;
			    Knight6.bCnt = Knight6.bCnt + 2;
			    board[nextIdx].setPos(Knight6.bCnt);
			    Knight6.boardF[nextIdx].setPos(Knight6.bCnt); 
			}
			else continue;
		    }
		}
		new Node6(moveForward, board, nextIdx).moveB(board, nextIdx);
		synchronized(Knight6.oTilesCnt) {
		    synchronized(Knight6.boardF[nextIdx]) {
			Knight6.tilesCnt--;
			board[nextIdx].setPos(0);
			Knight6.boardF[nextIdx].setPos(0);
			Knight6.bCnt = Knight6.bCnt - 2;
		    } 
		}
	    } 

	} // end for loop

	/*
	System.out.println("EXIT moveCnt " + Knight6.moveCnt);
	System.exit(0);
	*/
    } // end moveB

    int numFreeCells(Tile6 [] board, int nextIdx) {
	int cnt = 0;
	for ( int k = 0; k < Knight6.numMoves; k++ ) {
	    int idxk = getNeighbor(nextIdx, k);
	    if ( 0 == board[idxk].getPos() ) cnt++;
	}
	// System.out.println("numFreeCells  nextIdx " + nextIdx + " cnt " + cnt);
	return cnt;
    } // end numFreeCells

    int getNeighbor(int idx, int k) { 
	    return idx + Knight6.candidateMoves[k]; }
    void findMoves(Tile6 [] board, int [] moves, int idx) {
	// set zeroCnt and puts in moves candidate moves
	zeroCnt = 0;
	// System.out.println("findMoves(idx) " + idx);
	for ( int k = 0; k < Knight6.numMoves; k++ ) {
	    int idxk = getNeighbor(idx, k);
	    // System.out.println("idxk " + idxk + " board[idxk] " + board[idxk]);
	    if ( 0 == board[idxk].getPos() ) { // candidate loc
		moves[zeroCnt] = idxk;
		zeroCnt++;
	    }
        } 
    } // end findMoves
} // end Node6



class Tile6 {
    public Tile6(int bi) { boardIndex = bi; }
    private int boardIndex;
    public int getBoardIndex() { return boardIndex; }
    private int pos = -1;
    public void setPos(int z) { pos = z; }
    public int getPos() { return pos; }
    private Tile6 [] neighbors = new Tile6[] { 
	null, null, null, null,
	null, null, null, null
    };
    public Tile6 getNeighbor(int i) { return neighbors[i]; }
    public void setNeighbor(Tile6 t, int i) { neighbors[i] = t; }
    private int freeCnt = 0;
    public int getFreeCnt() { return freeCnt; }
    public void setFreeCnt(int z) { freeCnt = z; }
    public void incrementFreeCnt() { freeCnt++; }
    public void decrementFreeCnt() { freeCnt--; }
    private LinkedList<Tile6> nextTiles = new LinkedList<Tile6>();
    public LinkedList<Tile6> getNextTiles() { return nextTiles; }
    /*
    private int direction = -1;
    public int getDirection() { return direction; }
    public void unSetDirection() { direction = -1; }
    public void setDirForward() { direction = 0; }
    public boolean isDirectionForward() { return (0 == direction); }
    public void setDirBackward() { direction = 1; }
    public boolean isDirectionBackward() { return (1 == direction); }
    */
} // end Tile6



