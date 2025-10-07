package chess;

public class BoardUtils {

    public static boolean causesSelfCheck(String[][] board, int[] start, int[] end, Chess.Player player) {
        String[][] temp = copyBoard(board);
        temp[end[0]][end[1]] = temp[start[0]][start[1]];
        temp[start[0]][start[1]] = ".";
        return isKingInCheck(temp, player);
    }

    public static boolean isCheck(String[][] board, Chess.Player player) {
        return isKingInCheck(board, player);
    }

    public static boolean isCheckmate(String[][] board, Chess.Player player) {
        if (!isKingInCheck(board, player)) return false;

        for (int r1 = 0; r1 < 8; r1++) {
            for (int c1 = 0; c1 < 8; c1++) {
                String piece = board[r1][c1];
                if (piece.equals(".")) continue;

                boolean isWhite = Character.isUpperCase(piece.charAt(0));
                if ((player == Chess.Player.white && !isWhite) ||
                    (player == Chess.Player.black && isWhite)) continue;

                int[] start = new int[]{r1, c1};
                for (int r2 = 0; r2 < 8; r2++) {
                    for (int c2 = 0; c2 < 8; c2++) {
                        int[] end = new int[]{r2, c2};
                        if (!MoveUtils.isLegalMove(board, start, end, player)) continue;
                        if (!causesSelfCheck(board, start, end, player)) return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isKingInCheck(String[][] board, Chess.Player player) {
        int[] kingPos = findKing(board, player);
        if (kingPos == null) return false;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = board[r][c];
                if (piece.equals(".")) continue;

                boolean isWhite = Character.isUpperCase(piece.charAt(0));
                if ((player == Chess.Player.white && isWhite) ||
                    (player == Chess.Player.black && !isWhite)) continue;

                int[] attacker = new int[]{r, c};
                if (MoveUtils.isLegalMove(board, attacker, kingPos, opposite(player))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int[] findKing(String[][] board, Chess.Player player) {
        char kingChar = (player == Chess.Player.white) ? 'K' : 'k';
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c].equals(String.valueOf(kingChar))) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    private static Chess.Player opposite(Chess.Player player) {
        return (player == Chess.Player.white) ? Chess.Player.black : Chess.Player.white;
    }

    public static String[][] copyBoard(String[][] board) {
        String[][] copy = new String[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        }
        return copy;
    }
}
