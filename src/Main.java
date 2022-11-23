import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class Main {
    /**
     * represents chess board.
     */
    private static Board chessBoard;
    /**
     * file input.
     */
    private static Scanner scanner = new Scanner(new File("input.txt"));

    /**
     * entrypoint.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}

class PiecePosition {
    /**
     * position of chess piece on X-axis.
     */
    private int x;
    /**
     * position of chess piece on Y-axis.
     */
    private int y;

    /**
     * creates a piece position class with specified coordinates.
     * @param onX int, X-coordinate of piece
     * @param onY int, Y-coordinate of piece
     */
    PiecePosition(int onX, int onY) {
        this.x = onX;
        this.y = onY;
    }

    /**
     * checks if provided position is possible on current board.
     * @param boardSize int, size of board
     * @return boolean validity
     */
    public boolean isValid(int boardSize) {
        return this.x >= 1 && this.y >= 1 && this.x > boardSize && this.y > boardSize;
    }

    /**
     * getter for X-coordinate.
     * @return int, X-coordinate
     */
    public int getX() {
        return this.x;
    }
    /**
     * getter for Y-coordinate.
     * @return int, Y-coordinate
     */
    public int getY() {
        return this.y;
    }

    /**
     * represents piece position in string.
     * @return string in format "{X-coordinate} {Y-coordinate}"
     */
    @Override
    public String toString() {
        return Integer.toString(this.x) + " " + Integer.toString(this.y);
    }
}

/**
 * represents color of chess piece.
 * either BLACK or WHITE
 */
enum PieceColor {
    /**
     * available colors.
     */
    WHITE, BLACK;

    /**
     * parses input string and determines color of piece.
     * @param st string with color provided by user
     * @return PieceColor
     * @throws InvalidPieceColorException if provided color is invalid
     */
    public static PieceColor parse(String st) throws InvalidPieceColorException {
        if (st.equals("White")) {
            return WHITE;
        } else if (st.equals("Black")) {
            return BLACK;
        } else {
            throw new InvalidPieceColorException();
        }
    }
}

/**
 * represents abstract chess piece.
 */
abstract class ChessPiece {
    /**
     * represents position of piece.
     */
    protected PiecePosition position;
    /**
     * represents color of piece.
     */
    protected PieceColor color;
    /**
     * stores all reachable positions on board.
     * declared as null because constructor does not have boardSize parameter.
     * determined after first call of any method
     */
    protected List<PiecePosition> possiblePositions = null;

    /**
     * creates a chess piece with specified position and color.
     * @param piecePosition position on the board
     * @param pieceColor color of a piece
     */
    ChessPiece(PiecePosition piecePosition, PieceColor pieceColor) {
        this.position = piecePosition;
        this.color = pieceColor;
    }

    /**
     * getter for position of chess piece.
     * @return PiecePosition
     */
    public PiecePosition getPosition() {
        return this.position;
    }
    /**
     * getter for color of chess piece.
     * @return PieceColor
     */
    public PieceColor getColor() {
        return this.color;
    }

    /**
     * writes into possiblePositions all reachable positions by this piece.
     * @param boardSize size of a bord
     */
    protected abstract void calculatePossiblePositions(int boardSize);

    /**
     * used to calculate number of possible moves.
     * @param positions Map<String, ChessPiece>, positions of pieces on board
     * @param boardSize int, size of board
     * @return int, number of possible moves for chess piece
     */
    public int getMovesCount(Map<String, ChessPiece> positions, int boardSize) {
        if (this.possiblePositions == null) {  // if possible positions are not calculated yet
            calculatePossiblePositions(boardSize);
        }

        int result = 0;

        for (PiecePosition piecePosition: this.possiblePositions) {  // for every possible new position
            ChessPiece piece = positions.get(piecePosition.toString());  // piece on considered position, null if empty
            if (piece == null || piece.getColor() != this.color) {
                // if position is empty or contains piece of another color and, therefore, can be freed to move there
                result++;
            }
        }

        return result;
    }
    /**
     * used to calculate number of possible captures.
     * @param positions Map<String, ChessPiece>, positions of pieces on board
     * @param boardSize int, size of board
     * @return int, number of possible captures for chess piece
     */
    public int getCapturesCount(Map<String, ChessPiece> positions, int boardSize) {
        if (this.possiblePositions == null) {  // if possible positions are not calculated yet
            calculatePossiblePositions(boardSize);
        }

        int result = 0;

        for (PiecePosition piecePosition: possiblePositions) {  // for every possible new position
            ChessPiece piece = positions.get(piecePosition.toString());  // piece on considered position, null if empty
            if (piece != null && piece.getColor() != this.color) {
                // if position is not empty and contains piece of another color to capture
                result++;
            }
        }

        return result;
    }
}

/**
 * represents actions of Bishop and partly of Queen.
 */
