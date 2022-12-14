package com.pdgc.general.util.tuple;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.MutablePair;
import org.javatuples.Quartet;

/**
 * A {@link TupleWriter} is an object that can serialize a {@link ReadableTable}. The assumption is that most classes 
 * that implement {@link TupleWriter} will write the data to an {@link java.io.OutputStream}, e.g. 
 *  
 * There are three functions: 
 *  
 * <ul> 
 * <li>{@link writeColumnHeaders} - called once at the start of a batch of tuples to enable the writer to label columns 
 * if desired.</li> 
 * <li>{@link writeTuples} - called when there are new tuples to serialize.</li> 
 * <li>{@link done} - called when there will be no more tuples.</li> 
 * </ul> 
 *  
 *  
 */ 
public interface TupleWriter { 
 
	
  /**
   * Inform the {@link TupleWriter} of the column headers. In the standard case (CSV output, see {@link CsvTupleWriter} 
   * ), they are written out directly. In some cases, they may be cached and output with the data (see 
   * {@link JsonTupleWriter}). 
   *  
   * @param columnNames the names of the columns. 
   * @throws IOException if there is an error writing the tuples. 
   */ 
  void writeColumnHeaders(List<String> columnNames) throws IOException; 
 
  /**
   * Provide a {@link TupleWriter} with tuples to serialize. 
   *  
   * @param tuples the batch of tuples to be serialized. 
   * @throws IOException if there is an error writing the tuples. 
   */ 
  <T> void writeTuples(Set<MutablePair<T,T>> tuples) throws IOException; 
  
  <T> void writeQuartets(Set<Quartet<T,T,T,T>> tuples) throws IOException;
 
  /**
   * Inform the {@link TupleWriter} that no more tuples will come. It may close and flush an 
   * {@link java.io.OutputStream}, for example. 
   *  
   * @throws IOException if there is an error writing the tuples. 
   */ 
  void done() throws IOException; 
 
  /**
   * Called when the query filling in the {@link TupleWriter} has experienced an error. The {@link TupleWriter} should 
   * close the stream, preferably also informing the end-point of the error. 
   *  
   * @throws IOException if there is an error manipulating the state. 
   */ 
  void error() throws IOException; 
}
