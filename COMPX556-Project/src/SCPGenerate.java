import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
//Mann Sheth - 1364689
public class SCPGenerate {
    public static int size;
    public static void main(String[] args) {
        int smallersize = 180;
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream("test300.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(out);
        Random r = new Random();
        size = 300;
        List<Integer> universalSet = new ArrayList<>();
        List<List<Integer>> subsets = new ArrayList<>();
        List<Integer> seenNumbers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
              universalSet.add(i+1);
        }

        for (int i = 0; i < smallersize-1; i++) {
            List<Integer> list = new ArrayList<>(universalSet);
            Collections.shuffle(list);
            List<Integer> subset = new ArrayList<>(list.subList(0, (r.nextInt(smallersize - 1) + 1)));
            subsets.add(subset);
        }
        for(List<Integer> sub : subsets)
        {
            seenNumbers.addAll(sub);
        }
        List<Integer> lastSubset = new ArrayList<>();
        for (int num : universalSet)
        {
            if(!seenNumbers.contains(num))
            {
                lastSubset.add(num);
            }
        }

        subsets.add(lastSubset);

        System.out.println(Arrays.toString(universalSet.toArray()));

        for (List<Integer>subset: subsets)
        {
            System.out.println(Arrays.toString(subset.toArray()));
        }
    }
}
