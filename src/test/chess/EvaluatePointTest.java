package test.chess;

import org.junit.Assert;
import org.junit.Test;

import main.chess.Board;
import main.chess.EvaluatePoint;
import main.chess.Position;
import main.chess.Config;

public class EvaluatePointTest {
    Board b;

    public EvaluatePointTest() {
        Config.N = 7;
        b = new Board();
    }

    @Test
    public void scorePoint() {
        b.getModel().update(3,2,Position.COMPUTER);
        int score = EvaluatePoint.scorePoint(b, 3, 3, 2);
        Assert.assertEquals((long)(Position.TWO), score);
    }
}