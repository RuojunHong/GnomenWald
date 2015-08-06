# GnomenWald
GnomenWald- Final project of CS 2112 at Cornell

This traffic system of GnomenWald
****
In the country of GnomenWald there lie many isolated villages connected by roads (roads only ever go from village to village, never going nowhere nor branching out partway). In the days of the Grand Archgnome Zijphraagh, a complete road map was made of GnomenWald -- some of the gnomes were surprised to see that some villages had many roads connecting to them, whereas some had only two, or even only one. After several generations the population had increased so much that new villages started to spring up (though always connected via a road or roads to one or more villages in GnomenWald), and sometimes villages would disappear (though that made it very hard for the mapmakers since they insisted that roads only joined villages and never branched in the middle).

****
This data structure:
****
1.implements Dijkstra's Algorithm to find a shortest path
****
2.implements Prim's algorithm to find minimum spanning tree(The directed graph is regarded as undirected)
****
3.implements Topological BFS sort
****
4. has a GUI that displays the graph of villages and roads, and allows the construction ab initio of any reasonable number of villages plus an easy way to build one or two way roads between villages. It allows adding a new village to an existing setup, and permit deletion of individual villages( any roads that went through the village en route to other villages should be made direct. For example, if there is a road from A to B and roads from B to C and from B to D, then in case (2) there will now be a road from A to C and A to D)). There is also a way to place a gnome in a specified village, and a button provided a way to tell the gnome to move to a specified adjacent village. (The user should be able to see in which willage a gnome is residing.)
****
:)Enjoy
