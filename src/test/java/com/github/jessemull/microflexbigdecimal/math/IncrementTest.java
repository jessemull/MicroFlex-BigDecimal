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

package com.github.jessemull.microflexbigdecimal.math;

/* ------------------------------ Dependencies ------------------------------ */

import static org.junit.Assert.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.jessemull.microflexbigdecimal.math.Increment;
import com.github.jessemull.microflexbigdecimal.plate.Plate;
import com.github.jessemull.microflexbigdecimal.plate.Stack;
import com.github.jessemull.microflexbigdecimal.plate.Well;
import com.github.jessemull.microflexbigdecimal.plate.WellSet;
import com.github.jessemull.microflexbigdecimal.util.RandomUtil;

/**
 * This class tests the methods in the increment big decimal class.
 * 
 * @author Jesse L. Mull
 * @update Updated Oct 18, 2016
 * @address http://www.jessemull.com
 * @email hello@jessemull.com
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IncrementTest {

    /* ---------------------------- Local fields ---------------------------- */
	
    /* Minimum and maximum values for random well and lists */
	
	private static BigDecimal minValue = new BigDecimal(-100000);    // Minimum big decimal value for wells
	private static BigDecimal maxValue = new BigDecimal(1000000);    // Maximum big decimal value for wells
	private static int minPlate = 10;                                // Plate minimum
	private static int maxPlate = 25;                                // Plate maximum
	private static Random random = new Random();                     // Generates random integers
	
	/* The addition operation */
	
	private static Increment increment = new Increment();
	
	/* Random objects and numbers for testing */

	private static int rows;
	private static int columns;
	private static int length;
	private static int plateNumber = 10;
	private static int arrayNumber = 5;
	private static int stackNumber = 5;
	private static List<Plate[]> arrays = new ArrayList<Plate[]>();
	private static List<Stack> stacks = new ArrayList<Stack>();
	
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
		
		rows = Plate.ROWS_48WELL + random.nextInt(Plate.ROWS_1536WELL - 
	           Plate.ROWS_48WELL + 1);

		columns =  Plate.COLUMNS_48WELL + random.nextInt(Plate.COLUMNS_1536WELL - 
		           Plate.COLUMNS_48WELL + 1);

    	length = rows * columns / 5;
    	
    	for(int i = 0; i < stackNumber; i++) {
    		
    		plateNumber = minPlate + random.nextInt(maxPlate - minPlate + 1);
    		
    		Stack stack1 = RandomUtil.randomStackBigDecimal(rows, columns, minValue, maxValue, length, "Plate" + i, plateNumber);
    		
    		stacks.add(stack1);
    	}
    	
    	for(int i = 0; i < arrayNumber; i++) {

    		Plate[] array1 = new Plate[plateNumber];

    		for(int j = 0; j < array1.length; j++) {
    			
    			rows = Plate.ROWS_48WELL + random.nextInt(Plate.ROWS_1536WELL - 
    			       Plate.ROWS_48WELL + 1);

    			columns =  Plate.COLUMNS_48WELL + random.nextInt(Plate.COLUMNS_1536WELL - 
    			           Plate.COLUMNS_48WELL + 1);
    				
    			plateNumber = minPlate + random.nextInt(maxPlate - minPlate + 1);
    				
   		    	length = rows * columns / 5;
    			
    			Plate plate = RandomUtil.randomPlateBigDecimal(
    					rows, columns, minValue, maxValue, length, "Plate1-" + j);
    			
    			array1[j] = plate;

    		}
    		
    		arrays.add(array1);
    	}
    	
	}
	
	/**
	 * Toggles system error.
	 */
	@AfterClass
	public static void restoreErrorOut() {
		System.setErr(originalOut);
	}
	
	/* ---------------------------- Constructors ---------------------------- */
	
	/**
     * Tests the constructor.
     */
	@Test
	public void testConstructor() {
		Increment test = new Increment();
		assertNotNull(test);
	}

	/* ----------------------------- Well Methods --------------------------- */
	
    /**
     * Tests the increment well operation.
     */
	@Test
    public void testWells() {

		for(Plate[] plates : arrays) {
			
			for(Plate plate : plates) {

				for(Well well : plate) {
					
					List<BigDecimal> result = new ArrayList<BigDecimal>();
					List<BigDecimal> returned = increment.wells(well);
					
					for(BigDecimal bd : well) {
						result.add(bd.add(BigDecimal.ONE));
					}
					
					assertEquals(result, returned);
				}
			}			
		}
		
    }
    
    /**
     * Tests the increment well operation using indices.
     */
	@Test
    public void testWellIndices() {

        for(Plate[] plates : arrays) {
			
			for(Plate plate : plates) {

				for(Well well : plate) {

					int begin = 1 + random.nextInt(well.size() - 1);
	    			int end = begin + random.nextInt(well.size() - begin);
	    			
	    			List<BigDecimal> result = new ArrayList<BigDecimal>();
					List<BigDecimal> returned = increment.wells(well, begin, end - begin);				
					
					for(int i = begin; i < end; i++) {
						result.add(well.data().get(i).add(BigDecimal.ONE));
					}
					
					assertEquals(result, returned);
				}
			}			
		}

    }
    
    /* ---------------------------- Plate Methods --------------------------- */
    
    /**
     * Tests the increment plate operation.
     */
    @Test
    public void testPlates() {

        for(Plate[] plates : arrays) {
			
			for(Plate plate : plates) {

				WellSet result = new WellSet();

				for(Well well : plate) {
					
					List<BigDecimal> resultList = new ArrayList<BigDecimal>();
					
					for(BigDecimal bd : well) {
						resultList.add(bd.add(BigDecimal.ONE));
					}
					
					result.add(new Well(well.row(), well.column(), resultList));
				}
				
				Plate resultPlate = new Plate(plate.rows(), plate.columns(), result);
				Plate returnedPlate = increment.plates(plate);
				
				Iterator<Well> iter1 = resultPlate.iterator();
				Iterator<Well> iter2 = returnedPlate.iterator();
				
				while(iter1.hasNext()) {
					
					Well well1 = iter1.next();
					Well well2 = iter2.next();
					
					assertEquals(well1, well2);
					
				}
				
				assertEquals(resultPlate, returnedPlate);
			}			
		}
    }
    
    /**
     * Tests the increment plate operation using indices.
     */
    @Test
    public void testPlatesIndices() {
    	
    	for(Plate[] plates : arrays) {
			
			for(Plate plate : plates) {

				int begin = 1 + random.nextInt(plate.first().size() - 1);
    			int end = begin + random.nextInt(plate.first().size() - begin);
    			
				WellSet result = new WellSet();

				for(Well well : plate) {
					
					List<BigDecimal> resultList = new ArrayList<BigDecimal>();
					
					for(int i = begin; i < end; i++) {
						resultList.add(well.data().get(i).add(BigDecimal.ONE));
					}
					
					result.add(new Well(well.row(), well.column(), resultList));
				}
				
				Plate resultPlate = new Plate(plate.rows(), plate.columns(), result);
				Plate returnedPlate = increment.plates(plate, begin, end - begin);
				
				Iterator<Well> iter1 = resultPlate.iterator();
				Iterator<Well> iter2 = returnedPlate.iterator();
				
				while(iter1.hasNext()) {
					
					Well well1 = iter1.next();
					Well well2 = iter2.next();
					
					assertEquals(well1, well2);
					
				}
				
				assertEquals(resultPlate, returnedPlate);
			}			
		}
    }
    
    /* ----------------------------- Set Methods ---------------------------- */
    
    /**
     * Tests the increment set operation.
     */
    @Test
    public void testSets() {

        for(Plate[] plates : arrays) {
			
			for(Plate plate : plates) {

				WellSet result = new WellSet();
				WellSet returned = increment.sets(plate.dataSet());
				
				for(Well well : plate) {
					
					List<BigDecimal> resultList = new ArrayList<BigDecimal>();
					
					for(BigDecimal bd : well) {
						resultList.add(bd.add(BigDecimal.ONE));
					}
					
					result.add(new Well(well.row(), well.column(), resultList));
				}
				
				Iterator<Well> iter1 = result.iterator();
				Iterator<Well> iter2 = returned.iterator();
				
				while(iter1.hasNext()) {
					
					Well well1 = iter1.next();
					Well well2 = iter2.next();
					
					assertEquals(well1, well2);
					
				}
				
				assertEquals(result, returned);
			}			
		}
    	
    }
    
    /**
     * Tests the increment set operation using indices.
     */
    @Test
    public void testSetsIndices() {
    	
        for(Plate[] plates : arrays) {
			
			for(Plate plate : plates) {

				int begin = 1 + random.nextInt(plate.first().size() - 1);
    			int end = begin + random.nextInt(plate.first().size() - begin);
    			
				WellSet result = new WellSet();
				WellSet returned = increment.sets(plate.dataSet(), begin, end - begin);
				
				for(Well well : plate) {
					
					List<BigDecimal> resultList = new ArrayList<BigDecimal>();
					
					for(int i = begin; i < end; i++) {
						resultList.add(well.data().get(i).add(BigDecimal.ONE));
					}
					
					result.add(new Well(well.row(), well.column(), resultList));
				}
				
				Iterator<Well> iter1 = result.iterator();
				Iterator<Well> iter2 = returned.iterator();
				
				while(iter1.hasNext()) {
					
					Well well1 = iter1.next();
					Well well2 = iter2.next();
					
					assertEquals(well1, well2);
					
				}
				
				assertEquals(result, returned);
			}			
		}
    	
    }
    
    /* ---------------------------- Stack Methods --------------------------- */
    
    /**
     * Tests the increment stack operation.
     */
    @Test
    public void testStacks() {

        for(Stack stack : stacks) {
			
        	Stack resultStack = new Stack(stack.rows(), stack.columns());
        	
			for(Plate plate : stack) {

				WellSet result = new WellSet();

				for(Well well : plate) {
					
					List<BigDecimal> resultList = new ArrayList<BigDecimal>();
					
					for(BigDecimal bd : well) {
						resultList.add(bd.add(BigDecimal.ONE));
					}
					
					result.add(new Well(well.row(), well.column(), resultList));
				}
				
				Plate resultPlate = new Plate(plate.rows(), plate.columns(), result);
				resultStack.add(resultPlate);
				
			}
		
			Stack returnedStack = increment.stacks(stack);
			
			assertEquals(resultStack, returnedStack);
			
		    Iterator<Plate> plateIter1 = resultStack.iterator();
		    Iterator<Plate> plateIter2 = returnedStack.iterator();
		    
		    while(plateIter1.hasNext()) {
		    	
		    	Plate plate1 = plateIter1.next();
		    	Plate plate2 = plateIter2.next();
		    	
		    	assertEquals(plate1, plate2);
		    	
		    	Iterator<Well> wellIter1 = plate1.iterator();
		    	Iterator<Well> wellIter2 = plate2.iterator();
		    	
		    	while(wellIter1.hasNext()) {
		    		
		    		Well well1 = wellIter1.next();
		    		Well well2 = wellIter2.next();
		    		
		    		assertEquals(well1.data(), well2.data());
		    		
		    	}
		    }
        }			
    }
    
     /**
      * Tests the increment stack operation using indices.
      */
    @Test
    public void testStacksIndices() {

    	for(Stack stack : stacks) {
			
			int begin = 1 + random.nextInt(stack.first().first().size() - 1);
			int end = begin + random.nextInt(stack.first().first().size() - begin);
			
        	Stack resultStack = new Stack(stack.rows(), stack.columns());
        	
			for(Plate plate : stack) {

				WellSet result = new WellSet();

				for(Well well : plate) {
					
					List<BigDecimal> resultList = new ArrayList<BigDecimal>();
					
					for(int i = begin; i < end; i++) {
						resultList.add(well.data().get(i).add(BigDecimal.ONE));
					}
					
					result.add(new Well(well.row(), well.column(), resultList));
				}
				
				Plate resultPlate = new Plate(plate.rows(), plate.columns(), result);
				resultStack.add(resultPlate);
				
			}
		
			Stack returnedStack = increment.stacks(stack, begin, end - begin);
			
			assertEquals(resultStack, returnedStack);
			
		    Iterator<Plate> plateIter1 = resultStack.iterator();
		    Iterator<Plate> plateIter2 = returnedStack.iterator();
		    
		    while(plateIter1.hasNext()) {
		    	
		    	Plate plate1 = plateIter1.next();
		    	Plate plate2 = plateIter2.next();
		    	
		    	assertEquals(plate1, plate2);
		    	
		    	Iterator<Well> wellIter1 = plate1.iterator();
		    	Iterator<Well> wellIter2 = plate2.iterator();
		    	
		    	while(wellIter1.hasNext()) {
		    		
		    		Well well1 = wellIter1.next();
		    		Well well2 = wellIter2.next();
		    		
		    		assertEquals(well1.data(), well2.data());
		    		
		    	}
		    }
        }
    } 
}
