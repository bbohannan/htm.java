package org.numenta.nupic.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.numenta.nupic.data.PatternMachine;
import org.numenta.nupic.data.SequenceMachine;
import org.numenta.nupic.integration.TemporalMemoryTestMachine.DetailedResults;
import org.numenta.nupic.research.Parameters;
import org.numenta.nupic.research.TemporalMemory;

/**
 * Base class for integration tests of the {@link TemporalMemory}
 * 
 * @author Chetan Surpur
 * @author David Ray
 * @see BasicTemporalMemoryTest
 */
public abstract class AbstractTemporalMemoryTest {
	protected TemporalMemory tm;
	protected PatternMachine patternMachine;
	protected SequenceMachine sequenceMachine;
	protected TemporalMemoryTestMachine tmTestMachine;
	protected Parameters parameters;
	protected List<Set<Integer>> sequence;
	
	/**
	 * Called from each test to instantiate a fresh {@link TemporalMemory}
	 * object with configured parameters for the test.
	 * 
	 * @see	Parameters
	 * @see TemporalMemory
	 */
	protected void initTM() {
		tm = new TemporalMemory();
		if(parameters != null && parameters.getMap().size() > 0) {
			Parameters.apply(tm, parameters);
		}
		tm.init();
	}
	
	/**
	 * Validates the {@link Parameters} and their existence.
	 * @return
	 */
	private boolean checkParams() {
		return parameters != null && parameters.getMap().size() > 0;
	}
	
	/**
	 * Initializes the test data generators
	 * 
	 * @param patternMachine
	 */
	protected void finishSetUp(PatternMachine patternMachine) {
		this.patternMachine = patternMachine;
		this.sequenceMachine = new SequenceMachine(patternMachine);
		this.tmTestMachine = new TemporalMemoryTestMachine(tm);
	}
	
	/**
	 * Displays the test setup
	 * 
	 * @param sequence		list of sequences to be input the {@link TemporalMemory}
	 * @param learn			flag indicating whether the algorithm will execute learning functions
	 * @param num			number of times "sequence" should be repeated
	 */
	protected void showInput(List<Set<Integer>> sequence, boolean learn, int num) {
		if(checkParams()) {
			System.out.println("New TemporalMemory Parameters:");
			System.out.println(parameters);
		}
		
		String sequenceText = sequenceMachine.prettyPrintSequence(sequence, 1);
		
		String learnText = learn ? "(learning enabled)" : "(learning disabled)";
		System.out.println("Feeding sequence " + learnText + " " + sequenceText + " [" + num + " times]");
	}
	
	/**
	 * Starts the inputting of sequence(s) into the {@link TemporalMemory}
	 * 
	 * @param sequence		list of sequences to be input the {@link TemporalMemory}
	 * @param learn			flag indicating whether the algorithm will execute learning functions
	 * @param num			number of times "sequence" should be repeated
	 * @return
	 */
	protected DetailedResults feedTM(List<Set<Integer>> sequence, boolean learn, int num) {
		showInput(sequence, learn, num);
		
		List<Set<Integer>> actual = new ArrayList<Set<Integer>>(sequence);
		if(num > 1) {
			for(int i = 1;i < num;i++) {
				actual.addAll(sequence);
			}
		}
		
		List<Set<Integer>> results = tmTestMachine.feedSequence(actual, learn);
		
		DetailedResults detailedResults = tmTestMachine.computeDetailedResults(results, actual);
		
		String ppResults = tmTestMachine.prettyPrintDetailedResults(detailedResults, actual, patternMachine, 1);
		
		System.out.println(ppResults);
		
		System.out.println("");
		
		if(learn) {
			System.out.println(tmTestMachine.prettyPrintTemporalMemory());
		}
		
		return detailedResults;
	}
	
	

	

}
