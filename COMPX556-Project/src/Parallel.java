import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//Mann Sheth - 1364689
public class Parallel {
    public static int numberOfAnts;
    public static int repetitions;
    public static int threads;
    public static int maxSize;
    public static String file;
    public static List<Integer> universalSet = new ArrayList<>();
    public static List<Double> globalPheromones = new ArrayList<>();
    public static List<List<Integer>> setofSubsets = new ArrayList<>();
    public static List<Integer> bestSolution = new ArrayList<>();
    public static List<Integer> worstSolution = new ArrayList<>();
    public static List<Ant> antList = new ArrayList<>();
    public static BufferedReader reader;
    public static List<Thread> threadList = new ArrayList<>();
    public static long startTime = System.nanoTime();

    public static void main(String[] args) {
        //Start time for benchmarking
        long startTime = System.nanoTime();

        //getting number of ants, iterations, number of threads from user, and test file.
        try
        {
            numberOfAnts = Integer.parseInt(args[0]);
            repetitions = Integer.parseInt(args[1]);
            threads = Integer.parseInt(args[2]);
            file = args[3];
            if (threads>numberOfAnts)
            {
                throw new RuntimeException("cannot have more threads than number of ants");
            }
        }
        catch (Exception e)
        {
            System.out.println("invalid arguments passed");
            e.printStackTrace();
        }
        maxSize = numberOfAnts/threads;

        //read the problem file
        readProblem(file);
        //sort the main set for ease of comparison later on
        Collections.sort(universalSet);
        //create the number of ants
        createAnts();

        //print some general statistics of the problem
        System.out.println("Number of Ants: " + antList.size());
        System.out.println("Universal Set: " + Arrays.toString(universalSet.toArray()));
        System.out.println("Subsets: ");
        int counter = 0;
        for(List<Integer> subset : setofSubsets)
        {
            System.out.println(Arrays.toString(subset.toArray()));
            if(counter == 3)
            {
                break;
            }
            counter++;
        }
        System.out.println("...Continued");
        System.out.println("Amount of pheromones initialized: " + globalPheromones.size());
        System.out.println("Pheromones initialized: " + Arrays.toString(globalPheromones.toArray()));
        System.out.println("Amount of subsets: " + setofSubsets);
        System.out.println("most ants in thread" + maxSize);
        //now we run the algorithm to construct a solution.
        construct();

        //run time of the algorithm itself.
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("TIME TAKEN: " + (double)totalTime/1_000_000_000);

        //some statistics from the program after running
        for(int index : bestSolution)
        {
            List<Integer> subset = setofSubsets.get(index);
            System.out.println(Arrays.toString(subset.toArray()));
        }
        double averageSize = 0;
        for(Ant ant : antList)
        {
            averageSize += ant.getCurrentSolution().size();
        }
        averageSize = averageSize/antList.size();
        System.out.println("Average solution size: " + averageSize);
        System.out.println("Size of best solution: " + bestSolution.size());
        System.out.println("Size of worst solution: " + worstSolution.size());
        System.out.println("Pheromones after Ants: " + Arrays.toString(globalPheromones.toArray()));


    }

    //method runs the ACO algorithm
    public static void construct()
    {
        int counter = 0;
        do
        {
            sendAnts();
            if(counter == 0)
            {
                bestSolution = new ArrayList<>(antList.get(0).getCurrentSolution());
                worstSolution = new ArrayList<>(antList.get(0).getCurrentSolution());

            }
            //update the runs after sending the ants
            counter++;
            System.out.println("Running iternation: " + counter);
            long endTime = System.nanoTime();
            long totalTime = endTime - startTime;
            //this finds the best solution at this point
            for(Ant ant : antList)
            {
                if(ant.getCurrentSolution().size() < bestSolution.size())
                {
                    bestSolution = new ArrayList<>(ant.getCurrentSolution());
                }

                if(ant.getCurrentSolution().size() > worstSolution.size())
                {
                    worstSolution = new ArrayList<>(ant.getCurrentSolution());
                }

                if( ((double)totalTime/1_000_000_000) > 400)
                {
                    break;
                }

                if(counter != repetitions)
                {
                    ant.reset();
                }
            }

            if( ((double)totalTime/1_000_000_000) > 400)
            {
                break;
            }
        }
        while (counter < repetitions);
    }

    //starts sending ants to find solution for one iteration of the algorithm
    public static void sendAnts() {
        List<List<Ant>> antPartitions = new ArrayList<>();

        for (int i = 0; i < antList.size(); i += maxSize) {
            antPartitions.add(antList.subList(i, Math.min(i + maxSize,
                    antList.size())));
        }
        for(List<Ant> ants : antPartitions)
        {
            pAnts antThread = new pAnts(ants);
            Thread t = new Thread(antThread);
            threadList.add(t);
            t.start();
        }

        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<Integer> allSetsVisitedByAnts = new ArrayList<>();
        for(Ant ant : antList)
        {
            allSetsVisitedByAnts.addAll(ant.getCurrentSolution());
        }

        //degrade pheromones first
        for(int index : allSetsVisitedByAnts)
        {
            globalPheromones.set(index, globalPheromones.get(index)/3);
            //if the pheromones are degraded below the minimum set it back to the minimum
            if (globalPheromones.get(index) < 0.01) globalPheromones.set(index, 0.01);
        }
        //this will increment the pheromones at a given index based on the results of the ants
        for(int index : allSetsVisitedByAnts)
        {
            globalPheromones.set(index, globalPheromones.get(index) + 1);
        }
    }

    public static void createAnts()
    {
        for (int i = 0; i < numberOfAnts; i++)
        {
            antList.add(new Ant());
        }
    }

    public static void readProblem(String filename)
    {
        try
        {
            reader = new BufferedReader(new FileReader(filename));
            //first line of the txt file is the universal set
            String line = reader.readLine();
            //we need to know add these numbers to the universalSet List.
            for(String number : line.split(", "))
            {
                universalSet.add(Integer.parseInt(number));
            }
            //now we will get the subsets of the universal set
            line = reader.readLine();
            while(line != null)
            {
                List<Integer> subset = new ArrayList<>();
                for(String number: line.split(", "))
                {
                    subset.add(Integer.parseInt(number));
                }
                setofSubsets.add(subset);
                //we need to set the pheromones for each subset added
                globalPheromones.add(0.01);//it will not be initialised to 0 as it is not helpful when multiplied with heuristic score method
                line = reader.readLine();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

//runnable process to run in parallel
class pAnts implements Runnable
{
    private List<Ant> _ants;
    public pAnts(List<Ant> ants)
    {
        _ants = ants;
    }
    @Override
    public void run() {
        for (Ant ant : _ants)
        {
            ant.randomStart(Parallel.setofSubsets);
        }

        for (Ant ant : _ants)
        {
            while (!ant.getVisitedAll() && !ant.universalSetSeen(Parallel.universalSet, Parallel.setofSubsets))
            {
                ant.addToVisitedSets(ant.choosePath(Parallel.setofSubsets, Parallel.globalPheromones));
            }
        }
    }

    public List<Ant> getAnts()
    {
        return _ants;
    }
}
