package net.golonikum.wordnet;

import edu.princeton.cs.algs4.*;

public class SAP {
    private Digraph digraph;
    private int shortest = -1;
    private int ancestor = -1;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        digraph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        findSAP(convert(v), convert(w));
        return shortest;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        findSAP(convert(v), convert(w));
        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        findSAP(v, w);
        return shortest;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        findSAP(v, w);
        return ancestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

    private Iterable<Integer> convert(int vertex) {
        Bag<Integer> iterable = new Bag<Integer>();
        iterable.add(vertex);
        return iterable;
    }

    private void findSAP(Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths bfdp1 = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfdp2 = new BreadthFirstDirectedPaths(digraph, w);

        // find all ancestors
        Bag<Integer> ancestors = new Bag<Integer>();
        for (int i = 0; i < digraph.V(); i++) {
            if ( bfdp1.hasPathTo(i) && bfdp2.hasPathTo(i) ) {
                ancestors.add(i);
            }
        }

        // find shortest ancestral path
        shortest = -1;
        ancestor = -1;
        for (int a: ancestors) {
            int length = 0;
            for ( int j: bfdp1.pathTo(a) ) length++;
            for ( int j: bfdp2.pathTo(a) ) length++;
            length -= 2;
            if ( shortest == -1 || length < shortest ) {
                shortest = length;
                ancestor = a;
            }
        }
    }
}
