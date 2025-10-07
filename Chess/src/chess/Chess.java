package chess;

import java.util.ArrayList;

public class Chess {

    enum Player { white, black }

    private static String[][] board;
    private static Player currentPlayer;

    public static void start() {
        board = new String[8][8];
        currentPlayer = Player.white;

        // Black pieces
        board[0] = new String[]{"r", "n", "b", "q", "k", "b", "n", "r"};
        board[1] = new String[]{"p", "p", "p", "p", "p", "p", "p", "p"};

        // Empty squares
        for (int i = 2; i <= 5; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = ".";
            }
        }

        // White pieces
        board[6] = new String[]{"P", "P", "P", "P", "P", "P", "P", "P"};
        board[7] = new String[]{"R", "N", "B", "Q", "K", "B", "N", "R"};
    }

    public static ReturnPlay play(String move) {
        ReturnPlay result = new ReturnPlay();
        result.piecesOnBoard = new ArrayList<>();

        if (move.equals("resign")) {
            result.message = (currentPlayer == Player.white)
                ? ReturnPlay.Message.RESIGN_BLACK_WINS
                : ReturnPlay.Message.RESIGN_WHITE_WINS;
            result.piecesOnBoard = getCurrentPieces();
            return result;
        }

        boolean isDrawRequest = move.endsWith("draw?");
        if (isDrawRequest) {
            move = move.substring(0, move.length() - 6).trim();
        }

        String[] parts = move.trim().split(" ");
        if (parts.length != 2) {
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            result.piecesOnBoard = getCurrentPieces();
            return result;
        }

        int[] start = parsePosition(parts[0]);
        int[] end = parsePosition(parts[1]);

        if (start == null || end == null || !isLegalMove(start, end)) {
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            result.piecesOnBoard = getCurrentPieces();
            return result;
        }

        executeMove(start, end);

        if (isDrawRequest) {
            result.message = ReturnPlay.Message.DRAW;
        } else if (isCheckmate()) {
            result.message = (currentPlayer == Player.white)
                ? ReturnPlay.Message.CHECKMATE_BLACK_WINS
                : ReturnPlay.Message.CHECKMATE_WHITE_WINS;
        } else if (isCheck()) {
            result.message = ReturnPlay.Message.CHECK;
        } else {
            result.message = null;
        }

        result.piecesOnBoard = getCurrentPieces();
        switchTurn();
        return result;
    }

    private static int[] parsePosition(String pos) {
        if (pos.length() != 2) return null;
        char file = pos.charAt(0);
        char rank = pos.charAt(1);
        int col = file - 'a';
        int row = 8 - Character.getNumericValue(rank);
        if (col < 0 || col > 7 || row < 0 || row > 7) return null;
        return new int[]{row, col};
    }

    private static boolean isLegalMove(int[] start, int[] end) {
        String piece = board[start[0]][start[1]];
        if (piece.equals(".")) return false;

        boolean isWhite = Character.isUpperCase(piece.charAt(0));
        if ((currentPlayer == Player.white && !isWhite) ||
            (currentPlayer == Player.black && isWhite)) {
            return false;
        }

        // Basic legality: allow any move to empty square or enemy piece
        String target = board[end[0]][end[1]];
        if (target.equals(".")) return true;

        boolean targetIsWhite = Character.isUpperCase(target.charAt(0));
        return isWhite != targetIsWhite;
    }

    private static void executeMove(int[] start, int[] end) {
        board[end[0]][end[1]] = board[start[0]][start[1]];
        board[start[0]][start[1]] = ".";
    }

    private static void switchTurn() {
        currentPlayer = (currentPlayer == Player.white) ? Player.black : Player.white;
    }

    private static ArrayList<ReturnPiece> getCurrentPieces() {
        ArrayList<ReturnPiece> pieces = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String piece = board[row][col];
                if (!piece.equals(".")) {
                    ReturnPiece rp = new ReturnPiece();

                    // Convert column index to PieceFile enum
                    rp.pieceFile = ReturnPiece.PieceFile.values()[col];

                    // Convert board symbol to PieceType enum
                    String prefix = Character.isUpperCase(piece.charAt(0)) ? "W" : "B";
                    String type = piece.toUpperCase(); // P, R, N, B, Q, K
                    rp.pieceType = ReturnPiece.PieceType.valueOf(prefix + type);

                    rp.pieceRank = 8 - row;
                    pieces.add(rp);
                }
            }
        }
        return pieces;
    }


    private static boolean isCheck() {
        // Placeholder: no actual check logic yet
        return false;
    }

    private static boolean isCheckmate() {
        // Placeholder: no actual checkmate logic yet
        return false;
    }
}
