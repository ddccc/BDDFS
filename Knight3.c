// File: c:/ddc/Java/Knight/Knight3.c
// Date: Thu Feb 16 10:20:37 2023
// (C) OntoOO/ Dennis de Champeaux

// Create cyclic knight tours on 8x8 or 6x6 boards

#include <stdio.h>
#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>


FILE *fp;


typedef int boolean;
#define false 0
#define true 1

typedef struct Tile {
  int boardIndex;
  int pos;// = -1;
  struct Tile *neighbors [8]; /* = { 
	null, null, null, null,
	null, null, null, null
	};  */
  int freeCnt; //  = 0;
  int direction; // = -1;
} Tile;
 void setNeighbor(Tile *t, Tile *nk, int i) { 
   t->neighbors[i] = nk;
 }
Tile* getNeighbor(Tile *t, int i) { return t->neighbors[i]; }
void setBoardIndex(Tile *tk, int i) { tk->boardIndex = i; }
void setPos(Tile *tk, int i) { tk->pos = i; }
int getPos(Tile *tk) { return tk->pos; }
void setFreeCnt(Tile *tk, int i) { tk->freeCnt = i; }
int getFreeCnt(Tile *tk) { return tk->freeCnt; }
void incrementFreeCnt(Tile *tk) { tk->freeCnt++; }
void decrementFreeCnt(Tile *tk) { tk->freeCnt--; }
void setDirection(Tile *tk, int i) { tk->direction = i; }
void setDirForward(Tile *tk) { setDirection(tk, 0); }
void setDirBackward(Tile *tk) { setDirection(tk, 1); }
int getDirection(Tile *tk) { return tk->direction; }


// The board is a linear array with all sides having an edge of size 2.

    //   int side = 6;    
static const int side = 8;    
static const int edge = 2;
static int side2; // = side + 2 * edge; // 12
static int lng; // = side2 * side2; 
static int twoside2; // = 2 * side2; // 24
static int startState1; // = twoside2 + edge; // 26
static int startState; // = 3 * side2 + edge + 2; // 40
static int goalState; // = 4 * side2 + edge + 1; // 51
// static Tile *board[lng]; //lng
static Tile *board[144]; // lng -- change if side = 6
static int numMoves; // numMoves = 8;
static int maxTilesSet; // maxTilesSet = side*side;
static int targetTilesSet; // targetTilesSet = maxTilesSet-1; 
                           // one before last jump
static int halfTilesSet; // = maxTilesSet/2;
// static int candidateMoves[8]; 
/* = { 
	-twoside2 - 1, -twoside2 + 1,
	-side2 - 2, -side2 + 2,
	side2 - 2, side2 + 2,
	twoside2 - 1, twoside2 + 1};
*/
void show() {
  int item = twoside2 + 2, i, j;
  for (i = 1; i <= side; i++) {
    int last = item + side;
    for (j = item; j < last; j++) {      
      Tile *t = board[j];
      int k = getPos(t);
      if ( k < 10 ) printf("  %i", k); else
	printf(" %i", k);
    }
    printf("\n");
    item = item + side2;
  }
  printf("\n");
}

void showf(FILE *fp) {
  int item = twoside2 + 2, i, j;
  for (i = 1; i <= side; i++) {
    int last = item + side;
    for (j = item; j < last; j++) {      
      Tile *t = board[j];
      int k = getPos(t);
      if ( k < 10 ) fprintf(fp, "  %i", k); else
	fprintf(fp, " %i", k);
    }
    fprintf(fp, "\n");
    item = item + side2;
  }
  fprintf(fp,"\n");
}

/* obsolete
     void show1(Tile *tk) {
	int k = getPos(tk);
	// int k = tk.getFreeCnt();
	if ( k < 0 ) { printf("xxx"); return; }
	if ( k < 10 ) { printf("  %i", k); return; }
	printf(" %i", k);
    }
    void show() {
      int cnt = 0; int i;
	for (i = 0; i < lng; i++) {
	  // System.out.print(show1(board[i]));
	  // printf(show1(board[i]));
	  show1(board[i]);
	  cnt++;
	  if ( cnt == side2 ) {
	    cnt = 0;
	    // System.out.println();
	    printf("\n");
	  }
	}
	// System.out.println();
	printf("\n");
    }
*/
int maxDepth; // = side * side; 
long solutionCnt = 0; 
int moveCnt = 0; 
int failureCnt = 0;
//  int numSetTiles = 2;
int tilesCnt = 3;
int fCnt = 2;
int bCnt = 3;

