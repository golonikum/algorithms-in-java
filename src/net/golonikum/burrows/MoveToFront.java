package net.golonikum.burrows;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.LinkedList;
import java.util.List;

public class MoveToFront {
    private final static int R = 256;
    private static StringBuilder asciiChars;

    private static StringBuilder initSequence() {
        if ( asciiChars == null ) {
            asciiChars = new StringBuilder();
            for (int i = 0; i < R; i++) {
                asciiChars.append( (char)i );
            }
        }
        return new StringBuilder(asciiChars);
    }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        StringBuilder sequence = initSequence();
        while ( !BinaryStdIn.isEmpty() ) {
            char c = BinaryStdIn.readChar();
            int index = sequence.indexOf( String.valueOf(c) );
            BinaryStdOut.write(index, 8);
            sequence.deleteCharAt(index);
            sequence.insert(0, c);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        StringBuilder sequence = initSequence();
        while ( !BinaryStdIn.isEmpty() ) {
            int index = BinaryStdIn.readInt(8);
            char c = sequence.charAt(index);
            BinaryStdOut.write(c);
            sequence.deleteCharAt(index);
            sequence.insert(0, c);
        }
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if ( args[0].equals("-") ) encode();
        else if ( args[0].equals("+") ) decode();
        else throw new IllegalArgumentException();
    }
}