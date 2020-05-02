package my.chess;

import java.util.ArrayList;
import java.util.Random;

/**
 * 实现五子棋的机器人逻辑
 * 1、棋子得分计算
 * 2、通知棋盘更新
 * 3、行列得分计算
 * 
 */
public class Robots {
    int role = Position.COMPUTER;
    private GameModel model;
    private Board board;
    private int deep = 4;
    private static final int MAX = Position.FIVE * 100;
    private static final int MIN = -MAX;
    private Position start = null;

    // 总节点数， 剪枝的节点数
    int total = 0;  //总节点数
    int steps = 0;  //总步数
    int count;      //每次思考的节点数
    int PVcut; 
    int ABcut;      //AB剪枝次数

    public Robots(Board b) {
        this.board = b;
        this.model = b.getModel();
        // 初始一个随机值
        start();
    }

    public void start() {
        int n = GameModel.N;
        // 第一次落子,从棋盘位子中随机取一个
        // 或者没有可以放置的位置时,随机取一个值
        // 初始化一个位置
        Random rand = new Random();
        while (true) {
            int randIntX = rand.nextInt(6);
            int x;
            int y;
            if (randIntX % 2 == 0) {
                x = n/2 + randIntX;
            } else {
                x = n/2 - randIntX;
            }

            int randIntY = rand.nextInt(6);
            if (randIntY % 2 == 0) {
                y = n/2 + randIntY;
            } else {
                y = n/2 - randIntY;
            }
          
            start =
            model.matrix[x][y];
            if (start.role == Position.EMPTY) {
                start.role = Position.COMPUTER;
                break;
            } else if (start.role != Position.EMPTY) {
                continue;
            }
        }
    }

    /**
     * 电脑下棋
     * @return {@link Position}
     */
    public Position put() {
        Position p = genPosition();
        board.put(p);
        return p;
    }

    public Position genPosition() {
        Position p = minmax(deep);
        p.role = role;
        return p;
    }

    protected Position minmax(int deep) {
        int best = MIN;
        ArrayList<Position> bestPositions = new ArrayList<>();

        ArrayList<Position> points = (ArrayList<Position>) model.genPosition(deep);

        count = 0;
        ABcut = 0;
        PVcut = 0;

        Position[][] matrix = model.matrix;

        for (int i=0; i<points.size(); i++) {
            Position p = points.get(i);
            matrix[p.px][p.py].role = Position.COMPUTER;

            int v = - max(deep-1, best > MIN ? best : MIN, MAX);
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

        int comScore = evaluateRows((ArrayList<Position[]>) model.lines, Position.COMPUTER);
        int humScore = evaluateRows((ArrayList<Position[]>) model.lines, Position.HUMAN);

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
        int count = 0; //连子数
        int block = 0; //封闭数
        int empty = 0; //空位数
        int value = 0; //分数

        for (int i=1; i<line.length-1; i++) {
            if (line[i].role == role) {
                count = 1;
                block = 0;
                empty = 0;

                // 棋子在边界上
                if (i==0 || line[i-1].role != Position.EMPTY) {
                    block = 1;
                }
                // 向后查找同色棋子
                for (int j=i+1; j<line.length; j++) {
                    // 计算有多少相连的同色棋子
                    if (line[j].role == role) {
                        count++;
                    } else if (
                        empty == 0 &&
                        j < line.length-1 &&
                        line[j].role == Position.EMPTY &&
                        line[j+1].role == role 
                     ) {
                        empty = count;
                    }
                    else {
                        break;
                    }
                }

                //判断有边界
                if (i == line.length - 1 && line[i].role != Position.EMPTY) {
                    block++;
                }
                value += score(count, block, empty);
            }

        }
        return role;
    }

    private int score(int count, int block, int empty) {

        if (empty == 0) {
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
        } else if (empty == 1 || empty == count-1) {
            if (count >= 6) {
                return Position.FIVE;
            }
            if (block == 0) {
                switch(count) {
                    case 2: return Position.TWO;
                    case 3: 
                    case 4: return Position.THREE;
                    case 5: return Position.FOUR;
                    default: break;
                }
            }
            // 有一边被挡住的情况
            if(block == 1) {
                switch(count) {
                    case 2: return Position.BLOCKED_TWO;
                    case 3: return Position.BLOCKED_THREE;
                    case 4: return Position.THREE;
                    case 5: return Position.BLOCKED_FOUR;
                    default: break;
                }
            }
        } else if (empty == 2 || empty == count -2) {
            if (count >= 7) {
                return Position.FIVE;
            }
            if (block == 0) {
                switch(count) {
                    // 3的时候刚好前面有两个，后面有一个棋子
                    // 4的时候前面有两个，后面有两个棋子
                    // 5的时候前面有两个，后面有3个棋子
                    case 3:
                    case 4:
                    case 5: return Position.THREE;
                    case 6: return Position.FOUR;
                    default: break;
                }
            }
            if (block == 1) {
                switch(count) {
                    case 3: return Position.BLOCKED_THREE;
                    case 4: return Position.BLOCKED_FOUR;
                    case 5: return Position.BLOCKED_FOUR;
                    case 6: return Position.FOUR;
                    default: break;
                }
            }

            if (block == 2) {
                switch(count) {
                    case 4:
                    case 5:
                    case 6: return Position.BLOCKED_FOUR;
                    default: break;
                  }
            }
        } else if (empty == 3 || empty == count - 3) {
            if (count >= 8) {
                return Position.FIVE;
            }

            if (block == 0) {
                switch(count) {
                    case 4:
                    case 5: return Position.THREE;
                    case 6: return Position.THREE*2;
                    case 7: return Position.FOUR;
                    default: break;
                }
            }

            if (block == 1) {
                switch(count) {
                    case 4:
                    case 5:
                    case 6:
                    case 7: return Position.BLOCKED_FOUR;
                    default:break;
                }
            }
        } else if (empty == 4 || empty == count - 4) {
            if (count >= 9) {
                return Position.FIVE;
            }

            if (block == 0) {
                switch(count) {
                    case 5:
                    case 6:
                    case 7:
                    case 8: return Position.BLOCKED_FOUR;
                    default: break;
                }
            }

            if (block == 1) {
                switch(count) {
                    case 4:
                    case 5:
                    case 6:
                    case 7: return Position.BLOCKED_FOUR;
                    case 8: return Position.FOUR;
                    default: break;
                }
            }

            if (block == 2) {
                switch(count) {
                    case 5:
                    case 6:
                    case 7:
                    case 8: return Position.BLOCKED_FOUR;
                    default: break;
                }
            }
        } else if (empty == 5 || empty == count - 5) {
            return Position.FIVE;
        }

        

        return 0;
    }









}