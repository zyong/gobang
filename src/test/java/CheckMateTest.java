import org.junit.Assert;
import org.junit.Test;

import chess.Board;
import chess.CheckMate;
import chess.Config;
import chess.GameModel;
import chess.Position;


public class CheckMateTest {
    Board b;
    CheckMate cm;

    @Test
    public void check() throws Exception {
        Config.N = 6;
        b = new Board();
        b.setRule(Position.COMPUTER);
        GameModel model = b.getModel();
        /**
         *  [0, 0, 0, 0, 0, 0],
            [2, 1, 1, 1, 1, 0],
            [0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0],
         */
        model.update(1, 0, 2);
        model.update(1, 1, 1);
        model.update(1, 2, 1);
        model.update(1, 3, 1);
        model.update(1, 4, 1);
        cm = new CheckMate(b);
        Position p = cm.check(b, 1, 8, false);
        Assert.assertNotNull(p);
    }

    @Test
    public void check2() throws Exception {
        Config.N = 8;
        b = new Board();
        b.setRule(Position.COMPUTER);
        GameModel model = b.getModel();
        /**
            [0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0],
            [0, 1, 0, 1, 0, 0, 0, 0],
            [0, 0, 0, 2, 2, 0, 0, 0],
            [0, 0, 0, 1, 1, 1, 2, 0],
            [0, 0, 1, 2, 2, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0]
         */
        model.update(2, 1, 1);
        model.update(2, 3, 1);
        model.update(3, 3, 2);
        model.update(3, 4, 2);
        // model.update(4, 3, 1);
        model.update(4, 4, 1);
        model.update(4, 5, 1);
        model.update(4, 6, 1);

        model.update(5, 3, 2);
        model.update(5, 4, 2);
        cm = new CheckMate(b);
        Position p = cm.check(b, 1, 10, false);
        Assert.assertNotNull(p);
    }

    @Test
    public void check3() throws Exception {
        Config.N = 9;
        b = new Board();
        b.setRule(Position.COMPUTER);
        GameModel model = b.getModel();
        /**
         *  
      [ 0, 0, 2, 2, 1, 0, 0, 0, 0],
      [ 0, 2, 1, 1, 2, 0, 0, 0, 0],
      [ 0, 2, 1, 1, 2, 0, 0, 0, 0],
      [ 0, 2, 1, 1, 0, 0, 0, 0, 0],
      [ 0, 1, 0, 0, 0, 0, 0, 0, 0],
      [ 0, 0, 0, 0, 0, 0, 2, 2, 0],
      [ 0, 0, 0, 0, 0, 1, 1, 1, 2],
      [ 0, 0, 0, 0, 1, 0, 0, 0, 0],
      [ 0, 0, 0, 0, 0, 0, 0, 0, 0],
         */
        model.update(2, 0, 2);
        model.update(3, 0, 2);
        model.update(4, 0, 1);
        model.update(1, 1, 2);
        model.update(2, 1, 1);
        model.update(3, 1, 1);
        model.update(4, 1, 2);
        model.update(1, 2, 2);
        model.update(2, 2, 1);
        model.update(3, 2, 1);
        model.update(4, 2, 2);
        model.update(1, 3, 2);
        model.update(2, 3, 1);
        model.update(4, 3, 1);
        model.update(1, 4, 1);
        cm = new CheckMate(b);
        Position p = cm.check(b, 1, 6, false);
        Assert.assertNotNull(p);
    }

    @Test
    public void check4() throws Exception {
        Config.N = 10;
        b = new Board();
        b.setRule(Position.COMPUTER);
        GameModel model = b.getModel();
         /**
         *  
      [ 0, 0, 0, 0, 2, 0, 0, 0, 0, 0],
      [ 0, 0, 0, 1, 1, 0, 0, 0, 0, 0],
      [ 0, 0, 0, 0, 1, 0, 2, 0, 0, 0],
      [ 0, 0, 1, 2, 1, 1, 0, 0, 0, 0],
      [ 0, 0, 0, 2, 1, 2, 2, 0, 0, 0],
      [ 0, 0, 0, 1, 2, 0, 0, 0, 0, 0],
      [ 0, 0, 0, 0, 0, 2, 0, 0, 0, 0],
      [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
      [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
      [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
         */
        model.update(4, 0, 2);
        model.update(3, 1, 1);
        model.update(4, 1, 1);
        model.update(4, 2, 1);
        model.update(6, 2, 2);
        model.update(2, 3, 1);
        model.update(3, 3, 2);
        model.update(4, 3, 1);
        model.update(5, 3, 1);
        model.update(5, 4, 2);
        model.update(6, 4, 1);
        model.update(7, 4, 2);
        model.update(8, 4, 2);
        model.update(3, 5, 1);
        model.update(4, 5, 2);
        model.update(5, 6, 2);

        cm = new CheckMate(b);
        Position p = cm.check(b, 1, 0, false);
        Assert.assertNotNull(p);
    }

    @Test
    public void check5() throws Exception {
        Config.N = 6;
        b = new Board();
        b.setRule(Position.COMPUTER);
        GameModel model = b.getModel();
        /**
         *       
      [ 0, 1, 1, 0, 0, 0, 0],
      [ 0, 2, 2, 2, 0, 0, 0],
      [ 0, 0, 0, 0, 1, 0, 0],
      [ 0, 0, 0, 0, 1, 0, 0],
      [ 0, 0, 0, 0, 1, 0, 0],
      [ 0, 0, 0, 0, 2, 0, 0],
      [ 0, 0, 0, 0, 0, 0, 0],
         */

        model.update(1, 0, 1);
        model.update(2, 0, 1);
        model.update(1, 1, 2);
        model.update(2, 1, 2);
        model.update(3, 1, 2);
        model.update(4, 2, 1);
        model.update(4, 3, 1);
        model.update(5, 3, 1);
        model.update(5, 4, 2);

        cm = new CheckMate(b);
        Position p = cm.check(b, 1, 0, false);
        Assert.assertNull(p);
    }
}