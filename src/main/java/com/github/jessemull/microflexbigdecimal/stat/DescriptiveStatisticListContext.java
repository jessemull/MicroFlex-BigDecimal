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

/* -------------------------------- Package -------------------------------- */

package com.github.jessemull.microflexbigdecimal.stat;

/* ----------------------------- Dependencies ------------------------------ */

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Preconditions;

import com.github.jessemull.microflexbigdecimal.plate.Plate;
import com.github.jessemull.microflexbigdecimal.plate.Well;
import com.github.jessemull.microflexbigdecimal.plate.WellSet;

/**
 * This class performs statistical operations that return a list of BigDecimals as
 * a result for BigDecimal plate stacks, plates, wells and well sets. To create 
 * a custom statistical operation extend this class and override the calculate 
 * methods using the appropriate statistical operation. 
 *
 * <br><br>
 * 
 * Statistical operations can be performed on stacks, plates, sets and wells using
 * standard or aggregated functions. Standard functions calculate the desired
 * statistic for each well in the stack, plate or set. Aggregated functions aggregate
 * the values from all the wells in the stack, plate or set and perform the statistical
 * operation on the aggregated values. Both standard and aggregated functions can
 * be performed on a subset of data within the stack, plate, set or well.
 * 
 * <br><br>
 * 
 * The methods within the MicroFlex library are meant to be flexible and the
 * descriptive statistic object supports operations using a single stack, plate,
 * set or well as well as collections and arrays of stacks, plates, sets or wells. 
 *  
 * <table cellspacing="10px" style="text-align:left; margin: 20px;">
 *    <th><div style="border-bottom: 1px solid black; padding-bottom: 5px; padding-top: 18px;">Operation<div></th>
 *    <th><div style="border-bottom: 1px solid black; padding-bottom: 5px;">Beginning<br>Index<div></th>
 *    <th><div style="border-bottom: 1px solid black; padding-bottom: 5px;">Length of<br>Subset<div></th>
 *    <th><div style="border-bottom: 1px solid black; padding-bottom: 5px; padding-top: 18px;">Input/Output</div></th>
 *    <tr>
 *       <td valign="top">
 *          <table>
 *             <tr>
 *                <td>Standard</td>
 *             </tr>
 *          </table>  
 *       </td>
 *       <td valign="top">
 *          <table>
 *             <tr>
 *                <td>+/-</td>
 *             </tr>
 *          </table>  
 *       </td>
 *       <td valign="top">
 *          <table>
 *             <tr>
 *                <td>+/-</td>
 *             </tr>
 *          </table>  
 *       </td>
 *       <td valign="top">
 *          <table>
 *             <tr>
 *                <td>Accepts a single well, set, plate or stack as input</td>
 *             </tr>
 *             <tr>
 *                <td>Calculates the statistic for each well in a well, set, plate or stack</td>
 *             </tr>
 *          </table>  
 *       </td>
 *    </tr>
 *    <tr>
 *       <td valign="top">
 *          <table>
 *             <tr>
 *                <td>Aggregated</td>
 *             </tr>
 *          </table>  
 *       </td>
 *       <td valign="top">
 *          <table>
 *             <tr>
 *                <td>+/-</td>
 *             </tr>
 *          </table>  
 *       </td>
 *       <td valign="top">
 *          <table>
 *             <tr>
 *                <td>+/-</td>
 *             </tr>
 *          </table>  
 *       </td>
 *       <td valign="top">
 *          <table>
 *             <tr>
 *                <td>Accepts a single well/set/plate/stack or a collection/array of wells/sets/plates/stacks as input</td>
 *             </tr>
 *              <tr>
 *                <td>Aggregates the data from all the wells in a well/set/plate/stack and calculates the statistic using the aggregated data</td>
 *             </tr>
 *          </table>  
 *       </td>
 *    </tr>
 * </table>
 * 
 * MicroFlex currently supports the following statistical operations:
 * 
 * <table cellspacing="5px" style="text-align:left; margin: 20px;">
 *    <th><div style="border-bottom: 1px solid black; padding-bottom: 5px;">Descriptive Statistics<div></th>
 *    <tr>
 *       <td>Mean</td>
 *    </tr>
 *    <tr>
 *       <td>Geometric Mean</td>
 *    </tr>
 *    <tr>
 *      <td>Quadratic Mean</td>
 *    </tr>
 *    <tr>
 *       <td>Sum</td>
 *    </tr>
 *    <tr>
 *       <td>Sum Squared</td>
 *    </tr>
 *    <tr>
 *       <td>Skewness</td>
 *    </tr>
 *    <tr>
 *       <td>Kurtosis</td>
 *    </tr>
 *    <tr>
 *       <td>Maximum</td>
 *    </tr>
 *    <tr>
 *       <td>Minimum</td>
 *    </tr>
 *    <tr>
 *       <td>N</td>
 *    </tr>
 *    <tr>
 *       <td>Percentile</td>
 *    </tr>
 *    <tr>
 *       <td>Population Variance</td>
 *    </tr>
 *    <tr>
 *       <td>Sample Variance</td>
 *    </tr>
 *    <tr>
 *       <td>Standard Deviation</td>
 *    </tr>
 *    <tr>
 *       <td></td>
 *    </tr>
 * </table>
 * 
 * @author Jesse L. Mull
 * @update Updated Oct 18, 2016
 * @address http://www.jessemull.com
 * @email hello@jessemull.com
 */
