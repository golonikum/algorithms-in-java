package net.golonikum.burrows;

import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {
    private String s;
    private int[] indices;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if ( s == null ) throw new NullPointerException();
        this.s = s;
        indices = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            indices[i] =i;
        }
        Quick3.s = s;
        Quick3.sort(indices);
    }

    // length of s
    public int length() {
        return s.length();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i > length()) throw new IndexOutOfBoundsException();
        return indices[i];
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
        for (int i = 0; i < csa.length(); i++) {
            StdOut.println(csa.index(i));
        }
    }
}