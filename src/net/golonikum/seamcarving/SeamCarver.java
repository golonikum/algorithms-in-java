package net.golonikum.seamcarving;

import edu.princeton.cs.algs4.Picture;

import java.awt.*;

public class SeamCarver {
    private int[][] picture;
    private int width;
    private int height;

    private void transpose() {
        int temp = width;
        width = height;
        height = temp;
        int[][] transposedPicture = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                transposedPicture[y][x] = picture[x][y];
            }
        }
        picture = transposedPicture;
    }

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture)                 {
        width = picture.width();
        height = picture.height();
        this.picture = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.picture[y][x] = picture.get(x, y).getRGB();
            }
        }
    }

    // current picture
    public Picture picture() {
        Picture pic = new Picture(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pic.set(x, y, new Color(picture[y][x]));
            }
        }
        return pic;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    private double sumOfGradients(int x, int y, boolean isX) {
        return squaredDelta(16, x, y, isX) + squaredDelta(8, x, y, isX) + squaredDelta(0, x, y, isX);
    }

    private double squaredDelta(int rgb, int x, int y, boolean isX) {
        int color1 = picture[isX ? y : y - 1][isX ? x - 1 : x];
        int color2 = picture[isX ? y : y + 1][isX ? x + 1 : x];
        double delta = ((color1 >> rgb) & 0xFF) - ((color2 >> rgb) & 0xFF);
        return delta * delta;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if ( x < 0 || x >= width || y < 0 || y >= height )
            throw new IndexOutOfBoundsException();

        if ( x == 0 || y == 0 || x == (width - 1) || y == (height - 1) ) {
            return 1000;
        } else {
            return Math.sqrt( sumOfGradients(x, y, true) + sumOfGradients(x, y, false) );
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose();
        int[] seam = findVerticalSeam();
        transpose();
        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        // check
        if ( width == 1 || height == 1 ) {
            return new int[height];
        }

        // destination and path arrays
        double[][] distTo = new double[height][width];
        int[][] pathTo = new int[height][width];

        // fill arrays
        for (int x = 0; x < width; x++) {
            distTo[1][x] = energy(x, 1);
        }
        for (int y = 1; y < height - 2; y++) {
            for (int x = 1; x < width - 1; x++) {
                for (int col = x - 1; col <= x + 1; col++) {
                    double dist = distTo[y][x] + energy(col, y+1);
                    if ( pathTo[y+1][col] == 0 || distTo[y+1][col] > dist ) {
                        distTo[y+1][col] = dist;
                        pathTo[y+1][col] = x;
                    }
                }
            }
        }

        // find minimal index
        int index = 0;
        for (int i = 1; i < width - 1; i++) {
            if ( distTo[height-2][i] < distTo[height-2][index] ) {
                index = i;
            }
        }

        // build minimal path
        int seam[] = new int[height];
        for (int row = height - 2; row > 0; row--) {
            seam[row] = index;
            index = pathTo[row][index];
        }
        seam[0] = seam[1];
        seam[height-1] = seam[height-2];

        return seam;
    }

    private boolean isValidSeam(int[] seam, boolean vertical) {
        if ( seam[0] < 0 || seam[0] >= (vertical ? width : height) ) {
            return false;
        }
        for (int i = 1; i < seam.length; i++) {
            if ( seam[i] < 0 || seam[i] >= (vertical ? width : height) || Math.abs(seam[i] - seam[i-1]) > 1 ) {
                return false;
            }
        }
        return true;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if ( seam == null )
            throw new NullPointerException();
        if ( seam.length != height || !isValidSeam(seam, true) || width <= 1 )
            throw new IllegalArgumentException();

        int[][] pic = new int[height][width-1];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width - 1; x++) {
                pic[y][x] = picture[y][x < seam[y] ? x : x + 1];
            }
        }

        picture = pic;
        width--;
    }
}