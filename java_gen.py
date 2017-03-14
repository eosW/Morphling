import json

import sys


def genfile(infilename,outfilename):
    with open(outfilename,'wb') as target:
        with open(infilename) as source:
            root = json.load(source)
        target.write("import java.util.Scanner;\n")
        target.write("public class {:s} {{\n".format(outfilename[:-5]))
        target.write("    public static void main(String args[]){\n")
        target.write("        Scanner in = new Scanner(System.in);\n")
        genblock(root,target,2)
        target.write("    }\n")
        target.write("}\n")

def genblock(block,target,ind):
    indstr = "".join([" "]*(4*ind))
    for sentence in block:
        stype = sentence['sentence']
        if stype == "declare":
            dtype = sentence['type']
            name = sentence['name']
            target.write("{:s}{:s} {:s};\n".format(indstr,{'int':'int','float':'double','bool':'boolean'}[dtype],name))
        if stype == "declare_init":
            dtype = sentence['type']
            name = sentence['name']
            expr = sentence['expr']
            target.write("{:s}{:s} {:s} = {:s};\n".format(indstr,{'int':'int','float':'double','bool':'boolean'}[dtype],name,expreval(expr)))
        if stype == "assign":
            name = sentence['name']
            expr = sentence['expr']
            target.write("{:s}{:s} = {:s};\n".format(indstr,name,expreval(expr)))
        if stype == "break":
            target.write("{:s}break;\n".format(indstr))
        if stype == "continue":
            target.write("{:s}continue;\n".format(indstr))
        if stype == "read":
            dtype = sentence['type']
            name = sentence['name']
            target.write("{:s}{:s} = in.next{:s}();\n".format(indstr,name,{'int':'Int','float':'Double','bool':'Boolean'}[dtype]))
        if stype == "write":
            expr = sentence['expr']
            target.write("{:s}System.out.println({:s});\n".format(indstr,expreval(expr)))
        if stype == "if":
            cases = sentence['cases']
            for case in cases:
                condition = case['condition']
                subblock = case['block']
                target.write("{:s}if({:s}) {{\n".format(indstr,expreval(condition)))
                genblock(subblock,target,ind+1)
                target.write("{:s}}} else\n".format(indstr))
            target.write("{:s}{{}}\n".format(indstr))
        if stype == "while":
            expr = sentence['expr']
            subblock = sentence['block']
            target.write("{:s}while({:s}) {{\n".format(indstr,expreval(expr)))
            genblock(subblock,target,ind+1)
            target.write("{:s}}}\n".format(indstr))


def expreval(expr):
    if "value" in expr:
        value = expr['value']
        return value
    operator = expr['operator']
    e1 = expr['e1']
    if "e2" in expr:
        e2 = expr['e2']
        if operator=='cast':
            return "({:s}) ({:s})".format({'int':'int','float':'double','bool':'boolean'}[e1],expreval(e2))
        if operator=='**':
            return "Math.pow({:s},{:s})".format(expreval(e1),expreval(e2))
        return "({:s}) {:s} ({:s})".format(expreval(e1),operator,expreval(e2))
    return "{:s} ({:s})".format(operator, expreval(e1))


if __name__ == "__main__":
    args = sys.argv
    filein = args[1]
    genfile(filein,filein[:-4]+".java")