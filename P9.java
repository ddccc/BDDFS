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
    static public int direction = 0; // -1/ 0 +1 | B / <-> / F
    static public boolean theDirection = false;
    static public boolean getDirection() {
	theDirection = ( 1 == direction ? true :
			 ( -1 == direction ? false : !theDirection )) ;
	return theDirection;
    } // end getDirection

    public static void main(String[] args) {
	// Set up the board:
	System.out.println("P9 for 3x3 square");

	System.out.println("Start");
	// determines # random moves of zero after the init

	// Square start = new Square(32, true); 
	// Square start = new Square(64, true); 
	// Square start = new Square(128, true); 
	// Square start = new Square(256, true); 
	// Square start = new Square(512, true); 
	Square start = new Square(2*1024, true); 
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
	System.out.println("z: " + z); 
	if ( 1 == z ) addToHashtable(goal); // forward search
	if ( -1 == z ) addToHashtable(start); //  backward search
	if ( 0 == z ) { addToHashtable(start); addToHashtable(goal); }
	         // bidirection search
	// addToHashtable(start); addToHashtable(goal);
        Node9 n9 = new Node9(start, goal);
	           // n9.explore();
	P9.stack.push(n9);
	while ( P9.cnt < 200000 ) {
	// while ( P9.cnt < 3 ) {
	    P9.cnt++;
	    // System.out.println("--- loop cnt: " + cnt);
	    if ( stack.empty() ) {
		System.out.println("main stack.empty()"); 
		break;
	    }
	    Node9 nx = stack.pop();
	    nx.explore();
	}
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
    protected int cnt = 0;
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
    protected int[] findMoves() {
	int locSq = this.zeroLoc;
	return moves[locSq];
    } // findMoves
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




	if ( 200000 < P9.cnt  ) {
	// if ( 4 < P9.cnt  ) {
	    System.out.println("P9.cnt " + P9.cnt);
	    System.out.println("P9.cnt too large");
	    System.exit(0);
	}
	boolean zz = P9.getDirection();
	// if ( zz )  P9.show(sqS); else P9.show(sqG); 

	if ( 1 == P9.direction || zz) {
	    Square found = P9.squaresTable.get(P9.getKey(sqS));
	    if ( null != found ) {
		/* System.out.println("DDDDD");
		P9.show(found);
		P9.show(sqS); */
		if ( !found.forward ) {
		    System.out.println("Found backward 0");
		    System.out.println("P9.cnt " + P9.cnt);
		    P9.show(found);
		    System.out.println("found.cnt: " + found.cnt);
			System.out.println("hash size " +
					   P9.squaresTable.size());
		    int foundCnt = 0; Square fParent = found;
		    while (null != fParent) {
			foundCnt++; fParent = fParent.parent;
		    }
		    System.out.println("foundCnt " + foundCnt);
		    P9.show(sqS);
		    int sqCnt = 0; Square fParent2 = sqG;
		    while (null != fParent2) {
			sqCnt++; fParent2 = fParent2.parent;
		    }
		    System.out.println("sqGCnt " + sqCnt);
		    System.exit(0);
		}
	    }
	    // System.out.println("CCCCC");
	    P9.addToHashtable(sqS);
	    exploreForward();
	} 
	if ( -1 == P9.direction  || !zz) { 
	    // System.out.println("DDDDD");
	    Square found = P9.squaresTable.get(P9.getKey(sqG));
	    if ( null != found ) {
		if ( found.forward ) {		    
		    System.out.println("Found forward 0");
		    System.out.println("P9.cnt " + P9.cnt);
		    P9.show(found);
		    int foundCnt = 0; Square fParent = found;
		    while (null != fParent) {
			foundCnt++; fParent = fParent.parent;
		    }
		    System.out.println("foundCnt " + foundCnt);
		    P9.show(sqG);
		    int sqCnt = 0; Square fParent2 = sqS;
		    while (null != fParent2) {
			sqCnt++; fParent2 = fParent2.parent;
		    }
		    System.out.println("sqCnt " + sqCnt);
		    System.exit(0);
		}
	    }
	    P9.addToHashtable(sqG);
	    exploreBack();	
	}
    } // end explore
	
    public void exploreForward() {
	// System.out.println("P9.cnt " + P9.cnt);
	int locS = sqS.zeroLoc; // location of zero in sqS
	int [] moveS = sqS.findMoves(); //
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
			System.out.println("P9.cnt " + P9.cnt);
			System.out.println("Found backward found:");
			P9.show(found);
			System.out.println("When created: " + found.cnt);
			System.out.println("Hash size " +
					   P9.squaresTable.size());

			int foundCnt = 0; Square fParent = sqS;
			Square p1 = fParent;
			while (null != fParent) { p1 = fParent;
			    foundCnt++; fParent = fParent.parent;
			}
			System.out.println("p1 - Source");
			P9.show(p1);
			System.out.println("foundCnt " + foundCnt);

			P9.show(sqG); 
			int sqCnt = 0; Square fParent2 = sqG;
			Square p2 = fParent2;
			while (null != fParent2) { p2 = fParent2;
			    sqCnt++; fParent2 = fParent2.parent;
			}
			System.out.println("p2 - Goal");
			P9.show(p2); 
			System.out.println("sqCnt " + sqCnt);
			System.exit(0);
		    } 
		    continue; // ignore because encounterd earlier 
		}
		newSquare.parent = sqS;
		newSquare.cnt = P9.cnt;
		newSquare.previousZeroLoc = locS;
		// System.out.println("EEEE P9.show(newSquare)");
		// P9.show(newSquare);
		nextSquares[newSquareCnt] = newSquare;
		newSquareCnt++;
	    }
	}

	// System.out.println("newSquareCnt: " + newSquareCnt);
	// put in Hashtable
	// System.out.println(" newSquareCnt " +  newSquareCnt);
	if ( 0 == newSquareCnt ) {
	    // System.out.println("0 == newSquareCnt");
	    // System.out.println("P9.cnt" + P9.cnt);
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
	int [] moveG = sqG.findMoves(); //
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
			System.out.println("P9.cnt " + P9.cnt);
			System.out.println("Found forward found:");	
			P9.show(found);
			System.out.println("When created: " + found.cnt);
			System.out.println("Hash size " +
					   P9.squaresTable.size());
			int foundCnt = 0; Square fParent = sqS;
			Square p1 = fParent;
			while (null != fParent) { p1 = fParent;
			    foundCnt++; fParent = fParent.parent;
			}
			System.out.println("p1 - Source");
			P9.show(p1);
			System.out.println("foundCnt " + foundCnt);

			P9.show(sqS);
			int sqCnt = 0; Square fParent2 = sqG;
			Square p2 = fParent2;
			while (null != fParent2) { p2 = fParent2;
			    sqCnt++; fParent2 = fParent2.parent;
			}
			System.out.println("p2 - Goal");
			P9.show(p2); 
			System.out.println("sqCnt " + sqCnt);
			System.exit(0);
		    }  
		    continue; // ignore because encounterd earlier 
		}	
		newSquare.parent = sqG;
		newSquare.cnt = P9.cnt;
		newSquare.previousZeroLoc = locG;
		// P9.show(newSquare);
		nextSquares[newSquareCnt] = newSquare;
		newSquareCnt++;
	    }
	}
	// System.out.println("newSquareCnt: " + newSquareCnt);
	// put in Hashtable
	// System.out.println(" newSquareCnt " +  newSquareCnt);
	if ( 0 == newSquareCnt ) {
	    // System.out.println("0 == newSquareCnt");
	    // System.out.println("P9.cnt" + P9.cnt);
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


