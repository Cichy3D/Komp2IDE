:start
load a a;
mov e a
load h 30;
joy bxb

mov a b
div a e
add h
mov c a;

switch c;
mov a b
mod a e
add h
mov c a;
switch c;


mov a bx
div a e
add h
mov d a;

switch d;
mov a bx
mod a e
add h
mov d a;
switch d

print c
print cx
print ", "
print d
print dx
print e

goto :start
