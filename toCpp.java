/*
* A translator to translate the parse result into C++.
* Author : Lixue Zhang
*/
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.StringBuffer;
import java.io.File;
import java.util.ArrayList;

public class toCpp
{
	public static void main(String argv[])
	{
        System.out.println("\n#include <iostream>\nusing namespace::std;\nint main(int argc, char *argv[])\n{");
        ReadAndPreprocessing p = new ReadAndPreprocessing("[('declare', 'float', 'a'), ('assign', 'a', ('+', ('*', ('FLOAT', '4e4'), ('INT', '7')), ('-', ('**', ('FLOAT', '.5'), ('INT', '7'))))), ('declare_init', 'int', 'b', ('^', ('INT', '5'), ('<<', ('INT', '7'), ('INT', '4'))))][('while', ('>', ('NAME', 'a'), ('INT', '5')), [('assign', 'a', ('+', ('INT', '7'), ('INT', '9'))), ('assign', 'b', ('BOOL', 'false')), ('break',)])][('if', [(('>', ('NAME', 'a'), ('INT', '3')), [('assign', 'a', ('INT', '6'))]), (('>', ('NAME', 'a'), ('INT', '6')), [('assign', 'a', ('INT', '7'))]), (('<', ('NAME', 'a'), ('INT', '0')), [('assign', 'a', ('INT', '1'))]), ('true', [('assign', 'a', ('INT', '9'))])])]");
        p.commandParse();
        System.out.println("\treturn 0;\n}\n");
	}
}

// http://www.cnblogs.com/lovebread/archive/2009/11/23/1609122.html

class ReadAndPreprocessing
{
	String commands;
	public ReadAndPreprocessing(String com)
	{
        String ary[] = com.split(" ");
        commands = "";
        for(int i=0;i<ary.length;++i)
            commands += ary[i];
        //System.out.println(commands);
	}

    public void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                commands = commands + (tempString + "\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
            	}
            }
        }
    }

    public void commandParse()
    {
        new Parse().blockPar(commands, 1);
    }

    public void showCommands()
    {
    	System.out.println(commands);
    }
}

class Parse
{
    //final enum name {""};
    int counter;
    public Parse()
    {
        counter = 0;
    }
    // toBlocks: try to cut string into pieces
    // according to block identifier [] [] []
    public void blockPar(String s, int table)
    {
        if(s.length()==0 || s.charAt(0)!='[') return;
        int left = 0;
        int right = 1;
        int match = 1;
        do{
            while(right<s.length() && match!=0)
            {
                if(s.charAt(right)=='[')
                    match++;
                else if(s.charAt(right)==']')
                    match--;
                right++;
            } 
            //System.out.println("while = "+s.substring(left+1, right-1));
            sentencePar(s.substring(left+1, right-1), table);
            left = right;
            right++;
            match = 1;
        }while(left<s.length() && s.charAt(left)=='[');
    }

    // parse sentences recursively.
    public void sentencePar(String s, int table)
    {
        if(s.length()==0 || s.charAt(0)!='(') return;
        int left = 0;
        int right = findParenthesis(s);
        // delete the external "(" ")" pairs.

        //System.out.println("counter = "+counter);
        //counter++;
        //System.out.println(s.substring(left+1, right-1));
        dispatchAssignment(s.substring(left+1, right-1), table);
        right++;
        if(right>=s.length()) return;
        if(s.charAt(right)=='(')
            this.sentencePar(s.substring(right), table);
        else
            this.blockPar(s.substring(right), table);
    }

    private void dispatchAssignment(String s, int table)
    {
        //'declare', 'float', 'a'
        String keyWord[] = s.split("'");
        if(keyWord[1].equals("declare"))
        {
            declarePar(s, table);
        }
        else if(keyWord[1].equals("declare_init"))
        {
            declareInitPar(s, table);
        }
        else if(keyWord[1].equals("assign"))
        {
            assginPar(s, table);
        }    
        else if(keyWord[1].equals("if"))
        {
            ifelsePar(s, table);
        }
        else if(keyWord[1].equals("while"))
        {
            whilePar(s, table);
        }            
        else if(keyWord[1].equals("break"))
        {
            breakPar(s, table);
        }
        else if(keyWord[1].equals("continue"))
        {
            continuePar(s, table);
        }
        else
        {
            exprPar(s, table);
        }
    }

    private void exprPar(String s, int table)
    {
        // type 1: 'FLOAT/INT/BOOL/', '4e4'
        // type 2: '+-*/', ('INT', '7'), ('INT', '9')
        /*
            'INT','FLOAT','BOOL','+','-','*','/','%','<<','>>','^'
            ,'||','&&','!','==','!=','>','>=','<','<='
        */
        // a   possible operation(+ - * / % << >> && || ! == != > <)  b
        // possible operation(INT FLOAT BOOL) a
        String segments[] = s.split("'");
        String keyword = segments[1];
        if(keyword.equals("FLOAT") || keyword.equals("INT") || keyword.equals("BOOL") || keyword.equals("NAME"))
        {
            System.out.print(segments[3]);
        }
        else
        {
            // step 1: output : "("
            System.out.print("(");
            // step 2: find the left expression.
            int left = 0;
            for(int i=0;i<s.length();++i)
            {
                if(s.charAt(i)=='(')
                {
                    left = i;
                    break;
                }
            }
            int right = left+1;
            int match = 1;
            while(right<s.length() && match!=0)
            {
                if(s.charAt(right)=='(')
                    match++;
                else if(s.charAt(right)==')')
                    match--;
                right++;
            }
            if(keyword.equals("-") && (right+2)>=s.length())
            {
                System.out.print(keyword);
                dispatchAssignment(s.substring(left+1, right-1), 0);
            }
            else
            {
                dispatchAssignment(s.substring(left+1, right-1), 0);
                System.out.print(" " + keyword + " ");
                dispatchAssignment(s.substring(right+2, s.length()-1), 0);
            }
            // step 3: keep the priority.
            System.out.print(")");
        }
    }

