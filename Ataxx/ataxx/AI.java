/* Skeleton code copyright (C) 2008, 2022 Paul N. Hilfinger and the
 * Regents of the University of California.  Do not distribute this or any
 * derivative work without permission. */

package ataxx;

import java.util.ArrayList;
import java.util.Random;

import static ataxx.PieceColor.*;

/** A Player that computes its own moves.
 *  @author Christian T. Bernard
 */
class AI extends Player {

    /** Maximum minimax search depth before going to static evaluation. */
    private static final int MAX_DEPTH = 4;
    /** A position magnitude indicating a win (for red if positive, blue
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI for GAME that will play MYCOLOR. SEED is used to initialize
     *  a random-number generator for use in move computations.  Identical
     *  seeds produce identical behaviour. */
    AI(Game game, PieceColor myColor, long seed) {
        super(game, myColor);
        _random = new Random(seed);
    }

    @Override
    boolean isAuto() {
        return true;
    }

    @Override
    String getMove() {
        if (!getBoard().canMove(myColor())) {
            game().reportMove(Move.pass(), myColor());
            return "-";
        }
        Main.startTiming();
        Move move = findMove();
        Main.endTiming();
        game().reportMove(move, myColor());
        return move.toString();
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(getBoard());
        _lastFoundMove = null;
        if (myColor() == RED) {
            minMax(b, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            minMax(b, MAX_DEPTH, true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to the findMove method
     *  above. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */
    private int minMax(Board board, int depth, boolean saveMove, int sense,
                       int alpha, int beta) {
        /* We use WINNING_VALUE + depth as the winning value so as to favor
         * wins that happen sooner rather than later (depth is larger the
         * fewer moves have been made. */
        if (depth == 0 || board.getWinner() != null) {
            return staticScore(board, WINNING_VALUE + depth);
        }
        Move best;
        best = null;
        int bestScore;
        int aTemp = alpha;
        bestScore = 0;
        int bTemp = beta;
        ArrayList<Move> allPosMoves = makeMoveArr(board);
        for (Move posMove : allPosMoves) {
            if (sense == -1) {
                board.makeMove(posMove);
                bestScore = minMax(board, depth - 1, false, -1, aTemp, bTemp);
                if (bestScore < bTemp) {
                    bTemp =  bestScore;
                    best = posMove;
                }
            }
            if (sense == 1) {
                board.makeMove(posMove);
                bestScore = minMax(board, depth - 1, false, -1, aTemp, bTemp);
                if (bestScore > aTemp) {
                    aTemp =  bestScore;
                    best = posMove;
                }
            }
            board.undo();
            if (saveMove) {
                _lastFoundMove = best;
            }
            if (alpha >= beta) {
                return bestScore;
            }
        }
        return bestScore;
    }

    /** Return a heuristic value for BOARD.  This value is +- WINNINGVALUE in
     *  won positions, and 0 for ties. */
    private int staticScore(Board board, int winningValue) {
        PieceColor winner = board.getWinner();
        if (winner != null) {
            return switch (winner) {
            case RED -> winningValue;
            case BLUE -> -winningValue;
            default -> 0;
            };
        }
        int playersPieces = board.numPieces(board.whoseMove());
        int oppsPieces = board.numPieces(board.whoseMove().opposite());
        return  board.redPieces() - board.bluePieces();
    }

    /** Pseudo-random number generator for move computation. */
    private Random _random = new Random();

    /**@param board the board in use
     * @return an arraylist of all the possible moves.*/

    private ArrayList<Move> makeMoveArr(Board board) {
        ArrayList<Move> arrayOmoves = new ArrayList<>();
        for (char row = '1'; row <= '7'; row++) {
            for (char col = 'a'; col <= 'g'; col++) {
                int tryindex = Board.index(col, row);
                if (board.get(tryindex) == board.whoseMove()) {
                    for (int rBound = -2; rBound <= 2; rBound++) {
                        for (int cBound = -2; cBound <= 2; cBound++) {
                            char cheR = ((char) (row + rBound));
                            char cheC = ((char) (col + cBound));
                            if (board.legalMove(col, row, cheC, cheR)) {
                                Move addThis  = Move.move(col, row, cheC, cheR);
                                arrayOmoves.add(addThis);
                            }
                        }
                    }
                }
            }
        }
        return arrayOmoves;
    }
}

