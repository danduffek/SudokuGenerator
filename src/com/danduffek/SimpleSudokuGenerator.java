package com.danduffek;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

// Ridiculously simple Sudoko generator. puzzle is not very complex and pattern is easy to see.

public class SimpleSudokuGenerator {

    private static int[][] puzzle = new int[9][9];

    public static void main(String[] args) {

        System.out.println("This is a simple puzzle generation: ");
        System.out.println();
        simplePuzzle();
        System.out.println();
        System.out.println("And here is a more complex generation: ");
        System.out.println();
        complexPuzzle();
    }

    private static void simplePuzzle()
    {

        // ArrayList is a lot like an array, but it is an object. Which means you can do more things with it like easily add and remove elements.
        ArrayList<Integer> shuffledNumbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        int startIndex = 0, startIndexReset = 1, index;

        puzzle = new int[9][9];

        // Collection.shuffle is an easy way to randomly organize a list.
        // Comment this line if you want the pattern to be easier to see.
        Collections.shuffle(shuffledNumbers);

        // Loop through the nine rows of the puzzle
        for(int row=0; row < 9; row++)
        {

            // startIndex indicates where in the ArrayList to start copy the "random" list of numbers into the puzzle.
            if(startIndex >= 9) {
                // If the startIndex is larger than the max length (9) of the row, need to reset the startIndex.
                // However we don't want to reset it back to 0 because it will repeat a previous line.
                startIndex = startIndexReset++;
            }

            // index will be used to get the number from the list of random numbers.
            index = startIndex;

            // Set the number for each of the columns in this row of the puzzle.
            for(int col=0; col < 9; col++)
            {

                puzzle[row][col] = shuffledNumbers.get(index++);

                // Remember the list of random numbers is only 9 elements long.
                // If index is equal to 9 we are at the end of that list.
                // However it is very likely that we are not done looping the columns in the puzzle,
                // so we need to set the index back to the start.
                if(index == 9)
                    index = 0;

            }

            // Here we are going to move down one line, and to avoid having duplicates need to jump forward in the
            // list of random numbers by 3. This will prevent having duplicate numbers in the smaller 3x3 squares.
            startIndex = startIndex + 3;
        }

        // Now some silly stuff to print out the puzzle.
        // Look below to see the implementation of the function printPuzzle.
        System.out.println("The puzzle: ");
        printPuzzle(false);
        System.out.println();
        System.out.println("The solution: ");
        printPuzzle(true);

    }

    private static void complexPuzzle()
    {
        int colLimit = 9;

        // ArrayList is a lot like an array, but it is an object. Which means you can do more things with it like easily add and remove elements.
        ArrayList<Integer> shuffledNumbers;

        puzzle = new int[9][9];

        for(int row = 0; row < 9; row = row+3)
        {

            for(int col = 0; col < colLimit; col = col+3)
            {
                shuffledNumbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
                Collections.shuffle(shuffledNumbers);
                System.out.println("Row: " + row + " Col: " + col + " shuffled: " + shuffledNumbers.toString());
                fillSmallSquare(row, col, shuffledNumbers);
            }

            colLimit = colLimit - 3;
        }

        // Now I need to change approach?
        // Three of the smallSquares shouldn't be filled so much as randomly...
        // but rather as if you were trying to solve the puzzle.
        // TODO temp just to fill the squares.
        shuffledNumbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Collections.shuffle(shuffledNumbers);
        System.out.println("Row: 3 Col: 6 shuffled: " + shuffledNumbers.toString());
        fillSmallSquare(3, 6, shuffledNumbers);

        shuffledNumbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Collections.shuffle(shuffledNumbers);
        System.out.println("Row: 6 Col: 3 shuffled: " + shuffledNumbers.toString());
        fillSmallSquare(6, 3, shuffledNumbers);

        shuffledNumbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Collections.shuffle(shuffledNumbers);
        System.out.println("Row: 6 Col: 6 shuffled: " + shuffledNumbers.toString());
        fillSmallSquare(6, 6, shuffledNumbers);

        // Now some silly stuff to print out the puzzle.
        // Look below to see the implementation of the function printPuzzle.
        System.out.println("The puzzle: ");
        printPuzzle(false);
        System.out.println();
        System.out.println("The solution: ");
        printPuzzle(true);

    }

