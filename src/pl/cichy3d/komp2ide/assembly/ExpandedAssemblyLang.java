package pl.cichy3d.komp2ide.assembly;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ExpandedAssemblyLang {

	GOTO("GOTO (:[0-9A-Z_]+|[0-9A-F]+ [0-9A-F]+)",			m -> "mov axa "+g1(m)+"; mov pxp axa"),
	GOSUB("GOSUB (:[0-9A-Z_]+|[0-9A-F]+ [0-9A-F]+)",		m -> "mov axa "+g1(m)+"; push16 pxp; mov pxp axa"),
	RETURN("RETURN",				 						m -> "pop16 ixi; read; push16 ixi; pop16 pxp"),
	
	J0("J(0| Zero) ([ABCDEHIP])X\\2",						m -> "JZ "+rxr2(m)),
	J1("J(1| ?OVER| Overflow) ([ABCDEHIP])X\\2",			m -> "mov flag; load and 2; JNZ "+rxr2(m)),
	J2("J(2| DivZero) ([ABCDEHIP])X\\2",					m -> "mov flag; load and 4; JNZ "+rxr2(m)),
	J3("J(3| A>B) ([ABCDEHIP])X\\2",						m -> "mov flag; load and 8; JNZ "+rxr2(m)),
	J4("J(4| ?EQ) ([ABCDEHIP])X\\2",						m -> "mov flag; load and 10; JNZ "+rxr2(m)),
	J5("J(5| ?-| ?MINUS) ([ABCDEHIP])X\\2",					m -> "mov flag; load and 20; JNZ "+rxr2(m)),
	J6("J(6| ?8OVER| Stack8 Overflow) ([ABCDEHIP])X\\2",	m -> "mov flag; load and 40; JNZ "+rxr2(m)),
	J7("J(7| ?16OVER| Stack16 Overflow) ([ABCDEHIP])X\\2",	m -> "mov flag; load and 80; JNZ "+rxr2(m)),

	JN0("JN(0| Zero) ([ABCDEHIP])X\\2",						m -> "JZ "+rxr2(m)),
	JN1("JN(1| ?OVER| Overflow) ([ABCDEHIP])X\\2",			m -> "mov flag; load and 2; JZ "+rxr2(m)),
	JN2("JN(2| DivZero) ([ABCDEHIP])X\\2",					m -> "mov flag; load and 4; JZ "+rxr2(m)),
	JN3("JN(3| A>B) ([ABCDEHIP])X\\2",						m -> "mov flag; load and 8; JZ "+rxr2(m)),
	JN4("JN(4| ?EQ) ([ABCDEHIP])X\\2",						m -> "mov flag; load and 10; JZ "+rxr2(m)),
	JN5("JN(5| ?-| ?MINUS) ([ABCDEHIP])X\\2",				m -> "mov flag; load and 20; JZ "+rxr2(m)),
	JN6("JN(6| ?8OVER| Stack8 Overflow) ([ABCDEHIP])X\\2",	m -> "mov flag; load and 40; JZ "+rxr2(m)),
	JN7("JN(7| ?16OVER| Stack16 Overflow) ([ABCDEHIP])X\\2",m -> "mov flag; load and 80; JZ "+rxr2(m)),
	
	MOV_RXR_CONST("MOV ([BCDEHIP])X\\1 (:[0-9A-Z_]+|[0-9A-F]+ [0-9A-F]+)",	m -> "mov axa "+g2(m)+"; push16 axa; pop16 "+rxr1(m)),
	MOV_RXR_RXR("MOV ([ABCDEHIP])X\\1 ([BCDEHIP])X\\2",					m -> "push16 "+rxr2(m)+"; pop16 "+rxr1(m)),
	MOV_RXR_AXA("MOV ([ABCDEHI])X\\1 AXA",					m -> "push16 axa; pop16 "+rxr1(m)),
	
	READ_REG("READ ([BCDEHI])(X?)", 										m -> sw(m,1,2)+"read; mov " +g1(m)+" a"+unsw(m,1,2)),
	READ_AX("READ AX", 														m -> "read; switch a"),
	WRITE_REG("WRITE ([BCDEHIP]X?)",										m -> "mov a "+g1(m)+"; write"),
	WRITE_AX("WRITE AX",													m -> "mov a ax; write"),
	MOVFLAG_REG("MOV ([BCDEHIP])(X?) FLAG",	 								m -> sw(m,1,2)+"mov flag; mov " +g1(m)+" a"+unsw(m,1,2)),
	MOVFLAG_AX("MOV AX FLAG",	 											m -> "mov flag; switch a"),
	SH_REG("(SHL|SHR|ROTL|ROTR|NOT|INC|DEC) ([BCDEHIP])(X?)", 				m -> sw(m,2,3)+"mov a "+g2(m)+"; "+g1(m)+"; mov " +g2(m)+" a"+unsw(m,2,3)),
	SH_ZERO_AX("(SHL|SHR|ROTL|ROTR|NOT|INC|DEC|ZERO|#FF) AX",				m -> "switch a;"+g1(m)+"; switch a"),
	LOAD_REG("(LOAD) ([BCDEHI])(X?) ([0-9A-F]+)", 							m -> sw(m,2,3)+g1(m)+" "+m.group(4)+"; mov "+g2(m)+" a"+unsw(m,2,3)),
	LOAD_AX("(LOAD) AX ([0-9A-F]+)", 										m -> g1(m)+" "+g2(m)+"; switch a"),
	LOAD_AND_REG("(LOAD AND) ([BCDEHI])(X?) ([0-9A-F]+)", 					m -> sw(m,2,3)+"mov a "+g2(m)+";"+g1(m)+" "+m.group(4)+"; mov "+g2(m)+" a"+unsw(m,2,3)),
	LOAD_AND_AX("LOAD AND AX ([0-9A-F]+)", 									m -> "switch a; load and "+g1(m)+"; switch a"),
	ZERO_REG("(ZERO|#FF) ([BCDEHI])(X?)", 									m -> sw(m,2,3)+g1(m)+"; mov "+g2(m)+" a"+unsw(m,2,3)),
	MINUS_REG("(MINUS|-) ?([BCDEHI])(X?)", 									m -> sw(m,2,3)+"mov a "+g2(m)+"; -a; mov " +g2(m)+" a"+unsw(m,2,3)),
	MINUS_AX("(MINUS|-) ?AX", 												m-> "switch a; -a; switch a"),
	STACK8X("(PUSH8|POP8|PUSH|POP) ([ABCDEHI])(X?)", 						m -> sw(m,2,3)+g1(m)+" "+g2(m)+unsw(m,2,3)),
	MOV_FROM_RAM_BY_REG("MOV ([BCDEHI])(X?) \\[([ABCDEHIP])X\\3\\]", 		m -> "push16 "+rxr3(m)+"; pop16 IXI; read; "+sw(m,1,2)+"mov "+g1(m)+" a"+unsw(m,1,2)),
	MOV_FROM_RAM_BY_A("MOV (A)(X?) \\[([ABCDEHIP])X\\3\\]", 				m -> "push16 "+rxr3(m)+"; pop16 IXI; "+sw(m,1,2)+"read" + unsw(m,1,2)),
	MOV_FROM_RAM_BY_CONST("MOV ([BCDEHI])(X?) \\[(:[0-9A-Z_]+|[0-9A-F]+ [0-9A-F]+)\\]", 	m -> "mov axa "+g3(m)+"; push16 axa; pop16 ixi; read; "+sw(m,1,2)+"mov "+g1(m)+" a"+unsw(m,1,2)),
	MOV_FROM_RAM_A_CONST("MOV A \\[(:[0-9A-Z_]+|[0-9A-F]+ [0-9A-F]+)\\]",				 	m -> "mov axa "+g1(m)+"; push16 axa; pop16 ixi; read"),
	MOV_FROM_RAM_AX_CONST("MOV AX \\[(:[0-9A-Z_]+|[0-9A-F]+ [0-9A-F]+)\\]",				 	m -> "mov axa "+g1(m)+"; push16 axa; pop16 ixi; switch a; read; switch a"),
	MOV_TO_RAM_BY_REG("MOV \\[([BCDEHIP])X\\1\\] ([ABCDEHI]X?)", 							m -> "push16 "+rxr1(m)+"; pop16 IXI; mov a "+g2(m)+"; write"),
	MOV_TO_RAM_BY_A("MOV \\[([BCDEHIP])X\\1\\] A", 											m -> "push16 "+rxr1(m)+"; pop16 IXI; write"),
	MOV_TO_RAM_BY_AX("MOV \\[([BCDEHIP])X\\1\\] AX", 										m -> "push16 "+rxr1(m)+"; pop16 IXI; switch a; write; switch a"),
	MOV_TO_RAM_BY_CONST("MOV \\[(:[0-9A-Z_]+|[0-9A-F]+ [0-9A-F]+)\\] ([ABCDEHIP]X?)", 		m -> "mov axa "+g1(m)+"; push16 axa; pop16 ixi; mov a "+g2(m)+"; write"),
	MOV_TO_RAM_BY_CONST_A("MOV \\[(:[0-9A-Z_]+|[0-9A-F]+ [0-9A-F]+)\\] A",			 		m -> "mov axa "+g1(m)+"; push16 axa; pop16 ixi; write"),
	MOV_TO_RAM_BY_CONST_AX("MOV \\[(:[0-9A-Z_]+|[0-9A-F]+ [0-9A-F]+)\\] AX", 				m -> "mov axa "+g1(m)+"; push16 axa; pop16 ixi; switch a; write; switch a"),
	
	LOAD_TO_RAM("LOAD \\[(:[0-9A-Z_]+|[0-9A-F]+ [0-9A-F]+)\\] ([0-9A-F]+)", 				m->"mov axa "+g1(m)+"; push16 axa; pop16 ixi; WRITE BULK 1; "+g2(m)),
	
	MOV_RAM_REG_REG("MOV \\[([BCDEHIP])X\\1\\] \\[([ABCDEHIP])X\\2\\]", 					m -> "push16 "+rxr2(m)+"; pop16 IXI; read; push16 "+rxr1(m)+"; pop16 IXI; write"),
	MOV_RAM_CONST_REG("MOV \\[(:[0-9A-Z_]+|[0-9A-F]+ [0-9A-F]+)\\] \\[([BCDEHIP])X\\2\\]", 	m -> "mov axa "+g1(m)+"; push16 axa; push16 "+rxr2(m)+"; pop16 IXI; read; pop16 ixi; write"),
	MOV_RAM_REG_CONST("MOV \\[([ABCDEHIP])X\\1\\] \\[(:[0-9A-Z_]+|[0-9A-F]+ [0-9A-F]+)\\]",	m -> "push16 "+rxr1(m)+"; mov axa "+g2(m)+"; push16 axa; pop16 ixi; read;  pop16 IXI; write"),
	MOV_RAM_CONST_CONST("MOV \\[(:[0-9A-Z_]+|[0-9A-F]+ [0-9A-F]+)\\] \\[(:[0-9A-Z_]+|[0-9A-F]+ [0-9A-F]+)\\]", 
										m -> "mov axa "+g2(m)+"; push16 axa; pop16 ixi; read; push a; mov axa "+g1(m)+"; push16 axa; pop16 ixi; pop a; write"),
	
	PRINT_REG("PRINT ([ABCDEHIP]X?)", 	m->"mov axa ff 02; push16 axa; pop16 ixi; mov a "+g1(m)+"; write"),
	PRINT_STR("PRINT (\"[^\n\"]*\")", 	m->"mov axa ff 07; push16 axa; pop16 ixi; zero; write; mov axa ff 02; push16 axa; pop16 ixi; write bulk; "+g1(m)
											+"; 0; mov axa ff 07; push16 axa; pop16 ixi; zero; inc; write"),
	INPUT("INPUT ([BCDEH])(X?)", 		m->"mov axa ff 07; push16 axa; pop16 ixi; zero; write; push16 axa; pop16 ixi; push16 pxp; pop16 "
											+ rxr1(m) + "; "+ "push16 "+rxr1(m)+"; read; jz "+rxr1(m)+"; pop "+rxr1(m)+"; mov axa ff 07; push16 axa; pop16 ixi; zero; inc; write; "
											+ "mov axa ff 01; push16 axa; pop16 ixi; read; " + sw(m,1,2) + "mov "+g1(m)+" a"+unsw(m,1,2)),
	JOY("JOY ([ABCDEHI])X\\1", 			m->"mov axa ff 04; push16 axa; pop16 ixi; read; "+(g1(m).toUpperCase().equals("A")?"":"mov "+g1(m)+" a;")
											+ " switch "+g1(m)+"; read; "+(g1(m).toUpperCase().equals("A")?"":"mov "+g1(m)+" a;")+" switch "+g1(m)),
	CLSSCR("CLSSCR", 					m->"mov axa ff 03; push16 axa; pop16 ixi; #ff; write"),
	INPUT_STR("INPUT \\[([BCDEH])X\\1\\]", m->"push i; push i; push16 pxp; pop i; pop i; push16 "+ rxr1(m) +"; mov axa ff 07; push16 axa; pop16 ixi; zero; write;" 
											+ "push16 axa; pop16 ixi; push16 pxp; pop16 "+ rxr1(m) +"; push16 "+ rxr1(m) +"; read; jz "+ rxr1(m) +"; pop "+ rxr1(m) +"; mov axa ff 07;" 
											+ "push16 axa; pop16 ixi; zero; inc; write; mov axa ff 01; push16 axa; pop16 ixi; read; mov "+g1(m)+" a; pop16 ixi; push i; switch i; push i; switch i;" 
											+ "mov a "+g1(m)+"; write; switch a; push16 ixi; pop16 "+ rxr1(m) +"; pop16 ixi; push16 ixi; load a a; "
											+ "sub ax; JNZ IXI; pop16 ixi; pop i; switch i; pop i; zero; write;"),
	PRINT_RAM("PRINT \\[([BCDEH])X\\1\\]", m->"push16 pxp;push16 "+ rxr1(m) +";pop16 ixi;read;push16 ixi; pop16 "+ rxr1(m) +";push a;" 
											+ "mov axa ff 02;push16 axa;pop16 ixi;pop a;write;pop16 ixi;push16 ixi;mov a a;jnz ixi;pop16 ixi;"),
	
	MOV_REG_REG("MOV ([BCDEHIP]) ([BCDEHIP])", 			m->"push "+g2(m)+"; pop "+g1(m)),
	MOV_XREG_REG("MOV ([ABCDEHIP])X ([ABCDEHIP])", 		m->"push "+g2(m)+"; switch "+g1(m)+"; pop "+g1(m)+"; switch "+g1(m)),
	MOV_REG_XREG("MOV ([BCDEHIP]) ([ABCDEHIP])X", 		m->"switch "+g2(m)+"; push "+g2(m)+"; switch "+g2(m)+"; pop "+g1(m)),
	MOV_XREG_XREG("MOV ([ABCDEHIP])X ([ABCDEHIP])X", 	m->"switch "+g2(m)+"; push "+g2(m)+"; switch "+g2(m)+"; switch "+g1(m)+"; pop "+g1(m)+"; switch "+g1(m)),
	ALU_OP_REG_REG("(OR|XOR|AND|SUB|MUL|DIV|MOD|MAX) ([BCDEHIP]) ([ABCDEHIP])", 	m->"mov a "+g2(m)+";  "+g1(m)+" "+g3(m)+"; mov "+g2(m)+" a"),
	ALU_OP_XREG_REG("(OR|XOR|AND|SUB|MUL|DIV|MOD|MAX) ([ABCDEHIP])X ([ABCDEHIP])", 	m->"switch "+g2(m)+"; mov a "+g2(m)+";  "+g1(m)+" "+g3(m)+"; mov "+g2(m)+" a; switch "+g2(m)),
	ALU_OP_REG_XREG("(OR|XOR|AND|SUB|MUL|DIV|MOD|MAX) ([BCDEHIP]) ([ABCDEHIP])X", 	m->"mov a "+g2(m)+"; switch "+g3(m)+"; "+g1(m)+" "+g3(m)+"; switch "+g3(m)+"; mov "+g2(m)+" a"),
	ALU_OP_XREG_XREG("(OR|XOR|AND|SUB|MUL|DIV|MOD|MAX) ([ABCDEHIP])X ([ABCDEHIP])X",m->"switch "+g2(m)+"; switch "+g3(m)+"; mov a "+g2(m)+";  "+g1(m)+" "+g3(m)+"; switch "+g3(m)+"; mov "+g2(m)+" a; switch "+g2(m)),
	MIN_REG_REG("MIN ([BCDEHIP]) ([ABCDEHIP])", 	m->"mov a "+g1(m)+"; -a; mov "+g1(m)+" a; mov a "+g2(m)+"; -a; max a "+g1(m)+"; -a; mov "+g1(m)+" a"),
	MIN_REG_XREG("MIN ([BCDEHIP]) ([ABCDEHIP])X", 	m->"switch "+g2(m)+"; mov a "+g1(m)+"; -a; mov "+g1(m)+" a; mov a "+g2(m)+"; -a; max a "+g1(m)+"; -a; mov "+g1(m)+" a; switch "+g2(m)),
	MIN_XREG_REG("MIN ([ABCDEHIP])X ([ABCDEHIP])", 	m->"switch "+g1(m)+"; mov a "+g1(m)+"; -a; mov "+g1(m)+" a; mov a "+g2(m)+"; -a; max a "+g1(m)+"; -a; mov "+g1(m)+" a; switch "+g1(m)),
	MIN_XREG_XREG("MIN ([ABCDEHIP])X ([ABCDEHIP])X",m->"switch "+g1(m)+"; switch "+g2(m)+"; mov a "+g1(m)+"; -a; mov "+g1(m)+" a; mov a "+g2(m)+"; -a; max a "+g1(m)+"; -a; mov "+g1(m)+" a; switch "+g1(m)+"; switch "+g2(m)),
	
	;
	private Pattern regex;
	private Function<Matcher, String> toAsm;
	
	private ExpandedAssemblyLang(String regex, Function<Matcher, String> toAsm) {
		this.regex = Pattern.compile("(?i)"+regex);
		this.toAsm = toAsm;
	}

	public Pattern getRegex() {
		return regex;
	}

	public Function<Matcher, String> getToAsm() {
		return toAsm;
	}
	
	private static String g1(Matcher m) {
		return m.group(1);
	}
	
	private static String g2(Matcher m) {
		return m.group(2);
	}
	
	private static String g3(Matcher m) {
		return m.group(3);
	}
	
	private static String sw(Matcher m, int reg, int x) {
		return m.group(x).isEmpty()?"":"switch "+m.group(reg)+"; ";
	}
	
	private static String unsw(Matcher m, int reg, int x) {
		return m.group(x).isEmpty()?"":"; switch "+m.group(reg);
	}
	
	private static String rxr1(Matcher m) {
		return m.group(1)+"x"+m.group(1);
	}
	
	private static String rxr2(Matcher m) {
		return m.group(2)+"x"+m.group(2);
	}
	
	private static String rxr3(Matcher m) {
		return m.group(3)+"x"+m.group(3);
	}
	
}
