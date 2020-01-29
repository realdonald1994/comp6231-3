% generate problem of size 10
reachable(X,Y) :- edge(X,Y).
reachable(X,Y) :- edge(X,Z), reachable(Z,Y).
increasing(X,Y) :- edge(X,Y), lt(X,Y).
increasing(X,Y) :- edge(X,Z), lt(X,Z), increasing(Z,Y).
edge(0, 1).
edge(1, 2).
edge(2, 0).