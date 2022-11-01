import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
//Mann Sheth - 1364689
public class Ant {

    public List<Integer> getCurrentSolution() {
        return setsVisited;
    }
    public boolean getVisitedAll() { return visitedAll; }

    private final List<Integer> visitedNumbers = new ArrayList<>();
    private final List<Integer> setsVisited = new ArrayList<>();//the actual path of the ant (I guess we will call this the solution)
    private boolean visitedAll = false;//boolean if we have seen all location.

    //this empty constructor is just used for the creation of the ant object.
    public Ant() {}

    /**
     * This method will choose wat set to go to next to using the pheromones for
     * @param sets all the sets in the problem that need to be combined to create have the same values as the universal set
     * @param pheromones the global pheromones for all the possible set choices.
     * @return
     */
    public synchronized int choosePath(List<List<Integer>> sets, List<Double> pheromones)
    {
        //The array of probabilities for each possible choice which is the product of pheromones and cost.
        List<Double> pList = new ArrayList<>();
        List<Integer> scoreOfSets = new ArrayList<>();

        //this is to change the importance of what (pheromones/heuristic) is given more weight during probability calculations
        double alpha = 1;//for pheromones
        double beta = 1;//for heuristic

        //the possible sets the ant can go to for its next step.
        List<Integer> possibleSets = new ArrayList<>();

        Random r = new Random();

        //what will be the next set to go in the path by default its negative one to make sure construction ends if no next step is found.
        int nextSetIndex = -1;

        if(!visitedAll)
        {
            //we go through all the possible sets available and check if the ant has visited them if it hasn't, then we get the pheromones for the set and the set index itself.
            for (int i = 0; i < sets.size(); i++)
            {
                if(!hasVisited(i))
                {
                    possibleSets.add(i);
                    pList.add(pheromones.get(i));
                }
            }
            try
            {
                if (possibleSets.size() != 0)
                {
                    scoreOfSets = getScoreOfPossibilities(possibleSets, sets);
                    for (int i = 0; i < scoreOfSets.size(); i++)
                    {
                        //if there is nothing to gain from choosing a certain set remove it from the selection pool
                        if(scoreOfSets.get(i) == 0)
                        {
                            scoreOfSets.remove(i);
                            possibleSets.remove(i);
                            pList.remove(i);
                        }
                    }
                    //now we get the product of the pheromones and the heuristic function score to get the numerator for all the sets
                    for(int i = 0; i < possibleSets.size(); i++)
                    {
                        double phero = pList.get(i);
                        int hscore = scoreOfSets.get(i);
                        double numerator = (Math.pow(phero, alpha))*(Math.pow((double)hscore, beta));
                        pList.set(i, numerator);
                    }
                    double denominator = 0;
                    //now we have to normalise the probabilities in the list first sum all the numerator values
                    for(double numerator: pList)
                    {
                        denominator += numerator;
                    }
                    //then we normalise by division to get probabilities
                    for (int i = 0; i < pList.size(); i++)
                    {
                        double n = pList.get(i);
                        double normalised = n/denominator;
                        pList.set(i, normalised);
                    }
                    //now we select the choice based on the probability
                    double random = r.nextDouble();
                    double cumulativeProb = 0.0;

                    for (int i = 0; i < pList.size(); i++)
                    {
                        cumulativeProb += pList.get(i);
                        if(random <=cumulativeProb)
                        {
                            nextSetIndex = possibleSets.get(i);
                            break;
                        }
                    }
                    //after getting the set based on the random selection with probability taken into account we add the
                    //unique numbers from set the was chosen to the visitedNumbers List
                    for(int num: sets.get(nextSetIndex))
                    {
                        if(!(visitedNumbers.contains(num)))
                        {
                            visitedNumbers.add(num);
                        }
                    }
                }
                else
                {
                    visitedAll = true;
                }
            }
            catch (Exception e)
            {
                System.err.println("Error: cannot find next suitable set to add to path within Ant.Java choosePath method");
                e.printStackTrace();
            }
            return nextSetIndex;
        }
        else
        {
            return -1;
        }
    }

    public synchronized void addToVisitedSets(int setIndex)
    {
        if (setIndex != -1) setsVisited.add(setIndex);
    }

    //this is a sanity check to make sure the path that the ant has chooses actually encompasses all the numbers in the universal set
    public boolean universalSetSeen(List<Integer> universalSet, List<List<Integer>> setOfSubsets)
    {
        List<Integer> visitedDoubleCheck = new ArrayList<>();

        for(int index : setsVisited)
        {
            for(int number : setOfSubsets.get(index))
            {
                if(!(visitedDoubleCheck.contains(number)))
                {
                    visitedDoubleCheck.add(number);
                }
            }
        }
        Collections.sort(visitedDoubleCheck);
        return visitedDoubleCheck.equals(universalSet);

    }

    public void randomStart(List<List<Integer>> globalOptions)
    {
        Random random = new Random();
        int randomIndex = random.nextInt(globalOptions.size());
        setsVisited.add(randomIndex);
        for(int num: globalOptions.get(randomIndex))
        {
            if (!(visitedNumbers.contains(num)))
            {
                visitedNumbers.add(num);
            }
        }
    }

    //this goes through all the possible sets to travel and gets the score depending on how many new numbers are present in the option available.
    public List<Integer> getScoreOfPossibilities(List<Integer> p, List<List<Integer>> sets)
    {
        List<Integer> scoreOfPossibilities = new ArrayList<>();
        for (int i = 0; i < p.size(); i++) {
            int score = 0;
            for(int j : sets.get(p.get(i)))
            {
                if(!(visitedNumbers.contains(j)))
                {
                    score++;
                }
            }
            scoreOfPossibilities.add(score);
        }
        return scoreOfPossibilities;
    }
    //this resets the path of the ants once it does one run;
    public void reset()
    {
        visitedAll = false;
        setsVisited.clear();
        visitedNumbers.clear();
    }

    public boolean hasVisited(int index)
    {
        //this will go through all the indexes visited and checks if an index is already within the list that has been created.
        for(int i: setsVisited)
        {
            if(index == i) return true;
        }
        return false;
    }
}

