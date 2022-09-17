package com.pdgc.conflictcheck.structures.builders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.pdgc.conflictcheck.structures.FoxConflictSourceNonGroupFields;
import com.pdgc.conflictcheck.structures.component.impl.ConflictSourceNonGroupFields;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.CarveOut;
import com.pdgc.general.structures.carveout.grouping.FoxCarveOutContainer;
import com.pdgc.general.structures.rightsource.impl.FoxDealSource;
import com.pdgc.general.structures.rightstrand.FoxRightStrand;
import com.pdgc.general.structures.rightstrand.impl.FoxDealRightStrand;

/**
 * FoxConflictSourceNonGroupFieldsBuilder
 */
public final class FoxConflictSourceNonGroupFieldsBuilder {

    private FoxConflictSourceNonGroupFieldsBuilder() {

    }

    public static FoxConflictSourceNonGroupFields getConflictSourceNonGroupFields(FoxRightStrand rightStrand) {
        if (rightStrand == null) {
            //Can't use a static null version to pass in b/c there are setters for groupingProduct and releaseYear...
            return new FoxConflictSourceNonGroupFields(
                null,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new HashSet<>(),
                null,
                null
            );
        }

        Collection<Long> dealProducts;
        Set<Integer> episodeLimits = new HashSet<Integer>();
        Set<String> carveOutInfo = new HashSet<>();

        if (rightStrand.getRightSource() instanceof FoxDealSource) {
            dealProducts = Collections.singleton(((FoxDealSource) rightStrand.getRightSource()).getDealProductId());
        } else {
            dealProducts = new ArrayList<>();
        }

        if (rightStrand.getRightType().allowsEpisodeLimit()) {
            episodeLimits.add(rightStrand.getRightType().getEpisodeLimit());
        }

        if (rightStrand instanceof FoxDealRightStrand) {
            FoxCarveOutContainer carveOutContainer = ((FoxDealRightStrand) rightStrand).getCarveOuts();
            for (CarveOut carveOut : carveOutContainer.getAllCarveOuts()) {
                carveOutInfo.add(carveOut.toString());
            }
        }

        return new FoxConflictSourceNonGroupFields(
            rightStrand.getOrigTerm(),
            null,
            dealProducts,
            Arrays.asList(rightStrand.getComment()),
            episodeLimits,
            rightStrand.getDistributionRightsOwner(),
            carveOutInfo
        );
    }

    public static FoxConflictSourceNonGroupFields consolidate(Iterable<? extends ConflictSourceNonGroupFields> sourceInfos) {
        Term greatestTerm = null;
        Integer minReleaseYear = null;
        Collection<Long> dealProducts = new HashSet<>();
        Collection<String> comments = new HashSet<>();
        Set<Integer> episodeLimits = new HashSet<>();
        Long distributionRightsOwner = null;
        Set<String> carveOutInfo = new HashSet<>();

        for (ConflictSourceNonGroupFields sourceInfo : sourceInfos) {
            FoxConflictSourceNonGroupFields castedSourceInfo = (FoxConflictSourceNonGroupFields) sourceInfo;

            greatestTerm = findGreatestTerm(greatestTerm, castedSourceInfo.getOrigTerm());
            dealProducts.addAll(castedSourceInfo.getDealProductIds());

            //These are theoretically already externally filtered out to be the same in any group, so just arbitrarily assign
            minReleaseYear = findEarliestReleaseYear(minReleaseYear, castedSourceInfo.getReleaseYear());

            comments.addAll(castedSourceInfo.getComments());
            episodeLimits.addAll(castedSourceInfo.getEpisodeLimits());
            carveOutInfo.addAll(castedSourceInfo.getCarveOutInfo());

        }
        distributionRightsOwner = ((FoxConflictSourceNonGroupFields) sourceInfos.iterator().next()).getDistributionRightsOwner();

        return new FoxConflictSourceNonGroupFields(
            greatestTerm,
            minReleaseYear,
            dealProducts,
            comments,
            episodeLimits,
            distributionRightsOwner,
            carveOutInfo
        );
    }

    /**
     * Simple functional method that will take 2 terms and find the greatest start and end dates
     * between the two. Guarantee that primaryTerm exists
     *
     * @return
     */
    private static Term findGreatestTerm(Term primaryTerm, Term secondaryTerm) {
        //If either term doesn't exist, return the other term
        if (primaryTerm == null) {
            return secondaryTerm;
        }
        if (secondaryTerm == null) {
            return primaryTerm;
        }
        LocalDate greatestStartDate = primaryTerm.getStartDate();
        LocalDate greatestEndDate = primaryTerm.getEndDate();

        if (greatestStartDate.isAfter(secondaryTerm.getStartDate())) {
            greatestStartDate = secondaryTerm.getStartDate();
        }
        if (greatestEndDate.isBefore(secondaryTerm.getEndDate())) {
            greatestEndDate = secondaryTerm.getEndDate();
        }

        return new Term(greatestStartDate, greatestEndDate);
    }

    private static Integer findEarliestReleaseYear(Integer primaryReleaseYear, Integer secondaryReleaseYear) {
        //If either term doesn't exist, return the other term
        if (primaryReleaseYear == null) {
            return secondaryReleaseYear;
        }
        if (secondaryReleaseYear == null) {
            return primaryReleaseYear;
        }

        if (primaryReleaseYear < secondaryReleaseYear) {
            return primaryReleaseYear;
        }

        return secondaryReleaseYear;
    }
}
