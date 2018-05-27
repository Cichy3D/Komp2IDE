:start;

push ixi; pop axa; dec; push axa; pop hxh;

mov axa ff 04;
push axa;
pop ixi;
read; shl; shl;
mov b a;  // joy Y
read; 
mov c a; // joy X

// byte in a row
switch a;
load fe;
switch a;
shr; shr; shr; or b;
push axa;
pop ixi;

zero; inc; rotr; mov d a; //sets bit x
mov a c;
not;
load and 7;
inc; mov c a; // iterator

mov axa :petla_bits;
push axa; pop exe;
:petla_bits;
	mov a d; rotl; mov d a;
	mov a c; dec; mov c a;
	jnz exe;
push ixi; push hxh; pop ixi; zero; write; pop ixi;
mov a d; write;

// main loop
mov axa :start;
mov pxp axa;