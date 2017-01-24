package net.golonikum.burrows;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    // apply Burrows-Wheeler encoding, reading from standard input and writing to standard output
    public static void encode() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        for (int i = 0; i < csa.length(); i++) {
            if (csa.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }
        for (int i = 0; i < csa.length(); i++) {
            int index = csa.index(i) - 1;
            BinaryStdOut.write( s.charAt(index >= 0 ? index : s.length() - 1) );
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
    public static void decode() {
        int first = BinaryStdIn.readInt();
        String t = BinaryStdIn.readString();

        int[] next = new int[t.length()];
        char[] firstColumn = new char[t.length()];
        int R = 256;
        int[] count = new int[R+1];

        // calculate counters
        for (int i = 0; i < t.length(); i++)
            count[t.charAt(i) + 1]++;
        // transform counters into indices
        for (int r = 0; r < R; r++)
            count[r+1] += count[r];
        // spread the values
        for (int i = 0; i < t.length(); i++) {
            next[count[t.charAt(i)]] = i;
            firstColumn[count[t.charAt(i)]] = t.charAt(i);
            count[t.charAt(i)]++;
        }

        for (int i = 0; i < t.length(); i++) {
            BinaryStdOut.write( firstColumn[first] );
            first = next[first];
        }
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply Burrows-Wheeler encoding
    // if args[0] is '+', apply Burrows-Wheeler decoding
    public static void main(String[] args) {
        if      (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}