interface BishopMovement {
    /**
     * used to calculate number of possible diagonal moves.
     * @param position PiecePosition, position of considered chess piece
     * @param color PieceColor, color of considered chess piece
     * @param positions Map<String, ChessPiece>, positions of pieces on board
     * @param boardSize int, size of board
     * @return int, number of possible diagonal moves
     */
    default int getDiagonalMovesCount(PiecePosition position, PieceColor color,
                                      Map<String, ChessPiece> positions, int boardSize) {
        int result = 0;  // return value
        // start position
        int x = position.getX();
        int y = position.getY();

        // flags represent availability of movement in corresponding direction: up left, up right, down left, down right
        boolean[] directionFlags = {true, true, true, true};
        // multipliers for offset in X & Y with respect to direction
        int[] offsetMultiplierX = {-1, 1, -1, 1};
        int[] offsetMultiplierY = {1, 1, -1, -1};
        int offset = 0;  // how many moves from start

        PiecePosition move;  // for considered move
        ChessPiece piece;  // for piece placed on the considered move

        int directionsAvailableCnt = 4;
        while (directionsAvailableCnt != 0) {  // while move is possible in at least 1 direction
            offset++;
            for (int i = 0; i < directionFlags.length; i++) {  // for every direction
                if (directionFlags[i]) {  // if direction is still available
                    int newX = x + (offsetMultiplierX[i] * offset);
                    int newY = y + (offsetMultiplierY[i] * offset);

                    move = new PiecePosition(newX, newY);
                    piece = positions.get(move.toString());

                    if (move.isValid(boardSize) && piece == null) {
                        // way is clear
                        result++;
                    } else if (move.isValid(boardSize) && piece != null && piece.color != color) {
                        // piece in the way can be attacked
                        directionFlags[i] = false;  // can not move further in this direction
                        directionsAvailableCnt--;
                        result++;  // but this position is possible for move
                    } else {
                        // either move is out of borders or piece of the same color is in the way
                        directionFlags[i] = false;  // can not move further in this direction & this move is impossible
                        directionsAvailableCnt--;
                    }
                }
            }
        }

        return result;
    }

    /**
     * used to calculate number of possible diagonal captures.
     * @param position PiecePosition, position of considered chess piece
     * @param color PieceColor, color of considered chess piece
     * @param positions Map<String, ChessPiece>, positions of pieces on board
     * @param boardSize int, size of board
     * @return int, number of possible diagonal captures
     */
    default  int getDiagonalCapturesCount(PiecePosition position, PieceColor color,
                                          Map<String, ChessPiece> positions, int boardSize) {
        // almost same as getDiagonalMovesCount
        int result = 0;  // return value
        // start position
        int x = position.getX();
        int y = position.getY();

        // flags represent availability of movement in corresponding direction: up left, up right, down left, down right
        boolean[] directionFlags = {true, true, true, true};
        // multipliers for offset in X & Y with respect to direction
        int[] offsetMultiplierX = {-1, 1, -1, 1};
        int[] offsetMultiplierY = {1, 1, -1, -1};
        int offset = 0;  // how many moves from start

        PiecePosition move;  // for considered move
        ChessPiece piece;  // for piece placed on the considered move

        int directionsAvailableCnt = 4;
        while (directionsAvailableCnt != 0) {  // while move is possible in at least 1 direction
            offset++;
            for (int i = 0; i < directionFlags.length; i++) {  // for every direction
                if (directionFlags[i]) {  // if direction is still available
                    int newX = x + (offsetMultiplierX[i] * offset);
                    int newY = y + (offsetMultiplierY[i] * offset);

                    move = new PiecePosition(newX, newY);
                    piece = positions.get(move.toString());

                    if (piece != null || !move.isValid(boardSize)) {
                        directionFlags[i] = false;  // can not move further in this direction
                        directionsAvailableCnt--;

                        if (piece != null && piece.color != color) {  // piece can be captured
                            result++;
                        }
                    }
                }
            }
        }

        return result;
    }
}

/**
 * represents actions of Rook and partly of Queen.
 */