public abstract class DescriptiveStatisticListContext {
    
    /* ---------------- Well statistics for all plate wells ----------------- */
    
    /**
     * Returns the statistic for each plate well.
     * @param    Plate    the plate
     * @param    MathContext        the math context
     * @return                      map of wells and results
     */
    public Map<Well, List<BigDecimal>> plate(Plate plate, MathContext mc) {
        
        Preconditions.checkNotNull(plate, "The plate value cannot be null.");
        
        Map<Well, List<BigDecimal>> result = new TreeMap<Well, List<BigDecimal>>();
        
        for (Well well : plate) {
            Well clone = new Well(well);
            result.put(clone, well(well, mc));
        }
      
        return result;
        
    }
    
    /**
     * Returns the statistic for each plate well using the values between the beginning 
     * and ending indices.
     * @param    Plate    the plate
     * @param    int                beginning index of subset
     * @param    int                length of subset
     * @param    MathContext        the math context
     * @return                      map of wells and results   
     */
    public Map<Well, List<BigDecimal>> plate(Plate plate, int begin, int length, MathContext mc) {
        
        Preconditions.checkNotNull(plate, "The plate value cannot be null.");
        
        Map<Well, List<BigDecimal>> result = new TreeMap<Well, List<BigDecimal>>();
        
        for (Well well : plate) {
            Well clone = new Well(well);
            result.put(clone, well(well, begin, length, mc));
        }
        
        return result;
        
    }

    /* --------------------- Aggregated plate statistics -------------------  */
    
    /**
     * Returns the aggregated statistic for the plate.
     * @param    Plate    the plate
     * @param    MathContext        the math context
     * @return                      the aggregated result
     */
    public List<BigDecimal> platesAggregated(Plate plate, MathContext mc) {
        
        Preconditions.checkNotNull(plate, "The plate cannot be null.");

        List<BigDecimal> aggregated = new ArrayList<BigDecimal>();
            
        for (Well well : plate) {
            aggregated.addAll(well.data());
        }
      
        return calculate(aggregated, mc);
        
    }
    
    /**
     * Returns the aggregated statistic for each plate.
     * @param    Collection<PlateBigDecimal>    collection of plates
     * @param    MathContext                    the math context
     * @return                                  map of plates and aggregated results
     */
    public Map<Plate, List<BigDecimal>> platesAggregated(Collection<Plate> collection, MathContext mc) {
        
        Preconditions.checkNotNull(collection, "The plate collection cannot be null.");

        Map<Plate, List<BigDecimal>> results = new TreeMap<Plate, List<BigDecimal>>();
        
        for(Plate plate : collection) {
            
            List<BigDecimal> aggregated = new ArrayList<BigDecimal>();
            Plate clone = new Plate(plate);
            
            for (Well well : plate) {
                aggregated.addAll(well.data());
            }
      
            results.put(clone, calculate(aggregated, mc));
        
        }
        
        return results;
    }
    
