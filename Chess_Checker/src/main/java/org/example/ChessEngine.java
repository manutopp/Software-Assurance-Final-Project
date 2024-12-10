package org.example;

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

// Custom exception for chess-related errors
class ChessException extends Exception {
    public ChessException(String message) {
        super(message);
    }
}

// Enums for piece types and colors
enum PieceType {
    PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING
}

enum Color {
    WHITE, BLACK
}

// Class to represent a Chess Player
class ChessPlayer {
    private String name;
    private Color color;

    public ChessPlayer(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return name + " (" + color + ")";
    }
}

// Piece scorer to assign relative values
class PieceScorer {
    private static final Map<PieceType, Integer> PIECE_VALUES = new HashMap<>();

    static {
        PIECE_VALUES.put(PieceType.PAWN, 1);
        PIECE_VALUES.put(PieceType.KNIGHT, 3);
        PIECE_VALUES.put(PieceType.BISHOP, 3);
        PIECE_VALUES.put(PieceType.ROOK, 5);
        PIECE_VALUES.put(PieceType.QUEEN, 9);
        PIECE_VALUES.put(PieceType.KING, 0);
    }

    public static int getPieceValue(PieceType pieceType) {
        return PIECE_VALUES.getOrDefault(pieceType, 0);
    }
}

// Chess piece class
class ChessPiece {
    private PieceType pieceType;
    private Color color;
    private boolean hasMoved;
    private int value;

    public ChessPiece(PieceType pieceType, Color color) {
        // Explicit null checks
        if (pieceType == null) {
            throw new NullPointerException("Piece type cannot be null");
        }
        if (color == null) {
            throw new NullPointerException("Color cannot be null");
        }

        this.pieceType = pieceType;
        this.color = color;
        this.hasMoved = false;
    }

    // Getters and setters
    public PieceType getPieceType() { return pieceType; }
    public Color getColor() { return color; }
    public boolean hasMoved() { return hasMoved; }
    public void setMoved(boolean moved) { this.hasMoved = moved; }
    public int getValue() { return value; }

    @Override
    public String toString() {
        return String.format("%s%s",
                getFirstLetter(pieceType),
                color == Color.WHITE ? "W" : "B"
        );
    }

    private String getFirstLetter(PieceType type) {
        switch (type) {
            case PAWN: return "P";
            case ROOK: return "R";
            case KNIGHT: return "N";
            case BISHOP: return "B";
            case QUEEN: return "Q";
            case KING: return "K";
            default: return "?";
        }
    }

    public boolean isOfType(PieceType type) {
        return this.pieceType == type;
    }
}

// Chess board representation
class ChessBoard {
    private static final Logger LOGGER = Logger.getLogger(ChessBoard.class.getName());

    private ChessPiece[][] board;
    private List<Map<String, Object>> moveHistory;
    private Map<Color, List<ChessPiece>> capturedPieces;
    private int totalMoves;
    private Map<PieceType, Integer> pieceMovements;

    public ChessBoard() {
        board = new ChessPiece[8][8];
        moveHistory = new ArrayList<>();
        capturedPieces = new HashMap<>();
        capturedPieces.put(Color.WHITE, new ArrayList<>());
        capturedPieces.put(Color.BLACK, new ArrayList<>());

        totalMoves = 0;
        pieceMovements = new EnumMap<>(PieceType.class);
        for (PieceType type : PieceType.values()) {
            pieceMovements.put(type, 0);
        }

        setupInitialBoard();
    }

    private void setupInitialBoard() {
        // White pieces setup
        board[0][0] = new ChessPiece(PieceType.ROOK, Color.WHITE);
        board[0][1] = new ChessPiece(PieceType.KNIGHT, Color.WHITE);
        board[0][2] = new ChessPiece(PieceType.BISHOP, Color.WHITE);
        board[0][3] = new ChessPiece(PieceType.QUEEN, Color.WHITE);
        board[0][4] = new ChessPiece(PieceType.KING, Color.WHITE);
        board[0][5] = new ChessPiece(PieceType.BISHOP, Color.WHITE);
        board[0][6] = new ChessPiece(PieceType.KNIGHT, Color.WHITE);
        board[0][7] = new ChessPiece(PieceType.ROOK, Color.WHITE);

        // White pawns
        for (int i = 0; i < 8; i++) {
            board[1][i] = new ChessPiece(PieceType.PAWN, Color.WHITE);
        }

        // Black pieces setup (similar to white)
        board[7][0] = new ChessPiece(PieceType.ROOK, Color.BLACK);
        board[7][1] = new ChessPiece(PieceType.KNIGHT, Color.BLACK);
        board[7][2] = new ChessPiece(PieceType.BISHOP, Color.BLACK);
        board[7][3] = new ChessPiece(PieceType.QUEEN, Color.BLACK);
        board[7][4] = new ChessPiece(PieceType.KING, Color.BLACK);
        board[7][5] = new ChessPiece(PieceType.BISHOP, Color.BLACK);
        board[7][6] = new ChessPiece(PieceType.KNIGHT, Color.BLACK);
        board[7][7] = new ChessPiece(PieceType.ROOK, Color.BLACK);

        // Black pawns
        for (int i = 0; i < 8; i++) {
            board[6][i] = new ChessPiece(PieceType.PAWN, Color.BLACK);
        }
    }

    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public ChessPiece getPieceAt(int x, int y) {
        if (!isValidCoordinate(x, y)) {
            return null;
        }
        return board[x][y];
    }

    private void logPieceMovement(ChessPiece piece) {
        int currentMoves = pieceMovements.get(piece.getPieceType()) + 1;
        pieceMovements.put(piece.getPieceType(), currentMoves);
        LOGGER.info(String.format("Moved %s: Total %s moves = %d",
                piece, piece.getPieceType(), currentMoves));
    }

