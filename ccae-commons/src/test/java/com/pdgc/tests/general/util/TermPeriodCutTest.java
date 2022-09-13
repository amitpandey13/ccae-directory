package com.pdgc.tests.general.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.structures.timeperiod.TimePeriodPart;
import com.pdgc.general.structures.timeperiod.TimePeriodType;
import com.pdgc.general.util.DateTimeUtil;

public class TermPeriodCutTest {

    @Before
    public void setUp() throws IOException {
        Constants.instantiateConstants();
    }
    
    @Test
    public void singleTermPeriodNoLimiter() {
        Set<TermPeriod> cuttingTermPeriods = Sets.newHashSet(
            new TermPeriod(new Term(Constants.EPOCH, Constants.PERPETUITY), TimePeriod.FULL_WEEK)
        );
        
        Map<TermPeriod, Set<TermPeriod>> cutTermPeriodMappings = DateTimeUtil.createCutTermPeriodMappings(
            DateTimeUtil.createTermPeriodMap(cuttingTermPeriods)
        );
        
        TermPeriod cutPeriod;
        Set<TermPeriod> sourcePeriods;
        
        assertEquals(1, cutTermPeriodMappings.size());
        
        cutPeriod = new TermPeriod(new Term(Constants.EPOCH, Constants.PERPETUITY), TimePeriod.FULL_WEEK);
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(cutPeriod));
    }
    
    /**
     * Single termPeriod, limiting term/period are subsets of the original
     */
    @Test
    public void singleTermPeriodWithSubsetLimiters() {
        Set<TermPeriod> cuttingTermPeriods = Sets.newHashSet(
            new TermPeriod(new Term(Constants.EPOCH, Constants.PERPETUITY), TimePeriod.FULL_WEEK)
        );
        
        Term limitTerm = new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2019, 12, 31));
        TimePeriod limitPeriod = new TimePeriodPart(
            Duration.ofHours(17), Duration.ofHours(22), TimePeriodType.DAILY, 
            true, true, true, true, true, false, false
        ).convertToTimePeriod();
        Map<TermPeriod, Set<TermPeriod>> cutTermPeriodMappings = DateTimeUtil.createCutTermPeriodMappings(
            DateTimeUtil.createTermPeriodMap(cuttingTermPeriods),
            limitTerm,
            limitPeriod
        );
        
        TermPeriod cutPeriod;
        Set<TermPeriod> sourcePeriods;
        
        assertEquals(1, cutTermPeriodMappings.size());
        
        cutPeriod = new TermPeriod(limitTerm, limitPeriod);
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(new TermPeriod(new Term(Constants.EPOCH, Constants.PERPETUITY), TimePeriod.FULL_WEEK)));
    }

    /**
     * Single termPeriod which is smaller than the limiting term/period
     */
    @Test
    public void singleTermPeriodWithSupersetLimiters() {
        TermPeriod smallTermPeriod = new TermPeriod(
            new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2019, 12, 31)),
            new TimePeriodPart(
                Duration.ofHours(17), Duration.ofHours(22), TimePeriodType.DAILY, 
                true, true, true, true, true, false, false
            ).convertToTimePeriod()
        );
        Set<TermPeriod> cuttingTermPeriods = Sets.newHashSet(
            smallTermPeriod
        );
        
        Map<TermPeriod, Set<TermPeriod>> cutTermPeriodMappings = DateTimeUtil.createCutTermPeriodMappings(
            DateTimeUtil.createTermPeriodMap(cuttingTermPeriods),
            new Term(Constants.EPOCH, Constants.PERPETUITY),
            TimePeriod.FULL_WEEK
        );
            
        TermPeriod cutPeriod;
        Set<TermPeriod> sourcePeriods;
        
        assertEquals(1, cutTermPeriodMappings.size());
        
        cutPeriod = smallTermPeriod;
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(smallTermPeriod));
    }

    /**
     * Single termPeriod that is equal to the limiting term/period
     */
    @Test
    public void singleTermPeriodWithEqualLimiters() {
        TermPeriod termPeriod = new TermPeriod(
            new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2019, 12, 31)),
            new TimePeriodPart(
                Duration.ofHours(17), Duration.ofHours(22), TimePeriodType.DAILY, 
                true, true, true, true, true, false, false
            ).convertToTimePeriod()
        );
        Set<TermPeriod> cuttingTermPeriods = Sets.newHashSet(
            termPeriod
        );
        
        Map<TermPeriod, Set<TermPeriod>> cutTermPeriodMappings = DateTimeUtil.createCutTermPeriodMappings(
            DateTimeUtil.createTermPeriodMap(cuttingTermPeriods),
            termPeriod.getTerm(),
            termPeriod.getTimePeriod()
        );
            
        TermPeriod cutPeriod;
        Set<TermPeriod> sourcePeriods;
        
        assertEquals(1, cutTermPeriodMappings.size());
        
        cutPeriod = termPeriod;
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(termPeriod));
    }

    /**
     * Multiple termPeriods, with absolutely no overlaps
     */
    @Test
    public void multipleNonOverlappingTermPeriods() {
        Set<TermPeriod> cuttingTermPeriods = Sets.newHashSet(
            new TermPeriod(
                new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2019, 12, 31)),
                TimePeriod.FULL_WEEK
            ),
            new TermPeriod(
                new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2029, 12, 31)),
                TimePeriod.FULL_WEEK
            ),
            new TermPeriod(
                new Term(DateTimeUtil.createDate(2030, 1, 1), DateTimeUtil.createDate(2039, 12, 31)),
                TimePeriod.FULL_WEEK
            )
        );
        
        Map<TermPeriod, Set<TermPeriod>> cutTermPeriodMappings = DateTimeUtil.createCutTermPeriodMappings(
            DateTimeUtil.createTermPeriodMap(cuttingTermPeriods),
            new Term(Constants.EPOCH, Constants.PERPETUITY),
            TimePeriod.FULL_WEEK
        );
            
        TermPeriod cutPeriod;
        Set<TermPeriod> sourcePeriods;
        
        assertEquals(cuttingTermPeriods.size(), cutTermPeriodMappings.size());
        for (TermPeriod tp : cuttingTermPeriods) {
            cutPeriod = tp;
            assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
            sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
            assertEquals(1, sourcePeriods.size());
            assertTrue(sourcePeriods.contains(cutPeriod));
        }
    }

    /**
     * Two termPeriods. One of which fully encompasses the other in both term and timePeriod
     */
    @Test
    public void fullyEncompassedTermPeriods() {
        TermPeriod smallTermPeriod = new TermPeriod(
            new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2019, 12, 31)),
            new TimePeriodPart(
                Duration.ofHours(17), Duration.ofHours(22), TimePeriodType.DAILY, 
                true, true, true, true, true, false, false
            ).convertToTimePeriod()
        );
        TermPeriod bigTermPeriod = new TermPeriod(
            new Term(Constants.EPOCH, Constants.PERPETUITY),
            TimePeriod.FULL_WEEK
        );
        Set<TermPeriod> cuttingTermPeriods = Sets.newHashSet(
            smallTermPeriod,
            bigTermPeriod
        );
        
        Map<TermPeriod, Set<TermPeriod>> cutTermPeriodMappings = DateTimeUtil.createCutTermPeriodMappings(
            DateTimeUtil.createTermPeriodMap(cuttingTermPeriods)
        );
            
        TermPeriod cutPeriod;
        Set<TermPeriod> sourcePeriods;
        
        assertEquals(4, cutTermPeriodMappings.size());
        
        cutPeriod = new TermPeriod(new Term(Constants.EPOCH, DateTimeUtil.createDate(2009, 12, 31)), TimePeriod.FULL_WEEK);
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(bigTermPeriod));
        
        cutPeriod = smallTermPeriod;
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(2, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(smallTermPeriod));
        assertTrue(sourcePeriods.contains(bigTermPeriod));
        
        cutPeriod = new TermPeriod(smallTermPeriod.getTerm(), TimePeriod.subtractPeriods(TimePeriod.FULL_WEEK, smallTermPeriod.getTimePeriod()));
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(bigTermPeriod));
        
        cutPeriod = new TermPeriod(new Term(DateTimeUtil.createDate(2020, 1, 1), Constants.PERPETUITY), TimePeriod.FULL_WEEK);
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(bigTermPeriod));
    }

    /**
     * Two termPeriods - A and B. 
     * A.term fully encompasses B.term 
     * B.timePeriod merely overlaps with A.timePeriod instead of being encompassed
     */
    @Test
    public void fullyEncompassedTermOverlappingPeriod() {
        TermPeriod tpA = new TermPeriod(
            new Term(Constants.EPOCH, Constants.PERPETUITY),
            new TimePeriodPart(
                Duration.ofHours(17), Duration.ofHours(22), TimePeriodType.DAILY, 
                false, false, false, true, true, true, true
            ).convertToTimePeriod()
        );
        TermPeriod tpB = new TermPeriod(
            new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2019, 12, 31)),
            new TimePeriodPart(
                Duration.ofHours(17), Duration.ofHours(22), TimePeriodType.DAILY, 
                true, true, true, true, true, false, false
            ).convertToTimePeriod()
        );
        Set<TermPeriod> cuttingTermPeriods = Sets.newHashSet(
            tpA,
            tpB
        );
        
        Map<TermPeriod, Set<TermPeriod>> cutTermPeriodMappings = DateTimeUtil.createCutTermPeriodMappings(
            DateTimeUtil.createTermPeriodMap(cuttingTermPeriods)
        );
            
        TermPeriod cutPeriod;
        Set<TermPeriod> sourcePeriods;
        
        assertEquals(5, cutTermPeriodMappings.size());
        
        cutPeriod = new TermPeriod(new Term(Constants.EPOCH, DateTimeUtil.createDate(2009, 12, 31)), tpA.getTimePeriod());
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpA));
        
        cutPeriod = new TermPeriod(tpB.getTerm(), TimePeriod.subtractPeriods(tpA.getTimePeriod(), tpB.getTimePeriod()));
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpA));
        
        cutPeriod = new TermPeriod(tpB.getTerm(), TimePeriod.intersectPeriods(tpA.getTimePeriod(), tpB.getTimePeriod()));
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(2, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpA));
        assertTrue(sourcePeriods.contains(tpB));
        
        cutPeriod = new TermPeriod(tpB.getTerm(), TimePeriod.subtractPeriods(tpB.getTimePeriod(), tpA.getTimePeriod()));
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpB));
        
        cutPeriod = new TermPeriod(new Term(DateTimeUtil.createDate(2020, 1, 1), Constants.PERPETUITY), tpA.getTimePeriod());
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpA));
    }

    /**
     * Two termPeriods - A and B. 
     * A.term and B.term overlapping
     * A.timePeriod fully encompasses B.timePeriod
     */
    @Test
    public void overlappingTermFullyEncompassedPeriod() {
        TermPeriod tpA = new TermPeriod(
            new Term(Constants.EPOCH, DateTimeUtil.createDate(2014, 12, 31)),
            TimePeriod.FULL_WEEK
        );
        TermPeriod tpB = new TermPeriod(
            new Term(DateTimeUtil.createDate(2010, 1, 1), Constants.PERPETUITY),
            new TimePeriodPart(
                Duration.ofHours(17), Duration.ofHours(22), TimePeriodType.DAILY, 
                true, true, true, true, true, false, false
            ).convertToTimePeriod()
        );
        Set<TermPeriod> cuttingTermPeriods = Sets.newHashSet(
            tpA,
            tpB
        );
        
        Map<TermPeriod, Set<TermPeriod>> cutTermPeriodMappings = DateTimeUtil.createCutTermPeriodMappings(
            DateTimeUtil.createTermPeriodMap(cuttingTermPeriods)
        );
            
        TermPeriod cutPeriod;
        Set<TermPeriod> sourcePeriods;
        
        assertEquals(4, cutTermPeriodMappings.size());
        
        cutPeriod = new TermPeriod(new Term(Constants.EPOCH, DateTimeUtil.createDate(2009, 12, 31)), tpA.getTimePeriod());
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpA));
        
        cutPeriod = new TermPeriod(new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2014, 12, 31)), tpB.getTimePeriod());
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(2, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpA));
        assertTrue(sourcePeriods.contains(tpB));
        
        cutPeriod = new TermPeriod(new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2014, 12, 31)), TimePeriod.subtractPeriods(tpA.getTimePeriod(), tpB.getTimePeriod()));
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpA));
        
        cutPeriod = new TermPeriod(new Term(DateTimeUtil.createDate(2015, 1, 1), Constants.PERPETUITY), tpB.getTimePeriod());
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpB));
    }

    /**
     * Two termPeriods - A and B. 
     * A.term and B.term overlapping
     * A.timePeriod overlapping with B.timePeriod
     */
    @Test
    public void overlappingTermOverlappingPeriod() {
        TermPeriod tpA = new TermPeriod(
            new Term(Constants.EPOCH, DateTimeUtil.createDate(2014, 12, 31)),
            new TimePeriodPart(
                Duration.ofHours(17), Duration.ofHours(22), TimePeriodType.DAILY, 
                false, false, false, true, true, true, true
            ).convertToTimePeriod()
        );
        TermPeriod tpB = new TermPeriod(
            new Term(DateTimeUtil.createDate(2010, 1, 1), Constants.PERPETUITY),
            new TimePeriodPart(
                Duration.ofHours(17), Duration.ofHours(22), TimePeriodType.DAILY, 
                true, true, true, true, true, false, false
            ).convertToTimePeriod()
        );
        Set<TermPeriod> cuttingTermPeriods = Sets.newHashSet(
            tpA,
            tpB
        );
        
        Map<TermPeriod, Set<TermPeriod>> cutTermPeriodMappings = DateTimeUtil.createCutTermPeriodMappings(
            DateTimeUtil.createTermPeriodMap(cuttingTermPeriods)
        );
            
        TermPeriod cutPeriod;
        Set<TermPeriod> sourcePeriods;
        
        assertEquals(5, cutTermPeriodMappings.size());
        
        cutPeriod = new TermPeriod(new Term(Constants.EPOCH, DateTimeUtil.createDate(2009, 12, 31)), tpA.getTimePeriod());
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpA));
        
        cutPeriod = new TermPeriod(new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2014, 12, 31)), TimePeriod.subtractPeriods(tpA.getTimePeriod(), tpB.getTimePeriod()));
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpA));
        
        cutPeriod = new TermPeriod(new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2014, 12, 31)), TimePeriod.intersectPeriods(tpA.getTimePeriod(), tpB.getTimePeriod()));
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(2, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpA));
        assertTrue(sourcePeriods.contains(tpB));
        
        cutPeriod = new TermPeriod(new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2014, 12, 31)), TimePeriod.subtractPeriods(tpB.getTimePeriod(), tpA.getTimePeriod()));
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpB));
        
        cutPeriod = new TermPeriod(new Term(DateTimeUtil.createDate(2015, 1, 1), Constants.PERPETUITY), tpB.getTimePeriod());
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpB));
    }

    /**
     * Two termPeriods - A and B. 
     * A.term and B.term overlapping
     * A.timePeriod does not intersect with B.timePeriod
     */
    @Test
    public void overlappingTermNonOverlappingPeriod() {
        TermPeriod tpA = new TermPeriod(
            new Term(Constants.EPOCH, DateTimeUtil.createDate(2014, 12, 31)),
            new TimePeriodPart(
                Duration.ofHours(17), Duration.ofHours(22), TimePeriodType.DAILY, 
                false, false, false, true, true, true, true
            ).convertToTimePeriod()
        );
        TermPeriod tpB = new TermPeriod(
            new Term(DateTimeUtil.createDate(2010, 1, 1), Constants.PERPETUITY),
            new TimePeriodPart(
                Duration.ofHours(17), Duration.ofHours(22), TimePeriodType.DAILY, 
                true, true, true, false, false, false, false
            ).convertToTimePeriod()
        );
        Set<TermPeriod> cuttingTermPeriods = Sets.newHashSet(
            tpA,
            tpB
        );
        
        Map<TermPeriod, Set<TermPeriod>> cutTermPeriodMappings = DateTimeUtil.createCutTermPeriodMappings(
            DateTimeUtil.createTermPeriodMap(cuttingTermPeriods)
        );
            
        TermPeriod cutPeriod;
        Set<TermPeriod> sourcePeriods;
        
        assertEquals(4, cutTermPeriodMappings.size());
        
        cutPeriod = new TermPeriod(new Term(Constants.EPOCH, DateTimeUtil.createDate(2009, 12, 31)), tpA.getTimePeriod());
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpA));
        
        cutPeriod = new TermPeriod(new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2014, 12, 31)), tpA.getTimePeriod());
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpA));
        
        cutPeriod = new TermPeriod(new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2014, 12, 31)), tpB.getTimePeriod());
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpB));
        
        cutPeriod = new TermPeriod(new Term(DateTimeUtil.createDate(2015, 1, 1), Constants.PERPETUITY), tpB.getTimePeriod());
        assertTrue(cutTermPeriodMappings.containsKey(cutPeriod));
        sourcePeriods = cutTermPeriodMappings.get(cutPeriod);
        assertEquals(1, sourcePeriods.size());
        assertTrue(sourcePeriods.contains(tpB));
    }
}
