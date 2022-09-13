package com.pdgc.tests.general.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Term;
import com.pdgc.general.util.DateTimeUtil;

public class GapTermTest {

    @BeforeAll
    public static void setUp() throws IOException {
        Constants.instantiateConstants();
    }
    
    @Test
    public void noTerms() {
        Set<Term> existingTerms = new HashSet<>();
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            Constants.TERM_EPOCH_TO_PERPETUITY
        );
        
        assertEquals(1, gapTerms.size());
        assertTrue(gapTerms.contains(Constants.TERM_EPOCH_TO_PERPETUITY));
    }
    
    /**
     * Single term that starts and ends before the evaluation term even begins
     */
    @Test
    public void singleTermTooEarly() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(DateTimeUtil.createDate(2008, 1, 1), DateTimeUtil.createDate(2008, 12, 31))
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2010, 12, 31))
        );
        
        assertEquals(1, gapTerms.size());
        assertTrue(gapTerms.contains(new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2010, 12, 31))));
    }
    
    /**
     * Single term that starts and ends after the evaluation term ends
     */
    @Test
    public void singleTermTooLate() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2020, 12, 31))
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2010, 12, 31))
        );
        
        assertEquals(1, gapTerms.size());
        assertTrue(gapTerms.contains(new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2010, 12, 31))));
    }
    
    /**
     * Multiple terms that leave the relevantTerm in between them
     */
    @Test
    public void noRelevantTerms() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(DateTimeUtil.createDate(2008, 1, 1), DateTimeUtil.createDate(2008, 12, 31)),
            new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2020, 12, 31))
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2010, 12, 31))
        );
        
        assertEquals(1, gapTerms.size());
        assertTrue(gapTerms.contains(new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2010, 12, 31))));
    }
    
    /**
     * Terms have a gap, but the gap is before the evaluated term
     */
    @Test
    public void irrelevantEarlyGap() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31)),
            new Term(DateTimeUtil.createDate(2009, 1, 1), DateTimeUtil.createDate(2020, 12, 31))
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2010, 12, 31))
        );
        
        assertEquals(0, gapTerms.size());
    }
    
    /**
     * Terms have a gap, but the gap is before the evaluated term
     */
    @Test
    public void irrelevantLateGap() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2011, 12, 31)),
            new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2020, 12, 31))
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2010, 12, 31))
        );
        
        assertEquals(0, gapTerms.size());
    }
    
    /**
     * Single term equal to the evaluation period
     */
    @Test
    public void singleFullTerm() {
        LocalDate startDate = DateTimeUtil.createDate(2010, 1, 1);
        LocalDate endDate = DateTimeUtil.createDate(2019, 12, 31);
        
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(startDate, endDate)
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            new Term(startDate, endDate)
        );
        
        assertEquals(0, gapTerms.size());
    }
    
    /**
     * Single term greater than the evaluation period
     */
    @Test
    public void singleBigTerm() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(Constants.EPOCH, Constants.PERPETUITY)
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2019, 12, 31))
        );
        
        assertEquals(0, gapTerms.size());
    }
    
    /**
     * Single term that leaves a gap at the beginning of the evaluation period
     */
    @Test
    public void singleTermGapBefore() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(DateTimeUtil.createDate(2010, 1, 1), Constants.PERPETUITY)
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            Constants.TERM_EPOCH_TO_PERPETUITY
        );
        
        assertEquals(1, gapTerms.size());
        assertTrue(gapTerms.contains(new Term(Constants.EPOCH, DateTimeUtil.createDate(2009, 12, 31))));
    }
    
    /**
     * Single term that leaves a gap at the end of the evaluation period
     */
    @Test
    public void singleTermGapAfter() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(Constants.EPOCH, DateTimeUtil.createDate(2019, 12, 31))
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            Constants.TERM_EPOCH_TO_PERPETUITY
        );
        
        assertEquals(1, gapTerms.size());
        assertTrue(gapTerms.contains(new Term(DateTimeUtil.createDate(2020, 1, 1), Constants.PERPETUITY)));
    }
    
    /**
     * Single term that leaves gaps at the beginning and end of the evaluation period
     */
    @Test
    public void singleTermGapBeforeAfter() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2019, 12, 31))
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            Constants.TERM_EPOCH_TO_PERPETUITY
        );
        
        assertEquals(2, gapTerms.size());
        assertTrue(gapTerms.contains(new Term(Constants.EPOCH, DateTimeUtil.createDate(2009, 12, 31))));
        assertTrue(gapTerms.contains(new Term(DateTimeUtil.createDate(2020, 1, 1), Constants.PERPETUITY)));
    }
    
    /**
     * Multiple terms that are adjacent to each other and therefore leave no gaps
     */
    @Test
    public void multipleAdjacentTerms() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(Constants.EPOCH, DateTimeUtil.createDate(2009, 12, 31)),
            new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2019, 12, 31)),
            new Term(DateTimeUtil.createDate(2020, 1, 1), Constants.PERPETUITY)
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            Constants.TERM_EPOCH_TO_PERPETUITY
        );
        
        assertEquals(0, gapTerms.size());
    }

    /**
     * Multiple terms that are not adjacent to each other and therefore have gaps
     */
    @Test
    public void nonAdjacentTerms() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(Constants.EPOCH, DateTimeUtil.createDate(2009, 12, 31)),
            new Term(DateTimeUtil.createDate(2010, 6, 1), DateTimeUtil.createDate(2019, 12, 31)),
            new Term(DateTimeUtil.createDate(2020, 6, 1), Constants.PERPETUITY)
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            Constants.TERM_EPOCH_TO_PERPETUITY
        );
        
        assertEquals(2, gapTerms.size());
        assertTrue(gapTerms.contains(new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2010, 5, 31))));
        assertTrue(gapTerms.contains(new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2020, 5, 31))));
    }
    
    /**
     * Multiple terms that overlap with each other and don't leave any gaps
     */
    @Test
    public void overlappingTerms() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(Constants.EPOCH, DateTimeUtil.createDate(2010, 12, 31)),
            new Term(DateTimeUtil.createDate(2010, 1, 1), DateTimeUtil.createDate(2020, 12, 31)),
            new Term(DateTimeUtil.createDate(2020, 1, 1), Constants.PERPETUITY)
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            Constants.TERM_EPOCH_TO_PERPETUITY
        );
        
        assertEquals(0, gapTerms.size());
    }
    
    /**
     * Non-adjacent terms that would normally leave a gap but with a big term that spans over
     * the gap left by the non-adjacent terms. This version has the big term's start and end dates 
     * earlier than the earliest start and latest end of the other terms
     */
    @Test
    public void bigTermEarlyStartEndAndNonAdjacentTerms() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2009, 12, 31)),
            new Term(DateTimeUtil.createDate(2010, 6, 1), DateTimeUtil.createDate(2019, 12, 31)),
            new Term(DateTimeUtil.createDate(2020, 6, 1), Constants.PERPETUITY),
            new Term(Constants.EPOCH, DateTimeUtil.createDate(2049, 12, 31))
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            Constants.TERM_EPOCH_TO_PERPETUITY
        );
        
        assertEquals(0, gapTerms.size());
    }
    
    /**
     * Non-adjacent terms that would normally leave a gap but with a big term that spans over
     * the gap left by the non-adjacent terms. This version has the big term's start date 
     * earlier than the earliest start date of the other terms. The big term's end date
     * is equal the other terms' latest end date.
     */
    @Test
    public void bigTermEarlyStartEqualEndAndNonAdjacentTerms() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2009, 12, 31)),
            new Term(DateTimeUtil.createDate(2010, 6, 1), DateTimeUtil.createDate(2019, 12, 31)),
            new Term(DateTimeUtil.createDate(2020, 6, 1), Constants.PERPETUITY),
            new Term(Constants.EPOCH, Constants.PERPETUITY)
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            Constants.TERM_EPOCH_TO_PERPETUITY
        );
        
        assertEquals(0, gapTerms.size());
    }
    
    /**
     * Non-adjacent terms that would normally leave a gap but with a big term that spans over
     * the gap left by the non-adjacent terms. This version has the big term's start date 
     * earlier than the earliest start date of the other terms. The big term's end date
     * is later than the other terms' latest end date.
     */
    @Test
    public void bigTermEarlyStartLateEndAndNonAdjacentTerms() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2009, 12, 31)),
            new Term(DateTimeUtil.createDate(2010, 6, 1), DateTimeUtil.createDate(2019, 12, 31)),
            new Term(DateTimeUtil.createDate(2020, 6, 1), DateTimeUtil.createDate(2049, 12, 31)),
            new Term(Constants.EPOCH, Constants.PERPETUITY)
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            Constants.TERM_EPOCH_TO_PERPETUITY
        );
        
        assertEquals(0, gapTerms.size());
    }
    
    /**
     * Non-adjacent terms that would normally leave a gap but with a big term that spans over
     * the gap left by the non-adjacent terms. This version has the big term's start date
     * equal to the earliest start of the other terms. The big term's end date is earlier 
     * than the latest end of the other terms.
     */
    @Test
    public void bigTermEqualStartEarlyEndAndNonAdjacentTerms() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(Constants.EPOCH, DateTimeUtil.createDate(2009, 12, 31)),
            new Term(DateTimeUtil.createDate(2010, 6, 1), DateTimeUtil.createDate(2019, 12, 31)),
            new Term(DateTimeUtil.createDate(2020, 6, 1), Constants.PERPETUITY),
            new Term(Constants.EPOCH, DateTimeUtil.createDate(2020, 12, 31))
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            Constants.TERM_EPOCH_TO_PERPETUITY
        );
        
        assertEquals(0, gapTerms.size());
    }
    
    /**
     * Non-adjacent terms that would normally leave a gap but with a big term that spans over
     * the gap left by the non-adjacent terms. This version has the big term's start and end date
     * equal to the earliest start and latest end of the other terms
     */
    @Test
    public void bigTermEqualStartEndAndNonAdjacentTerms() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(Constants.EPOCH, DateTimeUtil.createDate(2009, 12, 31)),
            new Term(DateTimeUtil.createDate(2010, 6, 1), DateTimeUtil.createDate(2019, 12, 31)),
            new Term(DateTimeUtil.createDate(2020, 6, 1), Constants.PERPETUITY),
            new Term(Constants.EPOCH, Constants.PERPETUITY)
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            Constants.TERM_EPOCH_TO_PERPETUITY
        );
        
        assertEquals(0, gapTerms.size());
    }
    
    /**
     * Non-adjacent terms that would normally leave a gap but with a big term that spans over
     * the gap left by the non-adjacent terms. This version has the big term's start date
     * equal to the earliest start of the other terms. The big term's end date is later 
     * than the latest end of the other terms.
     */
    @Test
    public void bigTermEqualStartLateEndAndNonAdjacentTerms() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(Constants.EPOCH, DateTimeUtil.createDate(2009, 12, 31)),
            new Term(DateTimeUtil.createDate(2010, 6, 1), DateTimeUtil.createDate(2019, 12, 31)),
            new Term(DateTimeUtil.createDate(2020, 6, 1), DateTimeUtil.createDate(2020, 12, 31)),
            new Term(Constants.EPOCH, Constants.PERPETUITY)
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            Constants.TERM_EPOCH_TO_PERPETUITY
        );
        
        assertEquals(0, gapTerms.size());
    }
    
    /**
     * Non-adjacent terms that would normally leave a gap but with a big term that spans over
     * the gap left by the non-adjacent terms. This version has the big term's start date
     * later than the earliest start of the other terms. The big term's end date is earlier 
     * than the latest end of the other terms.
     */
    @Test
    public void bigTermLateStartEarlyEndAndNonAdjacentTerms() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(Constants.EPOCH, DateTimeUtil.createDate(2009, 12, 31)),
            new Term(DateTimeUtil.createDate(2010, 6, 1), DateTimeUtil.createDate(2019, 12, 31)),
            new Term(DateTimeUtil.createDate(2020, 6, 1), Constants.PERPETUITY),
            new Term(DateTimeUtil.createDate(2009, 1, 1), DateTimeUtil.createDate(2020, 12, 31))
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            Constants.TERM_EPOCH_TO_PERPETUITY
        );
        
        assertEquals(0, gapTerms.size());
    }
    
    /**
     * Non-adjacent terms that would normally leave a gap but with a big term that spans over
     * the gap left by the non-adjacent terms. This version has the big term's start date later
     * than the earliest start date of the other terms. The big term's end date is equal to the 
     * latest end date of the other terms
     */
    @Test
    public void bigTermLateStartEqualEndAndNonAdjacentTerms() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(Constants.EPOCH, DateTimeUtil.createDate(2009, 12, 31)),
            new Term(DateTimeUtil.createDate(2010, 6, 1), DateTimeUtil.createDate(2019, 12, 31)),
            new Term(DateTimeUtil.createDate(2020, 6, 1), Constants.PERPETUITY),
            new Term(DateTimeUtil.createDate(2009, 1, 1), Constants.PERPETUITY)
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            Constants.TERM_EPOCH_TO_PERPETUITY
        );
        
        assertEquals(0, gapTerms.size());
    }

    /**
     * Non-adjacent terms that would normally leave a gap but with a big term that spans over
     * the gap left by the non-adjacent terms. This version has the big term's start and end dates
     * are later than the other terms' earliest start and latest end dates
     */
    @Test
    public void bigTermLateStartEndAndNonAdjacentTerms() {
        Set<Term> existingTerms = Sets.newHashSet(
            new Term(Constants.EPOCH, DateTimeUtil.createDate(2009, 12, 31)),
            new Term(DateTimeUtil.createDate(2010, 6, 1), DateTimeUtil.createDate(2019, 12, 31)),
            new Term(DateTimeUtil.createDate(2020, 6, 1), DateTimeUtil.createDate(2020, 12, 31)),
            new Term(DateTimeUtil.createDate(2009, 1, 1), Constants.PERPETUITY)
        );
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            existingTerms, 
            Constants.TERM_EPOCH_TO_PERPETUITY
        );
        
        assertEquals(0, gapTerms.size());
    }
}