    public boolean validateMove(int startX, int startY, int endX, int endY) throws ChessException {
        // Basic move validation
        if (!isValidCoordinate(startX, startY) || !isValidCoordinate(endX, endY)) {
            throw new ChessException("Move outside board boundaries");
        }

        ChessPiece piece = board[startX][startY];
        if (piece == null) {
            throw new ChessException("No piece at start coordinate");
        }

        return true;
    }

    public boolean movePiece(int startX, int startY, int endX, int endY) {
        try {
            // Validate the move
            validateMove(startX, startY, endX, endY);

            ChessPiece piece = board[startX][startY];
            ChessPiece capturedPiece = board[endX][endY];

            // Capture logic
            if (capturedPiece != null) {
                capturedPieces.get(piece.getColor()).add(capturedPiece);
                LOGGER.info(String.format("Captured %s (value: %d)",
                        capturedPiece, capturedPiece.getValue()));
            }

            // Move the piece
            board[endX][endY] = piece;
            board[startX][startY] = null;
            piece.setMoved(true);

            // Tracking
            totalMoves++;
            logPieceMovement(piece);

            // Record move history
            Map<String, Object> moveRecord = new HashMap<>();
            moveRecord.put("piece", piece);
            moveRecord.put("startX", startX);
            moveRecord.put("startY", startY);
            moveRecord.put("endX", endX);
            moveRecord.put("endY", endY);
            moveRecord.put("capturedPiece", capturedPiece);
            moveRecord.put("moveNumber", totalMoves);
            moveHistory.add(moveRecord);

            return true;

        } catch (ChessException e) {
            LOGGER.log(Level.SEVERE, "Move validation failed", e);
            return false;
        }
    }

    public void printCapturedPieces(Color color) {
        List<ChessPiece> pieces = capturedPieces.get(color);
        System.out.println(color.name() + " captured pieces:");
        for (ChessPiece piece : pieces) {
            System.out.print(piece + " ");
        }
        System.out.println();
    }

    public void printBoard() {
        System.out.println("  0 1 2 3 4 5 6 7");
        for (int i = 0; i < 8; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board[i][j];
                System.out.print(piece != null ? piece + " " : ". ");
            }
            System.out.println();
        }
    }

    public ChessPiece[][] getBoardState() {
        return board;
    }

    public void resetBoard() {
        setupInitialBoard();
        capturedPieces.get(Color.WHITE).clear();
        capturedPieces.get(Color.BLACK).clear();
        moveHistory.clear();
        totalMoves = 0;
        System.out.println("Board reset!");
    }
}

// Utility class to help with board-related operations
class ChessBoardUtils {
    public static void printDetailedBoard(ChessPiece[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                ChessPiece piece = board[i][j];
                if (piece == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(piece.getPieceType().name().charAt(0) + (piece.getColor() == Color.WHITE ? "W" : "B") + " ");
                }
            }
            System.out.println();
        }
    }

    public static boolean isCapture(ChessPiece[][] board, int startX, int startY, int endX, int endY) {
        ChessPiece startPiece = board[startX][startY];
        ChessPiece endPiece = board[endX][endY];
        return startPiece != null && endPiece != null && startPiece.getColor() != endPiece.getColor();
    }

    // New method to count the number of pieces of a certain color
    public static int countPiecesByColor(ChessPiece[][] board, Color color) {
        int count = 0;
        for (ChessPiece[] row : board) {
            for (ChessPiece piece : row) {
                if (piece != null && piece.getColor() == color) {
                    count++;
                }
            }
        }
        return count;
    }
}


// New class to analyze the chess game
class ChessGameAnalyzer {
    private ChessBoard board;

    public ChessGameAnalyzer(ChessBoard board) {
        this.board = board;
    }

    // Method to calculate the total score of a player
    public int calculatePlayerScore(Color color) {
        int score = 0;
        ChessPiece[][] pieces = board.getBoardState();
        for (ChessPiece[] row : pieces) {
            for (ChessPiece piece : row) {
                if (piece != null && piece.getColor() == color) {
                    score += piece.getValue();
                }
            }
        }
        return score;
    }

    // Method to determine if a player is winning
    public boolean isPlayerWinning(Color color) {
        int playerScore = calculatePlayerScore(color);
        int opponentScore = calculatePlayerScore(color == Color.WHITE ? Color.BLACK : Color.WHITE);
        return playerScore > opponentScore;
    }

    public void printGameAnalysis() {
        System.out.println("White score: " + calculatePlayerScore(Color.WHITE));
        System.out.println("Black score: " + calculatePlayerScore(Color.BLACK));

        if (isPlayerWinning(Color.WHITE)) {
            System.out.println("White is winning.");
        } else if (isPlayerWinning(Color.BLACK)) {
            System.out.println("Black is winning.");
        } else {
            System.out.println("The game is currently tied.");
        }
    }
}

public class ChessEngine {
    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        ChessBoardUtils.printDetailedBoard(board.getBoardState());

        ChessGameAnalyzer analyzer = new ChessGameAnalyzer(board);

        // Simulate some moves
        board.movePiece(1, 0, 3, 0); // White pawn
        board.movePiece(6, 0, 4, 0); // Black pawn

        // Analyze the game
        analyzer.printGameAnalysis();

        // Additional utilities example
        System.out.println("Total white pieces: " + ChessBoardUtils.countPiecesByColor(board.getBoardState(), Color.WHITE));
        System.out.println("Total black pieces: " + ChessBoardUtils.countPiecesByColor(board.getBoardState(), Color.BLACK));
    }


}
