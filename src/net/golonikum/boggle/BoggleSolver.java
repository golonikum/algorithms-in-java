package net.golonikum.boggle;

import edu.princeton.cs.algs4.*;
import edu.princeton.cs.algs4.Queue;

import java.util.*;
import java.util.Stack;

public class BoggleSolver
{
    private TrieST dictionary;
    private Dice[][][] boggleGraph;
    private Set<String> result;

    private static final int R = 26;
    private static final int OFFSET = 65;

    // R-way trie node
    private static class Node {
        private boolean val;
        private Node[] next = new Node[R];
    }

    private class TrieST {
        private Node root;      // root of trie
        private int N;          // number of keys in trie

        public TrieST() {
        }

        public boolean get(String key) {
            Node x = get(root, key, 0);
            if (x == null) return false;
            return x.val;
        }

        public boolean contains(String key) {
            return get(key);
        }

        private Node get(Node x, String key, int d) {
            if (x == null) return null;
            if (d == key.length()) return x;
            char c = key.charAt(d);
            return get(x.next[c-OFFSET], key, d + 1);
        }

        public void put(String key, boolean val) {
            if (val == false) delete(key);
            else root = put(root, key, val, 0);
        }

        private Node put(Node x, String key, boolean val, int d) {
            if (x == null) x = new Node();
            if (d == key.length()) {
                if (x.val == false) N++;
                x.val = val;
                return x;
            }
            char c = key.charAt(d);
            x.next[c-OFFSET] = put(x.next[c-OFFSET], key, val, d + 1);

            return x;
        }

        public int size() {
            return N;
        }

        public boolean isEmpty() {
            return size() == 0;
        }

        public Iterable<String> keys() {
            return keysWithPrefix("");
        }

        public Iterable<String> keysWithPrefix(String prefix) {
            Queue<String> results = new Queue<String>();
            Node x = get(root, prefix, 0);
            collect(x, new StringBuilder(prefix), results);
            return results;
        }

        private void collect(Node x, StringBuilder prefix, Queue<String> results) {
            if (x == null) return;
            if (x.val != false) results.enqueue(prefix.toString());
            for (char c = 0; c < R; c++) {
                prefix.append(c);
                collect(x.next[c], prefix, results);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }

        public boolean prefixQuery(StringBuilder prefix) {
            Node node = root;
            int d = 0;

            while (d < prefix.length()) {
                char c = prefix.charAt(d);
                node = node.next[c-OFFSET];
                if (node == null) break;
                d++;
            }

            return d == prefix.length();
        }

        private void findPrefix(Node x, StringBuilder prefix, List<String> results) {
            if (x == null) return;
            if (x.val != false) results.add(prefix.toString());
            if (results.size() > 0) return;
            for (char c = 0; c < R; c++) {
                prefix.append(c);
                findPrefix(x.next[c], prefix, results);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }

        public void delete(String key) {
            root = delete(root, key, 0);
        }

        private Node delete(Node x, String key, int d) {
            if (x == null) return null;
            if (d == key.length()) {
                if (x.val != false) N--;
                x.val = false;
            }
            else {
                char c = key.charAt(d);
                x.next[c-OFFSET] = delete(x.next[c-OFFSET], key, d+1);
            }

            // remove subtrie rooted at x if it is completely empty
            if (x.val != false) return x;
            for (int c = 0; c < R; c++)
                if (x.next[c] != null)
                    return x;
            return null;
        }

    }

    private class Dice {
        public char letter;
        public int i;
        public int j;
        public Dice(char letter, int i, int j) {
            this.letter = letter;
            this.i = i;
            this.j = j;
        }
    }

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        this.dictionary = new TrieST();
        for (String word: dictionary) {
            this.dictionary.put(word, true);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        // precompute board
        int rows = board.rows(), cols = board.cols();
        Dice[][] diceBoard = new Dice[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                diceBoard[i][j] = new Dice(board.getLetter(i, j), i, j);
            }
        }
        boggleGraph = new Dice[rows][cols][];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                List<Dice> list = new ArrayList<Dice>();
                if (i > 0) {
                    if (j > 0) {
                        list.add(diceBoard[i-1][j-1]);
                    }
                    list.add(diceBoard[i-1][j]);
                    if (j < cols - 1) {
                        list.add(diceBoard[i-1][j+1]);
                    }
                }
                if (j > 0) {
                    list.add(diceBoard[i][j-1]);
                }
                if (j < cols - 1) {
                    list.add(diceBoard[i][j+1]);
                }
                if (i < rows - 1) {
                    if (j > 0) {
                        list.add(diceBoard[i+1][j-1]);
                    }
                    list.add(diceBoard[i+1][j]);
                    if (j < cols - 1) {
                        list.add(diceBoard[i+1][j+1]);
                    }
                }
                boggleGraph[i][j] = list.toArray(new Dice[list.size()]);
            }
        }

        // run
        result = new HashSet<String>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Stack<Dice> stack = new Stack<Dice>();
                stack.push(diceBoard[i][j]);
                find(buildWord(diceBoard[i][j].letter), stack, i, j);
            }
        }

        return result;
    }

    private StringBuilder buildWord(char letter) {
        StringBuilder sb = new StringBuilder();
        sb.append(letter);
        if (letter == 'Q') {
            sb.append("U");
        }
        return sb;
    }

    private void add(StringBuilder sb, char letter) {
        sb.append(letter);
        if (letter == 'Q') {
            sb.append("U");
        }
    }

    private void remove(StringBuilder sb, char letter) {
        sb.deleteCharAt(sb.length() - 1);
        if (letter == 'Q') {
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    private void find(StringBuilder word, Stack<Dice> path, int row, int col) {
        if (word.length() >= 3 && dictionary.contains(word.toString())) {
            result.add(word.toString());
        }
        for (Dice dice: boggleGraph[row][col]) {
            if (!path.contains(dice)) {
                path.push(dice);
                add(word, dice.letter);
                if (dictionary.prefixQuery(word))
                    find(word, path, dice.i, dice.j);
                path.pop();
                remove(word, dice.letter);
            }
        }
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (dictionary.contains(word)) {
            switch (word.length()) {
                case 0:
                case 1:
                case 2:
                    return 0;
                case 3:
                case 4:
                    return 1;
                case 5:
                    return 2;
                case 6:
                    return 3;
                case 7:
                    return 5;
                default:
                    return 11;
            }
        } else {
            return 0;
        }
    }

    public static void main(String[] args)
    {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board))
        {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}