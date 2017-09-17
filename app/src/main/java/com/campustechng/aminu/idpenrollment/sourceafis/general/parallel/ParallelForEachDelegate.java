/**
 * @author Veaceslav Dubenco
 * @since 14.10.2012
 */
package com.campustechng.aminu.idpenrollment.sourceafis.general.parallel;

/**
 * 
 */
public interface ParallelForEachDelegate<T> {
	T delegate(T item);

	T combineResults(T result1, T result2);
}
