package lr0;

import frame.OutText;

import java.io.PrintStream;
import java.util.*;

public class Grammar {
    private static final char Njump = 'ε';
    private static final char Point = '･';
    Set<String> VN;//非终结符
    Set<String> VT;//终结符集
    Set<Production> P;//规则集
    String S;//开始符
    Set<IE> IESet;//活前缀项目集间的边集
    ArrayList<Set<Production>> IList;//项目集的数组
    ArrayList<Production> PList;//规则集的数组
    String[][] LRTable;
    ArrayList<String> tableHead;
    Boolean isLRO = true;

    public Grammar(Set<String> VN, Set<String> VT, Set<Production> p, String s) {
        this.VN = VN;
        this.VT = VT;
        P = p;
        S = s;
    }

    public Grammar(String[] productions, String S) {
        this.S = "S'";//拓广文法
        VN = new HashSet<>();
        VT = new HashSet<>();
        P = new HashSet<>();
        VN.add(S);
        P.add(new Production(this.S + "->" + S));
        for (int i = 0; i < productions.length; i++) {
            Production p = new Production(productions[i]);
            VN.add(p.getLeft());
            if (p.isSimple()) {
                P.add(p);
                String pr = p.getRight();
                for (int j = 0; j < pr.length(); j++) {
                    VT.add(String.valueOf(pr.charAt(j)));
                }
            } else {
                for (Production sp : p.toSimple()) {
                    P.add(sp);
                    String spr = sp.getRight();
                    for (int j = 0; j < spr.length(); j++) {
                        VT.add(String.valueOf(spr.charAt(j)));
                    }
                }
            }
        }
        VT.removeAll(VN);
        VT.remove(String.valueOf(Njump));

        Set<Production> I0 = new HashSet<>();//活前缀项目集的开始集
        I0.add(new Production(this.S + "->" + Point + S));
        calculationCLOSURE(I0);
        IList = new ArrayList<>();
        IList.add(I0);
        calculationDFA();
        if (!(isLRO = createLRTable())) {
            System.out.println("NO LR(0)!");
        }

    }

    //对项目集I进行闭包运算
    private void calculationCLOSURE(Set<Production> I) {
        Set<String> nV = new HashSet<>();//点后面的非终结符
        int ISize;
        do {
            ISize = I.size();
            for (Production i : I) {
                String iRight = i.right;
                int Di = iRight.indexOf(Point);
                //截取 . 后面的非终结符
                if (Di + 1 < iRight.length()) {
                    String inV = iRight.substring(Di + 1, Di + 2);
                    if (VN.contains(inV)) {
                        nV.add(inV);
                    }
                }
            }
            for (Production ip : P) {
                if (nV.contains(ip.left)) {
                    I.add(ip.insertDian());//加点
                }
            }
        } while (ISize != I.size());
    }

    //求活前缀DFA,得到边集、项目集
    private void calculationDFA() {
        IESet = new HashSet<>();//存放DFA的边
        Queue<Set<Production>> queue = new LinkedList<>();
        queue.add(IList.get(0));
        Set<Production> nI;
        Map<String, Set<Production>> nIMap;

        while (!queue.isEmpty()) {
            Set<Production> iI = queue.poll();
            nIMap = new HashMap<>();
            //求转换
            for (Production i : iI) {
                String iRight = i.right;
                int Di = iRight.indexOf(Point);
                if (Di + 1 < iRight.length()) {
                    String iV = iRight.substring(Di + 1, Di + 2);
                    nI = nIMap.get(iV);
                    if (nI == null) {
                        nI = new HashSet<>();
                        nI.add(i.moveDian());//移点
                        nIMap.put(iV, nI);
                    } else {
                        nI.add(i.moveDian());
                    }
                }
            }
            //求闭包
            int iList = IList.indexOf(iI);
            for (String v : nIMap.keySet()) {
                nI = nIMap.get(v);
                calculationCLOSURE(nI);
                int jList = IList.indexOf(nI);
                if (jList == -1) {
                    queue.add(nI);
                    IList.add(nI);
                    jList = IList.size() - 1;
                }
                //加入边集
                IESet.add(new IE(iList, v, jList));
            }
            //清空
            nIMap.clear();
        }
    }
    public String format(int len,String str){
        if(str.length() == len)return str;
        String temp = str;
        for (int i = str.length(); i <= len; i++) {
            temp+=" ";
        }
        return temp;
    }

