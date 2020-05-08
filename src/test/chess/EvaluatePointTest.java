package test.chess;

import org.junit.Assert;
import org.junit.Test;

import main.chess.Board;
import main.chess.EvaluatePoint;
import main.chess.GameModel;
import main.chess.Position;
import main.Config;

public class EvaluatePointTest {
    Board b;
    EvaluatePoint ep;

    public EvaluatePointTest() {
        Config.N = 15;
        b = new Board();
        ep = new EvaluatePoint();
    }

    @Test
    public void scorePoint() {
        GameModel model = b.getModel();
        b.getModel().update(3,2,Position.COMPUTER);
        int score = ep.scorePoint(model, 3, 3, Position.COMPUTER);
        Assert.assertEquals((long)(Position.TWO + 3*Position.ONE), score);
    }

    @Test
    public void scorePoint2() {
        GameModel model = b.getModel();

        b.getModel().update(3,2,Position.COMPUTER);
        b.getModel().update(3,4,Position.COMPUTER);

        int score = ep.scorePoint(model, 3, 3, Position.COMPUTER);
        Assert.assertEquals((long)(Position.THREE + 3*Position.ONE), score);
    }

    @Test
    public void scorePoint3() {
        GameModel model = b.getModel();

        b.getModel().update(2,2,Position.COMPUTER);
        b.getModel().update(4,4,Position.COMPUTER);

        int score = ep.scorePoint(model, 5, 5, Position.COMPUTER);
        Assert.assertEquals((long)(Position.THREE + 3*Position.ONE), score);
    }

    @Test
    public void scorePoint4() {
        GameModel model = b.getModel();
        model.update(4,5,Position.HUMAN);
        model.update(4,6,Position.HUMAN);
        model.update(11,8,Position.COMPUTER);
        model.update(11,9,Position.COMPUTER);


        int score = ep.scorePoint(model, 11, 7, Position.COMPUTER);
        Assert.assertEquals((long)(Position.THREE + 3*Position.ONE), score);
    }

    @Test
    public void scorePoint5() {
        GameModel model = b.getModel();
        model.update(7,10,Position.HUMAN);
        model.update(8,10,Position.HUMAN);
        model.update(9,10,Position.HUMAN);
        model.update(10,10,Position.HUMAN);
        model.update(10,9,Position.HUMAN);

        model.update(10,8,Position.COMPUTER);
        model.update(11,8,Position.COMPUTER);
        model.update(11,9,Position.COMPUTER);
        model.update(11,10,Position.COMPUTER);


        int score = ep.scorePoint(model, 6, 10, Position.HUMAN);
        Assert.assertEquals((long)(Position.THREE + 3*Position.ONE), score);
    }
}