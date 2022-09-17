package com.pdgc.ccae.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdgc.ccae.dao.intermediateobjects.CarveOutEntry;
import com.pdgc.general.cache.dictionary.impl.CustomerDictionary;
import com.pdgc.general.cache.dictionary.impl.CustomerGenreDictionary;
import com.pdgc.general.cache.dictionary.impl.CustomerTypeDictionary;
import com.pdgc.general.cache.dictionary.impl.KeyWithBusinessUnit;
import com.pdgc.general.structures.Term;

import com.pdgc.general.structures.carveout.CarveOut;
import com.pdgc.general.structures.carveout.attributes.CarveOutImpactType;
import com.pdgc.general.structures.carveout.attributes.CustomerLimitType;
import com.pdgc.general.structures.carveout.attributes.FoxCarveOutType;
import com.pdgc.general.structures.carveout.grouping.CarveOutCombineRule;
import com.pdgc.general.structures.carveout.grouping.CarveOutGroup;
import com.pdgc.general.structures.carveout.impl.CustomerCarveOut;
import com.pdgc.general.structures.carveout.impl.CustomerCountCarveOut;
import com.pdgc.general.structures.carveout.impl.CustomerGenreCarveOut;
import com.pdgc.general.structures.carveout.impl.CustomerTypeCarveOut;
import com.pdgc.general.structures.carveout.impl.InformationalCarveOut;
import com.pdgc.general.structures.carveout.impl.MTLDimension;
import com.pdgc.general.structures.carveout.json.CarveOutDetail;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.customer.Customer.CustomerBuilder;
import com.pdgc.general.structures.customer.CustomerGenre;
import com.pdgc.general.structures.customer.CustomerType;
import com.pdgc.general.util.CollectionsUtil;

/**
 * CarveOutBuilder will build carve outs based on the carveoutentry
 *
 * @author Thomas Loh
 */
