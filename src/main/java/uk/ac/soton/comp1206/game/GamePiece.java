package uk.ac.soton.comp1206.game;


/**
 * Instances of GamePiece Represents the model of a specific Game Piece with
 * it's block makeup.
 *
 * The GamePiece class also contains a factory for producing a GamePiece of a
 * particular shape, as specified by it's number.
 */
public class GamePiece {

    /**
     * The total number of pieces in this game
     */
    public static final int PIECES = 15;

    /**
     * The 2D grid representation of the shape of this piece
     */
    private int[][] blocks;

    /**
     * The value of this piece
     */
    private final int value;

    /**
     * The name of this piece
     */
    private final String name;

    /**
     * Create a new GamePiece of the specified piece number
     * 
     * @param piece piece number
     * @return the created GamePiece
     */
    public static GamePiece createPiece(int piece) {
        switch (piece) {
        // Line
        case 0 -> {
            int[][] blocks = { { 0, 0, 0 }, { 1, 1, 1 }, { 0, 0, 0 } };
            return new GamePiece("Line", blocks, 1);
        }

        // C
        case 1 -> {
            int[][] blocks = { { 0, 0, 0 }, { 1, 1, 1 }, { 1, 0, 1 } };
            return new GamePiece("C", blocks, 2);
        }

        // Plus
        case 2 -> {
            int[][] blocks = { { 0, 1, 0 }, { 1, 1, 1 }, { 0, 1, 0 } };
            return new GamePiece("Plus", blocks, 3);
        }

        // Dot
        case 3 -> {
            int[][] blocks = { { 0, 0, 0 }, { 0, 1, 0 }, { 0, 0, 0 } };
            return new GamePiece("Dot", blocks, 4);
        }

        // Square
        case 4 -> {
            int[][] blocks = { { 1, 1, 0 }, { 1, 1, 0 }, { 0, 0, 0 } };
            return new GamePiece("Square", blocks, 5);
        }

        // L
        case 5 -> {
            int[][] blocks = { { 0, 0, 0 }, { 1, 1, 1 }, { 0, 0, 1 } };
            return new GamePiece("L", blocks, 6);
        }

        // J
        case 6 -> {
            int[][] blocks = { { 0, 0, 1 }, { 1, 1, 1 }, { 0, 0, 0 } };
            return new GamePiece("J", blocks, 7);
        }

        // S
        case 7 -> {
            int[][] blocks = { { 0, 0, 0 }, { 0, 1, 1 }, { 1, 1, 0 } };
            return new GamePiece("S", blocks, 8);
        }

        // Z
        case 8 -> {
            int[][] blocks = { { 1, 1, 0 }, { 0, 1, 1 }, { 0, 0, 0 } };
            return new GamePiece("Z", blocks, 9);
        }

        // T
        case 9 -> {
            int[][] blocks = { { 1, 0, 0 }, { 1, 1, 0 }, { 1, 0, 0 } };
            return new GamePiece("T", blocks, 10);
        }

        // X
        case 10 -> {
            int[][] blocks = { { 1, 0, 1 }, { 0, 1, 0 }, { 1, 0, 1 } };
            return new GamePiece("X", blocks, 11);
        }

        // Corner
        case 11 -> {
            int[][] blocks = { { 0, 0, 0 }, { 1, 1, 0 }, { 1, 0, 0 } };
            return new GamePiece("Corner", blocks, 12);
        }

        // Inverse Corner
        case 12 -> {
            int[][] blocks = { { 1, 0, 0 }, { 1, 1, 0 }, { 0, 0, 0 } };
            return new GamePiece("Inverse Corner", blocks, 13);
        }

        // Diagonal
        case 13 -> {
            int[][] blocks = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
            return new GamePiece("Diagonal", blocks, 14);
        }

        // Double
        case 14 -> {
            int[][] blocks = { { 0, 1, 0 }, { 0, 1, 0 }, { 0, 0, 0 } };
            return new GamePiece("Double", blocks, 15);
        }
        }

        // Not a valid piece number
        throw new IndexOutOfBoundsException("No such piece: " + piece);
    }


    /**
     * Transposes a given 2d array, used for switching from cartesian coordinates to
     * matrix indexing
     * 
     * @param q 2d array
     * @return transposed 2d array
     */
    private int[][] transpose(int[][] q) {
        var b = new int[q.length][q[0].length];
        for (int x = 0; x < blocks.length; x++) {
            for (int y = 0; y < blocks[x].length; y++) {
                b[x][y] = q[y][x];
            }
        }
        return b;
    }

    /**
     * Create a new GamePiece with the given name, block makeup and value. Should
     * not be called directly, only via the factory.
     * 
     * @param name   name of the piece
     * @param blocks block makeup of the piece
     * @param value  the value of this piece
     */
    private GamePiece(String name, int[][] blocks, int value) {
        this.name = name;
        this.blocks = blocks;

        this.value = value;

        // Use the shape of the block to create a grid with either 0 (empty) or the
        // value of this shape for each block.
        for (int x = 0; x < blocks.length; x++) {
            for (int y = 0; y < blocks[x].length; y++) {
                if (blocks[x][y] == 0)
                    continue;
                blocks[x][y] = value;
            }
        }
    }

    /**
     * Get the value of this piece
     * 
     * @return piece value
     */
    public int getValue() {
        return value;
    }

    /**
     * Get the block makeup of this piece
     * 
     * @return 2D grid of the blocks representing the piece shape
     */
    public int[][] getBlocks() {
        //Transpose because using Matrix notation not Cartesian
        return transpose(blocks);
    }

    /**
     * Rotation algorithm that rotates a piece either clockwise or anticlockwise
     * 
     * @param direction anticlockwise is -1, clockwise is 1
     */
    public void rotate(int direction) { // direction=1 is clockwise, -1 is anticlockwise
        //Creates a new empty array with the same size as blocks
        int[][] rotated = new int[blocks.length][blocks[0].length];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (direction == -1) {
                    //Translates single piece anticlockwise
                    rotated[i][j] = blocks[2 - j][i];
                }
                if (direction == 1) {
                    //Translates single piece clockwise
                    rotated[i][j] = blocks[j][2 - i];
                }
            }
        }

        blocks = rotated;
    }

    /**
     * Return the string representation of this piece
     * 
     * @return the name of this piece
     */
    public String toString() {
        return this.name;
    }

}
