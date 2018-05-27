# Komp2IDE

## Komp2 CPU Internal instraction set, with Komp2IDE assembler mnemonics*

0	0	0	0	0	0	0	0	NIL			

### Acumulator Register											

0	0	0	0	0	0	0	1	READ	A	[ IX, I ]++	

0	0	0	0	0	0	1	0	WRITE	A		

0	0	0	0	0	0	1	1	MOV Flag	A	Flag	

0	0	0	0	0	1	0	0	SHL	A		

0	0	0	0	0	1	0	1	SHR	A		

0	0	0	0	0	1	1	0	ROTL	A		

0	0	0	0	0	1	1	1	ROTR	A		

0	0	0	0	1	0	0	0	LOAD	A	const	reuse of overriden instruction 80

0	0	0	0	1	0	0	1	LOAD AND	A	const	reuse of overriden instruction 88

0	0	0	0	1	0	1	0	ZERO	A		

0	0	0	0	1	0	1	1	#FF	A		

0	0	0	0	1	1	0	0	'-A	A		

0	0	0	0	1	1	0	1	NOT	A		

0	0	0	0	1	1	1	0	INC	A		

0	0	0	0	1	1	1	1	DEC	A		

### Main registers REG				

0	0	0	1	0	R	E	G	PUSH8	REG		

0	0	0	1	1	R	E	G	POP8	REG		

0	0	1	0	0	R	E	G	MOV	A	REG	

0	0	1	0	1	R	E	G	OR	A	REG	

0	0	1	1	0	R	E	G	XOR	A	REG	

0	0	1	1	1	R	E	G	AND	A	REG	

0	1	0	0	0	R	E	G	ADD	A	REG	

0	1	0	0	1	R	E	G	SUB	A	REG	

0	1	0	1	0	R	E	G	MUL	A	REG	

0	1	0	1	1	R	E	G	DIV	A	REG	

0	1	1	0	0	R	E	G	MOD	A	REG	

0	1	1	0	1	R	E	G	MAX	A	REG	

0	1	1	1	0	R	E	G	MOV	REG	A	

0	1	1	1	1	R	E	G	SWITCH	REG <=> XREG		

### Data BULK Loading											

1	0	0	0	0	0	0	0	WRITE BULK		C String	

1	0	0	0	X	X	X	X	WRITE BULK Const		ROM => RAM	

### Extended registers XREG								

1	0	0	1	0	R	E	G	PUSH16	[ XREG, REG ]		

1	0	0	1	1	R	E	G	POP16	[ XREG, REG ]		

1	0	1	0	0	R	E	G	MOV	A	XREG	

1	0	1	0	1	R	E	G	OR	A	XREG	

1	0	1	1	0	R	E	G	XOR	A	XREG	

1	0	1	1	1	R	E	G	AND	A	XREG	

1	1	0	0	0	R	E	G	ADD	A	XREG	

1	1	0	0	1	R	E	G	SUB	A	XREG	

1	1	0	1	0	R	E	G	MUL	A	XREG	

1	1	0	1	1	R	E	G	DIV	A	XREG	

1	1	1	0	0	R	E	G	MOD	A	XREG

1	1	1	0	1	R	E	G	MAX	A	XREG	

1	1	1	1	0	R	E	G	JZ	[ PX, P ] := [ XREG, REG ]	

1	1	1	1	1	R	E	G	JNZ	[ PX, P ] := [ XREG, REG ]	

1	1	1	1	0	1	1	1	MOV [ AX, A ] const16			loading first AX, then A

1	1	1	1	1	1	1	1	MOV [ PX, P ] [ AX, A ] 			

