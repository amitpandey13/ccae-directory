package com.pdgc.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javatuples.Pair;

import com.pdgc.general.structures.pmtlgroup.idSets.PMTLIdSet;
import com.pdgc.general.structures.tsvLoader.RowMap;
import com.pdgc.general.structures.tsvLoader.TSVReader;

public class FoxPMTLTSVLoader {

    public static List<Pair<PMTLIdSet, Long>> readPMTLs(String filePath) throws Exception {
        RowMap rowMap = TSVReader.readTSVFile(filePath);

        List<Pair<PMTLIdSet, Long>> pmtlsWithHierarchyId = new ArrayList<>();

        String productHierarchyIdColumn = rowMap.getExistingColumnName(Arrays.asList("productHierarchyId", "hierarchyId"));

        List<PMTLIdSet> pmtls = PMTLTSVLoader.processRowMap(rowMap);
        for (int i = 0; i < pmtls.size(); i++) {
            PMTLIdSet pmtl = pmtls.get(i);

            Long hierarchyId = null;
            if (productHierarchyIdColumn != null) {
                hierarchyId = rowMap.getValue(productHierarchyIdColumn, i, Long::valueOf);
            }

            pmtlsWithHierarchyId.add(new Pair<>(pmtl, hierarchyId));
        }

        return pmtlsWithHierarchyId;
    }
}
