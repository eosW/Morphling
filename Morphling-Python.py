import json

def gencode(astname,codename):
    with open(codename,'w') as target: 
        with open(astname) as source:
            root = json.load(source)
        genblock(root,target,0)
        target.write('\n')

def expreval(expr):
    if "value" in expr:
        value = expr['value']
        return value
    else:
        operator = expr['operator']
        e1 = expr['e1']
        if "e2" in expr:
            e2 = expr['e2']
            if operator=='cast':
                return "({:s}) ({:s})".format({'int':'int','float':'float','bool':'bool'}[e1],expreval(e2))
            return "({:s}) {:s} ({:s})".format(expreval(e1),operator,expreval(e2))
        return "{:s} ({:s})".format(operator, expreval(e1))

def genblock(block,target,indent):
    indentstr = ''.join([' ']*(4*indent))
    for sentence in block:
        stype = sentence['sentence']
        if stype == "declare_init":
            name = sentence['name']
            expr = sentence['expr']
            target.write("{:s}{:s} = {:s}\n".format(indentstr,name,expreval(expr)))
        if stype == "assign":
            name = sentence['name']
            expr = sentence['expr']
            target.write("{:s}{:s} = {:s}\n".format(indentstr,name,expreval(expr)))
        if stype == "break":
            target.write("{:s}break\n".format(indentstr))
        if stype == "continue":
            target.write("{:s}continue\n".format(indentstr))
        if stype == "read":
            dtype = sentence['type']
            name = sentence['name']
            target.write("{:s}{:s} = {:s}(input())\n".format(indentstr,name,{'int':'int','float':'float','bool':'bool'}[dtype]))
        if stype == "write":
            expr = sentence['expr']
            target.write("{:s}print ({:s})\n".format(indentstr,expreval(expr)))
        if stype == "if":
            cases = sentence['cases']
            for i in range(len(cases)):
                condition = cases[i]['condition']
                subblock = cases[i]['block']
                if i == 0:
                    target.write("{:s}if {:s}:\n".format(indentstr,expreval(condition)))
                    genblock(subblock,target,indent+1)
                elif i > 0 and i < len(cases)-1:
                    target.write("{:s}elif {:s}:\n".format(indentstr,expreval(condition)))
                    genblock(subblock,target,indent+1)
                else:
                    target.write("{:s}else:\n".format(indentstr))
                    genblock(subblock,target,indent+1)
        if stype == "while":
            expr = sentence['expr']
            subblock = sentence['block']
            target.write("{:s}while {:s}: \n".format(indentstr,expreval(expr)))
            genblock(subblock,target,indent+1)

gencode("testcase2.ast","testcase2.py")

