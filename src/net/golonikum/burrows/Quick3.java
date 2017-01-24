package net.golonikum.burrows;

import edu.princeton.cs.algs4.StdRandom;

public class Quick3 {
    private static final int CUTOFF =  15;   // cutoff to insertion sort
    public static String s;

    // do not instantiate
    private Quick3() { }

    public static void sort(int[] a) {
        StdRandom.shuffle(a);
        sort(a, 0, a.length-1, 0);
    }

    // return the dth character of s, -1 if d = length of s
    private static int charAt(int index, int d) {
        assert d >= 0 && d <= s.length();
        if (d == s.length()) return -1;
        return s.charAt( (index + d) % s.length() );
    }


    // 3-way string quicksort a[lo..hi] starting at dth character
    private static void sort(int[] a, int lo, int hi, int d) {

        // cutoff to insertion sort for small subarrays
        if (hi <= lo + CUTOFF) {
            insertion(a, lo, hi, d);
            return;
        }

        int lt = lo, gt = hi;
        int v = charAt(a[lo], d);
        int i = lo + 1;
        while (i <= gt) {
            int t = charAt(a[i], d);
            if      (t < v) exch(a, lt++, i++);
            else if (t > v) exch(a, i, gt--);
            else              i++;
        }

        // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
        sort(a, lo, lt-1, d);
        if (v >= 0) sort(a, lt, gt, d+1);
        sort(a, gt+1, hi, d);
    }

    // sort from a[lo] to a[hi], starting at the dth character
    private static void insertion(int[] a, int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(a[j], a[j-1], d); j--)
                exch(a, j, j-1);
    }

    // exchange a[i] and a[j]
    private static void exch(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    // is v less than w, starting at character d
    private static boolean less(int indV, int indW, int d) {
        //assert v.substring(0, d).equals(w.substring(0, d));
        int len = s.length();
        for (int i = d; i < len; i++) {
            if ( s.charAt( (indV + i) % len ) < s.charAt( (indW + i) % len ) ) return true;
            if ( s.charAt( (indV + i) % len ) > s.charAt( (indW + i) % len ) ) return false;
        }
        return false;
    }

    /**
     * Reads in a sequence of fixed-length strings from standard input;
     * 3-way radix quicksorts them;
     * and prints them to standard output in ascending order.
     */
    public static void main(String[] args) { }
}
