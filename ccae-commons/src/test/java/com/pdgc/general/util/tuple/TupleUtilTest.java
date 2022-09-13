package com.pdgc.general.util.tuple;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.javatuples.Pair;
import org.junit.jupiter.api.Test;

public class TupleUtilTest {
    
    @Test
    void givenValidPair_whenPairToString_thenStringify() {
        //Assemble
        Pair<Long, Long> pair = new Pair<>(1L, 2L);
        
        // Act
        String actual = TupleUtil.pairToString(pair);
        
        // Assert
        String expected = "(1,2)";
        assertEquals(expected, actual);
        
        // Assemble
        Pair<String, String> pair2 = new Pair<>("test", "test2");
        
        // Act
        String actual2 = TupleUtil.pairToString(pair2);
        
        // Assert
        String expected2 = "(test,test2)";
        assertEquals(expected2, actual2);
    }
    
    @Test
    void givenObjectPair_whenPairToString_thenStringifyUsingImplementedToString() {
        // Assemble
        SingleVarPojo obj1, obj2;
        obj1 = new SingleVarPojo("abc");
        obj2 = new SingleVarPojo("def");
        Pair<SingleVarPojo, SingleVarPojo> objPair = new Pair<>(obj1, obj2);
        
        // Act
        String actual = TupleUtil.pairToString(objPair);
        
        // Assert
        String expected = "(test,test)";
        assertEquals(expected, actual);
    }
    
    
    @Test
    void givenPairCollection_whenToStringCollection_thenStringify() {
        // Assemble
        Collection<Pair<Long, Long>> pairCollection = new ArrayList<>();
        pairCollection.add(new Pair<>(1L, 1L));
        pairCollection.add(new Pair<>(2L, 1L));
        pairCollection.add(new Pair<>(3L, 2L));
        
        // Act
        String actual = TupleUtil.pairCollectionToString(pairCollection, ",", TupleUtil::pairToString);
        String actual2 = TupleUtil.pairCollectionToString(pairCollection, ",", x -> "test");
        
        // Assert
        String expected = "(1,1),(2,1),(3,2)";
        assertEquals(expected, actual);
        
        String expected2 = "test,test,test";
        assertEquals(expected2, actual2);
    }
    
    @Test
    void givenEmptyPairCollection_whenToStringCollection_thenReturnEmptyString() {
        // Assemble
        Collection<Pair<Long, Long>> pairCollection = new ArrayList<>();
        
        // Act
        String actual = TupleUtil.pairCollectionToString(pairCollection, "asdf", TupleUtil::pairToString);
        
        // Assert
        String expected = "";
        assertEquals(expected, actual);
    }
    
    class SingleVarPojo {
        private String name;
        
        public SingleVarPojo(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public String toString() {
            return "test";
        }
    }
}
