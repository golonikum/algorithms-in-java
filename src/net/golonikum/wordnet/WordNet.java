package net.golonikum.wordnet;

import edu.princeton.cs.algs4.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordNet {
    private Map<String,Bag<Integer>> nounsMap = new HashMap<String, Bag<Integer>>();
    private List<String> synsetsList = new ArrayList<String>();
    private Digraph digraph;
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new NullPointerException();

        // read synsets
        In in = new In(synsets);
        while (in.hasNextLine()) {
            String[] synset = in.readLine().split(",");
            int id = Integer.parseInt(synset[0]);
            synsetsList.add(synset[1]);
            String[] nouns = synset[1].split(" ");
            for (String noun: nouns) {
                if (nounsMap.containsKey(noun)) {
                    nounsMap.get(noun).add(id);
                } else {
                    Bag<Integer> ids = new Bag<Integer>();
                    ids.add(id);
                    nounsMap.put(noun, ids);
                }
            }
        }

        // read hypernyms
        in = new In(hypernyms);
        digraph = new Digraph(synsetsList.size());
        while (in.hasNextLine()) {
            String[] hypernym = in.readLine().split(",");
            int v = Integer.parseInt(hypernym[0]);
            for (int i = 1; i < hypernym.length; i++) {
                digraph.addEdge(v, Integer.parseInt(hypernym[i]));
            }
        }

        // check if digraph is one rooted DAG
        DirectedCycle dc = new DirectedCycle(digraph);
        int rootCount = 0;
        for (int i = 0; i < digraph.V(); i++) {
            if ( digraph.outdegree(i) == 0 ) {
                rootCount++;
            }
        }
        if (dc.hasCycle() || rootCount > 1)
            throw new IllegalArgumentException();

        // create SAP object
        sap = new SAP(digraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounsMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new NullPointerException();

        return nounsMap.containsKey(word);
    }

    // distance between nounA and nounB
    public int distance(String nounA, String nounB) {
        if ( nounA == null || nounB == null )
            throw new NullPointerException();
        if ( !nounsMap.containsKey(nounA) || !nounsMap.containsKey(nounB) )
            throw new IllegalArgumentException();

        return sap.length( nounsMap.get(nounA), nounsMap.get(nounB) );
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path
    public String sap(String nounA, String nounB) {
        if ( nounA == null || nounB == null )
            throw new NullPointerException();
        if ( !nounsMap.containsKey(nounA) || !nounsMap.containsKey(nounB) )
            throw new IllegalArgumentException();

        int ancestorId = sap.ancestor( nounsMap.get(nounA), nounsMap.get(nounB) );
        return synsetsList.get(ancestorId);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        while (!StdIn.isEmpty()) {
            String nounA = StdIn.readString();
            String nounB = StdIn.readString();
            int distance = wordnet.distance(nounA, nounB);
            String synset = wordnet.sap(nounA, nounB);
            StdOut.printf("distance = %d, synset = %s\n", distance, synset);
        }
    }
}