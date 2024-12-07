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

    public Direction lastDirection;
    public int lastStep = 0;//0为初始值，无意义，1为仅hero移动,2为hero和box都移动
    public boolean repealIsValid = true;//由于成功撤销会减小一次步数，为防止玩家通过反复撤销减小步数，设置此变量。当玩家进行了一次撤销后，无法再次撤销。（主要是因为后续开发的关卡中可能可以加入步数限制）
    public boolean isRepealing = false;

    public GameController(GamePanel view, MapMatrix model) {
        this.view = view;
        this.model = model;
        view.setController(this);
    }

    public void restartGame() {
        System.out.println("Do restart game here");
    }

    //移动共分三步，改变数组的数据，改变显示的frame和改变hero（和box）的属性
    public boolean doMove(int row, int col, Direction direction) {
        GridComponent currentGrid = view.getGridComponent(row, col);
        //target row can column.
        int tRow = row + direction.getRow();
        int tCol = col + direction.getCol();
        GridComponent targetGrid = view.getGridComponent(tRow, tCol);
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
            //record the last step
            if(!isRepealing) {//这是因为在撤销时也会调用doMove
                lastDirection = direction;
                lastStep = 1;
                repealIsValid = true;
            }
            return true;
        }else if((map[tRow][tCol] == 10 || map[tRow][tCol] == 12) &&
                checkValid(row + 2*direction.getRow(),col + 2*direction.getCol())){
            //tt开头的指的是下一格的下一格，即箱子将要落到的格子
            int ttRow = row + 2*direction.getRow();
            int ttCol = col + 2*direction.getCol();
            GridComponent ttargetGrid = view.getGridComponent(ttRow, ttCol);
            if(map[ttRow][ttCol] == 0
            || map[ttRow][ttCol] == 2){
                //和上面的一样
                model.getMatrix()[row][col] -= 20;
                model.getMatrix()[tRow][tCol] += 10;
                model.getMatrix()[ttRow][ttCol] += 10;
                Hero h = currentGrid.removeHeroFromGrid();
                targetGrid.setHeroInGrid(h);
                h.setRow(tRow);
                h.setCol(tCol);
                Box b = targetGrid.removeBoxFromGrid();
                b.setRow(ttRow);
                b.setCol(ttCol);
                ttargetGrid.setBoxInGrid(b);
                //record the step
                lastDirection = direction;
                lastStep = 2;
                repealIsValid = true;
                return true;
            }
        }
        return false;
    }

    public boolean checkValid(int row,int col) {
        return row >= 0 && row < model.getMatrix().length && col >= 0 && col < model.getMatrix()[row].length;
    }

    //目前而言，撤销只能撤销一步,如果后续有空，考虑通过arraylist实现全部步数的记录，以进行多步撤销
    //个人认为，就推箱子小游戏而言多步撤销意义不大，因为本来游戏流程就短，如果遇到需要多步撤销的时候。还不如直接重开
    public boolean repeal(int row,int col){
        if(repealIsValid) {
            if (lastStep == 1) {
                Direction repealDirection = lastDirection.turnRound();
                doMove(row, col, repealDirection);
                repealIsValid = false;
                return true;
            } else if (lastStep == 2) {
                isRepealing = true;
                //处理hero
                Direction repealDirection = lastDirection.turnRound();
                doMove(row, col, repealDirection);
                //处理box
                int boxRow = row+lastDirection.getRow();int boxCol = col+lastDirection.getCol();
                model.getMatrix()[row][col] += 10;
                model.getMatrix()[boxRow][boxCol] -= 10;
                GridComponent boxGrid = view.getGridComponent(boxRow,boxCol);
                Box b = boxGrid.removeBoxFromGrid();
                b.setRow(row);
                b.setCol(col);
                view.getGridComponent(row, col).setBoxInGrid(b);
                isRepealing = false;
                repealIsValid = false;
                return true;
            }
        }
        return false;
    }


    //todo: add other methods such as loadGame, saveGame...

}
