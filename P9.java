// File: c:/ddc/Java/Knight/P9.java
// Date: Wed Feb 11 09:48:07 2026 --- Fri Feb 27 11:42:01 2026
// (C) OntoOO/ Dennis de Champeaux

/*
   Solve 8-puzzle
   The 3x3 square is here a linear array [0-8]
   Two params: 
   -- select search direction: F | B | bidiretion
   -- select # relocations of zero in the start state
      the idea is that more relocations makes the problem more 
      difficult; no clear evidence however
   The BIG surprise is the loop count is consistently lower in
   bidiretion search against forward AND backward search!
*/
import java.io.*;
import java.util.*;

public class P9 {

    static public void show(Square sq) {
	for (int i = 0; i < 3; i++) {
	    System.out.print("   ");
	    for (int j = 0; j < 3; j++) 
		System.out.print(sq.state[i*3 + j] + " ");
	    System.out.println();
	}
	/*
	System.out.println("ZeroLoc: " + sq.zeroLoc);
	System.out.println("Forward: " + sq.forward);
	System.out.println("PreviousO: " + sq.previousZeroLoc);
	System.out.println();
	*/
    } // end show

    static public Hashtable<String, Square> squaresTable = 
	new Hashtable<>();

    static public String getKey(Square sq) {
	int [] state = sq.state;
	String key = "";
	for (int i = 0; i < state.length; i++)
	    key += state[i];
	return key;
    }
    static public void addToHashtable(Square sq) {
	String key = getKey(sq);
	squaresTable.put(key, sq);
    } // end addToHashtable

    static public Stack<Node9> stack = new Stack<>();
    static public int cnt = 0; 
    // determines bi-direction or forward or backward search
    static public int direction = -1; // -1/ 0 +1 | B / <-> / F
    static public boolean theDirection = false;
    
    static public boolean getDirection() {
	theDirection = ( 1 == direction ? true :
			 ( -1 == direction ? false : !theDirection )) ;
	return 	theDirection;
    } // end getDirection

    public static void main(String[] args) {
	// Set up the board:
	System.out.println("P9 for 3x3 square");

	System.out.println("Start");
	// determines # random moves of zero after the init
	Square start = new Square(8*1024, true); 
	// Square start = new Square(true);
	show(start);
	// System.exit(0);

	// Add to hashtable
	// addToHashtable(start);

	System.out.println("Goal");
	Square goal = new Square(false);
	show(goal);
	// addToHashtable(goal);
	int z = P9.direction;
	if ( 1 == z ) addToHashtable(goal); // forward search
	if ( -1 == z ) addToHashtable(start); //  backward search
	if ( 0 == z ) { addToHashtable(start); addToHashtable(goal); }
	// bidirection search

	// System.exit(0);

	// System.out.println("S = S ? " + start.equals(start));
	// System.out.println("S = G ? " + start.equals(goal));
	// System.exit(0);
        Node9 n9 = new Node9(start, goal);
	// n9.explore();
	P9.stack.push(n9);
	while ( P9.cnt < 400000 ) {
	    P9.cnt++;
	    System.out.println("--- loop cnt: " + cnt);
	    if ( stack.empty() ) {
		System.out.println("main stack.empty()"); 
		break;
	    }
	    Node9 nx = stack.pop();
	    nx.explore();
	}

	/*
	Node9 fNode = new Node9(true, startState); // forward thread
	Node9 bNode = new Node9(false, goalState); // backward thread

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
	// / *
	try { // wait for them to terminate
	    forward.join();
	    backward.join();
	} catch (InterruptedException e) {}
	// * /
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

} // end of P9



class Square {
      final int a = 0;  
      final int b = 1;  
      final int c = 2;  
      final int d = 3;  
      final int e = 4;  
      final int f = 5;  
      final int g = 6;  
      final int h = 7;  
      final int i = 8;  

    protected int [] state = new int[] { 0,1,2,3,4,5,6,7,8 };
    protected int zeroLoc = 0;
    protected boolean forward = true;
    protected int previousZeroLoc = -1;
    protected final int[][] moves =
          {     { -1,  d, -1,  b }, // a
	        { -1,  e,  a,  c }, // b
		{ -1,  f,  b, -1 }, // c
		{  a,  g, -1,  e }, // d
		{  b,  h,  d,  f }, // e
		{  c,  i,  e, -1 }, // f
		{  d, -1, -1,  h }, // g
		{  e, -1,  g,  i }, // h
		{  f, -1,  h, -1 }, // i
		    };
    protected Square parent = null;
    Square(boolean b) { forward = b; }
    Square(int k, boolean bb) { 
	forward = bb;
	Random random = new Random(777);
	this.previousZeroLoc = this.zeroLoc;
	for (int i = 0; i < k; i++) {
	    int [] moveS = moves[zeroLoc];
	    int [] moveS2 = new int[4];
	    int entries = 0;
	    for (int j = 0; j < 4; j++) {
		int w = moveS[j];
		/*
		int previousZeroLoc = this.previousZeroLoc;
		System.out.println("previousZeroLoc " +
				   previousZeroLoc);
		// */
		if (0 <= w && w != previousZeroLoc) {
		    moveS2[entries] = moveS[j];
		    entries++;
		}
	    }
	    int z = random.nextInt(entries);
	    int z2 = moveS2[z];
	    int a = state[zeroLoc]; int b = state[z2];
	    state[z2] = a; state[zeroLoc] = b;
	    this.previousZeroLoc = zeroLoc;
	    zeroLoc = z2;
	    /*
	    // System.out.println("Inside");
	    P9.show(this);
	    System.out.println();
	    */
	}
    }
    Square(Square old, int j, boolean b) {
	forward = b;
	int zeroOld = old.zeroLoc;
	this.state = Arrays.copyOf(old.state, old.state.length);
	int k = this.state[j];
	this.state[j] = 0;
	this.zeroLoc = j;
	this.state[zeroOld] = k;
    }
    /*
    protected int[] find(Square sq) {
	int locSq = sq.zeroLoc;
	return moves[locSq];
    } // find
    */
    protected int[] find() {
	int locSq = this.zeroLoc;
	return moves[locSq];
    } // find
    public boolean equals(Square sq) {
	// if ( zeroLoc != sq.zeroLoc ) return false;
	for (int i = 0; i < state.length; i++)
	    if ( state[i] != sq.state[i] ) return false;
	return true;
    } // end equals

} // end Square

