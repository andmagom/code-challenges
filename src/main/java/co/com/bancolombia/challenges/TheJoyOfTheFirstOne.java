package co.com.bancolombia.challenges;

import java.io.*;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;
import java.util.stream.IntStream;

public class TheJoyOfTheFirstOne {

    public int[] letsGoParty(int[] bigArray, int resultSize){
         /* Using Streams I got 2.5 seconds with a 6-core machine 
        
        return IntStream
            .of(numbers)
            .parallel()
            .sorted()
            .limit(resultSize)
            .toArray();
        */

        /* Using PriorityQueue I got 5.8 seconds
        
            Queue<Integer> pq = new PriorityQueue<Integer>(Collections.reverseOrder());
            int total = 0;
            for(int n: numbers) {
                pq.add(n);
                total++;

                if(total > resultSize)
                    pq.poll();
            }

            int[] result = new int[resultSize];

            int i = 0;
            Iterator values = pq.iterator();

            while( values.hasNext() ) {
                result[i++] = (int) values.next();
            }

            return result;
        */

        /*
            Around 400 ms with this implementation. Important values are the max and min value known while we walk throught the array, so
            1) If our array result has less than 50 elements, we just add the value to the result array, and we check if the max or min value should be updated
            2) If we have more than 50 elements in our result array, and we have a new item: 
               2.1) we could get a value smaller than the min value known, that is a value that we should include in the result array, 
                    so we add it to the array and we remove the last element in the array since it is the max value, we update the max value, keeping it at the end of the result array

               2.2) we get a value bigger than the max value known, we can ignore that value

               2.3) we get a value between min and max, so we should add that value to the array, we remove the last element in the array since that value is out of the 50 first elements,
                    we update the max value, keeping it at the end of the result array
        */ 
        int min = bigArray[0]; // Setting min boundary known
        int max = bigArray[0]; // Setting max boundary known
        
        // Used linkedlist to avoid overheat when adding new values
        LinkedList<Integer> result = new LinkedList<Integer>(); 
        result.add(bigArray[0]);
        int total = 1;

        for(int i = 1; i<bigArray.length; i++) {
            int val = bigArray[i];

            // if val is less than min, we have a new min boundary and we shpuld include that value in the List
            if(val <= min) {
                if(total >= resultSize) {
                    result.pollLast();  // we remove the last element since we want to keep only the 50 first ones
                    max = getNewMax(result);    // since I did't keep an internal order, I have to walked throught full array to get the max element (50 values are not a lot, so I'm not worry about this overheat)
                }
                min = val;
                result.addFirst(val); // since val is the new min, we added to the beginning, Easy task for a linkedlist since it just add a new pointer to the root
            } else if(val <= max || total < resultSize) {   // we get a value between min and max, so we should add that value to the array or the total of item in array result are less than 50
                if(total >= resultSize) {   // if we have more than 50 items, we should remove the max one.
                    result.pollLast();  // we remove the last element in the array since that value is out of the 50 first elements
                    max = getNewMax(result);    // we get a new max
                }
                max = Math.max(max, val);   // Used when we are adding and there are less than 50 items on the result array 
                if(max == val) {
                    result.addLast(val); // Since val is the max known value, we added to the end of the List
                } else {
                    result.add(1, val); // Since val is between min and max, we added in the posicion 1 (Pos 0 is reserved of the min val), this to avoid walk throught the full List
                }
            }

            total++;
        }

        return result.stream().mapToInt(i->i).sorted().toArray();
    }

    private static int getNewMax(LinkedList<Integer> values) {
        ListIterator<Integer> ite = values.listIterator();
        int max = ite.next();
        ite.remove(); // skipping first value
        while(ite.hasNext()) {
            int val = ite.next();
            if(val > max) {
                ite.remove(); // Removing the max value known to add it to the last position
                ite.add(max); // Adding again the old max value
                max = val;
            }
        }
        values.addLast(max);    // adding the biggest val to the end
        return max;
    }


    public static void main(String[] args) throws IOException {
        TheJoyOfTheFirstOne joy = new TheJoyOfTheFirstOne();

        Random random = new Random(42);
        final IntStream ints = random.ints(200_000_000, 0, Integer.MAX_VALUE);
        int[] array = ints.toArray();
        int resultSize = 50;

        long init = System.currentTimeMillis();
        final int[] solution = joy.letsGoParty(array, resultSize);
        System.out.println("Time: " + (System.currentTimeMillis()-init) + "ms");
        System.out.println("Fin");

        int[] validAnswer = IntStream
        .of(array)
        .parallel()
        .sorted()
        .limit(resultSize)
        .toArray();

        boolean valid = true;
        for(int i = 0; i<resultSize; i++) {
            if( validAnswer[i] != solution[i] ) {
                valid = false;
                break;
            }
        }

        System.out.println("Right array: " + valid);
    }

}
