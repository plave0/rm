package z01_farm;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Farm {

    private char[] farm;
    private int farmSize;
    private Lock farmLock;

    public Farm(int farmSize) {
        this.farmSize = farmSize;
        this.farm = initFarm(farmSize);
        this.farmLock = new ReentrantLock();
    }

    public Farm(char[] farm, int farmSize) {
        this.farm = farm;
        this.farmSize = farmSize;
    }

    public int getFarmSize() {
        return farmSize;
    }

    public char[] getFarm() {
        return farm;
    }

    public void mark(int i, int j) {
        this.farmLock.lock();
        try {
            this.farm[i * this.farmSize + j] = 'x';
        }
        finally {
            this.farmLock.unlock();
        }
    }

    public void repair(int i, int j) {
        this.farmLock.lock();
        try {
            this.farm[i * this.farmSize + j] = 'o';
        }
        finally {
            this.farmLock.unlock();
        }
    }

    public void printFarm() {

        StringBuilder output = new StringBuilder();

        output.append("+|");
        for (int i = 0; i != this.farmSize; ++i) { output.append(i); }
        output.append('\n');

        output.append("++");
        for (int i = 0; i != this.farmSize; ++i) { output.append('-'); }
        output.append('\n');

        for (int j = 0; j != this.farmSize; ++j) {

            output.append(j);
            output.append('|');
            for (int i = 0; i != this.farmSize; ++i) { output.append(this.farm[j * farmSize + i]); }
            output.append('\n');
        }

        System.out.println(output.toString());
    }

    private char[] initFarm(int farmSize) {

        char[] farm = new char[farmSize * farmSize];
        Arrays.fill(farm, 'o');
        return farm;
    }


}
