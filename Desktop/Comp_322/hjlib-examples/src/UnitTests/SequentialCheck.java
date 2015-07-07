package UnitTests;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marie on 7/3/15.
 */
public class SequentialCheck {

    public int sumSequential(int start, int end){
        int result = 0;
        for (int i = start; i <= end; i++){
            result += i;
        }
        return result;
    }

    public Integer maximumSearchSequential(final char[] dataArray, final char[] searchHit){
        Integer max = Integer.MIN_VALUE;
        for (int i = 0; i < dataArray.length; i++){
            if (dataArray[i] == searchHit[0]) {
                if (max.compareTo(i) < 0)
                    max = i;
            }
        }
        return max;
    }

    public Integer minimumSearchSequential(final char[] dataArray, final char[] searchHit){
        Integer min = Integer.MAX_VALUE;
        for (int i = 0; i < dataArray.length; i++){
            if (dataArray[i] == searchHit[0]) {
                if (min.compareTo(i) > 0)
                    min = i;
            }
        }
        return min;
    }

    public List<Integer> searchSequential(final char[] dataArray, final char [] searchHit){
        List<Integer> resultOptions = new ArrayList<Integer>();
        for (int i = 0; i < dataArray.length; i++){
            if (dataArray[i] == searchHit[0]) {
                resultOptions.add(i);
            }
        }
        return resultOptions;
    }

    public int[] incrementSequential(final int[] myArray) {
        for (int i = 0; i < myArray.length; i++) {
            myArray[i] += 1;
        }
        return myArray;
    }

    public int[] incrementTwiceSequential(final int[] myArray) {
        for (int i = 0; i < myArray.length; i++) {
            myArray[i] += 2;
        }
        return myArray;
    }

    public int sumArraySequential(final int[] myArray) {
        int sum = 0;
        for (int i = 0; i < myArray.length; i++) {
            sum += myArray[i];
        }
        return sum;
    }

    public int sum2DArraySequential(final int[][] myArray) {
        int sum = 0;
        for (int i = 0; i < myArray.length; i++){
            sum += sumArraySequential(myArray[i]);
        }
        return sum;
    }

}
