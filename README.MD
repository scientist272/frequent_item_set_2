# How to run:
 To run this program, get into out/artifacts/frequent_2_jar, then use this command:
java -jar frequent_2.jar, the result may take couple of minutes(depends on the performance 
of your computer, mine takes about two minutes).
# How to look over the source code:
  To look over the source code, get into src/com/company, FirstMapper.java
 is the first mapper, FirstReducer is the first reducer. The first pass will
 generate the candidate set. SecondMapper.java is second mapper, SecondReducer.java
 is the second reducer. The second pass will verify if the results of the first pass are
 true frequent item set. 
# Note:
 For better performance, this program will only output the largest k frequent set.