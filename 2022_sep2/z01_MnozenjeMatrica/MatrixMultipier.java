package z01_MnozenjeMatrica;

import java.util.concurrent.atomic.AtomicInteger;

public class MatrixMultipier extends Thread {

    private int row;
    private int col;
    private int[][] mat1;
    private int[][] mat2;
    private int[][] productMatrix;

    public MatrixMultipier(int row, int col, int[][] mat1, int[][] mat2, int[][] productMatrix) {
        this.row = row;
        this.col = col;
        this.mat1 = mat1;
        this.mat2 = mat2;
        this.productMatrix = productMatrix;
    }

    @Override
    public void run() {

        int n = mat1.length;
        int dotProduct = 0;

        for (int i = 0; i < n; i++) {
                dotProduct += mat1[this.row][i] * mat2[i][this.col];
        }

        this.productMatrix[this.row][this.col] = dotProduct;
    }
}
