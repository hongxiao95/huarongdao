package org.hongxiao;

import java.util.*;

/**
 * 本代码的主要贡献者是棋斌和鸿霄 先生
 */
public class HuaRongDao
{
    public static String nameValueString = "〇卒曹关张马黄赵";
    public static final Map<String, Integer> name2Value = new HashMap<>();
    public static final String[] value2Name;

    public static final int DOT_SHAPE = 1;
    public static final int H_SHAPE = 2;
    public static final int V_SHAPE = 3;
    public static final int SQ_SHAPE = 4;

    public static final Map<Integer, int[]> shapeMap = new HashMap<>(4);
    public static final Map<Integer, Integer> personShape = new HashMap<>();

    public static final int ROW = 5;
    public static final int COL = 4;

    static {
        value2Name =  nameValueString.split("");
        for(int i = 0; i < value2Name.length; i++){
            name2Value.put(value2Name[i], i);
        }
        shapeMap.put(DOT_SHAPE, new int[]{1,1});
        shapeMap.put(H_SHAPE, new int[]{1,2});
        shapeMap.put(V_SHAPE, new int[]{2,1});
        shapeMap.put(SQ_SHAPE, new int[]{2,2});
    }

    public static class Qipan{
        public int[][] data = new int[ROW][COL];

        public Qipan(){
            for(int i = 0; i < ROW; i++){
                Arrays.fill(data[i], name2Value.get("〇"));
            }
        }

        public Qipan(Qipan q){
            for(int i = 0; i < ROW; i++){
                for(int j = 0; j < COL; j++){
                    data[i][j] = q.data[i][j];
                }
            }
        }

        @Override
        public int hashCode() {
            int result = 0;
            for(int i = 0; i < ROW; i++){
                for(int j = 0; j < COL; j++){
                    result = result * 31 + data[i][j];
                }
            }
            return result;
        }

        @Override
        public boolean equals(Object o) {
            Qipan q2 = (Qipan)o;
            for(int i = 0; i < ROW; i ++){
                for (int j = 0; j < COL; j++){
                    if(data[i][j] != q2.data[i][j]){
                        return false;
                    }
                }
            }
            return true;
        }

        public boolean isMainUnit(int i, int j){
            int value = data[i][j];
            if(value == name2Value.get("〇")){
                return false;
            }
            if(value == name2Value.get("卒")){
                return true;
            }

            if(i == 0 && j == 0){
                return true;
            }else if(i == 0){
                if(data[i][j - 1] != value){
                    return true;
                }
            }else if(j == 0){
                if(data[i - 1][j] != value){
                    return true;
                }
            }else if (i != 0 && j != 0){
                if(data[i - 1][j] != value && data[i][j - 1]!= value){
                    return true;
                }
            }
            return false;
        }

        private static int[][] dir = {
                {0, -1},
                {0, 1},
                {1, 0},
                {-1, 0}
        };

        private boolean isLegalPosition(int i, int j){
            if(i < 0 || j < 0 || i >= ROW || j >=COL){return false;}
            return data[i][j] == 0;
        }

        public void delUnit(int i, int j){
            int[] shape = shapeMap.get(personShape.get(data[i][j]));
            for(int x = 0; x < shape[0]; x++){
                for(int y = 0; y < shape[1]; y++){
                    data[i + x][j + y] = 0;
                }
            }
        }

        public boolean putUnit(int i, int j, int value){
            int[] shape = shapeMap.get(personShape.get(value));
            for(int x = 0; x < shape[0]; x++){
                for(int y = 0; y < shape[1]; y++){
                    if(!isLegalPosition(i + x, j + y)){return false;}
                    data[i + x][j + y] = value;
                }
            }
            return true;
        }

        public Qipan tryMove(int i, int j, int dirIndex){
            if(!isMainUnit(i, j)){return null;}
            Qipan qipan = new Qipan(this);
            qipan.delUnit(i, j);
            if(qipan.putUnit(i + dir[dirIndex][0], j + dir[dirIndex][1], data[i][j])){
                return qipan;
            }
            return null;
        }

