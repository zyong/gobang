package test.my.chess;

import org.junit.Test;

import my.chess.GameModel;
import my.chess.Position;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

public class GameModelTest {

    private GameModel model;

    public GameModelTest() {
        model = new GameModel();
        model.update(1, 1, Position.COMPUTER);
    }

    @Test
    public void genPosition() {
        ArrayList<Position> result = (ArrayList<Position>)model.genPosition(4);
        System.out.print(result.size());
        assertEquals(8, result.size());
    }
}