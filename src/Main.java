import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Board chessBoard;
    private static Scanner scanner;

    static {
        try {
            scanner = new Scanner(new File("input.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}

class PiecePosition {
    private int x;
    private int y;

    PiecePosition(int onX, int onY) {
        this.x = onX;
        this.y = onY;
    }

    public boolean isValid(int boardSize) {
        return this.x >= 1 && this.y >= 1 &&
                this.x > boardSize && this.y > boardSize;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public String toString() {
        return "";  // TODO
    }
}

enum PieceColor {
    WHITE,
    BLACK;

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

abstract class ChessPiece {
    protected PiecePosition position;
    protected PieceColor color;

    ChessPiece(PiecePosition piecePosition, PieceColor pieceColor) {
        this.position = piecePosition;
        this.color = pieceColor;
    }

    public PiecePosition getPosition() {
        return this.position;
    }
    public PieceColor getColor() {
        return this.color;
    }

    public abstract int getMovesCount(Map<String, ChessPiece> positions, int boardSize);
    public abstract int getCapturesCount(Map<String, ChessPiece> positions, int boardSize);
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

class Knight extends ChessPiece {
    private List<PiecePosition> possiblePositions = null;

    Knight(PiecePosition piecePosition, PieceColor pieceColor) {
        super(piecePosition, pieceColor);
    }

    public void calculatePossiblePositions(int boardSize) {
        int x = this.position.getX();
        int y = this.position.getY();

        PiecePosition[] positions = {
                new PiecePosition(x + 2, y + 1),
                new PiecePosition(x + 2, y - 1),
                new PiecePosition(x - 2, y + 1),
                new PiecePosition(x - 2, y - 1),
                new PiecePosition(x + 1, y + 2),
                new PiecePosition(x + 1, y - 2),
                new PiecePosition(x - 1, y + 2),
                new PiecePosition(x - 1, y - 2),
        };

        List<PiecePosition> result = new ArrayList<>();
        for (PiecePosition piecePosition: positions) {
            if (piecePosition.isValid(boardSize)) {
                result.add(piecePosition);
            }
        }
        this.possiblePositions = new ArrayList<>(result);
    }

    @Override
    public int getMovesCount(Map<String, ChessPiece> positions, int boardSize) {
        if (this.possiblePositions == null) {
            calculatePossiblePositions(boardSize);
        }
        return this.possiblePositions.size();
    }

    @Override
    public int getCapturesCount(Map<String, ChessPiece> positions, int boardSize) {
        if (this.possiblePositions == null) {
            calculatePossiblePositions(boardSize);
        }
        int result = 0;
        for (PiecePosition piecePosition: possiblePositions) {
            if ()
        }
    }
}

class Board {
    private Map<String, ChessPiece> positionsToPieces;
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