    /**
     * Returns the aggregated statistic for each plate.
     * @param    PlateBigDecimal[]    array of plates
     * @param    MathContext          the math context
     * @return                        map of plates and aggregated result
     */
    public Map<Plate, List<BigDecimal>> platesAggregated(Plate[] array, MathContext mc) {
        
        Preconditions.checkNotNull(array, "The plate array cannot be null.");

        Map<Plate, List<BigDecimal>> results = new TreeMap<Plate, List<BigDecimal>>();
        
        for(Plate plate : array) {
            
            List<BigDecimal> aggregated = new ArrayList<BigDecimal>();
            Plate clone = new Plate(plate);
            
            for (Well well : plate) {
                aggregated.addAll(well.data());
            }
      
            results.put(clone, calculate(aggregated, mc));
        
        }
        
        return results;
    }    
    
    /**
     * Returns the aggregated statistic for each plate using the values between the 
     * indices.
     * @param    Plate    the plate
     * @param    int                beginning index of subset
     * @param    int                length of subset
     * @param    MathContext        the math context
     * @return                      the aggregated result
     */
    public List<BigDecimal> platesAggregated(
            Plate plate, int begin, int length, MathContext mc) {

        Preconditions.checkNotNull(plate, "The plate cannot be null.");

        List<BigDecimal> aggregated = new ArrayList<BigDecimal>();
            
        for (Well well : plate) {
            aggregated.addAll(well.data().subList(begin, begin + length));
        }
      
        return calculate(aggregated, mc);
        
    }
    
    /**
     * Returns the aggregated statistic for each plate using the values between 
     * the indices.
     * @param    Collection<PlateBigDecimal>    collection of plates
     * @param    int                            beginning index of subset
     * @param    int                            length of subset
     * @param    MathContext                    the math context
     * @return                                  map of plates and aggregated results
     */
    public Map<Plate, List<BigDecimal>> platesAggregated(
            Collection<Plate> collection, int begin, int length, MathContext mc) {
        
        Preconditions.checkNotNull(collection, "The plate collection cannot be null.");

        Map<Plate, List<BigDecimal>> results = new TreeMap<Plate, List<BigDecimal>>();
        
        for(Plate plate : collection) {
            
            List<BigDecimal> aggregated = new ArrayList<BigDecimal>();
            Plate clone = new Plate(plate);
            
            for (Well well : plate) {
                aggregated.addAll(well.data().subList(begin, begin + length));
            }
         
            results.put(clone, calculate(aggregated, mc));
        
        }
        
        return results;
        
    }
    
    /**
     * Returns the aggregated statistic for each plate using the values between 
     * the indices.
     * @param    PlateBigDecimal[]    array of plates
     * @param    int                  beginning index of subset
     * @param    int                  length of subset
     * @param    MathContext          the math context
     * @return                        map of plates and aggregated results	
     */
    public Map<Plate, List<BigDecimal>> platesAggregated(
            Plate[] array, int begin, int length, MathContext mc) {
       
        Preconditions.checkNotNull(array, "The plate array cannot be null.");
        
        Map<Plate, List<BigDecimal>> results = new TreeMap<Plate, List<BigDecimal>>();
        
        for(Plate plate : array) {
            
            List<BigDecimal> aggregated = new ArrayList<BigDecimal>();
            Plate clone = new Plate(plate);
            
            for (Well well : plate) {
                aggregated.addAll(well.data().subList(begin, begin + length));
            }      
            
            results.put(clone, calculate(aggregated, mc));
        
        }
        
        return results;
        
    }
    
    /* --------------- Well statistics for all wells in a set --------------  */
    
