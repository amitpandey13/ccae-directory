package com.pdgc.ccae.dao.intermediateobjects;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.postgresql.jdbc.PgArray;

import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.structures.carveout.CustomerCountLicense;
import com.pdgc.general.structures.carveout.attributes.FoxCarveOutType;
import com.pdgc.general.structures.carveout.grouping.CarveOutCombineRule;
import com.pdgc.general.structures.timeperiod.TimePeriodPart;
import com.pdgc.general.util.xml.JDomXmlDocumentFactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CarveOutEntry {

    private Long carveOutId;
    private String carveOutDefinition;
    private Integer carveOutTypeId;
    private Integer carveOutImpactTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long customerId;
    private Integer customerTypeId;
    private String carveOutComment;
    private Long rightsGroupId;
    private List<TimePeriodPart> carveoutTimePeriodList;

    private Long sourceId;
    private String sourceDetailId;
    private Long businessUnitId;
    private Long productId;
    private Set<Integer> mediaSet;
    private Set<Integer> territorySet;
    private Set<Integer> languageSet;
    private boolean hasTimePeriod;

    public String carveOutTimePeriodXml;
    public String carveOutDetails;

    private Integer carveOutCombineRule;
    private Integer carveOutOrder;

    private Integer carveOutGroupCombineRule;
    private Integer carveOutGroupOrder;
    private Integer carveOutGroupId;

    public Set<CustomerCountLicense> customerCountCarveOutLicenses;

    /**
     * Creates a CarveoutEntry object with the current row from the database.
     *
     * @param reader
     * @throws SQLException
     */
    public CarveOutEntry(DataRow reader) throws SQLException {
        /*
         * If any value is null, assign it to a non-null value because
         * CarveoutEntry doesn't take any null parameters. Those values won't be
         * used when creating the respective carve outs
         */
        this.carveOutId = reader.getLong("id");
        this.carveOutTypeId = reader.getInteger("CarveoutTypeId");
        this.startDate = reader.getDate("startDate");
        this.endDate = reader.getDate("endDate");
        this.carveOutComment = reader.getString("CarveOutComment", "");
        this.carveOutDetails = reader.getString("CarveOutDefinition");
        carveoutTimePeriodList = new ArrayList<>();
        this.rightsGroupId = reader.getLong("rightsGroupId");
        this.customerId = reader.getLong("customerId");

        this.sourceId = reader.getLong("sourceId");
        this.sourceDetailId = reader.getString("sourceDetailId");
        this.businessUnitId = reader.getLong("businessUnitId");
        this.productId = reader.getLong("productId");
        Integer[] testSet = (Integer[]) ((PgArray) reader.getColumn("mediaArray")).getArray();
        this.mediaSet = new HashSet<Integer>(Arrays.asList(testSet));
        testSet = (Integer[]) ((PgArray) reader.getColumn("territoryArray")).getArray();
        this.territorySet = new HashSet<Integer>(Arrays.asList(testSet));
        testSet = (Integer[]) ((PgArray) reader.getColumn("languageArray")).getArray();
        this.languageSet = new HashSet<Integer>(Arrays.asList(testSet));
        this.hasTimePeriod = reader.getBoolean("hasTimePeriod");

        this.carveOutImpactTypeId = reader.getInteger("CarveoutImpactTypeId");
        this.carveOutCombineRule = reader.getInteger("carveOutCombineRule");
        if (this.carveOutCombineRule == null) {
            log.warn("CarveoutId {} has no explicitly stated combineRule setting to default", this.carveOutId);
            if (FoxCarveOutType.CUSTOMERS.getId() == this.carveOutTypeId || FoxCarveOutType.MAX_CUSTOMERS.getId() == this.carveOutTypeId) {
                this.carveOutCombineRule = CarveOutCombineRule.OR.getId();
            } else {
                this.carveOutCombineRule = CarveOutCombineRule.AND.getId();
            }
        }
        this.carveOutOrder = reader.getInteger("carveOutOrder");

        this.carveOutGroupCombineRule = reader.getInteger("carveOutGroupCombineRule");
        this.carveOutGroupOrder = reader.getInteger("carveOutGroupOrder");
        this.carveOutGroupId = reader.getInteger("carveOutGroup");
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public CarveOutEntry(
        Long id,
        Integer carveOutTypeId,
        Integer carveOutImpactTypeId,
        Integer carveOutCombineRule,
        Integer carveOutOrder,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate maskedStartDate,  //NOPMD
        LocalDate maskedEndDate,    //NOPMD
        String carveOutComment,
        Integer carveOutGroupId,
        Integer carveOutGroupOrder,
        Integer carveOutGroupCombineRule,
        String carveOutDefinition,
        Long rightsGroupId,
        Long sourceId,
        String sourceDetailId,
        Long businessUnitId,
        Long productId,
        Object mediaArray,
        Object territoryArray,
        Object languageArray,
        Boolean timePeriod) throws SQLException {
        /*
         * If any value is null, assign it to a non-null value because
         * CarveoutEntry doesn't take any null parameters. Those values won't be
         * used when creating the respective carve outs
         */
        this.carveOutId = id;
        this.carveOutTypeId = carveOutTypeId;
        this.carveOutImpactTypeId = carveOutImpactTypeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.carveOutComment = carveOutComment;
//		this.carveOutTimePeriodXml = CarveOutTimePeriod;
        this.carveOutDetails = carveOutDefinition;
        carveoutTimePeriodList = new ArrayList<>();
        this.rightsGroupId = rightsGroupId;

        this.sourceId = sourceId;
        this.sourceDetailId = sourceDetailId;
        this.businessUnitId = businessUnitId;
        this.productId = productId;
        Integer[] testSet = (Integer[]) ((PgArray) mediaArray).getArray();
        this.mediaSet = new HashSet<Integer>(Arrays.asList(testSet));
        testSet = (Integer[]) ((PgArray) territoryArray).getArray();
        this.territorySet = new HashSet<Integer>(Arrays.asList(testSet));
        testSet = (Integer[]) ((PgArray) languageArray).getArray();
        this.languageSet = new HashSet<Integer>(Arrays.asList(testSet));
        this.hasTimePeriod = timePeriod;

        this.carveOutGroupId = carveOutGroupId;
        this.carveOutGroupCombineRule = carveOutGroupCombineRule;
        this.carveOutImpactTypeId = carveOutImpactTypeId;
        this.carveOutGroupOrder = carveOutGroupOrder;

        this.carveOutCombineRule = carveOutCombineRule;
        if (this.carveOutCombineRule == null) {
            log.warn("CarveoutId {} has no explicitly stated combineRule setting to default", this.carveOutId);
            if (FoxCarveOutType.CUSTOMERS.getId() == this.carveOutTypeId || FoxCarveOutType.MAX_CUSTOMERS.getId() == this.carveOutTypeId) {
                this.carveOutCombineRule = CarveOutCombineRule.OR.getId();
            } else {
                this.carveOutCombineRule = CarveOutCombineRule.AND.getId();
            }
        }
        this.carveOutImpactTypeId = carveOutImpactTypeId;
        this.carveOutOrder = carveOutOrder;
    }

    public List<TimePeriodPart> getTimePeriodPartsFromXml() {
        if (carveOutTimePeriodXml == null) {
            return null;
        }

        JDomXmlDocumentFactory factory = new JDomXmlDocumentFactory();
        List<TimePeriodPart> timePeriodParts = new ArrayList<TimePeriodPart>();
        factory.createDocumentFromXmlText(this.carveOutTimePeriodXml);
        //TODO: implement time period handling.
        return timePeriodParts;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setCarveOutTypeId(int id) {
        this.carveOutTypeId = id;
    }

    public void setCarveoutTimePeriodList(List<TimePeriodPart> parts) {
        this.carveoutTimePeriodList = parts;
    }

    public void setCarveOutId(Long carveOutId) {
        this.carveOutId = carveOutId;
    }

    public void setRightsGroupId(Long rightsGroupId) {
        this.rightsGroupId = rightsGroupId;
    }


    public Set<CustomerCountLicense> getCustomerCountCarveOutLicenses() {
        return customerCountCarveOutLicenses;
    }

    public void setCustomerCountCarveOutLicenses(Set<CustomerCountLicense> customerCountCarveOutLicenses) {
        this.customerCountCarveOutLicenses = customerCountCarveOutLicenses;
    }



}