public final class CarveOutBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(CarveOutBuilder.class);
    private static ObjectMapper mapper = new ObjectMapper();

    private CarveOutBuilder() {
    }

    /**
     * This will convert the carveOutEntries into carveOuts then sort them into their
     * proper carveOutGroups. <p>
     * We know which carveOuts go to which carveOutGroup because the carveOut contains the carveOutGroupId
     *
     * @param carveOutEntries
     * @return
     */
    public static Collection<CarveOutGroup> createCarveOutGroups(Collection<CarveOutEntry> carveOutEntries) {

        // Iterate through carveOuts and sort them into groups
        Map<Integer, Collection<CarveOut>> carveOutGroupsMap = new HashMap<>();
        for (CarveOutEntry carveOutEntry : carveOutEntries) {
            CarveOut carveOut = createCarveOut(carveOutEntry);
            Collection<CarveOut> carveOutGroup = carveOutGroupsMap.get(carveOutEntry.getCarveOutGroupId());
            if (CollectionsUtil.isNullOrEmpty(carveOutGroup)) {
                carveOutGroup = new ArrayList<>();
                carveOutGroupsMap.put(carveOut.getCarveOutGroupId(), carveOutGroup);
            }
            carveOutGroup.add(carveOut);
        }

        // Convert the collection<CarveOut> to carveOutGroups
        Collection<CarveOutGroup> carveOutGroups = new ArrayList<>();
        for (Collection<CarveOut> carveOutGroup : carveOutGroupsMap.values()) {
            CarveOut key = carveOutGroup.iterator().next();
            carveOutGroups.add(new CarveOutGroup(
                key.getCarveOutGroupCombineRule(),
                key.getCarveOutGroupId(),
                key.getCarveOutGroupOrder(),
                carveOutGroup
            ));
        }
        return carveOutGroups;
    }

    public static Collection<CarveOutGroup> groupCarveOuts(Iterable<CarveOut> carveOuts) {
        // Iterate through carveOuts and sort them into groups
        Map<Integer, Collection<CarveOut>> carveOutGroupsMap = new HashMap<>();
        for (CarveOut carveOut : carveOuts) {
            Collection<CarveOut> carveOutGroup = carveOutGroupsMap.get(carveOut.getCarveOutGroupId());
            if (CollectionsUtil.isNullOrEmpty(carveOutGroup)) {
                carveOutGroup = new ArrayList<>();
                carveOutGroupsMap.put(carveOut.getCarveOutGroupId(), carveOutGroup);
            }
            carveOutGroup.add(carveOut);
        }

        // Convert the collection<CarveOut> to carveOutGroups
        Collection<CarveOutGroup> carveOutGroups = new ArrayList<>();
        for (Collection<CarveOut> carveOutGroup : carveOutGroupsMap.values()) {
            CarveOut key = carveOutGroup.iterator().next();
            carveOutGroups.add(new CarveOutGroup(
                key.getCarveOutGroupCombineRule(),
                key.getCarveOutGroupId(),
                key.getCarveOutGroupOrder(),
                carveOutGroup
            ));
        }
        return carveOutGroups;
    }

    /**
     * creates a carve out when given a carve out entry
     *
     * @param carveOutEntry
     * @return
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public static CarveOut createCarveOut(
        CarveOutEntry carveOutEntry
    ) {
        CarveOut carveOut;
        CarveOutDetail detail = null;

        try {
            detail = mapper.readValue(carveOutEntry.getCarveOutDetails(), CarveOutDetail.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            //If we fail to read the carveOut details, then treat the carveOut as an informational only carveOut but log an error.
            carveOutEntry.setCarveOutTypeId(FoxCarveOutType.OTHER.getId());
        }

        FoxCarveOutType carveOutType = FoxCarveOutType.byId(carveOutEntry.getCarveOutTypeId());
        Term carveOutTerm = new Term(carveOutEntry.getStartDate(), carveOutEntry.getEndDate());

        switch (carveOutType) {
            case CUSTOMERS:
                if (detail.getCustomerIds() == null) {
                    return null;
                }
                List<Customer> carveOutCustomers = new ArrayList<Customer>();
                for (int customerId : detail.getCustomerIds()) {
                    carveOutCustomers.add(
                        // If the customer doesn't exist in CustomerDictionary, supplant with a "dummy" customer so that further processing doesn't break
                        CustomerDictionary.getInstance().get(new KeyWithBusinessUnit<Long>(Long.valueOf(customerId), carveOutEntry.getBusinessUnitId())) != null
                            ? CustomerDictionary.getInstance().get(new KeyWithBusinessUnit<Long>(Long.valueOf(customerId), carveOutEntry.getBusinessUnitId()))
                            : new CustomerBuilder(Long.valueOf(customerId), carveOutEntry.getBusinessUnitId()).customerName("Customer " + customerId).build()
                    );
                }

                carveOut = new CustomerCarveOut(
                    carveOutEntry.getCarveOutId(),
                    carveOutType,
                    carveOutTerm,
                    carveOutEntry.getTimePeriodPartsFromXml(),
                    carveOutEntry.getCarveOutComment(),
                    carveOutCustomers,
                    CarveOutImpactType.byValue(carveOutEntry.getCarveOutImpactTypeId(), CarveOutImpactType.EXCEPT_AGAINST),
                    CarveOutCombineRule.byValue(carveOutEntry.getCarveOutCombineRule(), CarveOutCombineRule.AND),
                    carveOutEntry.getCarveOutOrder(),
                    carveOutEntry.getCarveOutGroupId(),
                    CarveOutCombineRule.byValue(carveOutEntry.getCarveOutGroupCombineRule(), CarveOutCombineRule.AND),
                    carveOutEntry.getCarveOutGroupOrder(), null, null, null
                );

                break;
            case CUSTOMER_TYPES:
                List<CustomerType> customerTypes = new ArrayList<CustomerType>();
                for (int customerTypeId : detail.getCustomerTypeIds()) {
                    customerTypes.add(CustomerTypeDictionary.getInstance().get(Long.valueOf(customerTypeId)));
                }

                carveOut = new CustomerTypeCarveOut(
                    carveOutEntry.getCarveOutId(),
                    carveOutType,
                    carveOutTerm,
                    carveOutEntry.getTimePeriodPartsFromXml(),
                    carveOutEntry.getCarveOutComment(),
                    customerTypes,
                    CarveOutImpactType.byValue(carveOutEntry.getCarveOutImpactTypeId(), CarveOutImpactType.EXCEPT_AGAINST),
                    CarveOutCombineRule.byValue(carveOutEntry.getCarveOutCombineRule(), CarveOutCombineRule.AND),
                    carveOutEntry.getCarveOutOrder(),
                    carveOutEntry.getCarveOutGroupId(),
                    CarveOutCombineRule.byValue(carveOutEntry.getCarveOutGroupCombineRule(), CarveOutCombineRule.AND),
                    carveOutEntry.getCarveOutGroupOrder()
                );
                break;
            case CUSTOMER_GENRES:
                List<CustomerGenre> customerGenres = new ArrayList<CustomerGenre>();
                for (int customerGenreId : detail.getCustomerGenreIds()) {
                    customerGenres.add(CustomerGenreDictionary.getInstance().get(new Long(customerGenreId)));
                }

                carveOut = new CustomerGenreCarveOut(
                    carveOutEntry.getCarveOutId(),
                    carveOutType,
                    carveOutTerm,
                    carveOutEntry.getTimePeriodPartsFromXml(),
                    carveOutEntry.getCarveOutComment(),
                    customerGenres,
                    CarveOutImpactType.byValue(carveOutEntry.getCarveOutImpactTypeId(), CarveOutImpactType.EXCEPT_AGAINST),
                    CarveOutCombineRule.byValue(carveOutEntry.getCarveOutCombineRule(), CarveOutCombineRule.AND),
                    carveOutEntry.getCarveOutOrder(),
                    carveOutEntry.getCarveOutGroupId(),
                    CarveOutCombineRule.byValue(carveOutEntry.getCarveOutGroupCombineRule(), CarveOutCombineRule.AND),
                    carveOutEntry.getCarveOutGroupOrder()
                );
                break;
            case MAX_CUSTOMERS:
                List<Customer> internalCustomers = new ArrayList<Customer>();
                if (detail.getInternalCustomerIds() != null) {
                	for (int internalCustomerId : detail.getInternalCustomerIds()) {
                		internalCustomers.add(CustomerDictionary.getInstance().get(new KeyWithBusinessUnit<Long>(Long.valueOf(internalCustomerId), carveOutEntry.getBusinessUnitId())));
                	}
                }

                Collection<MTLDimension> spanningDimensions = new ArrayList<>();
                if (detail.getSpanningDimensions() != null) {
                    for (char dimension : detail.getSpanningDimensions()) {
                        switch (dimension) {
                            case 'm':
                            case 'M':
                                spanningDimensions.add(MTLDimension.MEDIA);
                                break;
                            case 't':
                            case 'T':
                                spanningDimensions.add(MTLDimension.TERRITORY);
                                break;
                            case 'l':
                            case 'L':
                                spanningDimensions.add(MTLDimension.LANGUAGE);
                                break;
                            default:
                                break;
                        }
                    }
                }

                carveOut = new CustomerCountCarveOut(
                    carveOutEntry.getCarveOutId(),
                    carveOutType,
                    carveOutTerm,
                    carveOutEntry.getTimePeriodPartsFromXml(),
                    carveOutEntry.getCarveOutComment(),
                    detail.getCustomerLimit(),
                    detail.getInternalCustomerCount(),
                    internalCustomers, // TODO: hash out how to do internal Customer Ids with danny in the future
                    CustomerLimitType.byValue(detail.getCustomerLimitTypeId(), CustomerLimitType.INTERNAL_BRANDED_NOT_APPLICABLE),
                    (detail.getSimultaneousCustomersNotAllowed() == 0), //The carveout engine wants "allowed" but the data engine and client is passing in "not allowed" so flip it here.
                    spanningDimensions,
                    carveOutEntry.getCustomerCountCarveOutLicenses(),
                    CarveOutImpactType.EXCEPT_AGAINST, // default values
                    CarveOutCombineRule.AND,           // default values
                    carveOutEntry.getCarveOutOrder(),
                    carveOutEntry.getCarveOutGroupId(),
                    CarveOutCombineRule.byValue(carveOutEntry.getCarveOutGroupCombineRule(), CarveOutCombineRule.AND),
                    carveOutEntry.getCarveOutGroupOrder(), null, null, null
                 
             
                );

                break;
            default:
                carveOut = new InformationalCarveOut(
                    carveOutEntry.getCarveOutId(),
                    carveOutType,
                    carveOutTerm,
                    carveOutEntry.getTimePeriodPartsFromXml(),
                    carveOutEntry.getCarveOutComment(),
                    carveOutEntry.getCarveOutTypeId(),
                    CarveOutImpactType.byValue(carveOutEntry.getCarveOutImpactTypeId(), CarveOutImpactType.EXCEPT_AGAINST),
                    CarveOutCombineRule.byValue(carveOutEntry.getCarveOutCombineRule(), CarveOutCombineRule.AND),
                    carveOutEntry.getCarveOutOrder(),
                    carveOutEntry.getCarveOutGroupId(),
                    CarveOutCombineRule.byValue(carveOutEntry.getCarveOutGroupCombineRule(), CarveOutCombineRule.AND),
                    carveOutEntry.getCarveOutGroupOrder()
                );
                break;
        }

        return carveOut;
    }
}
