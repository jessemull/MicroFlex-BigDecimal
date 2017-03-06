/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/* -------------------------------- Package --------------------------------- */

package com.github.jessemull.microflexbigdecimal.stat;

/* ------------------------------ Dependencies ------------------------------ */

import static org.junit.Assert.*;

import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.jessemull.microflexbigdecimal.plate.Plate;
import com.github.jessemull.microflexbigdecimal.plate.Well;
import com.github.jessemull.microflexbigdecimal.plate.WellSet;
import com.github.jessemull.microflexbigdecimal.stat.Mean;
import com.github.jessemull.microflexbigdecimal.util.RandomUtil;

/**
 * This class tests the methods in the mean big decimal class.
 * 
 * @author Jesse L. Mull
 * @update Updated Oct 18, 2016
 * @address http://www.jessemull.com
 * @email hello@jessemull.com
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MeanWeightsTest {


	/* ---------------------------- Local Fields -----------------------------*/
	
    /* Minimum and maximum values for random well and lists */
	
	private static BigDecimal minValue = new BigDecimal(0);     // Minimum big decimal value for wells
	private static BigDecimal maxValue = new BigDecimal(10);    // Maximum big decimal value for wells
	private static Random random = new Random();                                            // Generates random integers
	private static MathContext mc = new MathContext(10, RoundingMode.HALF_DOWN);            // The math context for input values
	private static MathContext contextWeights = new MathContext(2, RoundingMode.HALF_DOWN); // The math context for weights
	
	/* The addition operation */
	
	private static Mean mean = new Mean();

	/* Random objects and numbers for testing */

	private static int rows = 5;
	private static int columns = 4;
	private static int length = 5;
	private static int lengthIndices = 10;
	private static int plateNumber = 10;
	private static int plateNumberIndices = 5;
	private static Plate[] array = new Plate[plateNumber];
	private static Plate[] arrayIndices =  new Plate[plateNumberIndices];
	private static double[] weights = new double[length];
	private static double[] weightsIndices = new double[lengthIndices];
	
    /* Value of false redirects System.err */
	
	private static boolean error = true;
	private static PrintStream originalOut = System.out;

	/**
	 * Generates random objects and numbers for testing.
	 */
	@BeforeClass
	public static void setUp() {
		
		if(error) {

			System.setErr(new PrintStream(new OutputStream() {
			    public void write(int x) {}
			}));

		}

		for(int j = 0; j < array.length; j++) {

			Plate plate = RandomUtil.randomPlateBigDecimal(
					rows, columns, minValue, maxValue, length, "Plate1-" + j);

			array[j] = plate;
		}
		
		for(int j = 0; j < arrayIndices.length; j++) {
			
			Plate plateIndices = RandomUtil.randomPlateBigDecimal(
					rows, columns, minValue, maxValue, lengthIndices, "Plate1-" + j);

			arrayIndices[j] = plateIndices;		
		}
		
		for(int i = 0; i < weights.length; i++) {
			double randomDouble = random.nextDouble();
			weights[i] = new BigDecimal(randomDouble + "", contextWeights).doubleValue();
		}
		
		for(int i = 0; i < weightsIndices.length; i++) {
			double randomDouble = random.nextDouble();
			weightsIndices[i] = new BigDecimal(randomDouble + "", contextWeights).doubleValue();
		}
	}
	
	/**
	 * Toggles system error.
	 */
	@AfterClass
	public static void restoreErrorOut() {
		System.setErr(originalOut);
	}
	
    /* ---------------- Well statistics for all plate wells ----------------- */
    
    /**
     * Tests the plate statistics method.
     */
	@Test
    public void testPlate() {
		
		for(Plate plate : array) {

    		Map<Well, BigDecimal> resultMap = new TreeMap<Well, BigDecimal>();
    		Map<Well, BigDecimal> returnedMap = mean.plate(plate, weights, mc);
    		
			for(Well well : plate) {
				
				double[] input = new double[well.size()];
				int index = 0;				
		
				for(BigDecimal bd : well) {
					input[index] = bd.doubleValue() * weights[index];
					index++;
				}
			
				DescriptiveStatistics stat = new DescriptiveStatistics(input);
				double resultDouble = stat.getMean();
				
				BigDecimal result = new BigDecimal(resultDouble, mc);
				
				resultMap.put(well, result);
			}

			for(Well well : plate) {
				
				BigDecimal result = resultMap.get(well);
				BigDecimal returned = returnedMap.get(well);
				
				BigDecimal[] corrected = correctRoundingErrors(result, returned);
				assertEquals(corrected[0], corrected[1]);
			}
		} 
    }
    
    /**
     * Tests the plate statistics method using the values between the indices.
     */
    @Test
    public void testPlateIndices() {
    	
    	for(Plate plate : arrayIndices) {

    		int begin = random.nextInt(plate.first().size() - 4);
			int end = begin + random.nextInt(3) + 3;
			
    		Map<Well, BigDecimal> resultMap = new TreeMap<Well, BigDecimal>();
    		Map<Well, BigDecimal> returnedMap = mean.plate(plate, ArrayUtils.subarray(weightsIndices, begin, end), begin, end - begin, mc);
    		
			for(Well well : plate) {
				
				double[] input = new double[well.size()];
				int index = 0;
				
				for(BigDecimal bd : well) {
					input[index] = bd.doubleValue() * weightsIndices[index];
					index++;
				}

				DescriptiveStatistics stat = new DescriptiveStatistics(ArrayUtils.subarray(input, begin, end));
				double resultDouble = stat.getMean();
				
				BigDecimal result = new BigDecimal(resultDouble, mc);
				
				resultMap.put(well, result);
			}

			for(Well well : plate) {
				
				BigDecimal result = resultMap.get(well);
				BigDecimal returned = returnedMap.get(well);
				
				BigDecimal[] corrected = correctRoundingErrors(result, returned);
				
				assertEquals(corrected[0], corrected[1]);
			}
    	}
    }

    /* --------------------- Aggregated plate statistics -------------------  */
    
    /**
     * Tests the aggregated plate statistics method.
     */
    @Test
    public void testAggregatedPlate() {
        
    	for(Plate plate : array) {

    		List<BigDecimal> resultList = new ArrayList<BigDecimal>();
    		BigDecimal aggregatedReturned = mean.platesAggregated(plate, weights, mc);
    		
    		for (Well well : plate) {
            	
            	List<BigDecimal> input = well.data();
            	
            	for(int i = 0; i < input.size(); i++) {
            		resultList.add(input.get(i).multiply(new BigDecimal(weights[i])));
            	}
            	
            }

			double[] inputAggregated = new double[resultList.size()];
			
			for(int i = 0; i < resultList.size(); i++) {
				inputAggregated[i] = resultList.get(i).doubleValue();
			}
			
			DescriptiveStatistics statAggregated = new DescriptiveStatistics(inputAggregated);
			double resultAggregatedDouble = statAggregated.getMean();
			
			BigDecimal aggregatedResult = new BigDecimal(resultAggregatedDouble, mc);
			
			BigDecimal[] corrected = correctRoundingErrors(aggregatedResult, aggregatedReturned);		
			assertEquals(corrected[0], corrected[1]);
		}
    }
    
    /**
     * Tests the aggregated plate statistics method using a collection.
     */
    @Test
    public void testAggregatedPlateCollection() {

    	List<Plate> collection = Arrays.asList(array);
    	Map<Plate, BigDecimal> aggregatedReturnedMap = mean.platesAggregated(collection, weights, mc);
    	Map<Plate, BigDecimal> aggregatedResultMap = new TreeMap<Plate, BigDecimal>();
    	
    	for(Plate plate : collection) {

    		List<BigDecimal> resultList = new ArrayList<BigDecimal>();
    		
    		for (Well well : plate) {
            	
            	List<BigDecimal> input = well.data();
            	
            	for(int i = 0; i < input.size(); i++) {
            		resultList.add(input.get(i).multiply(new BigDecimal(weights[i])));
            	}
            	
            }

			double[] inputAggregated = new double[resultList.size()];
			
			for(int i = 0; i < resultList.size(); i++) {
				inputAggregated[i] = resultList.get(i).doubleValue();
			}
			
			DescriptiveStatistics statAggregated = new DescriptiveStatistics(inputAggregated);
			double resultAggregatedDouble = statAggregated.getMean();
			
			BigDecimal aggregatedResult = new BigDecimal(resultAggregatedDouble, mc);
			aggregatedResultMap.put(plate, aggregatedResult);
		}
    	
    	for(Plate plate : collection) {
    		
    		BigDecimal result = aggregatedResultMap.get(plate);
			BigDecimal returned = aggregatedReturnedMap.get(plate);
			
			BigDecimal[] corrected = correctRoundingErrors(result, returned);
			
			assertEquals(corrected[0], corrected[1]);
    	}
    }
    
    /**
     * Tests the aggregated plate statistics method using an array.
     */
    @Test
    public void testAggregatedPlateArray() {

    	Map<Plate, BigDecimal> aggregatedReturnedMap = mean.platesAggregated(array, weights, mc);
    	Map<Plate, BigDecimal> aggregatedResultMap = new TreeMap<Plate, BigDecimal>();
    	
    	for(Plate plate : array) {

    		List<BigDecimal> resultList = new ArrayList<BigDecimal>();
    		
    		for (Well well : plate) {
            	
            	List<BigDecimal> input = well.data();
            	
            	for(int i = 0; i < input.size(); i++) {
            		resultList.add(input.get(i).multiply(new BigDecimal(weights[i])));
            	}
            	
            }

			double[] inputAggregated = new double[resultList.size()];
			
			for(int i = 0; i < resultList.size(); i++) {
				inputAggregated[i] = resultList.get(i).doubleValue();
			}
			
			DescriptiveStatistics statAggregated = new DescriptiveStatistics(inputAggregated);
			double resultAggregatedDouble = statAggregated.getMean();
			
			BigDecimal aggregatedResult = new BigDecimal(resultAggregatedDouble, mc);
			aggregatedResultMap.put(plate, aggregatedResult);
		}
    	
    	for(Plate plate : array) {
    		
    		BigDecimal result = aggregatedResultMap.get(plate);
			BigDecimal returned = aggregatedReturnedMap.get(plate);
			
			BigDecimal[] corrected = correctRoundingErrors(result, returned);
			
			assertEquals(corrected[0], corrected[1]);
    	}
    	
    }    
    
    /**
     * Tests the aggregated plate statistics method using the values between the indices.
     */
    @Test
    public void testAggregatedPlateIndices() {
    	
    	for(Plate plate : arrayIndices) {

    		int begin = random.nextInt(plate.first().size() - 4);
			int end = begin + random.nextInt(3) + 3;
			
    		List<BigDecimal> resultList = new ArrayList<BigDecimal>();
    		BigDecimal aggregatedReturned = mean.platesAggregated(plate, weightsIndices, begin, end - begin, mc);
    		
    		for (Well well : plate) {
            	
            	List<BigDecimal> input = well.data().subList(begin, end);
            	
            	for(int i = 0; i < input.size(); i++) {
            		resultList.add(input.get(i).multiply(new BigDecimal(weightsIndices[i])));
            	}
            	
            }

			double[] inputAggregated = new double[resultList.size()];
			
			for(int i = 0; i < resultList.size(); i++) {
				inputAggregated[i] = resultList.get(i).doubleValue();
			}
			
			DescriptiveStatistics statAggregated = new DescriptiveStatistics(inputAggregated);
			double resultAggregatedDouble = statAggregated.getMean();
			
			BigDecimal aggregatedResult = new BigDecimal(resultAggregatedDouble, mc);
			
			BigDecimal[] corrected = correctRoundingErrors(aggregatedResult, aggregatedReturned);	
			assertEquals(corrected[0], corrected[1]);
		}
    }
    
    /**
     * Tests the aggregated plate statistics method using the values between the indices of
     * the collection.
     */
    @Test
    public void testAggregatedPlateCollectionIndices() {
    	
    	int begin = random.nextInt(arrayIndices[0].first().size() - 4);
		int end = begin + random.nextInt(3) + 3;

		List<Plate> collection = Arrays.asList(arrayIndices);
    	Map<Plate, BigDecimal> aggregatedReturnedMap = mean.platesAggregated(collection, weightsIndices, begin, end - begin, mc);
  
    	Map<Plate, BigDecimal> aggregatedResultMap = new TreeMap<Plate, BigDecimal>();
    	
    	for(Plate plate : collection) {

    		List<BigDecimal> resultList = new ArrayList<BigDecimal>();
    		
    		for (Well well : plate) {
            	
            	List<BigDecimal> input = well.data().subList(begin, end);
            	
            	for(int i = 0; i < input.size(); i++) {
            		resultList.add(input.get(i).multiply(new BigDecimal(weightsIndices[i])));
            	}
            	
            }

			double[] inputAggregated = new double[resultList.size()];
			
			for(int i = 0; i < resultList.size(); i++) {
				inputAggregated[i] = resultList.get(i).doubleValue();
			}
			
			DescriptiveStatistics statAggregated = new DescriptiveStatistics(inputAggregated);
			double resultAggregatedDouble = statAggregated.getMean();
			
			BigDecimal aggregatedResult = new BigDecimal(resultAggregatedDouble, mc);
			aggregatedResultMap.put(plate, aggregatedResult);
		}
    	
    	for(Plate plate : collection) {
    		
    		BigDecimal result = aggregatedResultMap.get(plate);
			BigDecimal returned = aggregatedReturnedMap.get(plate);
			BigDecimal[] corrected = correctRoundingErrors(result, returned);
			
			assertEquals(corrected[0], corrected[1]);
    	}
    }
    
    /**
     * Tests the aggregated plate statistics method using the values between the indices of
     * the array.
     */
    @Test
    public void testAggregatedPlateArrayIndices() {
    	
    	int begin = random.nextInt(arrayIndices[0].first().size() - 4);
		int end = begin + random.nextInt(3) + 3;

    	Map<Plate, BigDecimal> aggregatedReturnedMap = mean.platesAggregated(arrayIndices, weightsIndices, begin, end - begin, mc);
    	Map<Plate, BigDecimal> aggregatedResultMap = new TreeMap<Plate, BigDecimal>();
    	
    	for(Plate plate : arrayIndices) {

    		List<BigDecimal> resultList = new ArrayList<BigDecimal>();
    		
    		for (Well well : plate) {
            	
            	List<BigDecimal> input = well.data().subList(begin, end);
            	
            	for(int i = 0; i < input.size(); i++) {
            		resultList.add(input.get(i).multiply(new BigDecimal(weightsIndices[i])));
            	}
            	
            }
			
			double[] inputAggregated = new double[resultList.size()];
			
			for(int i = 0; i < resultList.size(); i++) {
				inputAggregated[i] = resultList.get(i).doubleValue();
			}

			DescriptiveStatistics statAggregated = new DescriptiveStatistics(inputAggregated);
			double resultAggregatedDouble = statAggregated.getMean();
	
			BigDecimal aggregatedResult = new BigDecimal(resultAggregatedDouble, mc);
			aggregatedResultMap.put(plate, aggregatedResult);
			
		}
    	
    	for(Plate plate : arrayIndices) {

    		BigDecimal result = aggregatedResultMap.get(plate);
			BigDecimal returned = aggregatedReturnedMap.get(plate);
			BigDecimal[] corrected = correctRoundingErrors(result, returned);
			
			assertEquals(corrected[0], corrected[1]);
    	}
    }
    
    /* --------------- Well statistics for all wells in a set --------------  */
    
    /**
     * Tests set calculation.
     */
    @Test
    public void testSet() {
    	
    	for(Plate plate : array) {

    		Map<Well, BigDecimal> resultMap = new TreeMap<Well, BigDecimal>();
    		Map<Well, BigDecimal> returnedMap = mean.set(plate.dataSet(), weights, mc);
    		
			for(Well well : plate) {
				
				double[] input = new double[well.size()];
				int index = 0;
				
				for(BigDecimal bd : well) {
					input[index] = bd.doubleValue() * weights[index];
					index++;
				}
			
				DescriptiveStatistics stat = new DescriptiveStatistics(input);
				double resultDouble = stat.getMean();
				
				BigDecimal result = new BigDecimal(resultDouble, mc);
				
				resultMap.put(well, result);
			}

			for(Well well : plate) {
				
				BigDecimal result = resultMap.get(well);
				BigDecimal returned = returnedMap.get(well);
				
				BigDecimal[] corrected = correctRoundingErrors(result, returned);
				
				assertEquals(corrected[0], corrected[1]);
			}
		} 
    	
    }
    
    /**
     * Tests set calculation using indices.
     */
    @Test
    public void testSetIndices() {
        
    	for(Plate plate : arrayIndices) {

    		int begin = random.nextInt(plate.first().size() - 4);
			int end = begin + random.nextInt(3) + 3;
			
    		Map<Well, BigDecimal> resultMap = new TreeMap<Well, BigDecimal>();
    		Map<Well, BigDecimal> returnedMap = mean.set(plate.dataSet(), ArrayUtils.subarray(weightsIndices, begin, end), begin, end - begin, mc);
    		
			for(Well well : plate) {
				
				double[] input = new double[well.size()];
				int index = 0;
				
				for(BigDecimal bd : well) {
					input[index] = bd.doubleValue() * weightsIndices[index];
					index++;
				}

				DescriptiveStatistics stat = new DescriptiveStatistics(ArrayUtils.subarray(input, begin, end));
				double resultDouble = stat.getMean();
				
				BigDecimal result = new BigDecimal(resultDouble, mc);
				
				resultMap.put(well, result);
			}

			for(Well well : plate) {
				
				BigDecimal result = resultMap.get(well);
				BigDecimal returned = returnedMap.get(well);
				
				BigDecimal[] corrected = correctRoundingErrors(result, returned);
				
				assertEquals(corrected[0], corrected[1]);
			}
    	}
    }

    /* ---------------------- Aggregated set statistics --------------------  */
    
    /**
     * Tests the aggregated plate statistics method.
     */
    @Test
    public void testAggregatedSet() {
        
    	for(Plate plate : array) {

    		List<BigDecimal> resultList = new ArrayList<BigDecimal>();
    		BigDecimal aggregatedReturned = mean.setsAggregated(plate.dataSet(), weights, mc);
    		
    		for (Well well : plate) {
            	
            	List<BigDecimal> input = well.data();
            	
            	for(int i = 0; i < input.size(); i++) {
            		resultList.add(input.get(i).multiply(new BigDecimal(weights[i])));
            	}
            	
            }

			double[] inputAggregated = new double[resultList.size()];
			
			for(int i = 0; i < resultList.size(); i++) {
				inputAggregated[i] = resultList.get(i).doubleValue();
			}
			
			DescriptiveStatistics statAggregated = new DescriptiveStatistics(inputAggregated);
			double resultAggregatedDouble = statAggregated.getMean();
			
			BigDecimal aggregatedResult = new BigDecimal(resultAggregatedDouble, mc);
			
			BigDecimal[] corrected = correctRoundingErrors(aggregatedResult, aggregatedReturned);		
			assertEquals(corrected[0], corrected[1]);
		}
    }
    
    /**
     * Tests the aggregated plate statistics method using a collection.
     */
    @Test
    public void testAggregatedSetCollection() {

    	List<WellSet> collection = new ArrayList<WellSet>();
    	
    	for(Plate plate : array) {
    		collection.add(plate.dataSet());
    	}
    	
    	Map<WellSet, BigDecimal> aggregatedReturnedMap = mean.setsAggregated(collection, weights, mc);
    	Map<WellSet, BigDecimal> aggregatedResultMap = new TreeMap<WellSet, BigDecimal>();
    	
    	for(WellSet set : collection) {

    		List<BigDecimal> resultList = new ArrayList<BigDecimal>();
    		
    		for (Well well : set) {
            	
            	List<BigDecimal> input = well.data();
            	
            	for(int i = 0; i < input.size(); i++) {
            		resultList.add(input.get(i).multiply(new BigDecimal(weights[i])));
            	}
            	
            }

			double[] inputAggregated = new double[resultList.size()];
			
			for(int i = 0; i < resultList.size(); i++) {
				inputAggregated[i] = resultList.get(i).doubleValue();
			}
			
			DescriptiveStatistics statAggregated = new DescriptiveStatistics(inputAggregated);
			double resultAggregatedDouble = statAggregated.getMean();
			
			BigDecimal aggregatedResult = new BigDecimal(resultAggregatedDouble, mc);
			aggregatedResultMap.put(set, aggregatedResult);
		}
    	
    	for(WellSet set : collection) {
    		
    		BigDecimal result = aggregatedResultMap.get(set);
			BigDecimal returned = aggregatedReturnedMap.get(set);
			
			BigDecimal[] corrected = correctRoundingErrors(result, returned);
			
			assertEquals(corrected[0], corrected[1]);
    	}
    }
    
    /**
     * Tests the aggregated plate statistics method using an array.
     */
    @Test
    public void testAggregatedSetArray() {

    	WellSet[] setArray = new WellSet[array.length];
    	
    	for(int i = 0; i < setArray.length; i++) {
    		setArray[i] = array[i].dataSet();
    	}
    	
    	Map<WellSet, BigDecimal> aggregatedReturnedMap = mean.setsAggregated(setArray, weights, mc);
    	Map<WellSet, BigDecimal> aggregatedResultMap = new TreeMap<WellSet, BigDecimal>();
    	
    	for(WellSet set : setArray) {

    		List<BigDecimal> resultList = new ArrayList<BigDecimal>();
    		
    		for (Well well : set) {
            	
            	List<BigDecimal> input = well.data();
            	
            	for(int i = 0; i < input.size(); i++) {
            		resultList.add(input.get(i).multiply(new BigDecimal(weights[i])));
            	}
            	
            }

			double[] inputAggregated = new double[resultList.size()];
			
			for(int i = 0; i < resultList.size(); i++) {
				inputAggregated[i] = resultList.get(i).doubleValue();
			}
			
			DescriptiveStatistics statAggregated = new DescriptiveStatistics(inputAggregated);
			double resultAggregatedDouble = statAggregated.getMean();
			
			BigDecimal aggregatedResult = new BigDecimal(resultAggregatedDouble, mc);
			aggregatedResultMap.put(set, aggregatedResult);
		}
    	
    	for(WellSet set : setArray) {
    		
    		BigDecimal result = aggregatedResultMap.get(set);
			BigDecimal returned = aggregatedReturnedMap.get(set);
			
			BigDecimal[] corrected = correctRoundingErrors(result, returned);
			
			assertEquals(corrected[0], corrected[1]);
    	}
    	
    }    
    
    /**
     * Tests the aggregated plate statistics method using the values between the indices.
     */
    @Test
    public void testAggregatedSetIndices() {
    	
    	for(Plate plate : arrayIndices) {

    		int begin = random.nextInt(plate.first().size() - 4);
			int end = begin + random.nextInt(3) + 3;
			
    		List<BigDecimal> resultList = new ArrayList<BigDecimal>();
    		BigDecimal aggregatedReturned = mean.setsAggregated(plate.dataSet(), weightsIndices, begin, end - begin, mc);
    		
    		for (Well well : plate) {
            	
            	List<BigDecimal> input = well.data().subList(begin, end);
            	
            	for(int i = 0; i < input.size(); i++) {
            		resultList.add(input.get(i).multiply(new BigDecimal(weightsIndices[i])));
            	}
            	
            }

			double[] inputAggregated = new double[resultList.size()];
			
			for(int i = 0; i < resultList.size(); i++) {
				inputAggregated[i] = resultList.get(i).doubleValue();
			}
			
			DescriptiveStatistics statAggregated = new DescriptiveStatistics(inputAggregated);
			double resultAggregatedDouble = statAggregated.getMean();
			
			BigDecimal aggregatedResult = new BigDecimal(resultAggregatedDouble, mc);
			
			BigDecimal[] corrected = correctRoundingErrors(aggregatedResult, aggregatedReturned);	
			assertEquals(corrected[0], corrected[1]);
		}
    }
    
    /**
     * Tests the aggregated plate statistics method using the values between the indices of
     * the collection.
     */
    @Test
    public void testAggregatedSetCollectionIndices() {
    	
    	int begin = random.nextInt(arrayIndices[0].first().size() - 4);
		int end = begin + random.nextInt(3) + 3;

		List<WellSet> collection = new ArrayList<WellSet>();
		
		for(Plate plate : arrayIndices) {
			collection.add(plate.dataSet());
		}
		
    	Map<WellSet, BigDecimal> aggregatedReturnedMap = mean.setsAggregated(collection, weightsIndices, begin, end - begin, mc);
    	Map<WellSet, BigDecimal> aggregatedResultMap = new TreeMap<WellSet, BigDecimal>();
    	
    	for(WellSet set : collection) {

    		List<BigDecimal> resultList = new ArrayList<BigDecimal>();
    		
    		for (Well well : set) {
            	
            	List<BigDecimal> input = well.data().subList(begin, end);
            	
            	for(int i = 0; i < input.size(); i++) {
            		resultList.add(input.get(i).multiply(new BigDecimal(weightsIndices[i])));
            	}
            	
            }

			double[] inputAggregated = new double[resultList.size()];
			
			for(int i = 0; i < resultList.size(); i++) {
				inputAggregated[i] = resultList.get(i).doubleValue();
			}
			
			DescriptiveStatistics statAggregated = new DescriptiveStatistics(inputAggregated);
			double resultAggregatedDouble = statAggregated.getMean();
			
			BigDecimal aggregatedResult = new BigDecimal(resultAggregatedDouble, mc);
			aggregatedResultMap.put(set, aggregatedResult);
		}
    	
    	for(WellSet set : collection) {
    		
    		BigDecimal result = aggregatedResultMap.get(set);
			BigDecimal returned = aggregatedReturnedMap.get(set);
			BigDecimal[] corrected = correctRoundingErrors(result, returned);
			
			assertEquals(corrected[0], corrected[1]);
    	}
    }
    
    /**
     * Tests the aggregated plate statistics method using the values between the indices of
     * the array.
     */
    @Test
    public void testAggregatedSetArrayIndices() {
    	
    	int begin = random.nextInt(arrayIndices[0].first().size() - 4);
		int end = begin + random.nextInt(3) + 3;
		
		WellSet[] setArrayIndices = new WellSet[arrayIndices.length];
		
		for(int i = 0; i < setArrayIndices.length; i++) {
			setArrayIndices[i] = arrayIndices[i].dataSet();
		}
		
    	Map<WellSet, BigDecimal> aggregatedReturnedMap = mean.setsAggregated(setArrayIndices, weightsIndices, begin, end - begin, mc);
    	Map<WellSet, BigDecimal> aggregatedResultMap = new TreeMap<WellSet, BigDecimal>();
    	
    	for(WellSet set : setArrayIndices) {

    		List<BigDecimal> resultList = new ArrayList<BigDecimal>();
    		
    		for (Well well : set) {
            	
            	List<BigDecimal> input = well.data().subList(begin, end);
            	
            	for(int i = 0; i < input.size(); i++) {
            		resultList.add(input.get(i).multiply(new BigDecimal(weightsIndices[i])));
            	}
            	
            }
			
			double[] inputAggregated = new double[resultList.size()];
			
			for(int i = 0; i < resultList.size(); i++) {
				inputAggregated[i] = resultList.get(i).doubleValue();
			}
			
			DescriptiveStatistics statAggregated = new DescriptiveStatistics(inputAggregated);
			double resultAggregatedDouble = statAggregated.getMean();
			
			BigDecimal aggregatedResult = new BigDecimal(resultAggregatedDouble, mc);

			aggregatedResultMap.put(set, aggregatedResult);
		}
    	
    	for(WellSet plate : setArrayIndices) {
    		
    		BigDecimal result = aggregatedResultMap.get(plate);
			BigDecimal returned = aggregatedReturnedMap.get(plate);
			BigDecimal[] corrected = correctRoundingErrors(result, returned);
			
			assertEquals(corrected[0], corrected[1]);
    	}
    }
    
    /* -------------------------- Well statistics --------------------------  */
    
    /**
     * Tests well calculation.
     */
    @Test
    public void testWell() {

		for(Plate plate : array) {

			for(Well well : plate) {
				
				double[] input = new double[well.size()];
				int index = 0;
				
				for(BigDecimal bd : well) {
					input[index] = bd.doubleValue() * weights[index];
					index++;
				}
			
				DescriptiveStatistics stat = new DescriptiveStatistics(input);
				double resultDouble = stat.getMean();

				BigDecimal returned = mean.well(well, weights, mc);					
				BigDecimal result = new BigDecimal(resultDouble, mc);
				
				BigDecimal[] corrected = correctRoundingErrors(returned, result);
				
				assertEquals(corrected[0], corrected[1]);
			}		
		}        
    }
    
    /**
     * Tests well calculation using indices.
     */
    @Test
    public void testWellIndices() {

    	for(Plate plate : arrayIndices) {

			for(Well well : plate) {

		    	double[] input = new double[well.size()];
				int index = 0;
				
				for(BigDecimal bd : well) {
					input[index] = bd.doubleValue() * weightsIndices[index];
					index++;
				}

				int begin = random.nextInt(well.size() - 4);
				int end = begin + random.nextInt(3) + 3;

				DescriptiveStatistics stat = new DescriptiveStatistics(ArrayUtils.subarray(input, begin, end));
				double resultDouble = stat.getMean();

				BigDecimal returned = mean.well(well, ArrayUtils.subarray(weightsIndices, begin, end), begin, end - begin, mc);					
				BigDecimal result = new BigDecimal(resultDouble, mc);

				BigDecimal[] corrected = correctRoundingErrors(returned, result);
				assertEquals(corrected[0], corrected[1]);
			}		
		}		
    }
    
    /*---------------------------- Helper Methods ----------------------------*/
    
    /**
     * Corrects any rounding errors due to differences in the implementation of
     * the statistic between the Apache and MicroFlex libraries
     * @param    BigDecimal    the first result
     * @param    BigDecimal    the second result
     * @return                 corrected results
     */
    private static BigDecimal[] correctRoundingErrors(BigDecimal bd1, BigDecimal bd2) {
	
    	BigDecimal[] array = new BigDecimal[2];
    	int scale = mc.getPrecision();
    	
    	while(!bd1.equals(bd2) && scale > mc.getPrecision() / 4) {
			
			bd1 = bd1.setScale(scale, RoundingMode.HALF_DOWN);
			bd2 = bd2.setScale(scale, RoundingMode.HALF_DOWN);
		
			if(bd1.subtract(bd1.ulp()).equals(bd2)) {
				bd1 = bd1.subtract(bd1.ulp());
			}
			
			if(bd1.add(bd1.ulp()).equals(bd2)) {
				bd1 = bd1.add(bd1.ulp());
			}
			
			scale--;
		}
		
		array[0] = bd1;
		array[1] = bd2;

		return array;
    }
}
