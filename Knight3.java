// File: c:/ddc/Java/Knight/Knight3.java
// Date: Wed Mar 16 13:17:39 2022
// (C) OntoOO/ Dennis de Champeaux

// Create cyclic knight tours on 8x8 or 6x6 boards
import java.io.*;
import java.util.*;

public class Knight3 {
    // The board is a linear array with all sides having an edge of size 2.

    // static final int side = 8;    
    static final int side = 6;    
    static final int edge = 2;
    static final int side2 = side + 2 * edge; // 12
    static final int lng = side2 * side2; 
    static final int twoside2 = 2 * side2; // 24
    static final int startState1 = twoside2 + edge; // 26
    static final int startState = 3 * side2 + edge + 2; // 40
    static final int goalState = 4 * side2 + edge + 1; // 51
    static Tile [] board = new Tile[lng];
    static final int numMoves = 8;
    static final int maxTilesSet = side*side;
    static final int targetTilesSet = maxTilesSet-1; // one before last jump
    static int [] candidateMoves = new int[] { 
	-twoside2 - 1, -twoside2 + 1,
	-side2 - 2, -side2 + 2,
	side2 - 2, side2 + 2,
	twoside2 - 1, twoside2 + 1};

    static private String show1(Tile tk) {
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

    public static void main(String[] args) {
	// Set up the board:
	System.out.println("side:" + side);
	System.out.println("side2:" + side2);
	System.out.println("startState:" + startState);
	System.out.println("goalState:" + goalState);
	// initialize board
	for ( int i = 0; i < lng; i++ ) board[i] = new Tile(i);
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
	board[startState1].setDirForward();
	board[startState].setPos(2);
	board[startState].setDirForward();
	board[goalState].setPos(3);
	board[goalState].setDirBackward();
	
	// set the neighbors of the tiles
	for ( int i = 0; i < side; i++ ) {
	    int row = twoside2 + i * side2;
	    for ( int j = 0; j < side ; j++ ) {
		int column = edge + j;
		int idx = row + column;
		Tile t = board[idx];
		for ( int k = 0; k < numMoves; k++ ) {
		    Tile nk = board[idx + candidateMoves[k]];
		    int posk = nk.getPos();
		    if ( 0 == posk ) t.setNeighbor(nk, k);
		}
	    }
	}
	// calculate the free neighbors
	for ( int i = 0; i < side; i++ ) {
	    int row = twoside2 + i * side2;
	    for ( int j = 0; j < 8 ; j++ ) {
		int column = edge + j;
		int idx = row + column;
		Tile t = board[idx];
		for ( int k = 0; k < numMoves; k++ ) {
		    Tile nk = t.getNeighbor(k);
		    if ( null == nk ) continue;
		    t.incrementFreeCnt();
		}
	    }
	}

	show();
	/*
	for ( int i = 40; i < 40+side2; i++ ) {
	    Tile ti = board[i];
	    System.out.println("i " + i + " pos " + ti.getPos() +
			       " FreeCnt " + ti.getFreeCnt());
	}
	System.exit(0);
	// */
	// Initialize the first node ...
	Node3 initNode = new Node3(startState, goalState);
	long startTime = System.currentTimeMillis();
	// ... get the ball rolling
	initNode.move();
	long endTime = System.currentTimeMillis();
	System.out.println("\nsolutionCnt " + solutionCnt);
	System.out.println("Duration " + (endTime-startTime));
	// show();

    } // end main
    
    static private int moverCnt = 0;
    static private void check() {
	boolean[] num = new boolean[maxDepth+1];
	for (int i = 0; i < maxDepth; i++) num[i] = false;
	for (int i = 0; i < lng; i++) {
	    Tile t = board[i];
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


} // end of Knight3

class Node3 {
    protected int fidx, bidx;
    protected boolean moveForward = false;
    protected int [] moves = new int[Knight3.numMoves];
    protected int zeroCnt = 0;
    Node3(int fidy, int bidy) {
	fidx = fidy; bidx = bidy;
	// Select one of the three options: 
	moveForward  = (Knight3.fCnt < Knight3.bCnt); // bidirectional search
	// moveForward = true; // unidirectional search
	// moveForward = false; // unidirectional search

	// this sets zeroCnt and puts in moves candidate moves
	if ( moveForward ) findMoves(fidx); else findMoves(bidx);
    } 

    void move() {
	Knight3.moveCnt++;
	int target = ( moveForward ? fidx : bidx );
	boolean trace = false;
	if ( Knight3.tilesCnt == Knight3.maxTilesSet ) { // path found
	    // check for a circular path 
	    int [] neighbors = getAllNeighbors(target);
	    boolean found = false;
	    // Select one of the two choices
	    // bidirectional::
	    int goalTile = (moveForward ? bidx : fidx); 
	    // unidirectional::
	    // int goalTile = (moveForward ? Knight3.goalState : Knight3.startState);
	    for ( int j = 0; j < Knight3.numMoves; j++ ) 
		if ( neighbors[j] == goalTile ) {
		    found = true; break;
		}
	    if ( found ) {
		/* // display the solution
		   System.out.println("\nmove moveCnt " + Knight3.moveCnt);
		   System.out.println("fidx " + Knight3.board[fidx].getPos() + 
		   " bidx " + Knight3.board[bidx].getPos());
		   Knight3.show();
		   System.out.println("move goal state!!!!");
		*/
		Knight3.solutionCnt++;
		// System.exit(0);
	    }
	    return;
	}
	// if ( 0 == zeroCnt ) return ( moveForward ? fidx : bidx );
	if ( 0 == zeroCnt ) return;

	// if ( 1 == zeroCnt && Knight3.tilesCnt == Knight3.targetTilesSet) {
	if ( Knight3.tilesCnt == Knight3.targetTilesSet) {
	    // prepare for the next last move 
	    int zeroTile = findZeroTile(target); // fetch unique tile
	    Knight3.tilesCnt++;
	    int fidxz, bidxz;
	    if ( moveForward ) {
		Knight3.fCnt = Knight3.fCnt + 2;
		fidxz = zeroTile;
		bidxz = bidx;
		Knight3.board[fidxz].setPos(Knight3.fCnt);
	    } else {
		Knight3.bCnt = Knight3.bCnt + 2;
		fidxz = fidx;
		bidxz = zeroTile;
		Knight3.board[bidxz].setPos(Knight3.fCnt);
	    }
	    (new Node3(fidxz, bidxz)).move();
	    Knight3.tilesCnt--;
	    Knight3.board[zeroTile].setPos(0);
	    if ( moveForward ) Knight3.fCnt = Knight3.fCnt - 2;
	    else Knight3.bCnt = Knight3.bCnt - 2;
	    return;
	}
	// go deeper
    	for ( int k = 0; k < zeroCnt; k++ ) {
	    int nextIdx = moves[k];
	    // check whether it is safe to move to nextIdx
	    int numFreeCellsk = numFreeCells(nextIdx);
	    int numFreeCellsTarget = numFreeCells(nextIdx);
	    if ( 0 == numFreeCellsTarget && 
		     Knight3.tilesCnt < Knight3.targetTilesSet ) continue;
	    if ( 0 < numFreeCellsk ) { // go down
		Knight3.tilesCnt++;
		int fidxz, bidxz;
		if ( moveForward ) {
		    Knight3.fCnt = Knight3.fCnt + 2;
		    fidxz = nextIdx;
		    bidxz = bidx;
		    Knight3.board[fidxz].setPos(Knight3.fCnt);
		} else {
		    Knight3.bCnt = Knight3.bCnt + 2;
		    fidxz = fidx;
		    bidxz = nextIdx;
		    Knight3.board[bidxz].setPos(Knight3.bCnt);
		}
		(new Node3(fidxz, bidxz)).move();
		Knight3.tilesCnt--;
		Knight3.board[nextIdx].setPos(0);
		if ( moveForward ) Knight3.fCnt = Knight3.fCnt - 2;
		else Knight3.bCnt = Knight3.bCnt - 2;
	    } 
	}
	return;
    } // end move

    int numFreeCells(int nextIdx) {
	int cnt = 0;
	for ( int k = 0; k < Knight3.numMoves; k++ ) {
	    int idxk = getNeighbor(nextIdx, k);
	    if ( 0 == Knight3.board[idxk].getPos() ) cnt++;
	}
	// System.out.println("numFreeCells  nextIdx " + nextIdx + " cnt " + cnt);
	return cnt;
    } // end numFreeCells

    int getNeighbor(int idx, int k) { return idx + Knight3.candidateMoves[k]; }
    void findMoves(int idx) {
	// set zeroCnt and puts in moves candidate moves
	zeroCnt = 0;
	// System.out.println("findMoves(idx) " + idx);
	for ( int k = 0; k < Knight3.numMoves; k++ ) {
	    int idxk = getNeighbor(idx, k);
	    // System.out.println("idxk " + idxk + " board[idxk] " + b<oard[idxk]);
	    if ( 0 == Knight3.board[idxk].getPos() ) { // candidate loc
		zeroCnt++;
		moves[zeroCnt-1] = idxk;
	    }
	}
    } // end findMoves
    int findZeroTile(int target) {
	int out = 0;
	for ( int k = 0; k < Knight3.numMoves; k++ ) {
	    int targetNeighbor = getNeighbor(target, k);
	    if ( 0 == Knight3.board[targetNeighbor].getPos() ) { 
		out = targetNeighbor;
		break;
	    }
	}
	return out;
    } 
    int [] getAllNeighbors(int targetTile) {
	int [] neighbors = new int [Knight3.numMoves]; 
	for ( int k = 0; k < Knight3.numMoves; k++ ) 
	    neighbors[k] = getNeighbor(targetTile, k);
	return neighbors;
    } // end getAllNeighbors


} // end Node3



class Tile {
    public Tile(int bi) { boardIndex = bi; }
    private int boardIndex;
    public int getBoardIndex() { return boardIndex; }
    private int pos = -1;
    public void setPos(int z) { pos = z; }
    public int getPos() { return pos; }
    private Tile [] neighbors = new Tile[] { 
	null, null, null, null,
	null, null, null, null
    };
    public Tile getNeighbor(int i) { return neighbors[i]; }
    public void setNeighbor(Tile t, int i) { neighbors[i] = t; }
    private int freeCnt = 0;
    public int getFreeCnt() { return freeCnt; }
    public void setFreeCnt(int z) { freeCnt = z; }
    public void incrementFreeCnt() { freeCnt++; }
    public void decrementFreeCnt() { freeCnt--; }
    private LinkedList<Tile> nextTiles = new LinkedList<Tile>();
    public LinkedList<Tile> getNextTiles() { return nextTiles; }
    private int direction = -1;
    public int getDirection() { return direction; }
    public void unSetDirection() { direction = -1; }
    public void setDirForward() { direction = 0; }
    public boolean isDirectionForward() { return (0 == direction); }
    public void setDirBackward() { direction = 1; }
    public boolean isDirectionBackward() { return (1 == direction); }
} // end Tile