    public void out(OutText outText) {
        outText.append("非终结符集 VN:{ ");
        for (Object iVN : VN) {
            outText.append(iVN.toString()+" ");
        }
        outText.append("}\r\n");
        outText.append("终结符集 VT:{ ");

        for (Object iVT : VT) {
            outText.append(iVT.toString()+" ");

        }
        outText.append("}\r\n");
        outText.append("产生式集 P:{\r\n");
        for (Object ip : PList) {
            outText.append(ip.toString()+" \r\n");
        }
        outText.append("}\r\n");

        outText.append("开始符 S: "+S+"\r\n");
        outText.append("DFA:\r\n\n"+IESet);
        PrintStream ps = null;
        ps = new PrintStream(System.out) {
            public void println(String x) {
                outText.append(x + "\r\n");
            }
            public PrintStream printf(String format, Object ... args){
                outText.append(String.format(format,args));
                return null;
            }
            public void print(String s) {
                outText.append(s);
            }
            public void println(int x){
                outText.append(x+"\r\n");
            }
        };
        System.setOut(ps);
        outText.append("\r\n\n\n");

        outText.append("LR0表:");
        outText.append("\r\n");
        outText.append(format(80, " "));
        for (String i : tableHead) {
            outText.append(format(10, i));
        }
        outText.append("\r\n");

        for (int i = 0; i < LRTable.length; i++) {
            outText.append(format(80, "S" + i + IList.get(i).toString()));
            outText.append("\r\n");
            outText.append(format(80, " "));

            for (int j = 0; j < LRTable[0].length; j++) {
                if (LRTable[i][j] != null) {
                    outText.append(format(10, LRTable[i][j]));
                }

                else
                {
                    outText.append(format(10, " "));

                }
            }
            outText.append("\r\n");
            //System.out.print("\n");
        }
    }

    //构造LR0分析表
    public boolean createLRTable() {
        PList = new ArrayList<>();
        PList.addAll(P);
        tableHead = new ArrayList<>();
        tableHead.addAll(VT);
        tableHead.add("#");
        tableHead.addAll(VN);
        tableHead.remove(S);


//        ArrayList<Set<Production>> IList;//项目集的数组
        LRTable = new String[IList.size()][tableHead.size()];
        //构造GO表
        int go[][] = new int[IList.size()][tableHead.size()];
        for (IE ie : IESet) {
            int ivalue = tableHead.indexOf(ie.getValue());
            int k = ie.getOrgin();
            if (go[k][ivalue] == 0)
                go[k][ivalue] = ie.getAim();
            else
                return false;       //说明一个表内有重复值  也就是说识别一个字符到达两个状态
        }
        //填Action表
        for (int k = 0; k < IList.size(); k++) {
            for (Production ip : IList.get(k)) {
                String right = ip.getRight();
                int iD = right.indexOf(Point);
                if (iD < right.length() - 1) { //A->α･aβ GO(Ik,a)=Ij
                    String a = right.substring(iD + 1, iD + 2);
                    if (VT.contains(a)) {//a为终结符
                        int ia = tableHead.indexOf(a);
                        if (LRTable[k][ia] == null)
                            LRTable[k][ia] = "S" + go[k][ia];
                        else
                            return false;//有重复值 出错
                    }
                } else {//A->α･
                    if (ip.getLeft().equals(S)) {
                        if (LRTable[k][VT.size()] == null)
                            LRTable[k][VT.size()] = "acc";
                        else
                            return false;
                    } else {
                        for (int ia = 0; ia < VT.size() + 1; ia++) {

                            if (LRTable[k][ia] == null)
                                LRTable[k][ia] = "r" + PList.indexOf(ip.deleteDian());
                            else
                                return false;
                        }
                    }
                }
            }
        }


        //合并表
        for (int j = VT.size(); j < tableHead.size(); j++) {
            for (int k = 0; k < IList.size(); k++) {
                if (go[k][j] != 0) {
                    if (LRTable[k][j] == null)
                        LRTable[k][j] = "" + go[k][j];
                    else
                        return false;
                }
            }
        }
        return true;
    }

    //判断st是否符合文法，LR0分析器
    public boolean contains(String st) throws Exception {
        if (isLRO) {
            st += "#";
            Stack<Integer> stateStack = new Stack<>();
            Stack<String> signStack = new Stack<>();
            stateStack.push(0);
            signStack.push("#");
            Production p;
            int VTL = VT.size();
            int bz = 0;//步骤数
            for (int i = 0; i < st.length(); i++) {
                if (true) {//显示分析过程
                    System.out.println(++bz);
                    System.out.print("状态栈：");
                    System.out.println(stateStack);
                    System.out.println(" ");
                    System.out.print("符号栈：");
                    System.out.println(signStack);
                    System.out.println(" ");
                    System.out.print("输入串：");
                    System.out.println(st.substring(i));
                    System.out.println(" ");

                }
                String a = st.substring(i, i + 1);
                int ai = tableHead.indexOf(a);
                String ag = LRTable[stateStack.peek()][ai];
                if (ag == null) return false;
                else if (ag.equals("acc")) {
                    return true;
                }
                if (ai < VT.size() + 1) {//action
                    int nub = Integer.valueOf(ag.substring(1));
                    if (ag.charAt(0) == 'S') {
                        stateStack.push(nub);
                        signStack.push(a);
                    } else {//r
                        p = PList.get(nub);
                        int k = p.getRight().length();
                        if (!p.getRight().equals(String.valueOf(Njump))) {//排除归约为 A->ε
                            while (k-- > 0) {
                                stateStack.pop();
                                signStack.pop();
                            }
                        }
                        //goto
                        String go = LRTable[stateStack.peek()][tableHead.indexOf(p.getLeft())];
                        if (go == null) return false;
                        stateStack.push(Integer.valueOf(go));
                        signStack.push(p.getLeft());
                        i--;
                    }
                }
            }
            return false;
        }
        throw new Exception("无法判断：该文法不是LR(0)文法！");

    }
}