    private void declarePar(String s, int table)
    {
        //('declare', 'float', 'a')
        String segments[] = s.split("'");
        String type = segments[3].toLowerCase();
        String name = segments[segments.length-1];
        printWithEnter(table, type + " " + name + ";");
    }

    private void declareInitPar(String s, int table)
    {
        // 'declare_init', 'int', 'b', ('^', ('INT', '5'), ('<<', ('INT', '7'), ('INT', '4')))
        String segments[] = s.split("'");
        String type = segments[3].toLowerCase();
        String name = segments[5];
        printWithoutEnter(table, type + " " + name + " = ");
        int i;
        for(i=0;i<s.length();++i)
        {
            if(s.charAt(i)=='(')
                break;
        }
        this.sentencePar(s.substring(i), table);
        System.out.println(";");
    }

    private void assginPar(String s, int table)
    {
        // 'assign', 'a', ('+', ('*', ('FLOAT', '4e4'), ('INT', '7')), ('-', ('**', ('FLOAT', '.5'), ('INT', '7'))))
        String segments[] = s.split("'");
        String name = segments[3];
        printWithoutEnter(table, name + " = ");
        int i;
        for(i=0;i<s.length();++i)
        {
            if(s.charAt(i)=='(')
                break;
        }
        this.sentencePar(s.substring(i), 0);
        System.out.println(";");
    }

    private void ifelsePar(String s, int table)
    {
        /*
            if (a>3): a=6; elif (a>6): a=7; elif (a<0): a=1; else: a=9; end;

            'if', [(('>', ('NAME', 'a'), ('INT', '3')), [('assign', 'a', ('INT', '6'))]), 
            (('>', ('NAME', 'a'), ('INT', '6')), [('assign', 'a', ('INT', '7'))]), 
            (('<', ('NAME', 'a'), ('INT', '0')), [('assign', 'a', ('INT', '1'))]), 
            ('true', [('assign', 'a', ('INT', '9'))])]
        */
        String segments[] = s.split("'");
        String name = segments[1];
        printWithoutEnter(table, "if ");
        String subs = s.substring(6, s.length()-1);
        int left = 1;
        int right = 0;
        do
        {
            right = findParenthesis(subs);
            String temp[] = subs.substring(left, right-1).split("\\[");

            // parse conditional expression.
            exprPar(temp[0].substring(1, temp[0].length()-2), 0);
            printWithEnter(table, "{");
            blockPar("[" + temp[1], table+1);
            printWithEnter(table, "}");

            // find next is else if or else
            subs = subs.substring(right + 1);
            String flag = subs.split("'")[1];
            if(flag.equals("true"))
            {
                printWithEnter(table, "else");
                printWithEnter(table, "{");
                subs = subs.substring(8, subs.length());
                blockPar(subs, table+1);
                printWithEnter(table, "}");
                subs = "";
            }
            else 
                printWithoutEnter(table, "else if ");
            left = 1;

        }while(subs.length()>0);
    }

    private void whilePar(String s, int table)
    {
        //'while',
        // ('>', ('NAME', 'a'), ('INT', '5')), 
        // [('assign', 'a', ('+', ('INT', '7'), ('INT', '9'))), ('assign', 'b', ('BOOL', 'false')), ('break',)]
        
        printWithoutEnter(table, "while ");
        String subs = s.substring(8);
        int left = 0;
        int right = findParenthesis(subs);
        exprPar(subs.substring(1, right-1), 0);
        subs = subs.substring(right+1);
        if(subs.length()==0) 
        {
            printWithEnter(0, "{}");
            return;
        }
        printWithEnter(0, " {");
        if(subs.charAt(left)=='[')
            blockPar(subs, table+1);
        else
            exprPar(subs, table + 1);
        printWithEnter(table, "}");
    }

    private void breakPar(String s, int table)
    {
        printWithEnter(table, "break;");
    }

    private void continuePar(String s, int table)
    {
        printWithEnter(table, "continue;");
    }

    private void printWithEnter(int indent, String s)
    {
        for(int i=0;i<indent;++i)
            System.out.print("\t");
        System.out.println(s);
    }

    private void printWithoutEnter(int indent, String s)
    {
        for(int i=0;i<indent;++i)
            System.out.print("\t");
        System.out.print(s);
    }
    private int findParenthesis(String s)
    {
        int left = 0;
        int right = left+1;
        int match = 1;
        while(right<s.length() && match!=0)
        {
            if(s.charAt(right)=='(')
                match++;
            else if(s.charAt(right)==')')
                match--;
            right++;
        }
        return right;
    }
}