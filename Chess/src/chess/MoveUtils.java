package chess;

public class MoveUtils {

    public static boolean isLegalMove(String[][] board, int[] start, int[] end, Chess.Player player) {
        String piece = board[start[0]][start[1]];
        if (piece.equals(".")) return false;

        boolean isWhite = Character.isUpperCase(piece.charAt(0));
        if ((player == Chess.Player.white && !isWhite) ||
            (player == Chess.Player.black && isWhite)) return false;

        String target = board[end[0]][end[1]];
        boolean isCapture = !target.equals(".") &&
            Character.isUpperCase(piece.charAt(0)) != Character.isUpperCase(target.charAt(0));

        int dr = end[0] - start[0];
        int dc = end[1] - start[1];

        switch (Character.toUpperCase(piece.charAt(0))) {
            case 'P': return isLegalPawnMove(board, start, end, isWhite, isCapture);
            case 'R': return isLegalRookMove(board, start, end);
            case 'N': return isLegalKnightMove(dr, dc);
            case 'B': return isLegalBishopMove(board, start, end);
            case 'Q': return isLegalQueenMove(board, start, end);
            case 'K': return isLegalKingMove(dr, dc);
            default: return false;
        }
    }

    public static boolean isCastling(int[] start, int[] end, String piece) {
        return piece.equalsIgnoreCase("k") &&
               Math.abs(start[1] - end[1]) == 2 &&
               start[0] == end[0];
    }

    public static void executeCastling(String[][] board, int[] start, int[] end) {
        board[end[0]][end[1]] = board[start[0]][start[1]];
        board[start[0]][start[1]] = ".";
        if (end[1] == 6) {
            board[end[0]][5] = board[end[0]][7];
            board[end[0]][7] = ".";
        } else {
            board[end[0]][3] = board[end[0]][0];
            board[end[0]][0] = ".";
        }
    }

    public static boolean isPromotion(int[] start, int[] end, String piece) {
        return piece.equalsIgnoreCase("p") &&
               (end[0] == 0 || end[0] == 7);
    }

    public static void executePromotion(String[][] board, int[] start, int[] end, Chess.Player player, String promotionPiece) {
        String promoted = (player == Chess.Player.white) ? promotionPiece.toUpperCase() : promotionPiece.toLowerCase();
        board[end[0]][end[1]] = promoted;
        board[start[0]][start[1]] = ".";
    }

    private static boolean isLegalPawnMove(String[][] board, int[] start, int[] end, boolean isWhite, boolean isCapture) {
        int dir = isWhite ? -1 : 1;
        int startRow = isWhite ? 6 : 1;
        int dr = end[0] - start[0];
        int dc = end[1] - start[1];

        if (isCapture) return dr == dir && Math.abs(dc) == 1;

        if (dc != 0) return false;
        if (dr == dir && board[end[0]][end[1]].equals(".")) return true;
        if (start[0] == startRow && dr == 2 * dir &&
            board[start[0] + dir][start[1]].equals(".") &&
            board[end[0]][end[1]].equals(".")) return true;

        return false;
    }

    private static boolean isLegalRookMove(String[][] board, int[] start, int[] end) {
        if (start[0] != end[0] && start[1] != end[1]) return false;
        return isPathClear(board, start, end);
    }

    private static boolean isLegalKnightMove(int dr, int dc) {
        return (Math.abs(dr) == 2 && Math.abs(dc) == 1) ||
               (Math.abs(dr) == 1 && Math.abs(dc) == 2);
    }

    private static boolean isLegalBishopMove(String[][] board, int[] start, int[] end) {
        if (Math.abs(end[0] - start[0]) != Math.abs(end[1] - start[1])) return false;
        return isPathClear(board, start, end);
    }

    private static boolean isLegalQueenMove(String[][] board, int[] start, int[] end) {
        return isLegalRookMove(board, start, end) || isLegalBishopMove(board, start, end);
    }

    private static boolean isLegalKingMove(int dr, int dc) {
        return Math.abs(dr) <= 1 && Math.abs(dc) <= 1;
    }

    private static boolean isPathClear(String[][] board, int[] start, int[] end) {
        int dr = Integer.compare(end[0], start[0]);
        int dc = Integer.compare(end[1], start[1]);
        int r = start[0] + dr, c = start[1] + dc;

        while (r != end[0] || c != end[1]) {
            if (!board[r][c].equals(".")) return false;
            r += dr;
            c += dc;
        }
        return true;
    }
}
