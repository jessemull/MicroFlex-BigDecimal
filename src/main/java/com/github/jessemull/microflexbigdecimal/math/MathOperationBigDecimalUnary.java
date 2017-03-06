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

import java.math.BigDecimal;
import java.util.List;

import com.github.jessemull.microflexbigdecimal.plate.Plate;
import com.github.jessemull.microflexbigdecimal.plate.Stack;
import com.github.jessemull.microflexbigdecimal.plate.Well;
import com.github.jessemull.microflexbigdecimal.plate.WellSet;

/**
 * This class performs mathematical operations with a single argument for BigDecimal 
 * plate stacks, plates, wells and well sets. To create a custom mathematical
 * operation extend this class and override the calculate methods using the
 * appropriate operation. Unary operations can also be performed on a subset of
 * data using a beginning index and subset length. 
 * <br><br>
 * MicroFlex currently supports the following unary mathematical operations 
 * for BigDecimal objects:
 * 
 * <table cellspacing="5px" style="text-align:left; margin: 20px;">
 *    <th><div style="border-bottom: 1px solid black; padding-bottom: 5px;">Operations<div></th>
 *    <tr>
 *       <td>Increment</td> 
 *    </tr>
 *    <tr>
 *       <td>Decrement</td>
 *    </tr>
 * </table>
 * 
 * @author Jesse L. Mull
 * @update Updated Oct 18, 2016
 * @address http://www.jessemull.com
 * @email hello@jessemull.com
 */
public abstract class MathOperationBigDecimalUnary {

	/* ---------------------------- Constructors ---------------------------- */
	
	/**
	 * Creates a new math operation.
	 */
	public MathOperationBigDecimalUnary() {}

	/* ----------------------------- Well Methods --------------------------- */
	
    /**
     * Returns the result of the mathematical operation.
     * @param    Well    the well
     * @return                     result of the operation
     */
    public List<BigDecimal> wells(Well well) {
    	
    	if(well == null) {
    		throw new NullPointerException("Well is null.");
    	}
    	
    	return calculate(well.data());
    }
    
    /**
     * Returns the result of the mathematical operation. Using the values between
     * the indices.
     * @param    Well    the well
     * @param    int               beginning index of the subset
     * @param    int               length of the subset
     * @return                     result of the operation
     */
    public List<BigDecimal> wells(Well well, int begin, int length) {
    	
    	if(well == null) {
    		throw new NullPointerException("Well is null.");
    	}
    	
    	return calculate(well.data(), begin, length);
    }
    
    /* ---------------------------- Plate Methods --------------------------- */
    
    /**
     * Returns the result of the mathematical operation.
     * @param    Plate    the plate
     * @return                      result of the operation
     */
    public Plate plates(Plate plate) {
    	
    	if(plate == null) {
    		throw new NullPointerException("Plate is null.");
    	}
    	
    	Plate result = new Plate(plate.rows(), plate.columns());
    	
    	for(WellSet set : plate.allGroups()) {
    		result.addGroups(set.wellList());
    	}
    	
    	for(Well well : plate) {
    		result.addWells(new Well(well.row(), well.column(), 
    				this.calculate(well.data())));
    	}
    	
    	return result;
    }
    
    /**
     * Returns the result of the mathematical operation using the values between
     * the indices.
     * @param    Plate    the plate
     * @param    int                beginning index of the subset
     * @param    int                length of the subset
     * @return                      result of the operation
     */
    public Plate plates(Plate plate, int begin, int length) {
    	
    	if(plate == null) {
    		throw new NullPointerException("Plate is null.");
    	}
    	
    	Plate result = new Plate(plate.rows(), plate.columns());
    	
    	for(WellSet set : plate.allGroups()) {
    		result.addGroups(set.wellList());
    	}
    	
    	for(Well well : plate) {
    		result.addWells(new Well(well.row(), well.column(), 
    				this.calculate(well.data(), begin, length)));
    	}
    	
    	return result;
    }
    
    /* ----------------------------- Set Methods ---------------------------- */
    
    /**
     * Returns the result of the mathematical operation.
     * @param    WellSet    the well set
     * @return                        result of the operation
     */
    public WellSet sets(WellSet set) {
    	
    	if(set == null) {
    		throw new NullPointerException("Well set is null.");
    	}
    	
    	WellSet result = new WellSet();
    	result.setLabel(set.label());
    	
    	for(Well well : set) {
    		result.add(new Well(well.row(), well.column(), 
    				this.calculate(well.data())));
    	}
    	
    	return result;
    }
    
    /**
     * Returns the result of the mathematical operation using the values between
     * the indices.
     * @param    WellSet    the well set
     * @param    int                  beginning index of the subset
     * @param    int                  length of the subset
     * @return                        result of the operation
     */
    public WellSet sets(WellSet set, int begin, int length) {
    	
    	if(set == null) {
    		throw new NullPointerException("Well set is null.");
    	}
    	
    	WellSet result = new WellSet();
    	result.setLabel(set.label());
    	
    	for(Well well : set) {
    		result.add(new Well(well.row(), well.column(), 
    				this.calculate(well.data(), begin, length)));
    	}
    	
    	return result;
    }
    
    /* ---------------------------- Stack Methods --------------------------- */
    
    /**
     * Returns the result of the mathematical operation.
     * @param    Stack    the stack
     * @return                      the result of the operation
     */
     public Stack stacks(Stack stack) {
    	
    	if(stack == null) {
    		throw new NullPointerException("Stack is null.");
    	}
    	
    	Stack result = new Stack(stack.rows(), stack.columns(), stack.label());
    	
    	for(Plate plate : stack) {
    		result.add(this.plates(plate));
    	}
    	
    	return result;
    }
    
     /**
      * Returns the result of the mathematical operation.
      * @param    Stack    the stack
      * @param    int                beginning index of the sub set
      * @param    int                length of the subset
      * @return                      the result of the operation
      */
     public Stack stacks(Stack stack, int begin, int length) {
     	
        if(stack == null) {
             throw new NullPointerException("Stack is null.");
     	}
     	
     	Stack result = new Stack(stack.rows(), stack.columns(), stack.label());
     	
     	for(Plate plate : stack) {
     		result.add(this.plates(plate, begin, length));
     	}
     	
     	return result;
     } 
     
    /*------------------------ List Operation Methods ------------------------*/
    
    /**
     * Performs the mathematical operation for the list.
     * @param    List<BigDecimal>    the list
     * @return                       result of the mathematical operation
     */
    public abstract List<BigDecimal> calculate(List<BigDecimal> list);
    
    /**
     * Performs the mathematical operation for the list using the values
     * between the indices.
     * @param    List<BigDecimal>    the list
     * @return                       result of the mathematical operation
     */
    public abstract List<BigDecimal> calculate(List<BigDecimal> list, int begin, int length);
}
