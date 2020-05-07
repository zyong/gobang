package main.chess;

import java.util.ArrayList;
import java.util.Comparator;
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
    

    // 总节点数， 剪枝的节点数
    int total = 0;  //总节点数
    int steps = 0;  //总步数
    int count;      //每次思考的节点数
    int pvCut; 
    int abCut;      //AB剪枝次数

    double threshold = 1.1;

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
            int randIntX = rand.nextInt(model.matrix.length/3);
            int x;
            int y;
            if (randIntX % 2 == 0) {
                x = n/2 + randIntX;
            } else {
                x = n/2 - randIntX;
            }

            int randIntY = rand.nextInt(model.matrix.length/3);
            if (randIntY % 2 == 0) {
                y = n/2 + randIntY;
            } else {
                y = n/2 - randIntY;
            }
          
            Position start =
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
     * 
     * @return {@link Position}
     * @throws Exception
     */
    public Position put() throws Exception {
        Position p = genPosition();
        board.put(p);
        return p;
    }

    public Position genPosition() throws Exception {
        Position p = maxmin(deep);
        p.role = role;
        return p;
    }

    protected Position maxmin(int deep) throws Exception {
        double best = MIN;
        ArrayList<Position> bestPositions = new ArrayList<>();

        ArrayList<Position> points = (ArrayList<Position>) model.genPosition(deep);

        count = 0;
        abCut = 0;
        pvCut = 0;

        Position[][] matrix = model.matrix;

        for (int i=0; i<points.size(); i++) {
            Position p = points.get(i);
            matrix[p.px][p.py].role = Position.COMPUTER;
            double v = - max(deep-1, MIN, (best > MIN ? best : MIN), Position.HUMAN);
            p.score = v;
            // 对边缘棋子要给分数打折，避免电脑总往边上走
            if (p.px < 3 || p.px > 11 || p.py < 3 || p.py > 11) {
                v = 0.5 * v;
            }
            // 在一个阈值范围内
            if ((v * threshold > best) && (v < best * threshold)) {
                bestPositions.add(p);
            }

            if (v > best * threshold) {
                best = v;
                bestPositions.clear();
                bestPositions.add(p);
            }
            matrix[p.px][p.py].role = Position.EMPTY;
        }
        // 解决没有最佳棋子的问题
        if (bestPositions.isEmpty()) {
            points.sort(
                new Comparator<Position>(){
                    @Override
                    public int compare(Position arg0, Position arg1) {
                        return arg0.score.compareTo(arg1.score);
                    }
            });
            bestPositions = (ArrayList<Position>)points.subList(0, points.size() - 1);
        }


        StringBuilder sb = new StringBuilder();
        for (Position p: bestPositions) {
            sb.append("x:" + p.px + " y:" + p.py);
        }

        System.out.println("当前局面分数：" + best +", 待选节点" + sb.toString());
        System.out.println("搜索节点数" + total + " 剪枝节点数" + abCut);
        Position result = bestPositions.get((int) Math.floor(bestPositions.size() * Math.random()));
        result.score = best;
        steps++;
        total += count;
        System.out.println("当前返回节点：x:" + result.px + " y:" + result.px);
        return result;
    }

    private double max(int deep, double alpha, double beta, int role) throws Exception {
        double v = evaluate();
        total++;

        int result = model.checkWin(); 
        if (deep <= 0 || result != Position.EMPTY) {
            return v;
        }

        double best = MIN;
        ArrayList<Position> points = (ArrayList<Position>) model.genPosition(deep);

        for (int i=0; i < points.size(); i++) {
            Position p = points.get(i);
            model.matrix[p.px][p.py].role = role;
            v = - max(deep - 1, best > alpha ? best : alpha, beta, Position.reverseRole(role)) * Config.deepDecrease;
            model.matrix[p.px][p.py].role = Position.EMPTY;

            if (v > alpha * threshold) {
                best = v;
            }

            if (v * threshold > beta) {
                abCut++;
                return v;
            }
        }

        if ((deep <= 2) && 
            role == Position.COMPUTER && 
            best*threshold <= Position.THREE*2 && 
            best >= threshold * Position.THREE-1
            ) {
                CheckMate cm = new CheckMate(board);
                Position mate = cm.check(board, role, 0, false);
                if (mate != null) {
                    return mate.score * 0.8 * (role == Position.COMPUTER ? 1: -1);
                }
            }

        return best;
    }

    /**
     * 迭代加深
     * 
     * @param deep
     * @return
     * @throws Exception
     */
    public Position deeping(int deep) throws Exception {
        if (deep < 2) {
            deep = Config.searchDeep;
        }
        Position result = new Position();
        for (int i=2; i <= deep; i+=2) {
            result = maxmin(i);
            if (result.score * threshold > Position.FOUR) 
                return result;
        }
        return result;
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
        count = 0; //连子数
        int block = 0; //封闭数
        int empty = 0; //空位数
        int value = 0;

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
        return value;
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