    /**
     * Returns the statistic of each well in the well set.
     * @param    WellSet    the well set
     * @param    MathContext          the math context
     * @return                        map of wells and results
     */
    public Map<Well, List<BigDecimal>> set(WellSet set, MathContext mc) {
    	
        Preconditions.checkNotNull(set, "The set cannot be null.");
    	
    	Map<Well, List<BigDecimal>> result = new TreeMap<Well, List<BigDecimal>>();
        
        for (Well well : set) {
            Well clone = new Well(well);
            result.put(clone, well(well, mc));
        }
      
        return result;
        
    }
    
    /**
     * Returns the statistic of each well in the well set using the values between 
     * the beginning and ending indices.
     * @param    WellSet    the well set
     * @param    int                  beginning index of subset
     * @param    int                  length of subset
     * @param    MathContext          the math context
     * @return                        map of wells and results
     */
    public Map<Well, List<BigDecimal>> set(WellSet set, int begin, int length, MathContext mc) {
        
    	Preconditions.checkNotNull(set, "The well set cannot be null.");
    	
    	Map<Well, List<BigDecimal>> result = new TreeMap<Well, List<BigDecimal>>();
        
        for (Well well : set) {
        	Well clone = new Well(well);
            result.put(clone,  well(well, begin, length, mc));
        }
        
        return result;
        
    }

    /* --------------------- Aggregated set statistics ---------------------  */
    
    /**
     * Returns the aggregated statistic for the well set.
     * @param    WellSet    the well set
     * @param    MathContext          the math context
     * @return                        the aggregated result
     */
    public List<BigDecimal> setsAggregated(WellSet set, MathContext mc) {
        
        Preconditions.checkNotNull(set, "The well set cannot be null.");

        List<BigDecimal> aggregated = new ArrayList<BigDecimal>();
        	
        for (Well well : set) {
            aggregated.addAll(well.data());
        }
      
        return calculate(aggregated, mc);
        
    }
    
    /**
     * Returns the aggregated statistic for each well set.
     * @param    Collection<WellSetBigDecimal>    collection of well sets
     * @param    MathContext                      the math context
     * @return                                    map of well sets and aggregated results
     */
    public Map<WellSet, List<BigDecimal>> setsAggregated(Collection<WellSet> collection, MathContext mc) {
        
        Preconditions.checkNotNull(collection, "The well set collection cannot be null.");

        Map<WellSet, List<BigDecimal>> results = new TreeMap<WellSet, List<BigDecimal>>();
        
        for(WellSet set : collection) {
            
            List<BigDecimal> aggregated = new ArrayList<BigDecimal>();
            WellSet clone = new WellSet(set);
            
            for (Well well : set) {
                aggregated.addAll(well.data());
            }
      
            results.put(clone, calculate(aggregated, mc));
        
        }
        
        return results;
    }
    
    /**
     * Returns the aggregated statistic for each well set.
     * @param    WellSetBigDecimal[]    array of well sets
     * @param    MathContext            the math context
     * @return                          map of well sets and aggregated results
     */
    public Map<WellSet, List<BigDecimal>> setsAggregated(WellSet[] array, MathContext mc) {
        
        Preconditions.checkNotNull(array, "The well set array cannot be null.");

        Map<WellSet, List<BigDecimal>> results = new TreeMap<WellSet, List<BigDecimal>>();
        
        for(WellSet set : array) {
            
            List<BigDecimal> aggregated = new ArrayList<BigDecimal>();
            WellSet clone = new WellSet(set);
            
            for (Well well : set) {
                aggregated.addAll(well.data());
            }
      
            results.put(clone, calculate(aggregated, mc));
        
        }
        
        return results;
    }    
    
    /**
     * Returns the aggregated statistic for each well set using the values between the 
     * indices.
     * @param    WellSet    the well set
     * @param    int                  beginning index of subset
     * @param    int                  length of subset
     * @param    MathContext          the math context
     * @return                        the aggregated result
     */
    public List<BigDecimal> setsAggregated(
            WellSet set, int begin, int length, MathContext mc) {

        Preconditions.checkNotNull(set, "The well set cannot be null.");

        List<BigDecimal> aggregated = new ArrayList<BigDecimal>();
            
        for (Well well : set) {
            aggregated.addAll(well.data().subList(begin, begin + length));
        }
      
        return calculate(aggregated, mc);
        
    }
    
