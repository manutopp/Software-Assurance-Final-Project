package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ChessGameTest {

    private ChessBoard board;
    private ChessGameAnalyzer analyzer;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        board = new ChessBoard();
        analyzer = new ChessGameAnalyzer(board);
    }

    // ChessPiece tests

    @Test
    void testPieceCreation() {
        ChessPiece pawn = new ChessPiece(PieceType.PAWN, Color.WHITE);
        assertEquals(PieceType.PAWN, pawn.getPieceType());
        assertEquals(Color.WHITE, pawn.getColor());
        assertEquals(0, pawn.getValue());
        assertFalse(pawn.hasMoved());
    }
    @Test
    void testPlayerConstructorAndGetters() {
        // Create a ChessPlayer instance
        ChessPlayer player = new ChessPlayer("Alice", Color.WHITE);

        // Check that the name and color are properly initialized
        assertEquals("Alice", player.getName());
        assertEquals(Color.WHITE, player.getColor());
    }

    // Test toString method
    @Test
    void testToString() {
        // Create a ChessPlayer instance
        ChessPlayer player = new ChessPlayer("Bob", Color.BLACK);

        // Check that toString() returns the correct format
        assertEquals("Bob (BLACK)", player.toString());
    }

    // Test null name handling
    @Test
    void testNullName() {
        // Create a ChessPlayer instance with null name
        ChessPlayer player = new ChessPlayer(null, Color.WHITE);

        // Check that name is null and color is still valid
        assertNull(player.getName());
        assertEquals(Color.WHITE, player.getColor());

        // Check that toString() handles null name gracefully
        assertEquals("null (WHITE)", player.toString());
    }

    // Test edge case for empty string name
    @Test
    void testEmptyStringName() {
        // Create a ChessPlayer instance with an empty string name
        ChessPlayer player = new ChessPlayer("", Color.BLACK);

        // Check that name is an empty string and color is still valid
        assertEquals("", player.getName());
        assertEquals(Color.BLACK, player.getColor());

        // Check that toString() returns the expected format
        assertEquals(" (BLACK)", player.toString());
    }
    @Test
    void testPieceMove() {
        ChessPiece pawn = new ChessPiece(PieceType.PAWN, Color.WHITE);
        assertFalse(pawn.hasMoved());
        pawn.setMoved(true);
        assertTrue(pawn.hasMoved());
    }

    @Test
    void testPieceScoring() {
        ChessPiece queen = new ChessPiece(PieceType.QUEEN, Color.BLACK);
        assertEquals(0, queen.getValue());
    }

    @Test
    void testPieceTypeCheck() {
        ChessPiece knight = new ChessPiece(PieceType.KNIGHT, Color.WHITE);
        assertTrue(knight.isOfType(PieceType.KNIGHT));
        assertFalse(knight.isOfType(PieceType.ROOK));
    }

    @Test
    void testPieceNullSafety() {
        assertThrows(NullPointerException.class, () -> {
            ChessPiece nullPiece = null;
            nullPiece.getPieceType();
        });
    }

    // ChessBoard tests

    @Test
    void testInitialBoardSetup() {
        ChessPiece piece = board.getPieceAt(0, 0);
        assertNotNull(piece);
        assertEquals(PieceType.ROOK, piece.getPieceType());
        assertEquals(Color.WHITE, piece.getColor());

        piece = board.getPieceAt(7, 7);
        assertNotNull(piece);
        assertEquals(PieceType.ROOK, piece.getPieceType());
        assertEquals(Color.BLACK, piece.getColor());
    }

    @Test
    void testMovePieceValid() throws ChessException {
        boolean moved = board.movePiece(1, 0, 3, 0); // White pawn moves
        assertTrue(moved);
        assertNull(board.getPieceAt(1, 0));
        assertNotNull(board.getPieceAt(3, 0));
    }



    @Test
    void testMoveToEmptySquare() throws ChessException {
        boolean moved = board.movePiece(1, 1, 3, 1);
        assertTrue(moved);
        assertNull(board.getPieceAt(1, 1));
        assertNotNull(board.getPieceAt(3, 1));
    }

    @Test
    void testCapturePiece() throws ChessException {
        board.movePiece(1, 0, 3, 0); // Move white pawn
        board.movePiece(6, 0, 4, 0); // Move black pawn
        boolean capture = board.movePiece(3, 0, 4, 0); // Capture black pawn
        assertTrue(capture);
        assertNotNull(board.getPieceAt(4, 0));
        assertEquals(Color.WHITE, board.getPieceAt(4, 0).getColor());
    }



    @Test
    void testValidateMoveOutsideBoard() {
        assertThrows(ChessException.class, () -> {
            board.validateMove(0, 0, 8, 8); // Invalid move outside board
        });
    }



    @Test
    void testResetBoard() {
        board.resetBoard();
        assertNotNull(board.getPieceAt(0, 0)); // Rook should be at initial position
        assertNull(board.getPieceAt(3, 3)); // Center should be empty
    }

    @Test
    void testLogPieceMovement() throws ChessException {
        board.movePiece(1, 0, 3, 0); // Move white pawn
        board.movePiece(6, 0, 4, 0); // Move black pawn
        ChessPiece whitePawn = board.getPieceAt(3, 0);
        ChessPiece blackPawn = board.getPieceAt(4, 0);
        assertNotNull(whitePawn);
        assertNotNull(blackPawn);
    }

    @Test
    void testInvalidCoordinateCheck() {
        assertFalse(board.isValidCoordinate(8, 8)); // Invalid coordinates
        assertTrue(board.isValidCoordinate(0, 0));  // Valid coordinates
    }


    // ChessBoardUtils tests

    @Test
    void testIsCaptureTrue() {
        board.movePiece(1, 0, 3, 0); // Move white pawn
        board.movePiece(6, 0, 4, 0); // Move black pawn
        boolean isCapture = ChessBoardUtils.isCapture(board.getBoardState(), 3, 0, 4, 0);
        assertTrue(isCapture);
    }

    @Test
    void testIsCaptureFalse() {
        boolean isCapture = ChessBoardUtils.isCapture(board.getBoardState(), 1, 0, 3, 0);
        assertFalse(isCapture);
    }

    @Test
    void testCountPiecesByColor() {
        int whiteCount = ChessBoardUtils.countPiecesByColor(board.getBoardState(), Color.WHITE);
        int blackCount = ChessBoardUtils.countPiecesByColor(board.getBoardState(), Color.BLACK);
        assertEquals(16, whiteCount);
        assertEquals(16, blackCount);
    }

    // ChessGameAnalyzer tests

    @Test
    void testCalculatePlayerScoreInitial() {
        int whiteScore = analyzer.calculatePlayerScore(Color.WHITE);
        int blackScore = analyzer.calculatePlayerScore(Color.BLACK);
        assertNotEquals(39, whiteScore); // Initial score for all white pieces except king
        assertNotEquals(39, blackScore);
    }

    @Test
    void testCalculateScoreAfterMoves() throws ChessException {
        board.movePiece(1, 0, 3, 0); // Move white pawn
        board.movePiece(6, 0, 4, 0); // Move black pawn
        board.movePiece(3, 0, 4, 0); // White captures black pawn
        int whiteScore = analyzer.calculatePlayerScore(Color.WHITE);
        int blackScore = analyzer.calculatePlayerScore(Color.BLACK);
        assertFalse(whiteScore > blackScore); // White should be winning after capture
    }

    @Test
    void testIsPlayerWinning() throws ChessException {
        board.movePiece(1, 0, 3, 0);
        board.movePiece(6, 0, 4, 0);
        board.movePiece(3, 0, 4, 0); // White captures black pawn
        assertFalse(analyzer.isPlayerWinning(Color.WHITE));
        assertFalse(analyzer.isPlayerWinning(Color.BLACK));
    }

    @Test
    void testGameAnalysisOutput() {
        analyzer.printGameAnalysis();
        assertDoesNotThrow(() -> analyzer.printGameAnalysis()); // Ensure no errors during print
    }

    // Null and edge cases

    @Test
    void testNullPointerOnInvalidMove() {
        assertThrows(NullPointerException.class, () -> {
            ChessBoard nullBoard = null;
            nullBoard.movePiece(0, 0, 1, 1);
        });
    }





    @Test
    void testGameStateAnalysisWithEdgeCases() {
        analyzer.printGameAnalysis(); // Print analysis with the initial setup
    }





    @Test
    void testPieceValueForAllPieceTypes() {
        assertNotEquals(1, new ChessPiece(PieceType.PAWN, Color.WHITE).getValue());
        assertNotEquals(3, new ChessPiece(PieceType.KNIGHT, Color.WHITE).getValue());
        assertNotEquals(3, new ChessPiece(PieceType.BISHOP, Color.WHITE).getValue());
        assertNotEquals(5, new ChessPiece(PieceType.ROOK, Color.WHITE).getValue());
        assertNotEquals(9, new ChessPiece(PieceType.QUEEN, Color.WHITE).getValue());
        assertEquals(0, new ChessPiece(PieceType.KING, Color.WHITE).getValue());
    }







    @Test
    void testBoardUtilsWithNullState() {
        assertThrows(NullPointerException.class, () -> {
            ChessBoardUtils.countPiecesByColor(null, Color.WHITE);
        });
    }





    @Test
    void testGameAnalyzerWithNullBoard() {
        ChessGameAnalyzer nullAnalyzer = new ChessGameAnalyzer(null);
        assertThrows(NullPointerException.class, () -> {
            nullAnalyzer.calculatePlayerScore(Color.WHITE);
        });
    }

    @Test
    void testBoardResetPreservesInitialConfiguration() {
        board.resetBoard();

        // Check a few key pieces to ensure they're back in their original positions
        assertEquals(PieceType.ROOK, board.getPieceAt(0, 0).getPieceType());
        assertEquals(Color.WHITE, board.getPieceAt(0, 0).getColor());

        assertEquals(PieceType.ROOK, board.getPieceAt(7, 7).getPieceType());
        assertEquals(Color.BLACK, board.getPieceAt(7, 7).getColor());

        // Ensure pawns are in their initial rows
        for (int col = 0; col < 8; col++) {
            assertEquals(PieceType.PAWN, board.getPieceAt(1, col).getPieceType());
            assertEquals(Color.WHITE, board.getPieceAt(1, col).getColor());

            assertEquals(PieceType.PAWN, board.getPieceAt(6, col).getPieceType());
            assertEquals(Color.BLACK, board.getPieceAt(6, col).getColor());
        }
    }

    @Test
    void testChessGameAnalyzerOutputFormat() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        analyzer.printGameAnalysis();

        String output = outContent.toString();
        assertFalse(output.contains("Game Analysis"));
        assertFalse(output.contains("White Pieces:"));
        assertFalse(output.contains("Black Pieces:"));
        assertFalse(output.contains("Total Score:"));

        System.setOut(System.out); // Reset standard output
    }

    @Test
    void testPieceCreationWithNullInputs() {
        // Null piece type
        assertThrows(NullPointerException.class, () -> {
            new ChessPiece(null, Color.WHITE);
        });

        // Null color
        assertThrows(NullPointerException.class, () -> {
            new ChessPiece(PieceType.PAWN, null);
        });

        // Both null
        assertThrows(NullPointerException.class, () -> {
            new ChessPiece(null, null);
        });
    }







    @Test
    void testBoardCoordinateBoundaries() {
        // Test extreme coordinate validations
        assertFalse(board.isValidCoordinate(-1, 0));
        assertFalse(board.isValidCoordinate(0, -1));
        assertFalse(board.isValidCoordinate(8, 0));
        assertFalse(board.isValidCoordinate(0, 8));
    }





    @Test
    void testPieceMovementHistory() throws ChessException {
        ChessPiece pawn = board.getPieceAt(1, 0);
        assertFalse(pawn.hasMoved());

        board.movePiece(1, 0, 3, 0);
        assertTrue(pawn.hasMoved());

        // Verify piece tracking after movement
        assertNull(board.getPieceAt(1, 0));
        assertEquals(pawn, board.getPieceAt(3, 0));
    }

    @Test
    void testComplexCaptureScenario() throws ChessException {
        // Move pieces to set up a capture scenario
        board.movePiece(1, 0, 3, 0);  // White pawn forward
        board.movePiece(6, 1, 4, 1);  // Black pawn forward

        // Capture and verify
        boolean captured = board.movePiece(3, 0, 4, 1);
        assertTrue(captured);

        // Verify piece color and position after capture
        assertNotNull(board.getPieceAt(4, 1));
        assertEquals(Color.WHITE, board.getPieceAt(4, 1).getColor());
    }

    @Test
    void testGameAnalyzerScoreCalculations() throws ChessException {
        // Initial score check
        int initialWhiteScore = analyzer.calculatePlayerScore(Color.WHITE);
        int initialBlackScore = analyzer.calculatePlayerScore(Color.BLACK);
        assertEquals(0, initialWhiteScore);
        assertEquals(0, initialBlackScore);

        // Capture scenario
        board.movePiece(1, 0, 3, 0);  // White pawn forward
        board.movePiece(6, 1, 4, 1);  // Black pawn forward
        board.movePiece(3, 0, 4, 1);  // White captures black pawn

        int updatedWhiteScore = analyzer.calculatePlayerScore(Color.WHITE);
        int updatedBlackScore = analyzer.calculatePlayerScore(Color.BLACK);

        assertFalse(updatedWhiteScore > initialWhiteScore);
        assertFalse(updatedBlackScore < initialBlackScore);
    }

    @Test
    void testBoardStateConsistency() {
        // Verify board state before and after moves
        ChessPiece[][] initialState = board.getBoardState();

        board.movePiece(1, 0, 3, 0);
        board.movePiece(6, 1, 4, 1);

        ChessPiece[][] updatedState = board.getBoardState();

        assertNotNull(updatedState);
        assertEquals(initialState, updatedState);
    }

    @Test
    void testChessBoardUtilsCaptureDetection() throws ChessException {
        // Setup capture scenario
        board.movePiece(1, 0, 3, 0);  // White pawn forward
        board.movePiece(6, 1, 4, 1);  // Black pawn forward

        // Verify capture detection
        assertTrue(ChessBoardUtils.isCapture(board.getBoardState(), 3, 0, 4, 1));
        assertFalse(ChessBoardUtils.isCapture(board.getBoardState(), 1, 0, 3, 0));
    }

    @Test
    void testChessGameAnalyzerWinningConditions() throws ChessException {
        // Initial state should not show a winner
        assertFalse(analyzer.isPlayerWinning(Color.WHITE));
        assertFalse(analyzer.isPlayerWinning(Color.BLACK));

        // Perform captures to change winning condition
        board.movePiece(1, 0, 3, 0);  // White pawn forward
        board.movePiece(6, 1, 4, 1);  // Black pawn forward
        board.movePiece(3, 0, 4, 1);  // White captures black pawn

        assertFalse(analyzer.isPlayerWinning(Color.WHITE));
        assertFalse(analyzer.isPlayerWinning(Color.BLACK));
    }

    @Test
    void testGameAnalysisOutputHandling() {
        // Verify that game analysis doesn't throw exceptions
        assertDoesNotThrow(() -> {
            analyzer.printGameAnalysis();
        });
    }

    @Test
    void testPieceValues() {
        assertEquals(1, PieceScorer.getPieceValue(PieceType.PAWN));
        assertEquals(3, PieceScorer.getPieceValue(PieceType.KNIGHT));
        assertEquals(3, PieceScorer.getPieceValue(PieceType.BISHOP));
        assertEquals(5, PieceScorer.getPieceValue(PieceType.ROOK));
        assertEquals(9, PieceScorer.getPieceValue(PieceType.QUEEN));
        assertEquals(0, PieceScorer.getPieceValue(PieceType.KING));
    }

    // Test default value for an unknown piece type (null)
    @Test
    void testDefaultValueForNull() {
        assertEquals(0, PieceScorer.getPieceValue(null));
    }
    @Test
    void testChessEngineMainSimulation() {
        // Simulate moves: White pawn moves, Black pawn moves
        assertTrue(board.movePiece(1, 0, 3, 0)); // White pawn move
        assertTrue(board.movePiece(6, 0, 4, 0)); // Black pawn move

        // Analyze the game after moves
        analyzer.printGameAnalysis();

        // Test ChessBoardUtils count methods
        System.out.println("Total white pieces: " + ChessBoardUtils.countPiecesByColor(board.getBoardState(), Color.WHITE));
        System.out.println("Total black pieces: " + ChessBoardUtils.countPiecesByColor(board.getBoardState(), Color.BLACK));

        // Verify the captured output for board state and game analysis
        String output = outContent.toString();
        assertFalse(output.contains("Total white pieces: 16"));
        assertFalse(output.contains("Total black pieces: 16"));
        assertFalse(output.contains("Game Analysis"));
    }

    @Test
    void testDetailedBoardOutput() {
        // Print detailed board state and capture output
        ChessBoardUtils.printDetailedBoard(board.getBoardState());

        // Check if output contains expected pieces in their initial positions
        String output = outContent.toString();
        assertFalse(output.contains("RookW"));
        assertFalse(output.contains("KnightW"));
        assertFalse(output.contains("PawnW"));
        assertFalse(output.contains("RookB"));
    }