class Node9 {
    Square sqS = null; Square sqG = null;

    Node9(Square sqs, Square sqg) {
	sqS = sqs; sqG = sqg;

	// P9.direction = true;
	// P9.direction = false;
	// P9.direction = !P9.direction;
    }
    public void explore() {
	if ( 330000 < P9.cnt  ) {
	// if ( 4 < P9.cnt  ) {
	    System.out.println("P9.cnt " + P9.cnt);
	    System.exit(0);
	}
	// P9.show(sqS); 
	boolean zz = P9.getDirection();
	// System.out.println("zz " + zz);
	// System.exit(0);
	if ( zz )  P9.show(sqS); else P9.show(sqG); 

	if ( 1 == P9.direction || zz) {
	    // System.out.println("BBBBB");
	    Square found = P9.squaresTable.get(P9.getKey(sqS));
	    if ( null != found ) {
		/* System.out.println("DDDDD");
		P9.show(found);
		P9.show(sqS); */
		if ( !found.forward ) {
		    System.out.println("Found backward");
		    P9.show(found);
		    P9.show(sqS);
		    System.exit(0);
		}
	    }
	    // System.out.println("CCCCC");
	    P9.addToHashtable(sqS);
	    exploreForward();
	} 
	if ( -1 == P9.direction  || !zz) { 
	    Square found = P9.squaresTable.get(P9.getKey(sqG));
	    if ( null != found ) {
		if ( found.forward ) {
		    System.out.println("Found forward");
		    P9.show(found);
		    P9.show(sqS);
		    System.exit(0);
		}
	    }
	    P9.addToHashtable(sqG);
	    exploreBack();	
	}
	// System.out.println("YYYY");
	// alternate ??? not here this time
	/*
	if ( P9.direction )
	    exploreForward();
	else
	    exploreBack();
	*/
    }
	