    /**
     * Returns the aggregated statistic for each well set using the values between 
     * the indices.
     * @param    Collection<WellSetBigDecimal>    collection of well sets
     * @param    int                              beginning index of subset
     * @param    int                              length of subset
     * @param    MathContext                      the math context
     * @return                                    map of well sets and aggregated results
     */
    public Map<WellSet, List<BigDecimal>> setsAggregated(
            Collection<WellSet> collection, int begin, int length, MathContext mc) {
        
        Preconditions.checkNotNull(collection, "The well set collection cannot be null.");
        
        Map<WellSet, List<BigDecimal>> results = new TreeMap<WellSet, List<BigDecimal>>();
        
        for(WellSet set : collection) {
            
            List<BigDecimal> aggregated = new ArrayList<BigDecimal>();
            WellSet clone = new WellSet(set);
            
            for (Well well : set) {
                aggregated.addAll(well.data().subList(begin, begin + length));
            }
      
            results.put(clone, calculate(aggregated, mc));
        
        }
        
        return results;
        
    }
    
    /**
     * Returns the aggregated statistic for each well set using the values between 
     * the indices.
     * @param    WellSetBigDecimal[]    array of well sets
     * @param    int                    beginning index of subset
     * @param    int                    length of subset
     * @param    MathContext            the math context
     * @return                          map of well sets and aggregated results
     */
    public Map<WellSet, List<BigDecimal>> setsAggregated(
            WellSet[] array, int begin, int length, MathContext mc) {
        
        Preconditions.checkNotNull(array, "The well set array cannot be null.");
        
        Map<WellSet, List<BigDecimal>> results = new TreeMap<WellSet, List<BigDecimal>>();
        
        for(WellSet set : array) {
            
            List<BigDecimal> aggregated = new ArrayList<BigDecimal>();
            WellSet clone = new WellSet(set);
            
            for (Well well : set) {
                aggregated.addAll(well.data().subList(begin, begin + length));
            }
      
            results.put(clone, calculate(aggregated, mc));
        
        }
        
        return results;
        
    }
    
    /* -------------------------- Well statistics --------------------------  */
    
    /**
     * Returns the well statistic.
     * @param    Well    the well
     * @param    MathContext       the math context
     * @return                     the result
     */
    public List<BigDecimal> well(Well well, MathContext mc) {
        Preconditions.checkNotNull(well, "The well cannot be null.");
        return calculate(well.data(), mc);
        
    }
    
    /**
     * Returns the well statistic for the values between the beginning and ending 
     * indices.
     * @param    Well    the well
     * @param    int               beginning index of subset
     * @param    int               length of the subset
     * @param    MathContext       the math context
     * @return                     the result
     */
    public List<BigDecimal> well(Well well, int begin, int length, MathContext mc) {
        Preconditions.checkNotNull(well, "The well cannot be null.");
        Preconditions.checkArgument(begin <= well.data().size() &&
                                    begin >= 0 &&
                                    begin + length <= well.data().size());  
       
        return calculate(well.data(), begin, length, mc);
        
    }

    /* -------- Methods for calculating the statistic of a data set --------  */
    
    /**
     * Calculates the statistic.
     * @param    List<BigDecimal>    the list
     * @param    MathContext         the math context
     * @return                       the result
     */
    public abstract List<BigDecimal> calculate(List<BigDecimal> list, MathContext mc);
    
    /**
     * Calculates the statistic of the values between the beginning and ending indices.
     * @param    List<BigDecimal>    the list
     * @param    int                 beginning index of subset
     * @param    int                 length of subset
     * @param    MathContext         the math context
     * @return                       the result
     */
    public abstract List<BigDecimal> calculate(List<BigDecimal> list, int begin, int length, MathContext mc);
}
