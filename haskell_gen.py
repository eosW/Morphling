import json,string

import sys

func = []

def genfile(infilename,outfilename):
    with open(outfilename,'wb') as target:
        with open(infilename) as source:
            root = json.load(source)
        target.write("{-#LANGUAGE ScopedTypeVariables, ExistentialQuantification#-}\n")
        target.write("import Utility as U\n")
        target.write("import qualified Data.Map as Map\n")
        target.write("main = do\n")
        target.write('    let vmap0 = Map.singleton "break" (Bool False)\n')
        genblock(root,target,1)
        c = 0
        for fun in func:
            target.write("\n")
            target.write("b{:d} :: Dict -> IO Dict\n".format(c))
            target.write("b{:d} vmap0 = do\n".format(c))
            genblock(fun,target,1)
            c += 1

def genblock(block,target,ind):
    count = 0
    indstr = "".join([" "]*(4*ind))
    for sentence in block:
        stype = sentence['sentence']
        # if stype == "declare":
        if stype == "declare_init" or stype == "assign":
            name = sentence['name']
            expr = sentence['expr']
            target.write('{:s}let vmap{:d} = Map.insert "{:s}" ({:s}) vmap{:d}\n'
                         .format(indstr,count+1,name,expreval(expr,count),count))
            count += 1
        if stype == "break":
            print ("break and continue is not yet supported")
        if stype == "continue":
            print ("break and continue is not yet supported")
        if stype == "read":
            dtype = sentence['type']
            name = sentence['name']
            target.write("{:s}temp <- getLine\n".format(indstr,name))
            target.write('{:s}let vmap{:d} = Map.insert "{:s}" ({:s} (read temp)) vmap{:d}\n'
                         .format(indstr,count+1,name,{'int':'Int','float':'Double','bool':'Bool'}[dtype],count))
            count += 1
        if stype == "write":
            expr = sentence['expr']
            target.write("{:s}print ({:s});\n".format(indstr,expreval(expr,count)))
        if stype == "if":
            cases = sentence['cases']
            target.write("{:s}vmap{:d} <- ".format(indstr,count+1))
            for case in cases:
                condition = case['condition']
                subblock = case['block']
                target.write("if eval({:s})\n".format(expreval(condition,count)))
                target.write("{:s}    then b{:d} vmap{:d}\n".format(indstr,len(func),count))
                target.write("{:s}    else ".format(indstr))
                func.append(subblock)
            target.write("return vmap{:d}\n".format(count))
            count += 1
        if stype == "while":
            expr = sentence['expr']
            subblock = sentence['block']
            target.write('{:s}vmap{:d} <- go (\\vmap0->({:s})) b{:d} vmap{:d}\n'
                         .format(indstr,count+1,expreval(expr,0),len(func),count))
            func.append(subblock)
            count += 1
    target.write("{:s}return vmap{:d}\n".format(indstr,count))


def expreval(expr,vmap):
    type = {'int':'Int','float':'Double','bool':'Bool'}[expr['type']]
    if "value" in expr:
        value = expr['value']
        if value == "true":
            return "Bool True"
        if value == "false":
            return "Bool False"
        if value[0] in string.ascii_letters + "_":
            return 'vmap{:d} Map.! "{:s}"'.format(vmap,value)
        return type+' '+value
    operator = expr['operator']
    e1 = expr['e1']
    if "e2" in expr:
        e2 = expr['e2']
        if operator=='cast':
            return 'cast "{:s}" ({:s})'.format({'int':'Int','float':'Double','bool':'Bool'}[e1],expreval(e2,vmap))
        conlist = {"&":".&.","|":".|.","^":".^.",">>":".>>.","<<":".<<."}
        if operator in conlist:
            operator = conlist[operator]
        return "({:s}) U.{:s} ({:s})".format(expreval(e1,vmap),operator,expreval(e2,vmap))
    operator = {"-":"negate","~":"bnot","!":"not"}[operator]
    return "U.{:s} ({:s})".format(operator, expreval(e1,vmap))

if __name__ == "__main__":
    args = sys.argv
    filein = args[1]
    genfile(filein,filein[:-4]+".hs")