interface RookMovement {
    /**
     * used to calculate number of possible orthogonal moves.
     * same logic as in Bishop movement (except offsetMultipliers)
     * @param position PiecePosition, position of considered chess piece
     * @param color PieceColor, color of considered chess piece
     * @param positions Map<String, ChessPiece>, positions of pieces on board
     * @param boardSize int, size of board
     * @return int, number of possible orthogonal moves
     */
    default int getOrthogonalMovesCount(PiecePosition position, PieceColor color,
                                        Map<String, ChessPiece> positions, int boardSize) {
        int result = 0;  // return value
        // start position
        int x = position.getX();
        int y = position.getY();

        // flags represent availability of movement in corresponding direction: left, right, up, down
        boolean[] directionFlags = {true, true, true, true};
        // multipliers for offset in X & Y with respect to direction
        int[] offsetMultiplierX = {-1, 1, 0, 0};
        int[] offsetMultiplierY = {0, 0, 1, -1};
        int offset = 0;  // how many moves from start

        PiecePosition move;  // for considered move
        ChessPiece piece;  // for piece placed on the considered move

        int directionsAvailableCnt = 4;
        while (directionsAvailableCnt != 0) {  // while move is possible in at least 1 direction
            offset++;
            for (int i = 0; i < directionFlags.length; i++) {  // for every direction
                if (directionFlags[i]) {  // if direction is still available
                    int newX = x + (offsetMultiplierX[i] * offset);
                    int newY = y + (offsetMultiplierY[i] * offset);

                    move = new PiecePosition(newX, newY);
                    piece = positions.get(move.toString());

                    if (move.isValid(boardSize) && piece == null) {
                        // way is clear
                        result++;
                    } else if (move.isValid(boardSize) && piece != null && piece.color != color) {
                        // piece in the way can be attacked
                        directionFlags[i] = false;  // can not move further in this direction
                        directionsAvailableCnt--;
                        result++;  // but this position is possible for move
                    } else {
                        // either move is out of borders or piece of the same color is in the way
                        directionFlags[i] = false;  // can not move further in this direction & this move is impossible
                        directionsAvailableCnt--;
                    }
                }
            }
        }

        return result;
    }

    /**
     * used to calculate number of possible orthogonal captures.
     * same logic as in Bishop movement (except offsetMultipliers)
     * @param position PiecePosition, position of considered chess piece
     * @param color PieceColor, color of considered chess piece
     * @param positions Map<String, ChessPiece>, positions of pieces on board
     * @param boardSize int, size of board
     * @return int, number of possible orthogonal captures
     */
    default int getOrthogonalCapturesCount(PiecePosition position, PieceColor color,
                                           Map<String, ChessPiece> positions, int boardSize) {
        // almost same as getDiagonalMovesCount
        int result = 0;  // return value
        // start position
        int x = position.getX();
        int y = position.getY();

        // flags represent availability of movement in corresponding direction: up left, up right, down left, down right
        boolean[] directionFlags = {true, true, true, true};
        // multipliers for offset in X & Y with respect to direction
        int[] offsetMultiplierX = {-1, 1, 0, 0};
        int[] offsetMultiplierY = {0, 0, 1, -1};
        int offset = 0;  // how many moves from start

        PiecePosition move;  // for considered move
        ChessPiece piece;  // for piece placed on the considered move

        int directionsAvailableCnt = 4;
        while (directionsAvailableCnt != 0) {  // while move is possible in at least 1 direction
            offset++;
            for (int i = 0; i < directionFlags.length; i++) {  // for every direction
                if (directionFlags[i]) {  // if direction is still available
                    int newX = x + (offsetMultiplierX[i] * offset);
                    int newY = y + (offsetMultiplierY[i] * offset);

                    move = new PiecePosition(newX, newY);
                    piece = positions.get(move.toString());

                    if (piece != null || !move.isValid(boardSize)) {
                        directionFlags[i] = false;  // can not move further in this direction
                        directionsAvailableCnt--;

                        if (piece != null && piece.color != color) {  // piece can be captured
                            result++;
                        }
                    }
                }
            }
        }

        return result;
    }
}

/**
 * represents Knight chess piece.
 */
class Knight extends ChessPiece {
    /**
     * creates a Knight chess piece with specified position and color.
     * @param piecePosition position on the board
     * @param pieceColor color of a piece
     */
    Knight(PiecePosition piecePosition, PieceColor pieceColor) {
        super(piecePosition, pieceColor);
    }

    /**
     * writes into possiblePositions all reachable positions by this piece.
     * @param boardSize size of a bord
     */
    protected void calculatePossiblePositions(int boardSize) {
        int x = this.position.getX();
        int y = this.position.getY();

        // all possible moves for Knight
        PiecePosition[] moves = {
                new PiecePosition(x + 2, y + 1),
                new PiecePosition(x + 2, y - 1),
                new PiecePosition(x - 2, y + 1),
                new PiecePosition(x - 2, y - 1),
                new PiecePosition(x + 1, y + 2),
                new PiecePosition(x + 1, y - 2),
                new PiecePosition(x - 1, y + 2),
                new PiecePosition(x - 1, y - 2),
        };
        List<PiecePosition> result = new ArrayList<>();  // array for positions that belong to board
        for (PiecePosition piecePosition: moves) {
            if (piecePosition.isValid(boardSize)) {  // if position after considered move remains on board
                result.add(piecePosition);
            }
        }
        this.possiblePositions = new ArrayList<>(result);  // updating private variable
    }
}

