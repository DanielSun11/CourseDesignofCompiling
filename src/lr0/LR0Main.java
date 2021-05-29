package lr0;
import frame.OutText;
import frame.ReadText;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;

public class LR0Main {
    private static final char Njump = 'ε';

    public static void run(OutText outText, ReadText readText) {
        String S="E";//开始符
//        String P[]={"E->aA|bB","A->cA|d","B->cB|d"};//规则集
        HashSet<String> productionSet = readFile(outText);
        String P[]=productionSet.toArray(new String[productionSet.size()]);//规则集

        Grammar G=new Grammar(P,S);
        G.out(outText);
        if(G.isLRO == false){
            outText.append("输入文法不是LR0文法！！\r\n");
            return;
        }
        try {
            if(G.contains(readText.getText()) == false){
                outText.append("不符合所给的LR0文法 分析失败！！");
            }
            else{
                outText.append("分析成功！！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static HashSet<String> readFile(OutText outText) {
        BufferedReader br = null;
        outText.append("从文件读入的文法为:"+"\r\n");
        HashSet<String> result = new HashSet<>();
        try {
            String path = System.getProperty("user.dir")+File.separator+"grammer"+File.separator;
            br = new BufferedReader(new FileReader(path+"a5.txt"));
            String s = null;
            while ((s = br.readLine()) != null) {
                outText.append("\t" + s+"\r\n");
                result.add(s.trim());
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
