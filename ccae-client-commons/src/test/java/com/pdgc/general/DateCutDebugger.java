package com.pdgc.general;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import com.pdgc.general.structures.Term;
import com.pdgc.general.util.DateTimeUtil;
import com.pdgc.general.util.timecutting.ReplacementTimeEntry;
import com.pdgc.general.util.timecutting.TimeCutResult;

public class DateCutDebugger {

    public static void main(String[] args) throws Exception {
        String filePath = "src/test/resources/simpsons_world_terms.tsv";

        List<Term> terms = TermLoader.readPMTLs(filePath);

        System.out.println(LocalDateTime.now() + ": Starting term cutting");

        Map<Term, Collection<Term>> cutTerms = new HashMap<>();
        for (Term cuttingTerm : terms) {
            TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), cuttingTerm);

            Collection<Pair<Term, Collection<Term>>> replacementEntries = new ArrayList<>();
            for (ReplacementTimeEntry<Term> replacementEntry : dateCutResult.getReplacementEntries()) {
                Collection<Term> revisedSourceTerms = cutTerms.get(replacementEntry.getNewEntry());
                if (revisedSourceTerms == null) {
                    if (replacementEntry.getOldEntry() == null) {
                        revisedSourceTerms = new HashSet<>();
                    } else {
                        revisedSourceTerms = new HashSet<>(cutTerms.get(replacementEntry.getOldEntry()));
                    }
                }

                if (replacementEntry.getUsesNewInfo()) {
                    revisedSourceTerms.add(cuttingTerm);
                }

                replacementEntries.add(new Pair<>(replacementEntry.getNewEntry(), revisedSourceTerms));
            }

            for (Term removedTerm : dateCutResult.getRemovedEntries()) {
                cutTerms.remove(removedTerm);
            }

            for (Pair<Term, Collection<Term>> replacementEntry : replacementEntries) {
                cutTerms.put(replacementEntry.getValue0(), replacementEntry.getValue1());
            }
        }

        System.out.println(LocalDateTime.now() + ": Finished term cutting");
    }
}
