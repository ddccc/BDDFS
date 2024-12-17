import java.io.*;
import java.util.*;

class Knight {

    static final int side = 6;    
    static final int edge = 2;
    static final int side2 = side + 2 * edge;
    static final int lng = side2 * side2;
    static int [] board = new int[lng];
    static final int numMoves = 8;
    static final int twoside2 = 2 * side2;
    static int []candidateMoves = new int[] { 
	-twoside2 - 1, -twoside2 + 1,
	-side2 - 2, -side2 + 2,
	side2 - 2, side2 + 2,
	twoside2 - 1, twoside2 + 1};

    static int analyzeDepth = 54;
    static int analyzeCnt = 0;
    static int analyzeGain = 0;

    static private String show1(int k) {
	return ( k<0 ? "xxx" :
		 ( k <10 ? "  " + k : " " + k ) );
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
    static private int move(int tile, int depth) {
	board[tile] = depth;
	show();
	if ( maxDepth == depth ) { // proto solution found
	    if ( tile == 4 * side2 + 3 ) { // tour solution found
		solutionCnt++;
		show();
		board[tile] = 0;
		// long endTime = (new Date()).getTime();
		// long delta = endTime - startTime0;
		// int averageTime = (int) delta / solutionCnt;
		/*
		System.out.println("# " + solutionCnt +
				   // " Duration: " + (endTime - startTime) +
				   " Average: " + averageTime + 
				   " FailureCnt: " + failureCnt +
				   " aDepth: " + analyzeDepth +
				   " aCnt: " + analyzeCnt +
				   " aGain: " + analyzeGain
				   );
		// */
		failureCnt = 0;
		// startTime = endTime;
		return depth;
	    }
	    board[tile] = 0;
	    return depth;
	}
	// go deeper
	for (int i = 0; i < numMoves; i++) {
	    int tile2 = tile + candidateMoves[i];
	    if ( 0 == board[tile2] ) { 
		int out = move(tile2, depth + 1);
		if ( out < depth ) {
		    board[tile] = 0;
		    return out;
		}
	    }
	}
	// must backtrack now
	failureCnt++;
	// /*
	if ( analyzeDepth <= depth ) { // check whether we can backtrack deeper
	    analyzeCnt++;
	    int targetDepth = findTarget();
	    if ( targetDepth < depth ) analyzeGain++;
	    board[tile] = 0;
	    return targetDepth;
	}
	// */

	// analyzeDepth++;
	board[tile] = 0;
	return depth;
    }
    static final int leftBottom = 2 * side2 + edge;
    static final int rightTop = lng - 2 * side2 - edge -1;

    static private int findTarget() {
	int out = maxDepth;
	int target;
	for ( int i = leftBottom; i <= rightTop; i++ ) 
	    if ( 0 == board[i] ) {
		target = findTarget1(i);
		if ( target < out ) out = target;
	    }
	return out;
    }
    static private int findTarget1(int tile) {
	int out = 0;
	int tile2;
	int bordValue;
	for (int i = 0; i < numMoves; i++) {
	    tile2 = tile + candidateMoves[i];
	    bordValue = board[tile2];
	    if ( bordValue < 0 ) continue; // outside board
	    if ( 0 == bordValue ) return maxDepth; // tile is still reachable
	    if ( out < bordValue ) out = bordValue; // tile reachable from this depth
	}
	return out;
    }

    static long startTime = 0;
    static long startTime0 = 0;

  public static void main(String[] args) {
      // initialize board
      for (int i = 0; i < lng ; i++) board[i] = 0;
      int end = twoside2;
      for (int i = 0; i < end ; i++) board[i] = -1;
      int start = lng - end;
      for (int i = start; i < lng ; i++) board[i] = -1;
      for (int i = end; i < start ; i = i + side2) {
	  board[i] = -1;
	  board[i+1] = -1;
	  board[i + side2 -2] = -1;
	  board[i + side2 -1] = -1;
      }
      // show();
      // startTime = (new Date()).getTime();
      // startTime0 = startTime;
      // move(end + edge, 1);

      board[end + edge] = 1; // 1st move
      long startTimeX = System.currentTimeMillis();
      move(end + side2 + edge + 2, 2); // 2nd move
      long endTime = System.currentTimeMillis();
      System.out.println("timing " + (endTime-startTimeX));
      System.out.println("solutionCnt " + solutionCnt);
  }
 
} // end of Knight

