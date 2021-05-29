package lr0;

import java.util.ArrayList;

import static java.util.Arrays.sort;

public class Production {//语句
    private static final char Point = '･';
    private static final char Njump = 'ε';
    String left;
    String right;

    //left->right
    public Production(String str) {
        String s[] = str.split("->");
        left = s[0];
        right = s[1];
    }
    public Production() { }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public boolean isSimple() {
        if (right.indexOf("|") == -1)
            return true;
        else
            return false;
    }
    //简化产生式
    public ArrayList<Production> toSimple() {
        ArrayList<Production> productionList = new ArrayList<Production>();
        String b[] = right.split("\\|");
        for (int i = 0; i < b.length; i++) {
            // System.out.println(b[i]);
            Production ib = new Production(left + "->" + b[i]);
            productionList.add(ib);
        }
        return productionList;
    }

    public String toString() {
        return left + "->" + right;
    }

    public Production insertDian() {//加点
        Production a;
        if(right.equals(String.valueOf(Njump))){// A->ε 转 A->･
            a=new Production(left + "->" + Point);
        }else{
            a=new Production(left + "->" + Point +right);
        }

        return a;
    }
    public Production deleteDian() {//去除点
        Production a=new Production();
        a.setLeft(left);
        int iD=right.indexOf(Point);
        StringBuffer ab=new StringBuffer(right);
        ab.deleteCharAt(iD);
        if(ab.length()==0){//A->･ 转 A->ε
            ab.append(Njump);
        }
        a.setRight(ab.toString());
        return a;
    }
    public Production moveDian() {//向右移动点
        Production a=new Production();
        a.setLeft(left);
        int iD=right.indexOf(Point);
        StringBuffer ab=new StringBuffer(right);
        ab.deleteCharAt(iD);
        ab.insert(iD+1, Point);
        a.setRight(ab.toString());
        return a;
    }
    @Override
    public boolean equals(Object obj) {
        Production other = (Production) obj;
        if(!left.equals(other.left)){
            return false;
        }
        String[] a = right.split("\\|");
        sort(a);
        String[] b = other.right.split("\\|");
        sort(b);
        if (a.length == b.length) {
            for (int i = 0; i < b.length; i++) {
                if (!a[i].equals(b[i]))
                    return false;
            }
            return true;
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {//不写的话HashSet认为同一“A->B”生成的Production（值）不相等
        int result = 0;
        String b[] = right.split("\\|");
        for (String ib : b) {
            result += ib.hashCode();
        }
        result += left.hashCode() * 31;
        return result;
    }

}
