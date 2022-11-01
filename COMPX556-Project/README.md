For this project two versions of the implementation exist.

-------------------------------------------------------------
Sequential.java takes 3 arguments.

number of ants, repitions, problem text file location.

-------------------------------------------------------------

Parallel.java takes 4 arguments.

number of ants, repitions, number of threads, problem text file location.

-------------------------------------------------------------

To generate a problem using SCPGenerate, it is more of a manual process as the variables of smllersize and size need to be changed and a filepath is needed in the code
to choose where the output will go.
Where size is the universal set size and smallersize is the number of subsets. I went with 60 percent of the universal set size for the number of subsets.

After and out put is made,
in the file all square brackets must be removed using find and replace.

SCPGenerate.java is more for generating solutions for debugging and development thus its rought nature. 3 files are already given for testing.
