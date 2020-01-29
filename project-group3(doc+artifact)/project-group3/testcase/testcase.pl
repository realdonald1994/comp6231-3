% generate problem of size 10
reachable(X,Y) :- edge(X,Y).
reachable(X,Y) :- edge(X,Z), reachable(Z,Y).
increasing(X,Y) :- edge(X,Y), lt(X,Y).
increasing(X,Y) :- edge(X,Z), lt(X,Z), increasing(Z,Y).
edge(1, 2).
edge(2, 4).
edge(2, 5).
edge(5, 3).
edge(4, 3).


