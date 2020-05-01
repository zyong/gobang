package my.chess;

import java.util.ArrayList;
import java.util.Random;

/*  
* 自动计算最佳的棋子位子，使用minmax和alpha-beta算法
*/ 
public class Robots {
    int role = Position.COMPUTER;
    private GameModel model;
    private int deep = 4;
    private static final int MAX = Position.FIVE * 100;
    private static final int MIN = -MAX;
    private Position start = null;

    // 总节点数， 剪枝的节点数
    int total;
    int cut;

    public Robots(GameModel model) {
        this.model = model;
    }

    public void genPosition() {
        if (start == null) {
            // 第一次落子,从棋盘位子中随机取一个
            // 或者没有可以放置的位置时,随机取一个值
            // 初始化一个位置
            Random rand = new Random();
            while (true) {
                int x = 1 + rand.nextInt(GameModel.N);
                int y = 1 + rand.nextInt(GameModel.N);
                start =
                model.matrix[x][y];
                if (start.role == Position.EMPTY) {
                    start.role = Position.COMPUTER;
                    break;
                } else if (start.role != Position.EMPTY) {
                    continue;
                }
            }
        } else {
            Position p = minmax(deep);
            p.role = role;
        }
    }

    protected Position minmax(int deep) {
        int best = MIN;
        ArrayList<Position> bestPositions = new ArrayList<>();

        ArrayList<Position> points = (ArrayList<Position>) model.genPosition(deep);

        for (int i=0; i<points.size(); i++) {
            Position p = points.get(i);
            p.role = Position.COMPUTER;
            int v = min(deep-1, best > MIN ? best : MIN, MAX);
            if (v == best) {
                bestPositions.add(p);
            }

            if (v > best) {
                best = v;
                bestPositions.clear();
                bestPositions.add(p);
            }
            p.role = Position.EMPTY;
        }

        System.out.println("当前局面分数：" + best);
        System.out.println("搜索节点数" + total + " 剪枝节点数" + cut);
        Position result = bestPositions.get((int) Math.floor(bestPositions.size() * Math.random()));
        System.out.println("当前返回节点：x:" + result.px + " y:" + result.px);
        return result;
    }


    /*
     * 计算最小
     */
    private int min(int deep, int alpha, int beta) {
        int v = evaluate();
        total++;

        if (deep <= 0) {
            return v;
        }

        int best = MAX;
        ArrayList<Position> points = (ArrayList<Position>) model.genPosition(deep);

        for (int i=0; i<points.size(); i++) {
            Position p = points.get(i);
            model.matrix[p.px][p.py].role = Position.HUMAN;

            v = max(deep - 1, alpha, best < beta ? best : beta);
            model.matrix[p.px][p.py].role = Position.EMPTY;

            if (v < best) {
                best = v;
            }

            if (v < alpha) {
                cut++;
                break;
            }
        }

        return best;
    }

    private int max(int deep, int alpha, int beta) {
        int v = evaluate();
        total++;

        if (deep <= 0) {
            return v;
        }

        int best = MIN;
        ArrayList<Position> points = (ArrayList<Position>) model.genPosition(deep);

        for (int i=0; i < points.size(); i++) {
            Position p = points.get(i);
            model.matrix[p.px][p.py].role = Position.COMPUTER;
            v = min(deep - 1, best > alpha ? best : alpha, beta);
            model.matrix[p.px][p.py].role = Position.EMPTY;

            if (v > alpha) {
                best = v;
            }

            if (v > beta) {
                cut++;
                break;
            }
        }
        return best;
    }

    private int evaluate() {
        ArrayList<Position[]> rows = (ArrayList<Position[]>) model.lines;
        int comScore = evaluateRows(rows, Position.COMPUTER);
        int humScore = evaluateRows(rows, Position.HUMAN);

        return comScore - humScore;
    }

    private int evaluateRows(ArrayList<Position[]> lines, int role) {
        int r = 0;
        for (int i=0; i < lines.size(); i++) {
            r += evaluateRow(lines.get(i), role);
        }
        return r;
    }

    private int evaluateRow(Position[] line, int role) {
        int count = 0;
        int block = 0;

        for (int i=1; i<line.length-1; i++) {
            if (line[i].role == role) {
                count = 1;
                block = 0;
                // 棋子在边界上
                if (i==0 || line[i-1].role != Position.EMPTY) {
                    block = 1;
                }
                // 向后查找同色棋子
                for (int j=i+1; j<line.length-1; j++) {
                    // 计算有多少相连的同色棋子
                    if (line[j].role == role) {
                        count++;
                    } else {
                        break;
                    }
                }
                if (i == line.length - 1 && line[i].role == role) {
                    block++;
                }
                role += score(count, block);
            }

        }
        return role;
    }

    private int score(int count, int block) {
        if (count >= 5) {
            return Position.FIVE;
        }

        if (block == 0) {
            switch (count) {
                case 1: return Position.ONE;
                case 2: return Position.TWO;
                case 3: return Position.THREE;
                case 4: return Position.FOUR;
                default: break;
            }
        }
        if (block == 1) {
            switch(count) {
                case 1: return Position.BLOCKED_ONE;
                case 2: return Position.BLOCKED_TWO;
                case 3: return Position.BLOCKED_THREE;
                case 4: return Position.BLOCKED_FOUR;
                default: break;
            }
        } 

        return 0;
    }









}