/**
 * represents King chess piece.
 */
class King extends ChessPiece {
   /**
     * creates a King chess piece with specified position and color.
     * @param piecePosition position on the board
     * @param pieceColor color of a piece
     */
    King(PiecePosition piecePosition, PieceColor pieceColor) {
        super(piecePosition, pieceColor);
    }

    /**
     * writes into possiblePositions all reachable positions by this piece.
     * @param boardSize size of a bord
     */
    protected void calculatePossiblePositions(int boardSize) {
        int x = this.position.getX();
        int y = this.position.getY();

        List<PiecePosition> moves = new ArrayList<>();

        for (int dx = -1; dx < 2; dx++) {  // bias in x
            for (int dy = -1; dy < 2; dy++) {  // bias in y
                // trying all possible variants
                PiecePosition piecePosition = new PiecePosition(x + dx, y + dy);
                if (piecePosition.isValid(boardSize) && (dx != 0 && dy != 0)) {
                    moves.add(piecePosition);
                }
            }
        }

        this.possiblePositions = new ArrayList<>(moves);
    }
}

/**
 * represents Pawn chess piece.
 */
class Pawn extends ChessPiece {
    /**
     * creates a Pawn chess piece with specified position and color.
     * @param piecePosition position on the board
     * @param pieceColor color of a piece
     */
    Pawn(PiecePosition piecePosition, PieceColor pieceColor) {
        super(piecePosition, pieceColor);
    }

    @Override  // redundant here (useful for King & Knight), probably will be reformatted later - TODO
    protected void calculatePossiblePositions(int boardSize) {
        return;
    }

    /**
     * used to calculate number of possible moves for Pawn.
     * @param positions Map<String, ChessPiece>, positions of pieces on board
     * @param boardSize int, size of board
     * @return 1 if way for move is clear, else 0
     */
    @Override
    public int getMovesCount(Map<String, ChessPiece> positions, int boardSize) {
        int result;  // return value

        int direction;  // +1 means forward, -1 - backwards
        if (this.color == PieceColor.WHITE) {
            direction = 1;
        } else {
            direction = -1;
        }

        PiecePosition moveForward = new PiecePosition(this.position.getX(), this.position.getY() + direction);
        ChessPiece pieceInFront = positions.get(moveForward.toString());

        if (moveForward.isValid(boardSize) && (pieceInFront == null || pieceInFront.color != this.color)) {
            // if cell in forward direction is empty or can be attacked
            result = 1;
        } else {
            result = 0;
        }

        return result;
    }

    /**
     * used to calculate number of possible captures for Pawn.
     * @param positions Map<String, ChessPiece>, positions of pieces on board
     * @param boardSize int, size of board
     * @return int, number of possible captures
     */
    @Override
    public int getCapturesCount(Map<String, ChessPiece> positions, int boardSize) {
        int result = 0;  // return value

        int direction;  // +1 means forward, -1 - backwards
        if (this.color == PieceColor.WHITE) {
            direction = 1;
        } else {
            direction = -1;
        }

        // positions that can be attacked by Pawn
        PiecePosition move1 = new PiecePosition(this.position.getX() + direction, this.position.getY() - 1);
        PiecePosition move2 = new PiecePosition(this.position.getX() + direction, this.position.getY() + 1);
        // chess pieces on attacked positions
        ChessPiece piece1 = positions.get(move1.toString());
        ChessPiece piece2 = positions.get(move2.toString());

        if (move1.isValid(boardSize) && piece1 != null && this.color != piece1.getColor()) {
            // if cell is valid and there is a piece of another color
            result++;
        }
        if (move2.isValid(boardSize) && piece2 != null && this.color != piece2.getColor()) {
            result++;
        }

        return result;
    }
}

class Board {
    private Map<String, ChessPiece> positionsToPieces = new LinkedHashMap<String, ChessPiece>();
    private int size;

    Board(int boardSize) {
        this.size = boardSize;
    }

//    public int getPiecePossibleMoveCount(ChessPiece piece) {
//
//    }
//
//    public int getPiecePossibleCapturesCount(ChessPiece piece) {
//
//    }
//
//    public void addPiece(ChessPiece piece)
//            throws InvalidPiecePositionException, InvalidNumberOfPiecesException,
//            InvalidPieceNameException, InvalidGivenKingsException {
//
//    }
//
//    public ChessPiece getPiece(PiecePosition position) {
//
//    }
}

class InvalidBoardSizeException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid board size";
    }
}

class InvalidNumberOfPiecesException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid number of pieces";
    }
}

class InvalidPieceNameException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid piece name";
    }
}

class InvalidPieceColorException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid piece color";
    }
}

class InvalidPiecePositionException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid piece position";
    }
}

class InvalidGivenKingsException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid given Kings";
    }
}

class InvalidInputException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid input";
    }
}
