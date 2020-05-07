package main.chess;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;


public class CheckMate {
    int debugNodeCount = 0;

    int MAX_SCORE = Position.THREE;
    int MIN_SCORE = Position.FOUR;

    GameModel model;
    Board b;
    Robots robot;
    EvaluatePoint ep;

    public CheckMate(Board b) throws Exception {
        this.b = b;
        this.model = b.getModel();
        this.robot = b.getRobot();
        this.ep = new EvaluatePoint();
    }

    public Position check(Board b, int role, int deep, Boolean onlyFour) throws Exception {
        if (deep <= 0) {
            deep = Config.checkmateDeep;
        }
        
        //先计算冲四赢的
        MAX_SCORE = Position.FOUR;
        MIN_SCORE = Position.FIVE;
        
        List<Position> result = deeping(b, role, deep);
        if(!result.isEmpty()) {
            result.get(0).score = (double) Position.FOUR;
            return result.get(0);
        }
        if (Boolean.TRUE.equals(onlyFour)) {
            throw new Exception("can't return for four");
        }
        
        //再计算通过 活三 赢的；
        MAX_SCORE = Position.THREE;
        MIN_SCORE = Position.FOUR;
        result = deeping(b, role, deep);
        if(!result.isEmpty()) {
            result.get(0).score = (double) (Position.THREE * 2); // 虽然不如活四分数高，但是还是比活三分数要高的
            return result.get(0);
        }
        return null;
    }

    public List<Position> findMax(Board b, int role, int score) {
        ArrayList<Position> result = new ArrayList<>();
        for (int i = 0; i < model.matrix.length; i++) {
            for (int j = 0; j < model.matrix[0].length; j++) {
                if (model.matrix[i][j].role == Position.EMPTY) {
                    Position p = model.matrix[i][j];
                    if (model.hasNeihbor(p, 2, 1)) {
                        int s = ep.scorePoint(b.getModel(), p.px, p.py, role);
                        p.score = (double) s;
                        if (s >= Position.FIVE) {
                            result.add(p);
                            return result;
                        }

                        if (s >= score) {
                            result.add(p);
                        }
                    }
                }
            }
        }
        result.sort(new Comparator<Position>() {
            @Override
            public int compare(Position arg0, Position arg1) {
                return arg0.score.compareTo(arg1.score);
            }
        });
        return result;
    }

    public List<Position> findMin(Board b, int role, int score) {
        ArrayList<Position> result = new ArrayList<>();
        ArrayList<Position> fives = new ArrayList<>();
        ArrayList<Position> fours = new ArrayList<>();

        for (int i=0; i<model.matrix.length; i++) {
            for (int j=0; j<model.matrix[0].length; j++) {
                if (model.matrix[i][j].role == Position.EMPTY) {
                    Position p = model.matrix[i][j];
                    if (model.hasNeihbor(p, 1, 1)) {
                        int s1 = ep.scorePoint(b.getModel(), p.px, p.py, role);
                        int s2 = ep.scorePoint(b.getModel(), p.px, p.py, Position.reverseRole(role));

                        if (s1 >= Position.FIVE) {
                            p.score = (double)-s1;
                            result.add(p);
                            return result;
                        }

                        if (s1 >= Position.FOUR) {
                            p.score = (double) -s1;
                            fours.add(p);
                            continue;
                        }
                        if (s2 >= Position.FIVE) {
                            p.score = (double)s2;
                            fives.add(p);
                            continue;
                        }
                        if (s2 >= Position.FOUR) {
                            p.score = (double)s2;
                            fours.add(p);
                            continue;
                        }

                        if (s1 >= score || s2 >= score) {
                            p.score = (double)s1;
                            result.add(p);
                        }
                    }
                }
            }
        }

        if (!fives.isEmpty()) {
            result.clear();
            result.add(fives.get(0));
            return result;
        }
        if (!fours.isEmpty()) {
            result.clear();
            result.add(fours.get(0));
            return result;
        }

        result.sort(new Comparator<Position>(){
            @Override
            public int compare(Position arg0, Position arg1) {
                return ((Double)Math.abs(arg0.score)).compareTo(Math.abs(arg1.score));
            }
        });

        return result;
    }


    public int max(Board b, int role, int deep, List<Position> result) throws Exception {
        debugNodeCount++;
        if (deep <= 0) {
            return -1;
        }

        List<Position> points = findMax(b, role, MAX_SCORE);
        //为了减少一层搜索，活四就行了。
        if (!points.isEmpty() && points.get(0).score >= Position.FOUR) {
            result.add(points.get(0));
            return 1;
        }

        if (points.isEmpty()) {
            return -1;
        }

        model = b.getModel();
        List<Position> result2 = new ArrayList<>();
        int m;
        for (int i=0; i<points.size(); i++) {
            Position p = points.get(i);
            model.matrix[p.px][p.py].role = role;
            m = min(b, role, deep - 1, result2);
            model.matrix[p.px][p.py].role = Position.EMPTY;

            if (m >= 0) {
                if (!result2.isEmpty()) {
                    result2.add(0, p);
                    result.addAll(result2);
                    return 0; 
                } else {
                    result.clear();
                    result.add(0, p);
                    return 0;
                }
            }
        }
        return -1;
    }

    private int min(Board b2, int role, int deep, List<Position> result) throws Exception {
        debugNodeCount ++;

        int w = model.checkWin();
        if (w == role) {
            return 1;
        }
        if (w == Position.reverseRole(role)) {
            return -1;
        }

        if (deep <= 0) {
            return -1;
        }

        // 为是什么要到5
        ArrayList<Position> points = (ArrayList<Position>) findMin(b, role, MIN_SCORE);
        if (points.isEmpty()) {
            return -1;
        }

        if (-1 * points.get(0).score >= Position.FOUR) {
            return -1;
        }

        ArrayList<ArrayList<Position>> cands = new ArrayList<>();
        ArrayList<Position> result2 = new ArrayList<>();
        for (int i=0; i<points.size(); i++) {
            Position p = points.get(i);
            model.matrix[p.px][p.py].role = Position.reverseRole(role);
        
            int m = max(b, role, deep-1, result2);
            model.matrix[p.px][p.py].role = Position.EMPTY;
            if (m >= 0) {
                result2.add(0, p);
                cands.add(result2);
                continue;
            } else {
                return -1;
            }
        }

        result = cands.get((int)Math.floor(cands.size() * Math.random()));
        return 0;
    }


    // 迭代加深
    List<Position> deeping(Board b, int role, int deep) throws Exception {
        var start = new Date().getTime();
        debugNodeCount = 0;
        int m;
        ArrayList<Position> result = new ArrayList<>();
        for(var i=1;i<=deep;i++) {
            m = max(b, role, i, result);
            if(m >= 0) break; //找到一个就行
        }
        long time = (new Date()).getTime() - start;

        StringBuilder sb = new StringBuilder();
        for (int i=0; i<result.size(); i++) {
            sb.append("x:" + result.get(i).px + " y:" + result.get(0).py);
        }

        if(!result.isEmpty()) {
            System.out.print("算杀成功(" + time + "毫秒, "+ debugNodeCount + "个节点):" + sb.toString());
        } else {
            System.out.print("算杀失败("+time+"毫秒)" );
        }
        return result;
    }


}