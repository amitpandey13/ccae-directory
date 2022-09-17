package com.pdgc.avails.structures.workbook.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Chen Su <chen.su@pdgc.com>
 * @version September 21, 2018
 * @since September 21, 2018
 * <p>
 * Workbook object used to translate Java to JSON
 */
public class Workbook {

    /**
     * List Objects of all report types
     * <p>
     * Each entry in a list represents a row in the
     */
    List<ReportModel> soldUnsoldList = new ArrayList<>();
    List<ReportModel> netList = new ArrayList<>();

    public Workbook() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public Workbook(Map<Class<?>, List<ReportModel>> map) {
        netList = (map.containsKey(NetReport.class)) ? map.get(NetReport.class) : null;
        soldUnsoldList = (map.containsKey(SoldUnsoldReport.class)) ? map.get(SoldUnsoldReport.class) : null;
    }

    public List<ReportModel> getSoldUnsoldList() {
        return soldUnsoldList;
    }

    public void setSoldUnsoldList(List<ReportModel> soldUnsoldList) {
        this.soldUnsoldList = soldUnsoldList;
    }

    public List<ReportModel> getNetList() {
        return netList;
    }

    public void set(List<ReportModel> netList) {
        this.netList = netList;
    }

}