    public void exploreForward() {
	// System.out.println("AAAA");
	int locS = sqS.zeroLoc; // location of zero in sqS
	// System.out.println("locS " + locS);
	int [] moveS = sqS.find(); //
	int moveSlength = moveS.length; // always 4
	//      P9.show(sqS);
	Square [] nextSquares = new Square[moveSlength];
	int newSquareCnt = 0;
	for (int i = 0; i < moveS.length; i++) {
	    int j = moveS[i];
	    if ( 0 <= j ) { 
		if ( j == sqS.previousZeroLoc ) {
		    // System.out.println("Encounter back move");
		    continue;
		}
		// System.out.print("0 moveTo " + j + "  "); 
		// int k = sqS.state[j];
		// System.out.println(k + " moveTo " + locS);
		Square newSquare = new Square(sqS, j, sqS.forward);

		// check whether newSquare is in hashtable
		String key = P9.getKey(newSquare);
		Square found = P9.squaresTable.get(key);
		if ( null != found ) {
		    if ( !found.forward ) {
			System.out.println("Found backward");
			P9.show(found);
			P9.show(newSquare);
			System.exit(0);
		    }			
		    continue;
		}
		newSquare.parent = sqS;
		newSquare.previousZeroLoc = locS;
		// System.out.println("EEEE P9.show(newSquare)");
		// P9.show(newSquare);
		nextSquares[newSquareCnt] = newSquare;
		newSquareCnt++;
	    }
	}

	// System.out.println("newSquareCnt: " + newSquareCnt);
	// put in Hashtable
	System.out.println(" newSquareCnt " +  newSquareCnt);
	if ( 0 == newSquareCnt ) {
	    System.out.println("0 == newSquareCnt");
	    System.out.println("P9.cnt" + P9.cnt);
	    return;
	}
	/*
	for (int i = 0; i < newSquareCnt; i++) {
	    Square sq = nextSquares[i];
	    P9.addToHashtable(sq);
	}
	*/

	// push all survivors on the stack
	for (int i = 0; i < newSquareCnt; i++) {
	    Square sq = nextSquares[i];
	    Node9 ny = new Node9(sq, sqG);
	    P9.stack.push(ny);
	}

    } // end exploreForward

    public void exploreBack() {
	int locG = sqG.zeroLoc; // location of zero in sqG
	// System.out.println("locS " + locS);
	int [] moveG = sqG.find(); //
	int moveGlength = moveG.length; // always 4
	// P9.show(sqG);
	Square [] nextSquares = new Square[moveGlength];
	int newSquareCnt = 0;
	for (int i = 0; i < moveG.length; i++) {
	    int j = moveG[i];
	    if ( 0 <= j ) { 
		if ( j == sqG.previousZeroLoc ) {
		    // System.out.println("Encounter back move");
		    continue;
		}
		// System.out.print("0 moveTo " + j + "  "); 
		// int k = sqS.state[j];
		// System.out.println(k + " moveTo " + locS);
		Square newSquare = new Square(sqG, j, sqG.forward);
		// check whether newSquare is in hashtable
		String key = P9.getKey(newSquare);
		Square found = P9.squaresTable.get(key);
		if ( null != found ) {
		    if ( found.forward ) {
			System.out.println("Found forward");
			P9.show(found);
			P9.show(newSquare);
			System.exit(0);
		    }		
		    continue;
		}
		newSquare.parent = sqG;
		newSquare.previousZeroLoc = locG;
		// P9.show(newSquare);
		nextSquares[newSquareCnt] = newSquare;
		newSquareCnt++;
	    }
	}
	System.out.println("newSquareCnt: " + newSquareCnt);
	// put in Hashtable
	// System.out.println(" newSquareCnt " +  newSquareCnt);
	if ( 0 == newSquareCnt ) {
	    System.out.println("0 == newSquareCnt");
	    System.out.println("P9.cnt" + P9.cnt);
	    return;
	}
	/*
	for (int i = 0; i < newSquareCnt; i++) {
	    Square sq = nextSquares[i];
	    P9.addToHashtable(sq);
	}
	*/

	// push all survivors on the stack
	for (int i = 0; i < newSquareCnt; i++) {
	    Square sq = nextSquares[i];
	    Node9 ny = new Node9(sqS, sq);
	    P9.stack.push(ny);
	}

    } // end exploreBack


} // end Node9


// OLD stuff for parallel processing - perhaps.
    /*
    // void move(int state) {
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
	/*
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
		/ * // display the solution
		   System.out.println("\nmove moveCnt " + Knight7.moveCnt);
		   System.out.println("move tilesCnt " + Knight7.tilesCnt);
		   System.out.println("move goalTile " + goalTile);
		   Knight7.show();
		   // System.out.println("move goal state!!!!");
		   if ( 0 < Knight7.solutionCnt ) System.exit(0);
		   // * /
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

	/ *
	System.out.println("EXIT moveCnt " + Knight7.moveCnt);
	System.exit(0);
	* /
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

}
	*/
