/*
 * SatisfactionSumJudge.java
 *
 * Created Wednesday June 30 2004 12:17pm
 *
 * Copyright 2003, Spallation Neutron Source
 * Oak Ridge National Laboratory
 * Oak Ridge, TN 37830
 */
 
 package xal.tools.solver.solutionjudge;
  
 import xal.tools.messaging.MessageCenter;
 
 import xal.tools.solver.*;
 
 import java.util.*;
 
 /**
 * SatisfactionSumJudge is a solution judge that decides whether a solution
 * should be kept based on the weighted sum of all the objective's satisfaction.
 *
 * @author ky6
 * @author t6p
 */
 public class SatisfactionSumJudge extends SolutionJudge {
	 protected final double DEFAULT_WEIGHT = 1.0;
	 protected double _bestWeightedSum;
	 protected List<Trial> _optimalSolutions;
	 protected Map<Objective,Double> _objectiveWeightMap;
	 
	 
	 /**Creates a new SatisfactionJudge instance*/
	 public SatisfactionSumJudge() {
		 _bestWeightedSum = 0.0;
		 _objectiveWeightMap = new HashMap<Objective,Double>();
		 _optimalSolutions = new ArrayList<Trial>();		 
	 }
	 
	 
	 /** Reset the satisfaction sum judge. */
	 public void reset() {
		 _bestWeightedSum = 0.0;
		 _optimalSolutions = new ArrayList<Trial>();
		 _objectiveWeightMap = new HashMap<Objective,Double>();
	 }
		 
	 
	 /**
	 * Set the weight of an objective.
	 * @param objective The objective to weight.
	 * @param weight The weight to give the objective.
	 */
	 public void setWeight( final Objective objective, final double weight ) {
		 _objectiveWeightMap.put( objective, weight );
	 }
	 
	 
	 /**
	 * Get the weight of an objective.
	 * @return The weight of the specified objective
	 */
	 private double getWeight( final Objective objective ) {
		 final Double weight = _objectiveWeightMap.get( objective );
		 return ( weight == null ) ? DEFAULT_WEIGHT : weight.doubleValue();		 
	 } 
		 
		 
	 /**
	 * Get the optimal solutions.
	 * @return A list of solutions
	 */
	 public List<Trial> getOptimalSolutions() {
		 return _optimalSolutions;	 	 
	 }
	 
	 
	 /**
	 * Judge the trial.
	 * @param trial the trial to judge.
	 */
	 public void judge( final Trial trial ) {
		 double weightedSum = 0.0;
		 double totalWeight = 0.0;
		 
		 // Calculate the overall satisfaction which is simply the weighted sum of all the score satisfactions.
		 final Iterator objectiveIter = trial.getProblem().getObjectives().iterator();		 		 
		 while( objectiveIter.hasNext() ) {
			 final Objective objective = (Objective)objectiveIter.next();
			 final double satisfaction = trial.getScore( objective ).getSatisfaction();
			 final double weight = getWeight( objective );
			 totalWeight += weight;
			 weightedSum += satisfaction * weight;
		 }
		 weightedSum /= totalWeight;
		 trial.setSatisfaction( weightedSum );
		 
		 if( weightedSum == _bestWeightedSum ) {
			 _optimalSolutions.add( trial );
			 _eventProxy.foundNewOptimalSolution( this, _optimalSolutions, trial );
		 }
		 else if( weightedSum > _bestWeightedSum ) {
			 _bestWeightedSum = weightedSum;
			 _optimalSolutions.clear();
			 _optimalSolutions.add( trial );
			 _eventProxy.foundNewOptimalSolution( this, _optimalSolutions, trial );
		 }
	 }
 }