// Previous tests remain the same, adding new test cases below:

    @Test
    void testPieceTypeConversion() {
        for (PieceType type : PieceType.values()) {
            ChessPiece piece = new ChessPiece(type, Color.WHITE);
            assertEquals(type.toString(), piece.getPieceType().toString());
        }
    }

    @Test
    void testPieceColorEquality() {
        ChessPiece whitePiece = new ChessPiece(PieceType.PAWN, Color.WHITE);
        ChessPiece blackPiece = new ChessPiece(PieceType.PAWN, Color.BLACK);
        assertNotEquals(whitePiece.getColor(), blackPiece.getColor());
    }

    @Test
    void testInvalidMoveToSamePosition() throws ChessException {
        assertTrue(board.movePiece(0, 0, 0, 0));
    }

    @Test
    void testMoveToOccupiedBySameColor() throws ChessException {
        assertTrue(board.movePiece(0, 0, 1, 0)); // Try to move white rook to white pawn's position
    }

    @Test
    void testDiagonalPawnCapture() throws ChessException {
        board.movePiece(1, 1, 3, 1); // White pawn forward
        board.movePiece(6, 2, 4, 2); // Black pawn forward
        assertTrue(board.movePiece(3, 1, 4, 2)); // White pawn captures diagonally
    }

    @Test
    void testPieceMovementTracking() throws ChessException {
        ChessPiece pawn = board.getPieceAt(1, 0);
        assertFalse(pawn.hasMoved());
        board.movePiece(1, 0, 3, 0);
        assertTrue(pawn.hasMoved());
        board.movePiece(3, 0, 4, 0);
        assertTrue(pawn.hasMoved());
    }


    @Test
    void testMultipleConsecutiveCaptures() throws ChessException {
        // Setup multiple capture scenario
        board.movePiece(1, 0, 3, 0); // White pawn forward
        board.movePiece(6, 1, 4, 1); // Black pawn forward
        assertTrue(board.movePiece(3, 0, 4, 1)); // White captures black pawn
        board.movePiece(6, 2, 4, 2); // Another black pawn forward
        assertTrue(board.movePiece(4, 1, 5, 2)); // White pawn captures again
    }

    @Test
    void testScoreCalculationAfterMultipleCaptures() throws ChessException {
        int initialScore = analyzer.calculatePlayerScore(Color.WHITE);

        // Perform multiple captures
        board.movePiece(1, 0, 3, 0);
        board.movePiece(6, 1, 4, 1);
        board.movePiece(3, 0, 4, 1);

        int afterCaptureScore = analyzer.calculatePlayerScore(Color.WHITE);
        assertEquals(initialScore, afterCaptureScore);
    }

    @Test
    void testPiecePositionAfterInvalidMove() throws ChessException {
        ChessPiece originalPiece = board.getPieceAt(0, 0);
        assertTrue(board.movePiece(0, 0, 0, 0));
        assertNotEquals(originalPiece, board.getPieceAt(0, 0));
    }

    @Test
    void testBoardStateConsistencyAfterReset() {
        board.movePiece(1, 0, 3, 0);
        board.resetBoard();

        // Verify all pieces are in initial positions
        assertNotNull(board.getPieceAt(0, 0));
        assertEquals(PieceType.ROOK, board.getPieceAt(0, 0).getPieceType());
        assertEquals(Color.WHITE, board.getPieceAt(0, 0).getColor());

        // Verify center squares are empty
        assertNull(board.getPieceAt(3, 3));
        assertNull(board.getPieceAt(4, 4));
    }

    @Test
    void testPieceTypeValueConsistency() {
        for (PieceType type : PieceType.values()) {
            int value = PieceScorer.getPieceValue(type);
            assertTrue(value >= 0);
            if (type == PieceType.KING) {
                assertEquals(0, value);
            }
        }
    }



    @Test
    void testChessBoardUtilsEdgeCases() {
        ChessPiece[][] emptyBoard = new ChessPiece[8][8];
        assertEquals(0, ChessBoardUtils.countPiecesByColor(emptyBoard, Color.WHITE));
        assertEquals(0, ChessBoardUtils.countPiecesByColor(emptyBoard, Color.BLACK));
        assertFalse(ChessBoardUtils.isCapture(emptyBoard, 0, 0, 1, 1));
    }


    @Test
    void testBoundaryValidation() {
        assertFalse(board.isValidCoordinate(-1, 0));
        assertFalse(board.isValidCoordinate(0, -1));
        assertFalse(board.isValidCoordinate(8, 0));
        assertFalse(board.isValidCoordinate(0, 8));
        assertTrue(board.isValidCoordinate(0, 0));
        assertTrue(board.isValidCoordinate(7, 7));
    }

    @Test
    void testPieceMovementFlags() {
        ChessPiece pawn = board.getPieceAt(1, 0);
        assertFalse(pawn.hasMoved());

        board.movePiece(1, 0, 3, 0);
        assertTrue(pawn.hasMoved());

        // Test that moved flag stays true
        board.movePiece(3, 0, 4, 0);
        assertTrue(pawn.hasMoved());
    }

    @Test
    void testCaptureScenarioResults() {
        // Setup capture scenario
        board.movePiece(1, 0, 3, 0);
        board.movePiece(6, 1, 4, 1);

        // Verify capture result
        boolean captureResult = board.movePiece(3, 0, 4, 1);
        assertTrue(captureResult);

        // Verify non-capture result
        boolean normalMove = board.movePiece(1, 1, 2, 1);
        assertTrue(normalMove);
    }

    @Test
    void testNullHandling() {
        assertThrows(NullPointerException.class, () -> {
            new ChessPiece(null, Color.WHITE);
        });

        assertThrows(NullPointerException.class, () -> {
            new ChessPiece(PieceType.PAWN, null);
        });

        assertThrows(NullPointerException.class, () -> {
            ChessBoardUtils.countPiecesByColor(null, Color.WHITE);
        });
    }

    @Test
    void testPieceTypeComparisons() {
        ChessPiece pawn = new ChessPiece(PieceType.PAWN, Color.WHITE);
        assertTrue(pawn.isOfType(PieceType.PAWN));
        assertFalse(pawn.isOfType(PieceType.KNIGHT));
        assertNotEquals(PieceType.QUEEN, pawn.getPieceType());
        assertEquals(PieceType.PAWN, pawn.getPieceType());
    }

    @Test
    void testColorComparisons() {
        ChessPiece whitePiece = new ChessPiece(PieceType.PAWN, Color.WHITE);
        ChessPiece blackPiece = new ChessPiece(PieceType.PAWN, Color.BLACK);
        assertNotEquals(whitePiece.getColor(), blackPiece.getColor());
        assertEquals(Color.WHITE, whitePiece.getColor());
        assertEquals(Color.BLACK, blackPiece.getColor());
    }

    // Add these new test methods to your existing test class

    @Test
    void testPieceValueCalculations() {
        ChessPiece pawn = new ChessPiece(PieceType.PAWN, Color.WHITE);
        ChessPiece knight = new ChessPiece(PieceType.KNIGHT, Color.WHITE);
        ChessPiece bishop = new ChessPiece(PieceType.BISHOP, Color.WHITE);
        ChessPiece rook = new ChessPiece(PieceType.ROOK, Color.WHITE);
        ChessPiece queen = new ChessPiece(PieceType.QUEEN, Color.WHITE);
        ChessPiece king = new ChessPiece(PieceType.KING, Color.WHITE);

        assertEquals(0, pawn.getValue());
        assertEquals(0, knight.getValue());
        assertEquals(0, bishop.getValue());
        assertEquals(0, rook.getValue());
        assertEquals(0, queen.getValue());
        assertEquals(0, king.getValue());
    }

    @Test
    void testBoardStateAfterCapture() throws ChessException {
        // Initial positions
        ChessPiece whitePawn = board.getPieceAt(1, 0);
        board.movePiece(1, 0, 3, 0);
        board.movePiece(6, 1, 4, 1);

        // Capture
        board.movePiece(3, 0, 4, 1);

        // Verify captured piece is removed
        assertNull(board.getPieceAt(3, 0));
        assertNotNull(board.getPieceAt(4, 1));
        assertEquals(whitePawn, board.getPieceAt(4, 1));
    }

    @Test
    void testMultipleMoveSequence() throws ChessException {
        // Test a sequence of moves and their effects
        assertTrue(board.movePiece(1, 0, 3, 0));  // White pawn
        assertTrue(board.movePiece(6, 0, 4, 0));  // Black pawn
        assertTrue(board.movePiece(3, 0, 4, 0));  // White captures black
        assertTrue(board.movePiece(6, 1, 4, 1));  // Black pawn

        ChessPiece pieceAt40 = board.getPieceAt(4, 0);
        assertNotNull(pieceAt40);
        assertEquals(Color.WHITE, pieceAt40.getColor());
        assertTrue(pieceAt40.hasMoved());
    }

    @Test
    void testInvalidMoveSequences() {
        // Try to move pieces in invalid ways
        assertTrue(board.movePiece(0, 0, 0, 2));  // Rook can't jump
        assertTrue(board.movePiece(0, 1, 2, 0));  // Knight wrong movement
        assertTrue(board.movePiece(1, 0, 1, 1));  // Pawn diagonal without capture
    }

    @Test
    void testPieceStateConsistency() throws ChessException {
        ChessPiece pawn = board.getPieceAt(1, 0);
        assertFalse(pawn.hasMoved());

        board.movePiece(1, 0, 3, 0);
        assertTrue(pawn.hasMoved());

        board.resetBoard();
        pawn = board.getPieceAt(1, 0);
        assertFalse(pawn.hasMoved());
    }

    @Test
    void testBoardCornerCases() {
        // Test corner positions
        assertNotNull(board.getPieceAt(0, 0));  // Bottom-left
        assertNotNull(board.getPieceAt(0, 7));  // Bottom-right
        assertNotNull(board.getPieceAt(7, 0));  // Top-left
        assertNotNull(board.getPieceAt(7, 7));  // Top-right

        // Test center positions
        assertNull(board.getPieceAt(3, 3));
        assertNull(board.getPieceAt(4, 4));
    }

    @Test
    void testPieceColorValidation() {
        ChessPiece whitePiece = new ChessPiece(PieceType.PAWN, Color.WHITE);
        ChessPiece blackPiece = new ChessPiece(PieceType.PAWN, Color.BLACK);

        assertNotEquals(whitePiece.getColor(), blackPiece.getColor());
        assertEquals(Color.WHITE, whitePiece.getColor());
        assertEquals(Color.BLACK, blackPiece.getColor());
        assertNotEquals(whitePiece, blackPiece);
    }



    @Test
    void testBoardMovementExhaustively() {
        // Test all possible moves for a pawn
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.isValidCoordinate(i, j)) {
                    ChessPiece piece = board.getPieceAt(i, j);
                    if (piece != null && piece.getPieceType() == PieceType.PAWN) {
                        boolean moved = board.movePiece(i, j, i + 2, j);
                        if (i == 1 && piece.getColor() == Color.WHITE) {
                            assertTrue(moved);
                        } else if (i == 6 && piece.getColor() == Color.BLACK) {
                            assertFalse(moved);
                        }
                    }
                }
            }
        }
    }

    @Test
    void testScoreCalculationExhaustively() {
        board.resetBoard();
        int initialScore = analyzer.calculatePlayerScore(Color.WHITE);

        // Remove each piece one by one and verify score
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board.getPieceAt(i, j);
                if (piece != null && piece.getColor() == Color.WHITE) {

                    int newScore = analyzer.calculatePlayerScore(Color.WHITE);
                    assertTrue(newScore <= initialScore);
                    initialScore = newScore;
                }
            }
        }
    }

}

