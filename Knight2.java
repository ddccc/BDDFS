// File: c:/ddc/Java/Knight/Knight2.java
// Date: Wed Mar 16 13:17:39 2022
// (C) OntoOO/ Dennis de Champeaux
import java.io.*;
import java.util.*;

public class Knight2 {

    // static final int side = 8;    
    static final int side = 6;    
    static final int edge = 2;
    static final int side2 = side + 2 * edge;
    static final int lng = side2 * side2;
    static Tile [] board = new Tile[lng];
    static final int numMoves = 8;
    static final int twoside2 = 2 * side2;
    static int [] candidateMoves = new int[] { 
	-twoside2 - 1, -twoside2 + 1,
	-side2 - 2, -side2 + 2,
	side2 - 2, side2 + 2,
	twoside2 - 1, twoside2 + 1};

    static private String show1(Tile tk) {
	int k = tk.getPos();
	// int k = tk.getFreeCnt();
	return ( k < 0 ? "xxx" :
		 //	 ( k < side + 2 ? "  " + k : " " + k ) );
	 ( k < 10 ? "  " + k : " " + k ) );
    }
    static private void show() {
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
    static  int solutionCnt = 0; 
    static int failureCnt = 0;

    // static private int move(int tile, int depth) {
    static private void move(int tile, int depth) {
	Tile t = board[tile];
	t.setPos(depth);
	if ( maxDepth == depth ) { // proto solution found
	    if ( tile == 4 * side2 + 3 ) { // tour solution found
		solutionCnt++;
		/*
		show();
		t.setPos(0);
		long endTime = (new Date()).getTime();
		long delta = endTime - startTime0;
		int averageTime = (int) delta / solutionCnt;
		System.out.println("# " + solutionCnt +
				   // " Duration: " + (endTime - startTime) +
				   " Average: " + averageTime + 
				   " FailureCnt: " + failureCnt
				   );
		failureCnt = 0;
		startTime = endTime;
		*/
	    }
	    t.setPos(0);
	    return;
	}
	// check whether we can proceed
	int zeroCnt = 0;
	int kIdx = 0;
	for ( int k = 0; k < numMoves; k++) {
	    Tile tk = t.getNeighbor(k);
	    if ( null != tk && 0 == tk.getPos() ) { // not yet visited
		tk.decrementFreeCnt();
		if ( 0 == tk.getFreeCnt() ) { 
		    zeroCnt++;
		    kIdx = k;
		}
	    }
	}
	if ( 1 < zeroCnt ) { // conflict
	    for ( int k = 0; k < numMoves; k++) {
		Tile tk = t.getNeighbor(k);
		if ( null != tk && 0 == tk.getPos() ) { // not yet visited
		    tk.incrementFreeCnt();
		}
	    }
	    t.setPos(0);
	    return;
	}
	if ( 0 == zeroCnt ) { // go deeper
	    for (int i = 0; i < numMoves; i++) {
		int tile2 = tile + candidateMoves[i];
		// System.out.println("trace tile:" + tile + " tile2 " + tile2);
		if ( 0 == board[tile2].getPos() ) { 
		    //     int out = move(tile2, depth + 1);
		    move(tile2, depth + 1);

		}
	    }
	    // must backtrack now
	    failureCnt++;

	    for ( int k = 0; k < numMoves; k++) {
		Tile tk = t.getNeighbor(k);
		if ( null != tk && 0 == tk.getPos() ) { // not yet visited
		    tk.incrementFreeCnt();
		}
	    }
	    t.setPos(0);
	    return;
	}
	// forced move to kIdx
	int tile2 = tile + candidateMoves[kIdx];
	move(tile2, depth + 1);
	for ( int k = 0; k < numMoves; k++) {
	    Tile tk = t.getNeighbor(k);
	    if ( null != tk && 0 == tk.getPos() ) { // not yet visited
		tk.incrementFreeCnt();
	    }
	}
	t.setPos(0);
	return;
    } // end move

    static long startTime = 0;
    static long startTime0 = 0;

  public static void main(String[] args) {
      // initialize board
      for ( int i = 0; i < lng; i++ ) board[i] = new Tile();
      // clear the board
      for ( int i = 0; i < side; i++ ) {
	  int row = twoside2 + i * side2;
	  for ( int j = 0; j < side ; j++ ) {
	      int column = edge + j;
	      int sum = row + column;
	      board[sum].setPos(0);
	  }
      }
      // set the neighbors
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
      startTime = System.currentTimeMillis();
      move(twoside2 + edge, 1);
      startTime0 = System.currentTimeMillis();
      System.out.println("solutionCnt " + solutionCnt);
      System.out.println("Duration " + (startTime0 - startTime));
  }
 
} // end of Knight


class Tile {
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
    // public void setFreeCnt(int z) { freeCnt = z; }
    public void incrementFreeCnt() { freeCnt++; }
    public void decrementFreeCnt() { freeCnt--; }
}



