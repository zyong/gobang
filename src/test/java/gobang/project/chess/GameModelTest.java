package gobang.project.chess;

import org.junit.Test;


import static org.junit.Assert.assertEquals;


import java.util.ArrayList;

public class GameModelTest {

    private GameModel model;

    public GameModelTest() {
        Config.N = 15;
        model = new GameModel();
    }

    @Test
    public void genPosition() {
        model.update(8, 8, Position.COMPUTER);
        model.update(9, 9, Position.COMPUTER);

        ArrayList<Position> result = (ArrayList<Position>)model.genPosition(4);
        assertEquals(12, result.size());

    }
}