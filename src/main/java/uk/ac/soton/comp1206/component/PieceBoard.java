package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;

public class PieceBoard extends GameBoard {
    private boolean doCircle;

    public PieceBoard(int cols, int rows, double width, double height) {
        super(cols, rows, width, height);

    }


    public void SetPieceToDisplay(GamePiece g){
        int[][] blocks = g.getBlocks();
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                if(blocks[i][j]>0){
                    grid.getGrid()[i][j].set(g.getValue());
                }
                else{
                    grid.getGrid()[i][j].set(0);
                }
            }
        }

        if(doCircle){
            GameBlock x = getBlock(1, 1);
            x.setShowCenter(true);
        }


    }
    public void setDoCircle(boolean m){
        doCircle = m;
    }


}