package main.chess;

public class EvaluatePoint {

    static int result = 0;
    static int empty = 0;
    static int radius = 8;
    static int count = 0;
    static int block = 0;
    static int secondCount = 0;


    public static int scorePoint(GameModel model, int px, int py, int role) {

        Position[][] matrix = model.matrix;
        int len = matrix.length;
  
         //横向
        count = 1;  //默认把当前位置当做己方棋子。因为算的是当前下了一个己方棋子后的分数
        block = 0;
        empty = 0;

        // 从右边开始计算
        for (int i=py+1; true; i++) {
            if (i>=len) {
                block++;
                break;
            }

            Position p = matrix[px][i];
            if (p.role == Position.EMPTY) {
                if (empty == 0 && i < len-1 && matrix[px][i+1].role == role) {
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
                if (empty == 0 && i > 0 && matrix[px][i-1].role == role) {
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

        result += score(count, block, empty);

        //列计算
        count = 1;
        block = 0;
        empty = 0;

        //列的右边
        for (int i=px+1; true; i++) {
            if (i>=len) {
                block++;
                break;
            }

            Position p = matrix[i][px];
            if (p.role == Position.EMPTY) {
                if (empty == 0 && i<len-1 && matrix[i+1][py].role == role) {
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

        // 列的右边计算
        for (int i=px-1; true; i--) {
            if (i<0) {
                block++;
                break;
            }

            Position p = matrix[i][py];
            if (p.role == Position.EMPTY) {
                if (empty == 0 && i>0 && matrix[i-1][py].role == role) {
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

        //计算这个列的得分+行的得分
        result += score(count, block, empty);


        // 计算撇的得分
        // \\
        count = 1;
        block = 0;
        empty = 0;

        // 类似上面，撇的上半部分
        for(int i=1;true;i++) {
            int x = px+i;
            int y = py+i;
            if(x>=len || y>=len) {
                block ++;
                break;
            }
            Position p = matrix[x][y];
            if(p.role == Position.EMPTY) {
                if(empty == 0 && (x<len-1 && y < len-1) && matrix[x+1][y+1].role == role) {
                    empty = count;
                    continue;
                } else {
                    break;
                }
            }
            if(p.role == role) {
                count++;
                continue;
            } else {
                block ++;
                break;
            }
        }


        // 撇的下半部分
        for(int i=1;true;i++) {
            int x = px-i;
            int y = py-i;
            if(x<0 || y<0) {
                block++;
                break;
            }
            Position p = matrix[x][y];
            if(p.role == Position.EMPTY) {
                if(empty == 0 && (x>0 && y>0) && matrix[x-1][y-1].role == role) {
                    empty = count;
                    continue;
                } else {
                    break;
                }
            }

            if(p.role == role) {
                count ++;
                continue;
            } else {
                block ++;
                break;
            }
        }

         // 将结果合并
        result += score(count, block, empty);


        // 计算捺的得分
        // \/
        count = 1;
        block = 0;
        empty = 0;

        // 捺的下半部分
        for(int i=1; true;i++) {
            int x = px+i;
            int y = py-i;
            if(x<0 || y<0 || x>=len || y>=len) {
                block ++;
                break;
            }
            Position p = matrix[x][y];
            if(p.role == Position.EMPTY) {
                if(empty == 0 && (x<len-1 && y<len-1 && y > 0) && matrix[x+1][y-1].role == role) {
                    empty = count;
                    continue;
                } else {
                    break;
                }
            }
            if(p.role == role) {
                count++;
                continue;
            } else {
                block++;
                break;
            }
        }

        // 捺的下半部分
        for(int i=1;true;i++) {
            int x = px-i;
            int y = py+i;
            if(x<0 || y<0 || x>=len || y>=len) {
                block ++;
                break;
            }
            Position p = matrix[x][y];
            if(p.role == Position.EMPTY) {
                if(empty == 0 && (x>0 && y>0 && y+1 < len) && matrix[x-1][y+1].role == role) {
                    empty = count;
                    continue;
                } else {
                    break;
                }
            }
            if(p.role == role) {
                count ++;
                continue;
            } else {
                block ++;
                break;
            }
        }

        // 加上得分
        result += score(count, block, empty);

        return result;

    }

    private static int score(int count, int block, int empty) {
		if (empty == 0) {
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
			
		} else if (empty == 1 || empty == count - 1) {
			//
			if(count >= 6) {
				return Position.FIVE;
			}
			if(block == 0) {
				switch(count) {
					case 2: return Position.TWO;
					case 3:
					case 4: return Position.THREE;
					case 5: return Position.FOUR;
					default: break;
				}
            }
            
            if (block == 1) {
                switch(count) {
                    case 2: return Position.BLOCKED_TWO;
                    case 3: return Position.BLOCKED_THREE;
                    case 4: return Position.THREE;
                    case 5: return Position.BLOCKED_FOUR;
                    default: break;
                }
            }
		} else if (empty == 2 || empty == count - 2) {
			if (count >= 7) {
				return Position.FIVE;
			}
			if (block == 0) {
				switch(count) {
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
					case 4: return Position.BLOCKED_THREE;
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
					case 6: return Position.THREE * 2;
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
		} else if(empty == 4 || empty == count - 4) {
			if (count >= 9) {
				return Position.FIVE;
			}
			if (block == 0) {
				switch(count) {
					case 5:
					case 6:
					case 7:
					case 8: return Position.FOUR;
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
		} else if (empty == 5 || empty == count-5) {
			return Position.FIVE;
		}

		return 0;
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