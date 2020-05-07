package main.chess;

public class EvaluatePoint {

    static int result = 0;
    static int empty = 0;
    static int radius = 8;
    static int count = 0;
    static int block = 0;
    static int secondCount = 0;


    public static int scorePoint(Board b, int px, int py, int role) {

        GameModel model = b.getModel();

        Position[][] matrix = model.matrix;
        int len = matrix.length;
  
        for (int i=py+1; true; i++) {
            if (i>=len) {
                block++;
                break;
            }

            Position p = matrix[px][i];
            if (p.role == Position.EMPTY) {
                if (empty == -1 && i < len-1 && matrix[px][i+1].role == role) {
                    empty = count;
                    continue;
                } else {
                    break;
                }
            }
            if (p.role == role) {
                count++;
                continue;
            } else {
                block++;
                break;
            }
        }


        for (int i=py-1;true;i--) {
            if (i<0) {
                block++;
                break;
            }
            Position p = matrix[px][i];
            if (p.role == Position.EMPTY) {
                if (empty == -1 && i > 0 && matrix[px][i-1].role == role) {
                    empty = 0;
                    continue;
                } else {
                    break;
                }
            }

            if (p.role == role) {
                secondCount++;
                if (empty != -1) 
                    empty++;
                continue;
            } else {
                block++;
                break;
            }
        }

        count += secondCount;
        // 添加摸个位子角色得分缓存
        System.out.printf("px %d, py %d", px, py);
        b.scoreCache[role][0][px][py] = countToScore(count, block, empty);
        
        // result += b.scoreCache[role][0][px][py]

        for (int i=px+1; true; i++) {
            if (i>=len) {
                block++;
                break;
            }
            Position p = matrix[i][py];
            if (p.role == Position.EMPTY) {
                if(empty == -1 && i < len-1 && matrix[i+1][py].role == role) {
                    empty = count;
                    continue;
                } else {
                    break;
                }
            }
            if (p.role == role) {
                count++;
                continue;
            } else {
                block++;
                break;
            }
        }

        for (int i=px-1;true;i--) {
            if (i<0) {
                block++;
                break;
            }
            Position p = matrix[i][py];
            if (p.role == Position.EMPTY) {
                if (empty == -1 && i>0 && matrix[i-1][py].role == role) {
                    empty = 0;
                    continue;
                } else {
                    break;
                }
            }

            if(p.role == role) {
                secondCount++;
                if(empty != -1) 
                    empty++;
                continue;
            } else {
                block++;
                break;
            }
        }

        count += secondCount;
        b.scoreCache[role][1][px][py] = countToScore(count, block, empty);

        result += b.scoreCache[role][1][px][py];


        // \方向

        for (int i=1;true;i++) {
            int x = px+i;
            int y = py+i;
            if (x >= len || y>=len) {
                block++;
                break;
            }

            Position p = matrix[x][y];
            if (p.role == Position.EMPTY) {
                if (empty == -1 && (x < len-1 && y < len -1) && matrix[x+1][y+1].role == role) {
                    empty = count;
                    continue;
                } else {
                    break;
                }
            }
            if (p.role == role) {
                count++;
                continue;
            } else {
                block++;
                break;
            }
        }

        for (int i=1;true;i++) {
            int x = px-i;
            int y = py-i;

            if (x<0 || y<0) {
                block++;
                break;
            }
            Position p = matrix[x][y];
            if (p.role == Position.EMPTY) {
                if (empty == -1 && (x>0 && y>0) && matrix[x-1][y-1].role == role) {
                    empty = 0;
                    continue;
                } else {
                    block++;
                    break;
                }
            }
            if (p.role == role) {
                secondCount++;
                if (empty == -1) 
                    empty++;
                continue;
            } else {
                block++;
                break;
            }
        }
        count+= secondCount;
        b.scoreCache[role][2][px][py] = countToScore(count, block, empty);
    
        result += b.scoreCache[role][2][px][py];

        // /方向

        for (int i=1; true; i++) {
            int x = px+i;
            int y = py-i;
            if (x<0 || y<0 || x>=len || y>= len) {
                block++;
                break;
            }
            Position p = matrix[x][y];
            if (p.role == Position.EMPTY) {
                if (empty == -1 && (x < len-1 && y<len-1) && matrix[x+1][y-1].role == role) {
                    empty = count;
                    continue;
                } else {
                    break;
                }
            }
            if (p.role == role) {
                count++;
                continue;
            } else {
                block++;
                break;
            }
        }

        for (int i=1;true;i++) {
            int x = px-i;
            int y = py+i;
            if (x<0 || y<0 || x >= len || y>=len) {
                block++;
                break;
            }

            Position p = matrix[x][y];
            if (p.role == Position.EMPTY) {
                if (empty == -1 && (x>0 && y>0) && matrix[x-1][y-1].role == role) {
                    empty = 0;
                    continue;
                } else {
                    break;
                }
            }

            if (p.role == role) {
                secondCount++;
                if (empty != -1) 
                    empty++;
                continue;
            } else {
                block++;
                break;
            }
        }
        count+=secondCount;
        b.scoreCache[role][3][px][py] = countToScore(count, block, empty);
        
        result += b.scoreCache[role][3][px][py];

        return result;

    }

    private static int countToScore(int count, int block, int empty) {
        if (empty <= 0) {
            if (count >= 5) 
                return Position.FIVE;
            if (block == 0) {
                switch(count) {
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
        } else if (empty == 1 || empty == count -1) {
            // 
            if (count >= 6) {
                return Position.FIVE;
            }
            if (block == 0) {
                switch(count) {
                    case 2: return Position.TWO/2;
                    case 3: return Position.THREE;
                    case 4: return Position.BLOCKED_FOUR;
                    case 5: return Position.FOUR;
                    default: break;
                }
            }

            if (block == 1) {
                switch(count) {
                    case 2: return Position.BLOCKED_TWO;
                    case 3: return Position.BLOCKED_THREE;
                    case 4: return Position.BLOCKED_FOUR;
                    case 5: return Position.BLOCKED_FOUR;
                    default: break;
                }
            }
        } else if (empty == 2 || empty == count-2) {
            if (count >= 7) {
                return Position.FIVE;
            }

            if (block == 0) {
                switch(count) {
                    case 3: return Position.THREE;
                    case 4: 
                    case 5: return Position.BLOCKED_FOUR;
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
                    case 6: return Position.BLOCKED_FOUR;
                    case 7: return Position.FOUR;
                    default: break;
                }
            }

            if (block == 1) {
                switch(count) {
                    case 4:
                    case 5:
                    case 6: return Position.BLOCKED_FOUR;
                    case 7: return Position.FOUR;
                    default: break;
                }
            }

            if (block == 2) {
                switch(count) {
                    case 4:
                    case 5:
                    case 6: 
                    case 7: return Position.BLOCKED_FOUR;
                    default: break;
                }
            }
        } else if (empty == 4 || empty == count-4) {
            if (count >= 9) {
                return Position.FIVE;
            }

            if (block == 0) {
                switch(count) {
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
        } else if (empty == 5 || empty == count-5) {
            return Position.FIVE;
        }



        return 0;
    }

}