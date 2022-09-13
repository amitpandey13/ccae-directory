package com.pdgc.general.util;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.javatuples.Pair;

import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.equivalenceCollections.EquivalenceMap;
import com.pdgc.general.util.equivalenceCollections.MapEquivalence;
import com.pdgc.general.util.timecutting.ReplacementTimeEntry;
import com.pdgc.general.util.timecutting.TimeCutResult;

public abstract class DateTimeUtil {
	
	private static SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
	private static DateTimeFormatter DEFAULT_LOCAL_DATE_FORMAT = new DateTimeFormatterBuilder().appendPattern("MM/dd/yyyy").toFormatter();
	private static SimpleDateFormat DEFAULT_TIME_FORMAT = new SimpleDateFormat("h:mma");
	private static DateTimeFormatter MESSAGE_LOCAL_DATE_TIME_FORMAT = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
			.toFormatter();
	private static DateTimeFormatter MESSAGE_LOCAL_DATE_FORMAT = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd")
			.toFormatter();
	private static DateTimeFormatter EXCEL_LOCAL_DATE_TIME_FORMAT = new DateTimeFormatterBuilder().appendPattern("MM/dd/yyyy HH:mm:ss")
			.toFormatter();
	
	public static String abbreviateNormalizedDay(int normalizedDay) {
		switch (normalizedDay) {
		case 0:
			return "Su";
		case 1:
			return "M";
		case 2:
			return "T";
		case 3:
			return "W";
		case 4:
			return "Th";
		case 5:
			return "F";
		case 6:
			return "Sa";
		}
	
		return "";
	}
	/**
	 * Provides a short-hand abbreviation for each day of the week
	 * 
	 * @param dayOfWeek
	 *            The day of the week whose abbreviation is being requested
	 * @return
	 *         <ul>
	 *         <li>Monday = "M"</li>
	 *         <li>Tuesday = "T"</li>
	 *         <li>Wednesday = "W"</li>
	 *         <li>Thursday = "Th"</li>
	 *         <li>Friday = "F"</li>
	 *         <li>Saturday = "Sa"</li>
	 *         <li>Sunday = "Su"</li>
	 *         </ul>
	 */
	public static String abbreviateDayOfWeek(int dayOfWeek) {
		return abbreviateNormalizedDay(DateTimeUtil.normalizeDay(dayOfWeek));
	}
	/**
	 * Provides a short-hand abbreviation for each day of the week
	 * 
	 * @param dayOfWeek
	 *            The day of the week whose abbreviation is being requested
	 * @return
	 *         <ul>
	 *         <li>Monday = "M"</li>
	 *         <li>Tuesday = "T"</li>
	 *         <li>Wednesday = "W"</li>
	 *         <li>Thursday = "Th"</li>
	 *         <li>Friday = "F"</li>
	 *         <li>Saturday = "Sa"</li>
	 *         <li>Sunday = "Su"</li>
	 *         </ul>
	 */
	public static String abbreviateDayOfWeek(DayOfWeek dayOfWeek) {
		return abbreviateNormalizedDay(DateTimeUtil.normalizeDay(dayOfWeek));
	}
	public static int normalizeDay(DayOfWeek dayOfWeek) {
		// the DayOfWeek enumeration starts from Monday, the ordinal for which is 0
		return dayOfWeek == DayOfWeek.SUNDAY ? 0 : dayOfWeek.ordinal() + 1;
	}
	public static int normalizeDay(int dayOfWeekFromCalendar) {
		// for Calendar, day of weeks start with SUNDAY, the value being 1
		return dayOfWeekFromCalendar - 1;
	}
	public static int getAbsoluteMinutes(Duration duration) {
		return (int) (duration.toMinutes() - duration.toHours() * 60);
	}
	public static int getAbsoluteSeconds(Duration duration) {
		return (int) (duration.getSeconds() - (duration.toMinutes() * 60));
	}
	public static String formatDate(Date date) {
		return DEFAULT_DATE_FORMAT.format(date);
	}
	public static String formatDate(LocalDate date) {
		return DEFAULT_LOCAL_DATE_FORMAT.format(date);
	}
	public static LocalDate parseMessageDate(String date) {
		return LocalDate.from(MESSAGE_LOCAL_DATE_FORMAT.parse(date));
	}
	public static OffsetDateTime parseMessageDateTime(String date) {
		return OffsetDateTime.from(MESSAGE_LOCAL_DATE_TIME_FORMAT.parse(date));
	}
	public static String formatTime(Date date) {
		String formattedDate = DEFAULT_TIME_FORMAT.format(date);
		return formattedDate.substring(0, formattedDate.length() - 1);
	}
	public static String formatTime(Duration duration) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, (int) duration.toHours());
		cal.set(Calendar.MINUTE, getAbsoluteMinutes(duration));
		Date date = cal.getTime();
		String formattedDate = DEFAULT_TIME_FORMAT.format(date);
		return formattedDate.substring(0, formattedDate.length() - 1);
	}
	public static String formatMessageDateTime(LocalDateTime dateTime) {
		return dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
	}
	public static String formatExcelDateTime(LocalDateTime dateTime) {
		return dateTime.format(EXCEL_LOCAL_DATE_TIME_FORMAT);
	}
	
	public static LocalDateTime getUTCNow() {
		return LocalDateTime.now(ZoneId.of("UTC"));
	}

	public static LocalDate getLocalDateFromUTC(OffsetDateTime offsetDateTime) {
		return offsetDateTime.toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
	}
	
	/**
	 * Returns the OADate string but accounts for Excel bug that accounts for leap year
	 * in 1900 when there isn't any
	 * 
	 * <p> 
	 * Excel doesn't
	 * <p>
	 * <br>
	 * 
	 * @see http://science.howstuffworks.com/science-vs-myth/everyday-myths/question50.htm
	 * 
	 * @param dateTime
	 *            the {@link LocalDateTime} to convert
	 * @return
	 */
	public static String toOADateWithExcelBug(LocalDateTime dateTime) {
	    if (dateTime == null) {
            return null;
        }
	    
	    long diff = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
				- LocalDateTime.of(DateTimeUtil.createDate(1899, 12, 31), LocalTime.of(0, 0)).toInstant(ZoneOffset.UTC).toEpochMilli();
		double javaDate = diff / (24 * 60 * 60 * 1000.0);
		int counter = 0;
		//This will account for February 29, 1900. Since it technically doesn't exist in real life
		//but it exists in Excel...
		if(javaDate > 59) {
			counter++;
		}
		return String.format("%.10f", (diff / (24 * 60 * 60 * 1000.0)) + counter);
	}
	
	/**
	 * Returns the OADate string as created by C# method DateTime.ToOADate() which is required by excel for date values.
	 * 
	 * @param dateTime
	 *            the {@link LocalDateTime} to convert
	 * @return
	 */
	public static String toOADate(LocalDateTime dateTime) {
		return toOADateWithExcelBug(dateTime);
	}
	
	/**
	 * Returns the OADate string as created by C# method DateTime.ToOADate() which is required by excel for date values.
	 * 
	 * @param date
	 *            the {@link LocalDate} to convert
	 * @return
	 */
	public static String toOADate(LocalDate date) {
		if (date == null) {
			return null;
		}
		return toOADate(LocalDateTime.of(date, LocalTime.of(0, 0)));
	}
	
	/**
	 * 
	 * @param instant
	 * @return
	 * Defaulted to UTC - not sure this is correct need to revisit
	 * TODO: make sure UTC is ok here.
	 */
	public static String formatInstant(Instant instant) {
		return formatMessageDateTime(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
	}
	
	public static int getCalendarField(Date date, int field) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(field);
	}
	
	/**
	 * Creates a {@link Date} object based on the year, month and day passed
	 * 
	 * @param year
	 *            the year
	 * @param month
	 *            1-based index of month e.g 1 for January, 2 for February etc.
	 * @param day
	 *            the day of month
	 * @return the constructed {@link Date} object
	 */
	public static LocalDate createDate(int year, int month, int day) {
		return LocalDate.of(year, month, day);
	}
	
	/**
	 * Convert from {@link java.util.Date} to {@link java.sql.Date}
	 */
	public static LocalDate toSqlDate(Date date) {
		return LocalDate.from(date.toInstant());
	}
	
	/**
	 * Returns the least of the non-null dates.
	 * Returns null if all dates are null.
	 * @param dates
	 * @return
	 */
	public static LocalDate getMinDate(Iterable<LocalDate> dates) {
	    LocalDate minDate = null;
        for (LocalDate date : dates) {
	        if (date != null) {
	            if (minDate == null || date.isBefore(minDate)) {
	                minDate = date;
	            }
	        }
	    }
        return minDate;
	}
	
	/**
	 * @see DateTimeUtil#getMinDate(Iterable)
	 * @param dates
	 * @return
	 */
	public static LocalDate getMinDate(LocalDate... dates) {
	    return getMinDate(Arrays.asList(dates));
	}
	
	/**
     * Returns the greatest of non-null. 
     * Returns null if all dates are null.
     * @param dates
     * @return
     */
    public static LocalDate getMaxDate(Iterable<LocalDate> dates) {
        LocalDate maxDate = null;
        for (LocalDate date : dates) {
            if (date != null) {
                if (maxDate == null || date.isAfter(maxDate)) {
                    maxDate = date;
                }
            }
        }
        return maxDate;
    }
	
    /**
     * @see DateTimeUtil#getMaxDate(Iterable)
     * @param dates
     * @return
     */
    public static LocalDate getMaxDate(LocalDate... dates) {
        return getMaxDate(Arrays.asList(dates));
    }
    
    /**
	 * Convert from {@link java.sql.Date} to {@link java.util.Date}
	 */
	public static Date toUtilDate(java.sql.Date date) {
		return new Date(date.getTime());
	}
	
	/**
	 * Adds the passed number to days to the {@link Date} specified and returns the new {@link Date}
	 * 
	 * @param date
	 *            the date to add days to
	 * @param days
	 *            the number of days to add, can be negative which will subtract the days
	 * @return the {@code Date} object with number of days added
	 */
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, days);
		return cal.getTime();
	}
	
	/**
	 * Takes in a list of terms and a cutting term. The list of old terms CANNOT be overlapping, not even by one day (ie. a term with end
	 * date Jan 1, 2016 would be considered overlapping with a term that begins on Jan 1, 2016) Returns a list of terms to be removed b/c
	 * they overlapped with the cutting term, as well as a set of replacement terms.Replacement terms are actually returned in a tuple. 1st
	 * = new start/ end, 2nd = old term being cut/replaced, 3rd = boolean indicating whether or not the cutting term overlaps with this new,
	 * cut term
	 * 
	 * <p>
	 * <b>Examples.</b>
	 * 
	 * If there were 3 terms
	 * <ol>
	 * <li>Jan 1, 2015 - Dec 31, 2015</li>
	 * <li>Jan 1, 2016 - Dec 31, 2016</li>
	 * <li>Jan 1, 2017 - Dec 31, 2017</li>
	 * </ol>
	 * <br>
	 * and the cutting term was from July 1, 2015 to Jun 30, 2016...
	 * <p>
	 * <b>Result:</b> Removed terms:
	 * <ul>
	 * <li>Jan 1, 2015 - Dec 31, 2015;</li>
	 * <li>Jan 1, 2016 - Dec 31, 2016</li>
	 * </ul>
	 * <p>
	 * Replacement terms:
	 * <ul>
	 * <li>Jan 1, 2015 - Jun 30, 2015 (old term = Jan 1, 2015 - Dec 31, 2015, includes cutting term = false)</li>
	 * <li>July 1, 2015 - Dec 31, 2015 (old term = Jan 1, 2015 - Dec 31, 2015, includes cutting term = true)</li>
	 * <li>Jan 1, 2016 - Jun 30, 2016 (old term = Jan 1, 2016 - Dec 31, 2016, includes cutting term = true)</li>
	 * <li>July 1, 2016 - Dec 31, 2016 (old term = Jan 1, 2016 - Dec 31, 2016, includes cutting term = false)</li>
	 * </ul>
	 * 
	 * @param oldTerms
	 *            The collection of existing terms
	 * @param removedTerms
	 *            The terms to be removed
	 * @param replacementTerms
	 *            The new terms that are meant to replace the old terms.
	 *            <ul>
	 *            <li>Left = The new term's start/end date</li>
	 *            <li>Middle = The old term's start/end</li>
	 *            <li>Right = whether or not this date range overlaps with the {@code cuttingTerm}</li>
	 *            </ul>
	 * @param cuttingTerm
	 *            The term that is potentially introducing new date cuts
	 */
	public static TimeCutResult<Term> cutDates(Collection<Term> oldTerms, Term cuttingTerm) {
		List<Term> removedTerms = new ArrayList<Term>();
		List<ReplacementTimeEntry<Term>> replacementTerms = new ArrayList<>();
		
		// No need to check the other terms if the cutting term perfectly matches one of them
		if (oldTerms.contains(cuttingTerm)) {
			removedTerms.add(cuttingTerm);
			replacementTerms.add(new ReplacementTimeEntry<Term>(
				new Term(cuttingTerm.getStartDate(), cuttingTerm.getEndDate()),
				cuttingTerm, 
				true
			));
			return new TimeCutResult<Term>(removedTerms, replacementTerms);
		}
		
		for (Term oldTerm : oldTerms) {
			//cutting term is completely covered by an old term
			if (cuttingTerm.isCoveredBy(oldTerm)) {
				removedTerms.add(oldTerm);
				
				if (cuttingTerm.getStartDate().isAfter(oldTerm.getStartDate())) {
					replacementTerms.add(new ReplacementTimeEntry<Term>(
						new Term(oldTerm.getStartDate(), cuttingTerm.getStartDate().plusDays(-1L)), 
						oldTerm, 
						false
					));
				}
				
				replacementTerms.add(new ReplacementTimeEntry<Term>(
					new Term(cuttingTerm.getStartDate(), cuttingTerm.getEndDate()), 
					oldTerm, 
					true
				));
				
				if (cuttingTerm.getEndDate().isBefore(oldTerm.getEndDate())) {
					replacementTerms.add(new ReplacementTimeEntry<Term>(
						new Term(cuttingTerm.getEndDate().plusDays(1L), oldTerm.getEndDate()), 
						oldTerm, 
						false
					));
				}
				
				// No need to check the other terms if the cutting term is completely encompassed within one of them
				return new TimeCutResult<Term>(removedTerms, replacementTerms);
			} 
			
			// old term falls completely within new term
			if (oldTerm.getStartDate().isAfter(cuttingTerm.getStartDate()) && oldTerm.getEndDate().isBefore(cuttingTerm.getEndDate())) { 
				removedTerms.add(oldTerm);
				replacementTerms.add(new ReplacementTimeEntry<Term>(oldTerm, oldTerm, true));
				continue;
			}
	
			/* 
				Analyzes the old term in relationship with the cutting term's start date;
			 	Creates entries when the old term is adjacent before or overlaps with cutting term
				Given old = -, new = X, overlap = *, this encompasses the following scenarios:
				|--**XX|	-> 2 terms
				|--**| 		-> 2 terms  
				|--**--| 	-> 3 terms (dealt with during the full-encompassment),
				|**XX| 		-> 1 term, 
				|***| 		-> 1 term (dealt with during the exact match), 
				|**--| 		-> 2 terms
			*/	
			if (!oldTerm.getStartDate().isAfter(cuttingTerm.getStartDate()) && !oldTerm.getEndDate().isBefore(cuttingTerm.getStartDate())) {
				removedTerms.add(oldTerm); 
				if (cuttingTerm.getStartDate().isAfter(oldTerm.getStartDate())) {
					replacementTerms.add(new ReplacementTimeEntry<Term>(
						new Term(oldTerm.getStartDate(), cuttingTerm.getStartDate().plusDays(-1L)), 
						oldTerm, 
						false
					));
				}
				replacementTerms.add(new ReplacementTimeEntry<Term>(
					new Term(cuttingTerm.getStartDate(), oldTerm.getEndDate()),
					oldTerm, 
					true
				));
			}
			
			/* 
				Analyzes the old term in relationship with the cutting term's end date;
			 	Creates entries when the old term is adjacent after or overlaps with cutting term
				Given old = -, new = X, overlap = *, this encompasses the following scenarios:
				|--**|		-> 2 terms
				|--**--| 	-> 3 terms (dealt with during the full-encompassment),
				|***| 		-> 1 term (dealt with during the exact match),
				|**--|		-> 2 term 
				|XX**| 		-> 1 term, 
				|XX**--|	-> 2 terms
			*/	
			if (!oldTerm.getStartDate().isAfter(cuttingTerm.getEndDate()) && !oldTerm.getEndDate().isBefore(cuttingTerm.getEndDate())) {
				removedTerms.add(oldTerm); // obsolete the original term
				replacementTerms.add(new ReplacementTimeEntry<Term>(new Term(
					oldTerm.getStartDate(), cuttingTerm.getEndDate()),
					oldTerm, 
					true
				));
				if (cuttingTerm.getEndDate().isBefore(oldTerm.getEndDate())) {
					replacementTerms.add(new ReplacementTimeEntry<Term>(
						new Term(cuttingTerm.getEndDate().plusDays(1L), oldTerm.getEndDate()), 
						oldTerm, 
						false
					));
				}
			}
		}
	
		if (replacementTerms.isEmpty()) {
			replacementTerms.add(new ReplacementTimeEntry<Term>(cuttingTerm, null, true));
			return new TimeCutResult<Term>(removedTerms, replacementTerms);
		}
	
		List<Term> orderedEncompassingReplacementTerms = replacementTerms.stream()
			.filter(t -> t.getUsesNewInfo())
			.map(t -> t.getNewEntry())
			.sorted()
			.collect(Collectors.toList());
	
		// Insert a term if the cutting term started before all of the existing terms
		LocalDate earliestStart = CollectionsUtil.findFirst(orderedEncompassingReplacementTerms).getStartDate();
		if (cuttingTerm.getStartDate().isBefore(earliestStart)) {
			// We're guaranteed that the cutting term was itself cut b/c of the existence of replacement terms
			replacementTerms.add(new ReplacementTimeEntry<Term>(
				new Term(cuttingTerm.getStartDate(), earliestStart.plusDays(-1L)),
				null, 
				true
			));
		}
	
		// Insert a term if the cutting term ended after all of the existing terms
		LocalDate latestEnd = CollectionsUtil.findLast(orderedEncompassingReplacementTerms).getEndDate();
		if (cuttingTerm.getEndDate().isAfter(latestEnd)) {
			replacementTerms.add(new ReplacementTimeEntry<Term>(
				new Term(latestEnd.plusDays(1L), cuttingTerm.getEndDate()), 
				null, 
				true
			));
		}
	
		// Insert terms for any places where the cutting term existing on top of gaps in the existing terms
		Term previousTerm = CollectionsUtil.findFirst(orderedEncompassingReplacementTerms);
		for (Term replacement : orderedEncompassingReplacementTerms.subList(1, orderedEncompassingReplacementTerms.size())) {
			if (replacement.getStartDate().isAfter(previousTerm.getEndDate().plusDays(1L))) {
				// Don't check that the new replacement overlaps with the cutting term, 
				// since theoretically the only way to get a gap in replacement terms is 
				// if the cutting term extends over an existing one
				replacementTerms.add(new ReplacementTimeEntry<Term>(
					new Term(previousTerm.getEndDate().plusDays(1L), replacement.getStartDate().plusDays(-1L)), 
					null, 
					true
				));
			}
			previousTerm = replacement;
		}
		
		return new TimeCutResult<Term>(removedTerms, replacementTerms);
	}
	
	/**
	 * Similar to the cutDates function, except that this one operates on time periods,
	 * and will not attempt to fill 'gaps' the way the date cutting function does
	 * 
	 * To function properly, none of the oldPeriods should be overlapping
	 * 
	 * @param oldPeriods
	 * @param cuttingPeriod
	 * @return
	 */
	public static TimeCutResult<TimePeriod> cutTimePeriods (
		Collection<TimePeriod> oldPeriods,
		TimePeriod cuttingPeriod
	) {
		List<TimePeriod> removedPeriods = new ArrayList<TimePeriod>();
		List<ReplacementTimeEntry<TimePeriod>> replacementPeriods = new ArrayList<>();
		
		if (oldPeriods.contains(cuttingPeriod)) {
			removedPeriods.add(cuttingPeriod);
			replacementPeriods.add(new ReplacementTimeEntry<TimePeriod>(cuttingPeriod, cuttingPeriod, true));
			return new TimeCutResult<TimePeriod>(removedPeriods, replacementPeriods);
		}
		
		TimePeriod leftOverCuttingPeriod = cuttingPeriod;
		for (TimePeriod oldPeriod : oldPeriods) {
			//No need to check the other terms if the strand's term perfectly matches one of them
			TimePeriod intersectionPeriod = TimePeriod.intersectPeriods(oldPeriod, leftOverCuttingPeriod);
			
			if (!intersectionPeriod.isEmpty()) {
				TimePeriod leftOverPeriod = TimePeriod.subtractPeriods(oldPeriod, intersectionPeriod);
				removedPeriods.add(oldPeriod);
				replacementPeriods.add(new ReplacementTimeEntry<TimePeriod>(intersectionPeriod, oldPeriod, true));
				
				if (!leftOverPeriod.isEmpty()) {
					replacementPeriods.add(new ReplacementTimeEntry<TimePeriod>(leftOverPeriod, oldPeriod, false));
				}
				leftOverCuttingPeriod = TimePeriod.subtractPeriods(leftOverCuttingPeriod, intersectionPeriod);
			}
			else {
				replacementPeriods.add(new ReplacementTimeEntry<TimePeriod>(oldPeriod, oldPeriod, false));
			}
		}
		
		if (!leftOverCuttingPeriod.isEmpty()) {
			replacementPeriods.add(new ReplacementTimeEntry<TimePeriod>(leftOverCuttingPeriod, null, true));
		}
		
		return new TimeCutResult<TimePeriod>(removedPeriods, replacementPeriods);
	}
	
	/**
	 * Utility method for updating nested Term/TimePeriod maps, as that is often the scenario in which the term/period cut methods get used
	 * This method will not fix any overlapping windows that may have existed in the original termMap
	 * 	 * 
	 * @param termMap
	 * @param cuttingTerm
	 * @param cuttingPeriod
	 * @param defaultValueProducer - produces the default value stored in the timeperiod to start with when a new term-period is produced 
	 * 		that does not overlap with an existing term-period
	 * @param valueDeepCopy - deep copies the value stored in the timeperiod map when a new term-period is produces that overlaps with an existing term-period
	 * @param valueUpdater - method for updating the value stored in the timeperiod map for those entries that overlap with the cutting term/period
	 */
	public static <E> void updateTermPeriodValueMap(
		Map<Term, Map<TimePeriod, E>> termMap,
		Term cuttingTerm,
		TimePeriod cuttingPeriod,
		Supplier<E> defaultValueProducer,
		Function<E, E> valueDeepCopy,
		Function<E, E> valueUpdater
	) {
		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(termMap.keySet(), cuttingTerm);
		List<Pair<Term, Map<TimePeriod, E>>> replacementTermEntries = new ArrayList<>();

		for(ReplacementTimeEntry<Term> replacementTermEntry : dateCutResult.getReplacementEntries()) {
			//It is safe to avoid making a copy of the old result if the new term matches the old term, since there will not be a new term that uses the old term's result
			Map<TimePeriod, E> periodMap = termMap.get(replacementTermEntry.getNewEntry());
			if (periodMap == null) {
				periodMap = new HashMap<>();
				if (replacementTermEntry.getOldEntry() != null) {
					for(Entry<TimePeriod, E> entry : termMap.get(replacementTermEntry.getOldEntry()).entrySet()) {
						periodMap.put(entry.getKey(), valueDeepCopy.apply(entry.getValue()));
					}
				}
			}

			if (replacementTermEntry.getUsesNewInfo()) {
				
				TimeCutResult<TimePeriod> periodCutResult = DateTimeUtil.cutTimePeriods(periodMap.keySet(), cuttingPeriod);
				List<Pair<TimePeriod, E>> replacementPeriodEntries = new ArrayList<>();

				for(ReplacementTimeEntry<TimePeriod> replacementPeriodEntry : periodCutResult.getReplacementEntries()) {
					E value = periodMap.get(replacementPeriodEntry.getNewEntry());
					if (value == null) {
						if (replacementPeriodEntry.getOldEntry() != null) {
							value = valueDeepCopy.apply(periodMap.get(replacementPeriodEntry.getOldEntry()));
						}
						else {
							value = defaultValueProducer.get();
						}
					}

					if (replacementPeriodEntry.getUsesNewInfo()) {
						value = valueUpdater.apply(value);
					}
					
					replacementPeriodEntries.add(new Pair<>(replacementPeriodEntry.getNewEntry(), value));
				}

				for(TimePeriod removedPeriod : periodCutResult.getRemovedEntries()) {
					periodMap.remove(removedPeriod);
				}

				for(Pair<TimePeriod, E> replacementEntry : replacementPeriodEntries) {
					periodMap.put(replacementEntry.getValue0(), replacementEntry.getValue1());
				}
			}

			replacementTermEntries.add(new Pair<>(replacementTermEntry.getNewEntry(), periodMap));
		}

		for(Term removedTerm : dateCutResult.getRemovedEntries()) {
			termMap.remove(removedTerm);
		}

		for(Pair<Term, Map<TimePeriod, E>> replacementEntry : replacementTermEntries) {
			termMap.put(replacementEntry.getValue0(), replacementEntry.getValue1());
		}
	}
	
	/**
	 * Performs date and time period cutting on the term-periods.
	 * Is better for than a nested Map<Term, Map<TimePeriod, ?>> structure for sorting results
	 * 
	 * @param sourceTermPeriods
	 * @return map of the cut termPeriods to their source termPeriods
	 */
	public static Map<TermPeriod, Set<TermPeriod>> createCutTermPeriodMappings(
		Map<Term, Set<TimePeriod>> sourceTermPeriods
	) {
		return createCutTermPeriodMappings(sourceTermPeriods, null, null);
	}
	
	/**
     * Performs date and time period cutting on the term-periods. 
     * @param sourceTermPeriods
     * @param limitingTerm - defines the term for which we care about results. 
     *      All cut terms that fall outside of the relevantTerm will be ignored and not returned.
     *      Method does not fill in gaps left by the sourceTerms that are encompassed within the relevantTerm.
     *      To make sure there are no gaps, insert the releveantTerm to the sourceTerms
     * @param limitingPeriod - defines the period for which we care about results. 
     *      All cut period that fall outside of the relevantPeriod will be ignored and not returned
     *      Method does not fill in gaps left by the sourcePeriods that are encompassed within the relevantPeriod.
     *      To make sure there are no gaps, insert the relevantPeriod to the sourcePeriods
     * @return map of the cut termPeriods to their source termPeriods
     */
    public static Map<TermPeriod, Set<TermPeriod>> createCutTermPeriodMappings(
        Map<Term, Set<TimePeriod>> sourceTermPeriods,
        Term limitingTerm,
        TimePeriod limitingPeriod
    ) {
        Map<TermPeriod, Set<TermPeriod>> newToSourceMappings = new HashMap<>();
        
        Map<Term, Set<Term>> termMappings = createCutTermMappings(
            sourceTermPeriods.keySet(),
            limitingTerm
        );
        
        for (Entry<Term, Set<Term>> termEntry : termMappings.entrySet()) {
            Map<TimePeriod, Set<Term>> sourcePeriodTermMap = new HashMap<>();
            for (Term sourceTerm : termEntry.getValue()) {
                for (TimePeriod tp : sourceTermPeriods.get(sourceTerm)) {
                    Set<Term> termsWithPeriods = sourcePeriodTermMap.get(tp);
                    if (termsWithPeriods == null) {
                        termsWithPeriods = new HashSet<>();
                        sourcePeriodTermMap.put(tp, termsWithPeriods);
                    }
                    termsWithPeriods.add(sourceTerm);
                }
            }
            
            Map<TimePeriod, Set<TimePeriod>> periodMappings = createCutTimePeriodMappings(
                sourcePeriodTermMap.keySet(),
                limitingPeriod
            );
            
            for (Entry<TimePeriod, Set<TimePeriod>> periodEntry : periodMappings.entrySet()) {
                TermPeriod newTermPeriod = new TermPeriod(termEntry.getKey(), periodEntry.getKey());
                Set<TermPeriod> origTermPeriods = new HashSet<>();
                newToSourceMappings.put(newTermPeriod, origTermPeriods);
                
                for (TimePeriod sourcePeriod : periodEntry.getValue()) {
                    for (Term sourceTerm : sourcePeriodTermMap.get(sourcePeriod)) {
                        origTermPeriods.add(new TermPeriod(sourceTerm, sourcePeriod));
                    }
                }
            }
        }
    
        return newToSourceMappings;
    }
    
    /**
     * Performs term period cutting on the terms. 
     * @param sourceTerms
     * @return map of the cut terms to their source terms
     */
    public static Map<Term, Set<Term>> createCutTermMappings(
		Set<Term> sourceTerms
	) {
		return createCutTermMappings(sourceTerms, null);
	}
    
    /**
     * Performs term period cutting on the terms. 
     * @param sourceTerms
     * @param limitingTerm - defines the term for which we care about results. 
     *      All cut terms that fall outside of the relevantTerm will be ignored and not returned.
     *      Method does not fill in gaps left by the sourceTerms that are encompassed within the relevantTerm.
     *      To make sure there are no gaps, insert the releveantTerm to the sourceTerms
     * @return map of the cut terms to their source terms
     */
    public static Map<Term, Set<Term>> createCutTermMappings(
        Set<Term> sourceTerms,
        Term limitingTerm
    ) {
        Map<Term, Set<Term>> newToSourceMappings = new HashMap<>();
        
        for (Term sourceTerm : sourceTerms) {
            Term cuttingTerm = sourceTerm;
            if (limitingTerm != null) {
                cuttingTerm = Term.getIntersectionTerm(limitingTerm, sourceTerm);
                if (cuttingTerm == null) {
                    continue;
                }
            }
            
            TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(newToSourceMappings.keySet(), cuttingTerm);
            List<Pair<Term, Set<Term>>> replacementEntries = new ArrayList<>();
            
            for (ReplacementTimeEntry<Term> replacementEntry : dateCutResult.getReplacementEntries()) {
                Set<Term> origTerms = newToSourceMappings.get(replacementEntry.getNewEntry());
                if (origTerms == null) {
                    if (replacementEntry.getOldEntry() != null) {
                        origTerms = new HashSet<>(newToSourceMappings.get(replacementEntry.getOldEntry()));
                    }
                    else {
                        origTerms = new HashSet<>();
                    }
                }
                
                if (replacementEntry.getUsesNewInfo()) {
                    origTerms.add(sourceTerm);
                }
                
                replacementEntries.add(new Pair<>(replacementEntry.getNewEntry(), origTerms));
            }
            
            for (Term removedTerm : dateCutResult.getRemovedEntries()) {
                newToSourceMappings.remove(removedTerm);
            }
            
            for (Pair<Term, Set<Term>> replacementEntry : replacementEntries) {
                newToSourceMappings.put(replacementEntry.getValue0(), replacementEntry.getValue1());
            }
        }
        return newToSourceMappings;
    }
	
    /**
     * Performs timeperiod cutting on the timeperiods. 
     * @param sourcePeriods
     * @return map of the cut timePeriods to their source timeperiods
     */
	public static Map<TimePeriod, Set<TimePeriod>> createCutTimePeriodMappings(
		Set<TimePeriod> sourcePeriods
	) {
		return createCutTimePeriodMappings(sourcePeriods, null);
	}
	
	/**
     * Performs timePeriod cutting on the timePeriods. 
     * @param sourcePeriods
     * @param limitingPeriod - defines the period for which we care about results. 
     *      All cut period that fall outside of the relevantPeriod will be ignored and not returned
     *      Method does not fill in gaps left by the sourcePeriods that are encompassed within the relevantPeriod.
     *      To make sure there are no gaps, insert the relevantPeriod to the sourcePeriods
     * @return map of the cut timePeriods to their source timePeriods
     */
    public static Map<TimePeriod, Set<TimePeriod>> createCutTimePeriodMappings(
        Set<TimePeriod> sourcePeriods,
        TimePeriod limitingPeriod
    ) {
        Map<TimePeriod, Set<TimePeriod>> newToSourceMappings = new HashMap<>();
        
        for (TimePeriod sourcePeriod : sourcePeriods) {
            TimePeriod cuttingPeriod = sourcePeriod;
            if (limitingPeriod != null) {
                cuttingPeriod = TimePeriod.intersectPeriods(limitingPeriod, sourcePeriod);
                if (cuttingPeriod.isEmpty()) {
                    continue;
                }
            }
            
            TimeCutResult<TimePeriod> periodCutResult = DateTimeUtil.cutTimePeriods(newToSourceMappings.keySet(), cuttingPeriod);
            List<Pair<TimePeriod, Set<TimePeriod>>> replacementEntries = new ArrayList<>();
            
            for (ReplacementTimeEntry<TimePeriod> replacementEntry : periodCutResult.getReplacementEntries()) {
                Set<TimePeriod> origPeriods = newToSourceMappings.get(replacementEntry.getNewEntry());
                if (origPeriods == null) {
                    if (replacementEntry.getOldEntry() != null) {
                        origPeriods = new HashSet<>(newToSourceMappings.get(replacementEntry.getOldEntry()));
                    }
                    else {
                        origPeriods = new HashSet<>();
                    }
                }
                
                if (replacementEntry.getUsesNewInfo()) {
                    origPeriods.add(sourcePeriod);
                }
                
                replacementEntries.add(new Pair<>(replacementEntry.getNewEntry(), origPeriods));
            }
            
            for (TimePeriod removedPeriod : periodCutResult.getRemovedEntries()) {
                newToSourceMappings.remove(removedPeriod);
            }
            
            for (Pair<TimePeriod, Set<TimePeriod>> replacementEntry : replacementEntries) {
                newToSourceMappings.put(replacementEntry.getValue0(), replacementEntry.getValue1());
            }
        }
    
        return newToSourceMappings;
    }
    
    /**
     * Groups termPeriod objects according to their terms, returning the object as Map<Term, Set<TimePeriod>>
     * @param termPeriods
     * @return
     */
    public static Map<Term, Set<TimePeriod>> createTermPeriodMap(
		Iterable<TermPeriod> termPeriods
	) {
		Map<Term, Set<TimePeriod>> termMap = new HashMap<>();
		
		for (TermPeriod termPeriod : termPeriods) {
			Set<TimePeriod> periods = termMap.get(termPeriod.getTerm());
			if (periods == null) {
				periods = new HashSet<>();
				termMap.put(termPeriod.getTerm(), periods);
			}
			periods.add(termPeriod.getTimePeriod());
		}
		
		return termMap;
	}
	
	/**
	 * Maps the sourceTerms to the resultTerms they are a part of. 
	 * Key of the return map is a resultTerm, and value contains the relevant sourceTerms 
	 * @param resultTerms
	 * @param sourceTerms
	 * @return
	 */
	public static Map<Term, Set<Term>> getRelevantTerms(
		Collection<Term> resultTerms,
		Collection<Term> sourceTerms
	) {
		List<Term> orderedCutTerms = new ArrayList<>(resultTerms);
		orderedCutTerms.sort(Term::compareTo);
		
		List<Term> orderedSourceTerms = new ArrayList<>(sourceTerms);
		orderedSourceTerms.sort(Term::compareTo);
		
		Map<Term, Set<Term>> mappedTerms = new HashMap<>();
		
		int earliestSourceTermIndex = 0;
		for (Term resultTerm : orderedCutTerms) {
			Set<Term> relevantTerms = new HashSet<>();
			
			boolean hasFoundIntersection = false;
			for (int i=earliestSourceTermIndex; i<orderedSourceTerms.size(); i++) {
				Term sourceTerm = orderedSourceTerms.get(i);
				
				if (Term.hasIntersection(resultTerm, sourceTerm)) {
					relevantTerms.add(sourceTerm);
					
					if (!hasFoundIntersection) {
						hasFoundIntersection = true;
						earliestSourceTermIndex = i;
					}
				}
				//cannot use an else + break here in the scenario where a term with an earlier start date has a later end date than another:
				//ex: cutTerm = 2018, source terms contain 2015-2019 and 2018
			}
			
			mappedTerms.put(resultTerm, relevantTerms);
		}
		
		return mappedTerms;
	}
	
	/**
	 * Condenses a term-period by condensing periods with the same values together and converting adjacent terms 
	 * (with the same internal period-value maps) to single terms
	 * 
	 * To function properly, there should not be any overlapping term/periods in the termMap
	 * 
	 * @param termMap
	 * @return
	 */
	public static <E> Map<Term, Map<TimePeriod, E>> condenseTermPeriodValueMap(
		Map<Term, Map<TimePeriod, E>> termMap
	) {
		EquivalenceMap<Map<TimePeriod, E>, Collection<Term>> termGroupingMap = new EquivalenceMap<>(new MapEquivalence<>());
		for (Entry<Term, Map<TimePeriod, E>> termEntry : termMap.entrySet()) {
			Map<E, TimePeriod> valueMap = new HashMap<>();
			for (Entry<TimePeriod, E> periodEntry : termEntry.getValue().entrySet()) {
				if (valueMap.containsKey(periodEntry.getValue())) {
					valueMap.put(
						periodEntry.getValue(), 
						TimePeriod.unionPeriods(valueMap.get(periodEntry.getValue()), periodEntry.getKey())
					);
				}
				else {
					valueMap.put(periodEntry.getValue(), periodEntry.getKey());
				}
			}
			
			Map<TimePeriod, E> condensedPeriodMap = new HashMap<>();
			for (Entry<E, TimePeriod> entry : valueMap.entrySet()) {
				condensedPeriodMap.put(entry.getValue(), entry.getKey());
			}
			
			Collection<Term> similarTerms = termGroupingMap.get(condensedPeriodMap);
			if (similarTerms == null) {
				similarTerms = new HashSet<>();
				termGroupingMap.put(condensedPeriodMap, similarTerms);
			}
			
			similarTerms.add(termEntry.getKey());
		}
	
		Map<Term, Map<TimePeriod, E>> condensedTermMap = new HashMap<>();
		for (Entry<Map<TimePeriod, E>, Collection<Term>> entry : termGroupingMap.entrySet()) {
			Term lastTerm = null;
            Term earliestTerm = null;
            List<Term> orderedTerms = entry.getValue().stream()
            	.sorted((kv1, kv2)->kv1.getStartDate().compareTo(kv2.getStartDate()))
            	.collect(Collectors.toList());
            for(Term relevantTerm : orderedTerms) {
                if (earliestTerm != null) {
                    if (lastTerm.getEndDate().plusDays(1L).isBefore(relevantTerm.getStartDate())) {
                    	condensedTermMap.put(new Term(earliestTerm.getStartDate(), lastTerm.getEndDate()), entry.getKey());
                        earliestTerm = relevantTerm;
                    }
                }
                else {
                    earliestTerm = relevantTerm;
                }

                lastTerm = relevantTerm;
            }

            if (earliestTerm != null) {
            	condensedTermMap.put(new Term(earliestTerm.getStartDate(), lastTerm.getEndDate()), entry.getKey());
            }
		}
		
		return condensedTermMap;
	}
	
	/**
	 * Finds the terms that between startDate and endDate that are not covered by the existingTerms
	 * @param existingTerms
	 * @param evaluationTerm
	 * @return
	 */
	public static Set<Term> findGapTerms(
        Set<Term> existingTerms, 
        Term evaluationTerm
    ) {
	    if (evaluationTerm == null) {
            throw new IllegalArgumentException("EvaluationTerm cannot be null");
        }
		
	    SortedSet<Term> sortedRelevantTerms = new TreeSet<>();
        for (Term term : existingTerms) {
            if (Term.hasIntersection(term, evaluationTerm)) {
                sortedRelevantTerms.add(Term.getIntersectionTerm(term, evaluationTerm));
            }
        }
        
        Set<Term> gapTerms = new TreeSet<>();
        if (!CollectionsUtil.isNullOrEmpty(sortedRelevantTerms)) {
            LocalDate earliestStartDate = sortedRelevantTerms.first().getStartDate();
            if (evaluationTerm.getStartDate().isBefore(earliestStartDate)) {
                gapTerms.add(new Term(evaluationTerm.getStartDate(), earliestStartDate.minusDays(1)));
            }
            
            LocalDate latestEndDate = sortedRelevantTerms.first().getEndDate();
            for (Term term : sortedRelevantTerms) {
                if (term.getStartDate().isAfter(latestEndDate)) {
                    LocalDate preTermEnd = term.getStartDate().minusDays(1);
                    if (latestEndDate.isBefore(preTermEnd)) {
                        gapTerms.add(new Term(latestEndDate.plusDays(1), preTermEnd));
                    }
                }
                latestEndDate = DateTimeUtil.getMaxDate(latestEndDate, term.getEndDate());
            }
            
            if (latestEndDate.isBefore(evaluationTerm.getEndDate())) {
                gapTerms.add(new Term(latestEndDate.plusDays(1), evaluationTerm.getEndDate()));
            }
        } else {
            gapTerms.add(evaluationTerm);
        }
        
        return gapTerms;
	}

	/**
	 * Glues adjacent/overlapping terms together to form the smallest collection of terms 
	 * that encompass the same terms covered by the existingTerms
	 * @param existingTerms
	 * @return
	 */
	public static List<Term> glueTerms(Iterable<Term> existingTerms) {	
		Iterable<Term> orderedTerms = CollectionsUtil.orderBy(
			existingTerms, 
			(t1, t2) -> t1.getStartDate().compareTo(t2.getStartDate())
		);
		
		List<Term> condensedTerms = new ArrayList<>();
		LocalDate lastCountedStartDate = null;
		LocalDate lastEndDate = null;
		
		for (Term term : orderedTerms) {
			if (lastCountedStartDate == null) {
				lastCountedStartDate = term.getStartDate();
			}
			
			if (lastEndDate != null) {
				if (term.getStartDate().isAfter(lastEndDate.plusDays(1))) {
					condensedTerms.add(new Term(lastCountedStartDate, lastEndDate));					
					lastCountedStartDate = term.getStartDate();
				}
			}
			
			lastEndDate = term.getEndDate();
		}
		condensedTerms.add(new Term(lastCountedStartDate, lastEndDate));
		
		return condensedTerms;
	}	

}
