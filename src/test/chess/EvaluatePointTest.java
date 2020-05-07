package test.chess;

import org.junit.Assert;
import org.junit.Test;

import main.chess.Board;
import main.chess.EvaluatePoint;
import main.chess.GameModel;
import main.chess.Position;
import main.chess.Config;

public class EvaluatePointTest {
    Board b;
    EvaluatePoint ep;

    public EvaluatePointTest() {
        Config.N = 7;
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
}