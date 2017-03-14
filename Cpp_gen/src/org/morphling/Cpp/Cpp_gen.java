/*
* A translator to translate the parse result into C++.
* Author : Lixue Zhang
*/

package org.morphling.Cpp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;  
  
public class Cpp_gen {
    
    public static void main(String[] args) { 
        ReadAndPreprocessing r = new ReadAndPreprocessing(args[0]);
        String filename = args[0].split("\\.")[0]+".cpp";
        new Parser(r.commands, filename);
        /*ReadAndPreprocessing r = new ReadAndPreprocessing("./testcase");
        String filename = "mytestcase.txt";
        JSONArray ja = new JSONArray(r.commands);
        for(int i=0;i<ja.length();++i)
            System.out.println(ja.get(i));
        new Parser(r.commands, filename);*/
    }  
}  

class ReadAndPreprocessing
{
    public String commands;

    public ReadAndPreprocessing(String fileName) {
        commands = "";
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
}

class Parser
{
    String cmg;
    File file;
    FileWriter fw;
    public Parser(String command_group, String filename)
    {
        cmg = command_group;
        try {  
                file = new File(filename);
                fw = new FileWriter(file);
                fw.write("#include <iostream>\nusing namespace::std;\n\n");
                fw.write("int main(int argc, char *argv[]) {\n");
                translate(cmg, 1);
                fw.write("\treturn 0;\n}\n");
                fw.close();
            } catch (IOException e) {  
                e.printStackTrace();  
       }
    }
    private void translate(String obj, int tab) throws JSONException, IOException
    {
        JSONArray job = new JSONArray(obj);
        for(int i=0;i<job.length();++i)
        {
            JSONObject temp = job.getJSONObject(i);
            if(temp.get("sentence").equals("declare"))
                declare(temp, tab);
            else if(temp.get("sentence").equals("declare_init"))
                declare_init(temp, tab);
            else if(temp.get("sentence").equals("read"))
                read(temp, tab);
            else if(temp.get("sentence").equals("write"))
                write(temp, tab);
            else if(temp.get("sentence").equals("if"))
                ifelse(temp, tab);
            else if(temp.get("sentence").equals("while"))
                WHILE(temp, tab);
            else if(temp.get("sentence").equals("expr"))
                expr(temp);
            else if(temp.get("sentence").equals("assign"))
                assign(temp, tab);
            else if(temp.get("sentence").equals("break"))
                Break(temp, tab);
            else if(temp.get("sentence").equals("continue"))
                Continue(temp, tab);
        }
    }
    
    private void declare(JSONObject obj, int tab) throws JSONException, IOException
    {
        indention(tab);
        fw.write(obj.get("type") + " " + obj.get("name") + ";\n");
    }
    
    private void declare_init(JSONObject obj, int tab) throws JSONException, IOException
    {
        indention(tab);
        fw.write(obj.get("type") + " " + obj.get("name") + " = ");
        expr(obj.getJSONObject("expr"));
        fw.write(";\n");
    }
    
    private void read(JSONObject obj, int tab) throws JSONException, IOException
    {
        indention(tab);
        fw.write("cin<<"+obj.get("name")+";\n");
    }
    
    private void write(JSONObject obj, int tab) throws IOException
    {
        indention(tab);
        fw.write("cout<<");
        expr(obj.getJSONObject("expr"));
        fw.write("<<endl;\n");
    }
    
    private void expr(JSONObject obj) throws JSONException, IOException
    {
        if(!obj.has("operator"))
        {
            fw.write(obj.getString("value"));
            return;
        }
        String key = obj.get("operator").toString();
        if(key.equals("cast"))
        {
            fw.write("("+obj.get("e1").toString()+")");
            expr(obj.getJSONObject("e2"));
            return;
        }
        JSONObject e1 = null;
        JSONObject e2 = null;
        if(obj.has("e1"))
            e1 = obj.getJSONObject("e1");
        if(obj.has("e2"))
            e2 = obj.getJSONObject("e2");
        fw.write("(");
        if(key.equals("-") && !obj.has("e2"))
        {
            fw.write("-");
            expr(e1);
            fw.write(")");
            return;
        }
        expr(e1);
        if(key.equals("**"))
            fw.write("^");
        else
            fw.write(" "+key+" ");
        expr(e2);
        fw.write(")");
    }
    
    private void ifelse(JSONObject obj, int tab) throws IOException
    {
        indention(tab);
        fw.write("if");
        JSONArray branches = new JSONArray(obj.getJSONArray("cases").toString());
        for(int i=0;i<branches.length();++i)
        {
            expr(branches.getJSONObject(i).getJSONObject("condition"));
            fw.write("{\n");
            translate(branches.getJSONObject(i).getJSONArray("block").toString(), tab+1);
            indention(tab);
            fw.write("}\n");
            if(i<(branches.length()-1))
            {
                JSONObject temp = branches.getJSONObject(i+1).getJSONObject("condition");
                if(!temp.has("e1"))
                {
                    indention(tab);
                    fw.write("else {\n");
                    translate(branches.getJSONObject(i+1).getJSONArray("block").toString(), tab+1);
                    System.out.println("}");
                    indention(tab);
                    fw.write("}\n");
                    break;
                }
                else
                {
                    indention(tab);
                    fw.write("else if");
                }
            }
        }
    }
    
    private void WHILE(JSONObject obj, int tab) throws IOException
    {
        indention(tab);
        fw.write("while(");
        expr(obj.getJSONObject("expr"));
        fw.write(")");
        fw.write("{\n");
        translate(obj.getJSONArray("block").toString(), tab+1);
        indention(tab);
        fw.write("}\n");
    }
    
    private void assign(JSONObject obj, int tab) throws IOException
    {
        indention(tab);
        fw.write(obj.get("name")+" = ");
        expr(obj.getJSONObject("expr"));
        fw.write(";\n");
    }

    private void Break(JSONObject obj, int tab) throws IOException
    {
        indention(tab);
        fw.write("break;\n");
    }

    private void Continue(JSONObject obj, int tab) throws IOException
    {
        indention(tab);
        fw.write("continue;\n");
    }
    
    void indention(int tab) throws IOException
    {
        for(int i=0;i<tab;++i)
            fw.write("\t");
    }
}

