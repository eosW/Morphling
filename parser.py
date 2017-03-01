import sys,json
from ply import lex,yacc

reserved = {
   'if' : 'IF',
   'else' : 'ELSE',
   'elif' : 'ELIF',
   'while' : 'WHILE',
   'break' : 'BREAK',
   'continue' : 'CONTINUE',
   'end' : 'END',
   'int' : 'TYPE',
   'float' : 'TYPE',
   'bool' : 'TYPE',
   'false' : 'BOOL',
   'true' : 'BOOL',
   'read' : 'READ',
   'write' : 'WRITE',
}

tokens = ['NAME','TYPE','INT','FLOAT','BOOL','PLUS','MINUS','TIMES','DIVIDES','MODS','EXP','BRSHIFT','BLSHIFT','BXOR'
            ,'BOR','BAND','BNOT','OR','AND','NOT','EQ','NEQ','GT','GET','LT','LET','LP','RP','COLON','SEMICOLON'
            ,'ASSIGN','END','WHILE','IF','ELIF','ELSE','BREAK','CONTINUE','READ','WRITE']

def t_NAME(t):
    r'[a-zA-Z_][a-zA-Z_0-9]*'
    t.type = reserved.get(t.value,'NAME')
    return t
# t_TYPE = r'(int|float|bool)'
t_INT = r'\d+'
t_FLOAT = r'((\d+|\.\d+|\d+\.\d*)([eE]-?\d+)|(\.\d+|\d+\.\d*))'
# t_BOOL = r'(false|true)'
t_PLUS = r'\+'
t_MINUS = r'-'
t_TIMES = r'\*'
t_DIVIDES = r'/'
t_MODS = r'%'
t_EXP = r'\*\*'
t_BRSHIFT = r'>>'
t_BLSHIFT = r'<<'
t_BXOR = r'\^'
t_BOR = r'\|'
t_BAND = r'&'
t_BNOT = r'~'
t_OR = r'\|\|'
t_AND = r'&&'
t_NOT = r'!'
t_EQ = r'=='
t_NEQ = r'!='
t_GT = r'>'
t_GET = r'>='
t_LT = r'<'
t_LET = r'<='
t_LP = r'\('
t_RP = r'\)'
t_COLON = r':'
t_SEMICOLON = r';'
t_ASSIGN = r'='
# t_END = r'end'
# t_WHILE = r'while'
# t_IF = r'if'
# t_ELIF = r'elif'
# t_ELSE = r'else'
# t_BREAK = r'break'
# t_CONTINUE = r'continue'

def t_newline(t):
    r'\n+'
    t.lexer.lineno += len(t.value)

t_ignore = ' \t'

def t_error(t):
    print("Illegal character '%s'" % t.value[0])
    t.lexer.skip(1)

lex.lex()

def p_block(p):
    """
    block : block sentence
    """
    p[0] = p[1] + [p[2]]

def p_block2(p):
    """
    block :
    """
    p[0] = []

def p_sentence(p):
    """
    sentence : declare SEMICOLON
    | assign SEMICOLON
    | control SEMICOLON
    | read SEMICOLON
    | write SEMICOLON
    """
    p[0] = p[1]

def p_declare(p):
    """
    declare : TYPE NAME
    """
    p[0] = {'sentence':'declare', 'type':p[1], 'name':p[2]}

def p_declare2(p):
    """
    declare : TYPE NAME ASSIGN expr
    """
    p[0] = {'sentence':'declare_init', 'type':p[1], 'name':p[2], 'expr':p[4]}

def p_assign(p):
    """
    assign : NAME ASSIGN expr
    """
    p[0] = {'sentence':'assign', 'type':p[1], 'expr':p[3]}

def p_control(p):
    """
    control : while
    | if
    """
    p[0] = p[1]

def p_control2(p):
    """
    control : BREAK
    | CONTINUE
    """
    p[0] = {'sentence':p[1]}

def p_while(p):
    """
    while : WHILE LP expr RP COLON block END
    """
    p[0] = {'sentence':'while', 'expr':p[3], 'block':p[6]}

def p_if(p):
    """
    if : IF LP expr RP COLON block elif else END
    """
    p[0] = {'sentence':'if', 'cases':[{'condition':p[3], 'block':p[6]}] + p[7] + p[8]}