    private static void fillSmallSquare(int rowStart, int colStart, ArrayList<Integer> numbers)
    {
        int numIndex = 0;

        for(int row = rowStart; row < rowStart + 3; row++)
        {

            for(int col = colStart; col < colStart + 3; col++)
            {
                // Loop through the shuffled numbers until we find one that works, or we run out of numbers.
                while((numIndex < numbers.size()) && (!isValid(numbers.get(numIndex), row, col)))
                    numIndex++;

                if(numIndex < numbers.size()) {
                    // We found one that works.
                    puzzle[row][col] = numbers.get(numIndex);
                    numbers.remove(numIndex);
                }
                else{
                    // There are no valid numbers in the list that work....
                    // need to replace it with a number already placed.
                    replaceWithExisting(rowStart, colStart, row, col, numbers);
                }

                numIndex = 0;

            }

        }

    }

    private static void specialCaseRow(int row, int col)
    {
        ArrayList<Integer> shuffledNumbers;


        // Find the list of numbers that is valid for this row.
        shuffledNumbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        for(int i=0; i < shuffledNumbers.size(); i++)
        {
            if(!isValidForRow(shuffledNumbers.get(i), row, col))
                shuffledNumbers.remove(i);
        }

        // TODO At this point we know the three numbers will work for the row, but the given order may conflict with
        // TODO the column. Maybe a solution would be to find an order that would work and then put them in the puzzle.
    }

    private static boolean doesRowEqualColumn(ArrayList<Integer> shuffledNumbers, int rowStart, int colStart)
    {
        ArrayList<Integer> col;
        int rowBase = rowStart - 3;
        boolean isEqual = false;

        Collections.sort(shuffledNumbers);

        for(int c=colStart; c < colStart + 3; c++)
        {

            col = new ArrayList<>();

            for(int r=rowBase; (r < rowBase + 3); r++)
            {
                col.add(puzzle[r][c]);
            }

            if(shuffledNumbers.equals(col))
            {
                isEqual = true;
                break;
            }

            colStart = colStart - 3;
            if(colStart < 0)
                break;
        }

        // re-shuffle the numbers;
        Collections.shuffle(shuffledNumbers);

        return isEqual;
    }

    private static void replaceWithExisting(int rowStart, int colStart, int targetRow, int targetCol, ArrayList<Integer> numbers)
    {
        int numIndex = 0, temp;
        boolean done = false;

        while((!done) && (numIndex < numbers.size())) {

            for (int row = rowStart; (row < rowStart + 3) && (!done); row++) {

                for (int col = colStart; (col < colStart + 3) && (!done); col++) {
                    // First save the value at the location in the puzzle.
                    // Then zero it out so it won't incorrectly fail the invalid test.
                    temp = puzzle[row][col];
                    puzzle[row][col] = 0;

                    // Check if they can be swapped.
                    if ((isValid(temp, targetRow, targetCol)) && (isValid(numbers.get(numIndex), row, col))) {
                        puzzle[row][col] = numbers.get(numIndex);
                        numbers.remove(numIndex);
                        puzzle[targetRow][targetCol] = temp;
                        done = true;
                    } else {
                        // Can't be swapped so replace the value in the puzzle.
                        puzzle[row][col] = temp;
                    }
                }

            }

            if(!done)
                numIndex++;

        }
    }

    private static  boolean isValid(int num, int row, int col)
    {
        return isValidForRow(num, row, col) && isValidForCol(num, row, col);
    }

    private static boolean isValidForRow(int num, int row, int col)
    {
        for(int c = 0; c < col; c++)
        {
            if(puzzle[row][c] == num)
                return false;
        }

        return true;

    }

    private static boolean isValidForCol(int num, int row, int col)
    {
        for(int r = 0; r < row; r++)
        {
            if(puzzle[r][col] == num)
                return false;
        }

        return true;
    }

    private static void printPuzzle(boolean showSolution)
    {
        // showSolution is a parameter. It will be used to identify if the puzzle will be shown with all elements visible
        // or with some blank spots.
        Random randGen = new Random();

        // Loop through each row of the puzzle.
        for(int row=0; row < 9; row++)
        {

            // Loop through each column.
            for(int col=0; col < 9; col++)
            {

                // If showSolution is true, then print the number as it appears in the puzzle.
                if(showSolution) {
                    System.out.print(puzzle[row][col] + " ");
                }
                else {

                    // If showSolution is false then do a 50/50 guess to decide if the number will be printed or a blank spot.
                    if (randGen.nextInt(10) > 5)
                        System.out.print(puzzle[row][col] + " ");
                    else
                        System.out.print("_ ");
                }

                // This is just formatting code to print a vertical bar after the 2nd and 5th columns
                if(col == 2 || col == 5)
                    System.out.print("| ");

                randGen = new Random();
            }

            System.out.println();

            // This is just some more formatting code to print a horizontal line between the 2nd and 5th rows.
            if(row == 2 || row == 5)
            {
                System.out.print("---------------------");
                System.out.println();
            }
        }
    }

}
