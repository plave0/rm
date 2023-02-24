package z03_student;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class Student {

    private String id;
    private String name;
    private String surename;
    private List<Integer> grades;

    public Student(String id, String name, String surename) {
        this.id = id;
        this.name = name;
        this.surename = surename;
        this.grades = new Vector<>();
    }

    public String getId() {
        return id;
    }

    public double getAverage() {

        return this.grades.stream().reduce(0, Integer::sum) / (double) this.grades.size();
    }

    public Student(String line) {

        StringTokenizer lineTokenizer = new StringTokenizer(line);
        this.id = lineTokenizer.nextToken();
        this.name = lineTokenizer.nextToken();
        this.surename = lineTokenizer.nextToken();

        this.grades = new Vector<>();
        while (lineTokenizer.hasMoreTokens()) {
            this.grades.add(Integer.parseInt(lineTokenizer.nextToken()));
        }
    }

    @Override
    public String toString() {
        return this.name + " " + this.surename;
    }
}
