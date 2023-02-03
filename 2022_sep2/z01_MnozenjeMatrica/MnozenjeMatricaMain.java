package z01_MnozenjeMatrica;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class MnozenjeMatricaMain {
    public static void main(String[] args) {

        String inPath1 = "./2022_sep2/z01_MnozenjeMatrica/01.in";
        String inPath2 = "./2022_sep2/z01_MnozenjeMatrica/02.in";
        int[][] mat1;
        int[][] mat2;

        try {
            mat1 = loadMatrix(inPath1);
            mat2 = loadMatrix(inPath2);

            System.out.println("Matrica 1:");
            printMatrix(mat1);

            System.out.println("Matrica 2:");
            printMatrix(mat2);

            int[][] product = multipyMatrix(mat1, mat2);
            printMatrix(product);

        } catch (FileNotFoundException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private static int[][] loadMatrix(String inputPath) throws FileNotFoundException {

        Scanner inputScanner = new Scanner(new FileInputStream(inputPath));
        int n = inputScanner.nextInt();

        int[][] returnMatrix = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                returnMatrix[i][j] = inputScanner.nextInt();
            }
        }

        inputScanner.close();

        return returnMatrix;
    }

    private static void printMatrix(int[][] matrix) {
        int n = matrix.length;
        for (int[] row : matrix) {
            for (int element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }

    private static int[][] multipyMatrix(int[][] mat1, int[][] mat2) throws IllegalArgumentException {
        if(mat1.length != mat2.length) {
            throw new IllegalArgumentException("Matrices have to be of same size");
        }

        int n = mat1.length;
        int[][] product = new int[n][n];
        AtomicInteger matrixSum = new AtomicInteger(0);

        return product;
    }
}


