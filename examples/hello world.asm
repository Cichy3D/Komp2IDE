mov axa ff 07;
push axa;
pop ixi;
zero; write;
mov axa ff 02;
push axa;
pop ixi;
write bulk; 

"Mary had a little lamb"; d;
"Its fleece was white as snow"; d;
"And everywhere that Mary went"; d;
"The lamb was sure to go"; d; 0;


mov axa :end;
:end;
mov pxp axa;