def p_elif(p):
    """
    elif : elif ELIF LP expr RP COLON block
    """
    p[0] = p[1] + [{'condition':p[4], 'block':p[7]}]

def p_elif2(p):
    """
    elif :
    """
    p[0] = []

def p_else(p):
    """
    else : ELSE COLON block
    """
    p[0] = [{'condition':{'type':'BOOL','value':'true'}, 'block':p[3]}]

def p_else2(p):
    """
    else :
    """
    p[0] = []


def p_aexpr_b(p):
    """
    aexpr : aexpr BOR aexpr
    | aexpr BXOR aexpr
    | aexpr BAND aexpr
    | aexpr BRSHIFT aexpr
    | aexpr BLSHIFT aexpr
    | aexpr PLUS aexpr
    | aexpr MINUS aexpr
    | aexpr TIMES aexpr
    | aexpr MODS aexpr
    | aexpr DIVIDES aexpr
    | aexpr EXP aexpr
    """
    p[0] = {'operator':p[2], 'e1':p[1], 'e2':p[3]}

def p_aexpr_u(p):
    """
    aexpr : MINUS aexpr %prec NEGATIVE
    | BNOT aexpr
    """
    p[0] = {'operator':p[1], 'e1':p[2]}

def p_aexpr_p(p):
    """
    aexpr : LP aexpr RP
    """
    p[0] = p[2]

def p_aexpr_cast(p):
    """
    aexpr : TYPE LP aexpr RP
    """
    p[0] = {'operator': 'cast', 'e1': p[1], 'e2':p[3]}

def p_aexpr_n(p):
    """
    aexpr : INT
    | FLOAT
    | BOOL
    | NAME
    """
    p[0] = {'type':p.slice[1].type,'value':p[1]}

def p_expr_b(p):
    """
    expr : expr OR expr
    | expr AND expr
    | aexpr EQ aexpr
    | aexpr NEQ aexpr
    | aexpr GT aexpr
    | aexpr GET aexpr
    | aexpr LT aexpr
    | aexpr LET aexpr
    """
    p[0] = {'operator':p[2], 'e1':p[1], 'e2':p[3]}

def p_expr_u(p):
    """
    expr : NOT expr
    """
    p[0] = {'operator':p[1], 'e1':p[2]}

def p_expr_dg(p):
    """
    expr : aexpr
    """
    p[0] = p[1]

def p_read(p):
    """
    read : READ NAME
    """
    p[0] = {'sentence':'read', 'name':p[2]}

def p_write(p):
    """
    write : WRITE expr
    """
    p[0] = {'sentence':'write', 'expr':p[2]}

precedence = (
    ('left', 'OR'),
    ('left', 'AND'),
    ('right', 'NOT'),
    ('nonassoc', 'EQ', 'NEQ', 'GT', 'GET', 'LT', 'LET'),
    ('left', 'BOR'),
    ('left', 'BXOR'),
    ('left', 'BAND'),
    ('left', 'BRSHIFT', 'BLSHIFT'),
    ('left', 'PLUS', 'MINUS'),
    ('left', 'TIMES', 'DIVIDES', 'MODS'),
    ('right', 'BNOT', 'NEGATIVE'),
    ('left', 'EXP'),
)

import logging
logging.basicConfig(
    level = logging.DEBUG,
    filename = "parselog.txt",
    filemode = "w",
    format = "%(filename)10s:%(lineno)4d:%(message)s"
)
log = logging.getLogger()

parser = yacc.yacc()

# data = "float a; a = 4e4*7+-.5**7; int b = 5^7<<4;"
# print data
# print parser.parse(data)
# data = "while (a>5): a = 7+9; b = false; break; end;"
# print data
# print parser.parse(data)
# data = "if (a>3): a=6; elif (a>6): a=7; elif (a<0): a=1; else: a=9; end;"
# print data
# print parser.parse(data)
# data = "read a; write a+6*b;"
# print data
# print parser.parse(data)

if __name__ == "__main__":
    args = sys.argv
    filein = args[1]
    segs = filein.split('.')
    if len(segs)>1:
        fileout = '.'.join(segs[:-1])+'.ast'
    else:
        fileout = filein+'.ast'
    with open(filein) as source, open(fileout,'wb') as target:
        text = source.read()
        ast = parser.parse(text)
        json.dump(ast,target,sort_keys=True, indent=2, separators=(',', ': '))