        public void output(){
            for(int i = 0; i < ROW; i++){
                for(int j = 0; j < COL; j++){
                    if(data[i][j] >= 0){
                        System.out.print(value2Name[data[i][j]]);
                    }else{
                        System.out.print("旧");
                    }
                }
                System.out.println();
            }
            System.out.println();
        }

        private Map<Qipan, Qipan> stateMap = new HashMap<>();

        public void outSteps(Qipan state, int depth){
            Qipan father = stateMap.get(state);
            if(father == null){
                System.out.println(depth);
                return;
            }
            outSteps(father, depth + 1);
            Qipan before = new Qipan(father);
            Qipan now = new Qipan(state);
/*
            for(int i =0; i < ROW; i++){
                for(int j = 0; j < COL; j++){
                    now.data[i][j] -= before.data[i][j];
                }
            }*/
            System.out.println(depth);
            now.output();
            glscanner.nextLine();
        }

        public static Scanner glscanner = new Scanner(System.in);

        public static int[][] stringToStates(String desc){
            if(desc.length() != ROW*COL){return null;}
            String[] persons = desc.split("");
            int[][] initdata = new int[ROW][COL];
            for(int i = 0; i < ROW; i++){
                for(int j = 0; j < COL; j++){
                    initdata[i][j] = name2Value.get(persons[i*COL + j]);
                }
            }
            return initdata;
        }

        public boolean escape(){
            return data[3][1] == name2Value.get("曹") && isMainUnit(3,1);
        }

        public void go(){
            Queue<Qipan> queue = new LinkedList<>();
            Scanner scanner = new Scanner(System.in);
            personShape.put(name2Value.get("卒"), DOT_SHAPE);
            personShape.put(name2Value.get("曹"), SQ_SHAPE);
            personShape.put(name2Value.get("关"), V_SHAPE);
            personShape.put(name2Value.get("张"), V_SHAPE);
            personShape.put(name2Value.get("马"), V_SHAPE);
            personShape.put(name2Value.get("黄"), V_SHAPE);
            personShape.put(name2Value.get("赵"), V_SHAPE);
            System.out.println("请连续输入棋盘配置，人物用单字表示，空格用空表示，如张曹槽马空");
            String model = scanner.nextLine().trim().replace("空","〇").replace(" ","");
            System.out.println("卒默认为单点，曹操默认2x2");
            String hPersons[] = model.split("");
            for(int i = 1; i < hPersons.length; i++){
                if(hPersons[i].equals(hPersons[i - 1]) && hPersons[i].matches("[关张马黄赵]")){
                    personShape.put(name2Value.get(hPersons[i]), H_SHAPE);
                }
            }



            Qipan initState = new Qipan();
            initState.data = stringToStates(model);
            queue.offer(initState);

            stateMap.put(initState, null);
            int totalStep = 1;

            while(queue.size() > 0){
                Qipan begin = queue.poll();
                for(int i = 0; i < ROW; i++){
                    for(int j = 0; j < COL; j++){
                        for(int d = 0; d < 4; d++){
                            Qipan nextState = begin.tryMove(i, j, d);
                            if(nextState != null){

                                if(nextState.escape()){
                                    stateMap.put(nextState, begin);
                                    outSteps(nextState, 0);
                                    System.out.println(totalStep);
                                    System.exit(0);
                                }
                                if(!stateMap.containsKey(nextState)){
                                    queue.offer(nextState);
                                    totalStep++;
                                    if(totalStep % 512 == 0){ System.out.print('.');}
                                    if(totalStep % 16384 == 0){ System.out.println();}
                                    stateMap.put(nextState, begin);
                                }
                            }
                        }
                    }
                }
            }
        }

    }


    public static void main( String[] args )
    {
        Qipan qipan = new Qipan();
        qipan.go();
//        System.out.println("馆长觉得".split("")[2]);
    }
}
