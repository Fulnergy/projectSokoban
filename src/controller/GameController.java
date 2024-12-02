package controller;

import model.Direction;
import model.MapMatrix;
import view.game.Box;
import view.game.GamePanel;
import view.game.GridComponent;
import view.game.Hero;

/**
 * It is a bridge to combine GamePanel(view) and MapMatrix(model) in one game.
 * You can design several methods about the game logic in this class.
 */
public class GameController {
    private final GamePanel view;
    private final MapMatrix model;

    public GameController(GamePanel view, MapMatrix model) {
        this.view = view;
        this.model = model;
        view.setController(this);
    }

    public void restartGame() {
        System.out.println("Do restart game here");
    }

    public boolean doMove(int row, int col, Direction direction) {
        GridComponent currentGrid = view.getGridComponent(row, col);
        //target row can column.
        int tRow = row + direction.getRow();int ttRow = row + 2*direction.getRow();
        int tCol = col + direction.getCol();int ttCol = col + 2*direction.getCol();
        GridComponent targetGrid = view.getGridComponent(tRow, tCol);
        GridComponent ttargetGrid = view.getGridComponent(ttRow, ttCol);
        int[][] map = model.getMatrix();
        if (map[tRow][tCol] == 0 || map[tRow][tCol] == 2) {
            //update hero in MapMatrix
            model.getMatrix()[row][col] -= 20;
            model.getMatrix()[tRow][tCol] += 20;
            //Update hero in GamePanel
            Hero h = currentGrid.removeHeroFromGrid();
            targetGrid.setHeroInGrid(h);
            //Update the row and column attribute in hero
            h.setRow(tRow);
            h.setCol(tCol);
            return true;
        }else if(map[tRow][tCol] == 10 || map[tRow][tCol] == 12){
            if(map[ttRow][ttCol] == 0
            || map[ttRow][ttCol] == 2){
                //update hero and box in MapMatrix
                model.getMatrix()[row][col] -= 20;
                model.getMatrix()[tRow][tCol] += 10;
                model.getMatrix()[ttRow][ttCol] += 10;
                //Update hero and box in GamePanel
                Hero h = currentGrid.removeHeroFromGrid();
                targetGrid.setHeroInGrid(h);
                h.setRow(tRow);
                h.setCol(tCol);
                Box b = targetGrid.removeBoxFromGrid();
                b.setRow(ttRow);
                b.setCol(ttCol);
                ttargetGrid.setBoxInGrid(b);
                return true;
            }
        }
        return false;
    }

    //todo: add other methods such as loadGame, saveGame...

}
