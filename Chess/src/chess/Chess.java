package chess;

import java.util.ArrayList;

public class Chess {

    enum Player { white, black }

    private static String[][] board;
    private static Player currentPlayer;

    public static void start() {
        board = new String[8][8];
        currentPlayer = Player.white;

        board[0] = new String[]{"r", "n", "b", "q", "k", "b", "n", "r"};
        board[1] = new String[]{"p", "p", "p", "p", "p", "p", "p", "p"};
        for (int i = 2; i <= 5; i++)
            for (int j = 0; j < 8; j++) board[i][j] = ".";
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
        if (isDrawRequest) move = move.substring(0, move.length() - 6).trim();

        String[] parts = move.trim().split(" ");
        if (parts.length < 2 || parts.length > 3) {
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            result.piecesOnBoard = getCurrentPieces();
            return result;
        }

        int[] start = parsePosition(parts[0]);
        int[] end = parsePosition(parts[1]);
        String promotionPiece = (parts.length == 3) ? parts[2].toUpperCase() : "Q";

        if (start == null || end == null) {
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            result.piecesOnBoard = getCurrentPieces();
            return result;
        }

        String piece = board[start[0]][start[1]];
        if (!MoveUtils.isLegalMove(board, start, end, currentPlayer)) {
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            result.piecesOnBoard = getCurrentPieces();
            return result;
        }

        if (BoardUtils.causesSelfCheck(board, start, end, currentPlayer)) {
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            result.piecesOnBoard = getCurrentPieces();
            return result;
        }

        if (MoveUtils.isCastling(start, end, piece)) {
            MoveUtils.executeCastling(board, start, end);
        } else if (MoveUtils.isPromotion(start, end, piece)) {
            MoveUtils.executePromotion(board, start, end, currentPlayer, promotionPiece);
        } else {
            board[end[0]][end[1]] = board[start[0]][start[1]];
            board[start[0]][start[1]] = ".";
        }

        result.message = isDrawRequest ? ReturnPlay.Message.DRAW :
                         BoardUtils.isCheckmate(board, currentPlayer) ? (
                             currentPlayer == Player.white ?
                             ReturnPlay.Message.CHECKMATE_BLACK_WINS :
                             ReturnPlay.Message.CHECKMATE_WHITE_WINS) :
                         BoardUtils.isCheck(board, currentPlayer) ?
                             ReturnPlay.Message.CHECK : null;

        result.piecesOnBoard = getCurrentPieces();
        switchTurn();
        return result;
    }

    private static int[] parsePosition(String pos) {
        if (pos.length() != 2) return null;
        int col = pos.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(pos.charAt(1));
        if (col < 0 || col > 7 || row < 0 || row > 7) return null;
        return new int[]{row, col};
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
                    rp.pieceFile = ReturnPiece.PieceFile.values()[col];
                    String prefix = Character.isUpperCase(piece.charAt(0)) ? "W" : "B";
                    String type = piece.toUpperCase();
                    rp.pieceType = ReturnPiece.PieceType.valueOf(prefix + type);
                    rp.pieceRank = 8 - row;
                    pieces.add(rp);
                }
            }
        }
        return pieces;
    }
}