long startTime = 0;
long startTime0 = 0;

void Node3();

// change for side = 6
int candidateMoves[8] = {-25, -23, -14, -10, 10, 14, 23, 25};

int main (int argc, char *argv[]) {
  //    public  void main(String[] args) {
  side2 = side + 2 * edge; // 12
  lng = side2 * side2; 
  twoside2 = 2 * side2; // 24
  startState1 = twoside2 + edge; // 26
  startState = 3 * side2 + edge + 2; // 40
  goalState = 4 * side2 + edge + 1; // 51
  // board[lng] = malloc(lng * sizeof(Tile));
  numMoves = 8;
  maxTilesSet = side*side;
  targetTilesSet = maxTilesSet-1; // one before last jump
  halfTilesSet = maxTilesSet/2;
  /*
  candidateMoves = { 
    -twoside2 - 1, -twoside2 + 1,
    -side2 - 2, -side2 + 2,
    side2 - 2, side2 + 2,
    twoside2 - 1, twoside2 + 1};
  */
  // Set up the board:
  // System.out.println("side:" + side);
  printf("side: %i \n", side);
  // System.out.println("side2:" + side2);
  printf("side2: %i \n", side2);
  // System.out.println("startState:" + startState);
  printf("startState: %i \n", startState);
  // System.out.println("goalState:" + goalState);
  printf("goalState: %i \n", goalState);
  // initialize board
  for ( int i = 0; i < lng; i++ ) { 
    Tile *tilei = malloc(sizeof(Tile));
    setBoardIndex(tilei, i);
    setPos(tilei, -1);
    setFreeCnt(tilei, 0);
    setDirection(tilei, -1);
    board[i] = tilei;
  }
  // clear the board
  for ( int i = 0; i < side; i++ ) {
    int row = twoside2 + i * side2;
    for ( int j = 0; j < side ; j++ ) {
      int column = edge + j;
      int sum = row + column;
      // board[sum].setPos(0);
      setPos(board[sum], 0);
    }
  }

  // show();
  // init 3 starting positions
  // board[startState1].setPos(1);
  setPos(board[startState1], 1);
  // board[startState1].setDirForward();
  setDirForward(board[startState1]);
  // board[startState].setPos(2);
  setPos(board[startState], 2);
  // board[startState].setDirForward();
  setDirForward(board[startState]);
  // board[goalState].setPos(3);
  setPos(board[goalState], 3);
  // board[goalState].setDirBackward();
  setDirBackward(board[goalState]);
	
  // set the neighbors of the tiles
  for ( int i = 0; i < side; i++ ) {
    int row = twoside2 + i * side2;
    for ( int j = 0; j < side ; j++ ) {
      int column = edge + j;
      int idx = row + column;
      Tile *t = board[idx];
      for ( int k = 0; k < numMoves; k++ ) {
	Tile *nk = board[idx + candidateMoves[k]];
	// int posk = nk.getPos();
	int posk = getPos(nk);
	// if ( 0 == posk ) t.setNeighbor(nk, k);
	if ( 0 == posk ) // t.setNeighbor(nk, k);
	  setNeighbor(t, nk, i);
      }
    }
  }
  // calculate the free neighbors
  for ( int i = 0; i < side; i++ ) {
    int row = twoside2 + i * side2;
    for ( int j = 0; j < 8 ; j++ ) {
      int column = edge + j;
      int idx = row + column;
      Tile *t = board[idx];
      for ( int k = 0; k < numMoves; k++ ) {
	Tile *nk = // t.getNeighbor(k);
	  getNeighbor(t, k);
	if ( NULL == nk ) continue;
	// t.incrementFreeCnt();
	incrementFreeCnt(t);
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
  // Node3 initNode = new Node3(startState, goalState);
  double startTime = clock();
  Node3(startState, goalState);
  // long startTime = System.currentTimeMillis();
  // ... get the ball rolling
  // initNode.move();
  double endTime = clock();
  // System.out.println("\nsolutionCnt " + solutionCnt);
  printf("\nsolutionCnt %i\n", solutionCnt);
  // System.out.println("Duration " + (endTime-startTime));
  printf("Duration %d\n", (endTime-startTime));
  // show();
  
} // end main

/*
     private int moverCnt = 0;
     private void check() {
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
*/

int findMoves();
void move();

// /*
// class Node3 {
void Node3(int fidy, int bidy) {
    int fidx, bidx;
    boolean moveForward = false;
    // int [] moves = new int[Knight3.numMoves];
    int moves [numMoves];
    int zeroCnt = 0;

    fidx = fidy; bidx = bidy;
    // Select one of the three options: 
    
    // moveForward  = (Knight3.fCnt < Knight3.bCnt); // bidirectional search
    moveForward = true; // unidirectional search
    // moveForward = false; // unidirectional search
    // alternative does not help and creates weird (correct) solutions
    // moveForward  =  Knight3.fCnt < Knight3.halfTilesSet;
    
    // this sets zeroCnt and puts in moves candidate moves
    // if ( moveForward ) findMoves(fidx); else findMoves(bidx);
    if ( moveForward ) zeroCnt = findMoves(fidx, moves); 
    else zeroCnt = findMoves(bidx, moves); 
    move(fidx, bidx, moveForward, moves, zeroCnt);
}

void getAllNeighbors();
int findZeroTile();
int numFreeCells();
int getNeighbor2(int idx, int k) { return idx + candidateMoves[k]; }

void move(int fidx, int bidx, boolean moveForward, int moves[],
	  int zeroCnt) {
  // Knight3.moveCnt++;
  moveCnt++;
  // printf("move moveCnt %i\n", moveCnt);
  // show();
  // if ( 3 < moveCnt ) exit(0);
  int target = ( moveForward ? fidx : bidx );
  boolean trace = false;
  if ( tilesCnt == maxTilesSet ) { // path found
    // check for a circular path 
    int neighbors [numMoves]; getAllNeighbors(target, neighbors);
    boolean found = false;
    // Select one of the two choices
    // bidirectional::
    int goalTile = (moveForward ? bidx : fidx); 
    // unidirectional::
    // int goalTile = (moveForward ? Knight3.goalState : 
    //                               Knight3.startState);
    for ( int j = 0; j < numMoves; j++ ) 
      if ( neighbors[j] == goalTile ) {
	found = true; break;
      }
    if ( found ) {
      // show();
       /* // display the solution 
	 System.out.println("\nmove moveCnt " + Knight3.moveCnt);
	 System.out.println("move tilesCnt " + Knight3.tilesCnt);
	 System.out.println("move goalTile " + goalTile);
	 System.out.println("fidx " + fidx + " bidx " + bidx); 
	 System.out.println("fidx " + Knight3.board[fidx].getPos() + 
	 " bidx " + Knight3.board[bidx].getPos());
	 Knight3.show();
	 System.out.println("move goal state!!!!");
	 if ( 3 < Knight3.solutionCnt ) System.exit(0);
	 
	 // end */
      solutionCnt++;
      if ( 0 == solutionCnt%100 ) {
      // if (true ) {
	double now = clock();
	double diff = now - startTime;
	int rate = (diff/solutionCnt);
	printf("solutionCnt %i rate %i\n", solutionCnt, rate);
	       
	// only 1/1M:
	if ( 0 == solutionCnt%1000000 ) {
	  fp = fopen("Knight3c.txt", "a");
	  fprintf(fp, "solutionCnt %i rate %i\n", solutionCnt, rate);
	  showf(fp);
	  fclose(fp);
	}
      }
      //      if ( 200 < solutionCnt ) exit(0);
    }
    return;
  }
  // if ( 0 == zeroCnt ) return ( moveForward ? fidx : bidx );
  if ( 0 == zeroCnt ) return;
  
  // if ( 1 == zeroCnt && Knight3.tilesCnt == Knight3.targetTilesSet) {
  if ( tilesCnt == targetTilesSet) {
    // prepare for the next last move 
    int zeroTile = findZeroTile(target); // fetch unique tile
    tilesCnt++;
    int fidxz, bidxz;
    if ( moveForward ) {
      fCnt = fCnt + 2;
      fidxz = zeroTile;
      bidxz = bidx;
      // board[fidxz]->setPos(fCnt);
      setPos(board[fidxz], fCnt);
    } else {
      bCnt = bCnt + 2;
      fidxz = fidx;
      bidxz = zeroTile;
      // board[bidxz]->setPos(fCnt);
      setPos(board[bidxz], fCnt);
    }
    // (new Node3(fidxz, bidxz))..move();
    Node3(fidxz, bidxz);
    tilesCnt--;
    // board[zeroTile]->setPos(0);
    setPos(board[zeroTile], 0);
    if ( moveForward ) fCnt = fCnt - 2;
    else bCnt = bCnt - 2;
    return;
  }
  // go deeper
  for ( int k = 0; k < zeroCnt; k++ ) {
    int nextIdx = moves[k];
    // check whether it is safe to move to nextIdx
    int numFreeCellsk = numFreeCells(nextIdx); 
    if ( 0 < numFreeCellsk ) { // go down
      // check whether a cell becomes unreachable
      // if ( Knight3.tilesCnt <= 33 ) { //6x6
      if ( tilesCnt <= 60 ) { //8x8
	boolean found = false;
	// for ( int z = 0; z < Knight3.numMoves; z++ ) {
	for ( int z = 0; z < numMoves; z++ ) {
	  int idxz = getNeighbor2(nextIdx, z); // ++++++++++++++++++++++
	  // if ( 0 != Knight3.board[idxz].getPos() ) continue; // no worry
	  if ( 0 != getPos(board[idxz]) ) continue; // no worry
	  int numFreeCellsz = numFreeCells(idxz);
	  if ( numFreeCellsz <= 1 ) { found = true; break; } // out of z loop
	}
	if ( found ) continue; // move into nextIdx causes idxz to be dead
      }
      tilesCnt++;
      int fidxz, bidxz;
      if ( moveForward ) {
	fCnt = fCnt + 2;
	fidxz = nextIdx;
	bidxz = bidx;
	// board[fidxz].setPos(fCnt);
	setPos(board[fidxz], fCnt);
      } else {
	bCnt = bCnt + 2;
	fidxz = fidx;
	bidxz = nextIdx;
	// board[bidxz].setPos(bCnt);
	setPos(board[bidxz], fCnt);
      }
      //(new Node3(fidxz, bidxz)).move();
      Node3(fidxz, bidxz);
      tilesCnt--;
      // board[nextIdx].setPos(0);
      setPos(board[nextIdx], 0);
      if ( moveForward ) fCnt = fCnt - 2;
      else bCnt = bCnt - 2;
    } 
  }
  return;
} // end move

int numFreeCells(int nextIdx) {
  int cnt = 0;
  for ( int k = 0; k < numMoves; k++ ) {
    int idxk = getNeighbor2(nextIdx, k);
    // if ( 0 == board[idxk].getPos() ) cnt++;
    if ( 0 == getPos(board[idxk]) ) cnt++;
  }
  // System.out.println("numFreeCells  nextIdx " + nextIdx + " cnt " + cnt);
  return cnt;
} // end numFreeCells


int findMoves(int idx, int moves[]) {
  // set zeroCnt and puts in moves candidate moves
  int zeroCnt = 0;
  // System.out.println("findMoves(idx) " + idx);
  for ( int k = 0; k < numMoves; k++ ) {
    int idxk = getNeighbor2(idx, k);
    // System.out.println("idxk " + idxk + " board[idxk] " + b<oard[idxk]);
    // if ( 0 == board[idxk].getPos() ) { // candidate loc
    if ( 0 == getPos(board[idxk]) ) {
      moves[zeroCnt] = idxk;
      zeroCnt++;
    }
  }
  return zeroCnt;
} // end findMoves

int findZeroTile(int target) {
  int out = 0;
  for ( int k = 0; k < numMoves; k++ ) {
    int targetNeighbor = getNeighbor2(target, k);
    // if ( 0 == board[targetNeighbor].getPos() ) { 
    if ( 0 == getPos(board[targetNeighbor]) ) {
      out = targetNeighbor;
      break;
    }
  }
  return out;
} 
void getAllNeighbors(int targetTile, int neighbors [] ) {
  // int [] neighbors = new int [numMoves]; 
  // int neighbors [numMoves]; 
  for ( int k = 0; k < numMoves; k++ ) 
    neighbors[k] = getNeighbor2(targetTile, k);
  // return neighbors;
} // end getAllNeighbors


    // } // end Node3
// */

/*
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

*/

