package pl.cichy3d.komp2ide.assembly;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum OriginalAssemblyLang {

	NIL          ("NIL",                       m -> "0"),
	READ         ("READ( A)?",                 m -> "1"),
	WRITE        ("WRITE( A)?",                m -> "2"),
	MOV_FLAG     ("MOV( A)? FLAG",             m -> "3"),
	SHL          ("SHL( A)?",                  m -> "4"),
	SHR          ("SHR( A)?",                  m -> "5"),
	ROTL         ("ROTL( A)?",                 m -> "6"),
	ROTR         ("ROTR( A)?",                 m -> "7"),
	LOAD         ("LOAD A? ?([0-9A-F]+)",      m -> "8 "+ m.group(1), 2),
	LOAD_AND     ("LOAD AND A? ?([0-9A-F]+)",  m -> "9 "+ m.group(1), 2),
	ZERO_A       ("ZERO( A)?",                 m -> "a"),
	FF_A         ("#FF( A)?",                  m -> "b"),
	MINUS_A      ("(MINUS|-) ?A",              m -> "c"),
	NOT          ("NOT( A)?",                  m -> "d"),
	INC          ("INC( A)?",                  m -> "e"),
	DEC          ("DEC( A)?",                  m -> "f"),
	PUSH8        ("PUSH(8)? ([ABCDEHIP])",     m -> "1"+Register.get(m.group(2)).getNum()),
	POP8         ("POP(8)? ([ABCDEHIP])",      m -> "1"+Integer.toHexString(Register.get(m.group(2)).getNum() + 8)),
	MOV_A_REG    ("MOV( A)? ([ABCDEHIP])",     m -> "2"+Register.get(m.group(2)).getNum()),
	OR_A_REG     ("OR( A)? ([ABCDEHIP])",      m -> "2"+Integer.toHexString(Register.get(m.group(2)).getNum() + 8)),
	XOR_A_REG    ("XOR( A)? ([ABCDEHIP])",     m -> "3"+Register.get(m.group(2)).getNum()),
	AND_A_REG    ("AND( A)? ([ABCDEHIP])",     m -> "3"+Integer.toHexString(Register.get(m.group(2)).getNum() + 8)),
	ADD_A_REG    ("ADD( A)? ([ABCDEHIP])",     m -> "4"+Register.get(m.group(2)).getNum()),
	SUB_A_REG    ("SUB( A)? ([ABCDEHIP])",     m -> "4"+Integer.toHexString(Register.get(m.group(2)).getNum() + 8)),
	MUL_A_REG    ("MUL( A)? ([ABCDEHIP])",     m -> "5"+Register.get(m.group(2)).getNum()),
	DIV_A_REG    ("DIV( A)? ([ABCDEHIP])",     m -> "5"+Integer.toHexString(Register.get(m.group(2)).getNum() + 8)),
	MOD_A_REG    ("MOD( A)? ([ABCDEHIP])",     m -> "6"+Register.get(m.group(2)).getNum()),
	MAX_A_REG    ("MAX( A)? ([ABCDEHIP])",     m -> "6"+Integer.toHexString(Register.get(m.group(2)).getNum() + 8)),
	MOV_REG_A    ("MOV ([BCDEHIP]) A",         m -> "7"+Register.get(m.group(1)).getNum()),
	SWITCH       ("SWITCH ([ABCDEHIP])",       m -> "7"+Integer.toHexString(Register.get(m.group(1)).getNum() + 8)),
	PUSH16       ("PUSH(16)? ([ABCDEHIP])X\\2",m -> "9"+Register.get(m.group(2)).getNum()),
	POP16        ("POP(16)? ([ABCDEHIP])X\\2", m -> "9"+Integer.toHexString(Register.get(m.group(2)).getNum() + 8)),
	MOV_A_XREG   ("MOV( A)? ([ABCDEHIP])X",    m -> "a"+Register.get(m.group(2)).getNum()),
	OR_A_XREG    ("OR( A)? ([ABCDEHIP])X",     m -> "a"+Integer.toHexString(Register.get(m.group(2)).getNum() + 8)),
	XOR_A_XREG   ("XOR( A)? ([ABCDEHIP])X",    m -> "b"+Register.get(m.group(2)).getNum()),
	AND_A_XREG   ("AND( A)? ([ABCDEHIP])X",    m -> "b"+Integer.toHexString(Register.get(m.group(2)).getNum() + 8)),
	ADD_A_XREG   ("ADD( A)? ([ABCDEHIP])X",    m -> "c"+Register.get(m.group(2)).getNum()),
	SUB_A_XREG   ("SUB( A)? ([ABCDEHIP])X",    m -> "c"+Integer.toHexString(Register.get(m.group(2)).getNum() + 8)),
	MUL_A_XREG   ("MUL( A)? ([ABCDEHIP])X",    m -> "d"+Register.get(m.group(2)).getNum()),
	DIV_A_XREG   ("DIV( A)? ([ABCDEHIP])X",    m -> "d"+Integer.toHexString(Register.get(m.group(2)).getNum() + 8)),
	MOD_A_XREG   ("MOD( A)? ([ABCDEHIP])X",    m -> "e"+Register.get(m.group(2)).getNum()),
	MAX_A_XREG   ("MAX( A)? ([ABCDEHIP])X",    m -> "e"+Integer.toHexString(Register.get(m.group(2)).getNum() + 8)),
	JZ           ("JZ ([ABCDEHIP])X\\1",       m -> "f"+Register.get(m.group(1)).getNum()),
	JNZ          ("JNZ ([ABCDEHIP])X\\1",      m -> "f"+Integer.toHexString(Register.get(m.group(1)).getNum() + 8)),
	MOV_AXA_CONST("MOV AXA ([0-9A-F]+ [0-9A-F]+|:[0-9A-Z_]+)", m -> "f7 "+m.group(1), 3),
	MOV_PXP_AXA  ("MOV PXP AXA",               m -> "ff"),
	WRITE_BULK   ("WRITE BULK",                m -> "80"),
	WRITE_BULK_X ("WRITE BULK ([0-9A-F])",     m -> "8" + m.group(1)),
	NUM          ("([0-9A-F]+)",               m -> m.group(1));

	private Pattern regex;
	private Function<Matcher, String> toBinary;
	private int byteSize;
	
	private OriginalAssemblyLang(String regex, Function<Matcher, String> toBinary) {
		this(regex, toBinary, 1);
	}
	
	private OriginalAssemblyLang(String regex, Function<Matcher, String> toBinary, int byteSize) {
		this.regex = Pattern.compile("(?i)"+regex);
		this.toBinary = toBinary;
		this.byteSize = byteSize;
	}

	public Pattern getRegex() {
		return regex;
	}

	public Function<Matcher, String> getToBinary() {
		return toBinary;
	}

	public int getByteSize() {
		return byteSize;
	}

}
