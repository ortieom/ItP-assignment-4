import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
     * @param onX int, X-coordinate of pice
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
     * declared as null because constructor does not have boardSize parameter
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
     * @param boardSize int, size of boards
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
     * @param boardSize int, size of boards
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

interface BishopMovement {
    int getDiagonalMovesCount(PiecePosition position, PieceColor color,
                              Map<String, ChessPiece> positions, int boardSize);
    int getDiagonalCapturesCount(PiecePosition position, PieceColor color,
                                 Map<String, ChessPiece> positions, int boardSize);
}

interface RookMovement {
    int getOrthogonalMovesCount(PiecePosition position, PieceColor color,
                                Map<String, ChessPiece> positions, int boardSize);
    int getOrthogonalCapturesCount(PiecePosition position, PieceColor color,
                                   Map<String, ChessPiece> positions, int boardSize);
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

class Board {
    private Map<String, ChessPiece> positionsToPieces = new LinkedHashMap<String, ChessPiece>();
    private int size;

    Board(int boardSize) {
        this.size = boardSize;
    }

    public int getPiecePossibleMoveCount(ChessPiece piece) {

    }

    public int getPiecePossibleCapturesCount(ChessPiece piece) {

    }

    public void addPiece(ChessPiece piece)
            throws InvalidPiecePositionException, InvalidNumberOfPiecesException,
            InvalidPieceNameException, InvalidGivenKingsException {

    }

    public ChessPiece getPiece(PiecePosition position) {

    }
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
