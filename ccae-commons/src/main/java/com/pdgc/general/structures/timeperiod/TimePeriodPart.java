package com.pdgc.general.structures.timeperiod;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.BitSet;

import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.util.DateTimeUtil;
import com.pdgc.general.util.xml.XmlNode;

/**
 * A class that matches the RightScopeTimePerid and CarveoutTimePeriod entities
 * in the DB. This class does not store things in a way that is conducive for
 * calculations, but does translate all the fields in such a way that the time
 * period can be described with a user-friendly string such as "T 5:00P-10:30P
 * (D)"
 * 
 * @author Vishal Raut
 */
public class TimePeriodPart implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Duration startTime;
	private Duration endTime;
	private TimePeriodType timePeriodType = TimePeriodType.CONTINUOUS;
	private boolean[] daysOfWeek = new boolean[7];

	// Used by daily time periods
	// start and end day used by continuous time periods
	private int startDay, endDay;
	private String prettyString;

	public TimePeriodPart(DataRow reader) {
		this.startTime = Duration.between(LocalTime.MIDNIGHT, reader.getTime("startTime"));
		this.endTime = Duration.between(LocalTime.MIDNIGHT, reader.getTime("endTime"));
//		this.endTime = reader.getDuration("endTime");
		this.timePeriodType = (reader.getInteger("TimePeriodTypeId") == 1) ? TimePeriodType.CONTINUOUS : TimePeriodType.DAILY;
		analyzeDays(reader.getBoolean("isMonday"), reader.getBoolean("isTuesday"), reader.getBoolean("isWednesday"),
				reader.getBoolean("isThursday"), reader.getBoolean("isFriday"), reader.getBoolean("isSaturday"),
				reader.getBoolean("isSunday"));
		createPrettyString();
	}
	
	public TimePeriodPart(
			LocalTime startTime,
			LocalTime endTime,
			Integer TimePeriodTypeId,
			Boolean isMonday,
			Boolean isTuesday, 
			Boolean isWednesday,
			Boolean isThursday, 
			Boolean isFriday, 
			Boolean isSaturday,
			Boolean isSunday) {
		this.startTime = Duration.between(LocalTime.MIDNIGHT, startTime);
		this.endTime = Duration.between(LocalTime.MIDNIGHT, endTime);
//		this.endTime = reader.getDuration("endTime");
		this.timePeriodType = (TimePeriodTypeId == 1) ? TimePeriodType.CONTINUOUS : TimePeriodType.DAILY;
		analyzeDays(isMonday, isTuesday, isWednesday, isThursday, isFriday, isSaturday, isSunday);
		createPrettyString();
	}

	public TimePeriodPart(Duration startTime, Duration endTime, TimePeriodType timePeriodType, boolean isMonday, boolean isTuesday,
			boolean isWednesday, boolean isThursday, boolean isFriday, boolean isSaturday, boolean isSunday) {
		// Assumed: startTime and endTime fit within the 24-hour structure b/c
		// they are actually naming time of days...rather than length of time
		if (endTime.toHours() == 0) {
			endTime = Duration.ofHours(24).plusMinutes(DateTimeUtil.getAbsoluteMinutes(endTime)).plusSeconds(DateTimeUtil.getAbsoluteSeconds(endTime));
		}

		this.startTime = startTime;
		this.endTime = endTime;
		this.timePeriodType = timePeriodType;
		analyzeDays(isMonday, isTuesday, isWednesday, isThursday, isFriday, isSaturday, isSunday);
		createPrettyString();
	}
	
	public TimePeriodPart(XmlNode timePeriod) {
		// Assumed: startTime and endTime fit within the 24-hour structure b/c
		// they are actually naming time of days...rather than length of time
		//TODO: make time periods work.
//        timePeriodParts.add(new TimePeriodPart(
//                TimeSpan.Parse(timePeriod.Attributes["StartTime"].Value),
//                TimeSpan.Parse(timePeriod.Attributes["EndTime"].Value),
//                MasterDataCache.TimePeriodTypeDictionary.getTimePeriodType(Convert.ToInt32(timePeriod.Attributes["TimePeriodTypeId"])),
//                Convert.ToBoolean(timePeriod.Attributes["IsMonday"]),
//                Convert.ToBoolean(timePeriod.Attributes["IsTuesday"]),
//                Convert.ToBoolean(timePeriod.Attributes["IsWednesday"]),
//                Convert.ToBoolean(timePeriod.Attributes["IsThursday"]),
//                Convert.ToBoolean(timePeriod.Attributes["IsFriday"]),
//                Convert.ToBoolean(timePeriod.Attributes["IsSaturday"]),
//                Convert.ToBoolean(timePeriod.Attributes["IsSunday"])
//            ));
		
		
//		if (endTime.toHours() == 0) {
//			endTime = Duration.ofHours(24).plusMinutes(Util.getAbsoluteMinutes(endTime)).plusSeconds(Util.getAbsoluteSeconds(endTime));
//		}
//
//		this.startTime = LocalDate.timePeriod.getAttribute("StartTime").getValue();
//		this.endTime = endTime;
//		this.timePeriodType = timePeriodType;
//		analyzeDays(isMonday, isTuesday, isWednesday, isThursday, isFriday, isSaturday, isSunday);
//		createPrettyString();
	}
	

	/**
	 * populates daysOfWeek and finds the start and end days for continuous time
	 * periods, which are needed to generate the calculation-friend TimePeriod
	 * structure, as well as the user-friendly prettyString
	 * 
	 * @param isMonday
	 * @param isTuesday
	 * @param isWednesday
	 * @param isThursday
	 * @param isFriday
	 * @param isSaturday
	 * @param isSunday
	 */
	private void analyzeDays(boolean isMonday, boolean isTuesday, boolean isWednesday, boolean isThursday, boolean isFriday,
			boolean isSaturday, boolean isSunday) {
		int firstDay = -1;
		int lastDay = -1;
		boolean prevDayFound = false;
		boolean gapFound = false;
		if (isMonday) {
			daysOfWeek[DateTimeUtil.normalizeDay(DayOfWeek.MONDAY)] = true;
			firstDay = DateTimeUtil.normalizeDay(DayOfWeek.MONDAY);
			lastDay = DateTimeUtil.normalizeDay(DayOfWeek.MONDAY);
			prevDayFound = true;
		}

		if (isTuesday) {
			daysOfWeek[DateTimeUtil.normalizeDay(DayOfWeek.TUESDAY)] = true;
			if (!prevDayFound) {
				firstDay = DateTimeUtil.normalizeDay(DayOfWeek.TUESDAY);
			}

			lastDay = DateTimeUtil.normalizeDay(DayOfWeek.TUESDAY);
			prevDayFound = true;
		} else {
			if (prevDayFound) {
				gapFound = true;
			}

			prevDayFound = false;
		}
		if (isWednesday) {
			daysOfWeek[DateTimeUtil.normalizeDay(DayOfWeek.WEDNESDAY)] = true;
			if (!prevDayFound) {
				firstDay = DateTimeUtil.normalizeDay(DayOfWeek.WEDNESDAY);
				// Avoid resetting the last day if there was a gap. Ex:
				// Wednesday through Monday
				if (!gapFound) {
					lastDay = DateTimeUtil.normalizeDay(DayOfWeek.WEDNESDAY);
				}

				prevDayFound = true;
			} else {
				if (!gapFound) {
					lastDay = DateTimeUtil.normalizeDay(DayOfWeek.WEDNESDAY);
				}

			}
			prevDayFound = true;
		} else {
			if (prevDayFound) {
				gapFound = true;
			}

			prevDayFound = false;
		}
		if (isThursday) {
			daysOfWeek[DateTimeUtil.normalizeDay(DayOfWeek.THURSDAY)] = true;
			if (!prevDayFound) {
				firstDay = DateTimeUtil.normalizeDay(DayOfWeek.THURSDAY);
				// Avoid resetting the last day if there was a gap. Ex:
				// Wednesday through Monday
				if (!gapFound) {
					lastDay = DateTimeUtil.normalizeDay(DayOfWeek.THURSDAY);
				}

				prevDayFound = true;
			} else {
				if (!gapFound) {
					lastDay = DateTimeUtil.normalizeDay(DayOfWeek.THURSDAY);
				}

			}
			prevDayFound = true;
		} else {
			if (prevDayFound) {
				gapFound = true;
			}

			prevDayFound = false;
		}
		if (isFriday) {
			daysOfWeek[DateTimeUtil.normalizeDay(DayOfWeek.FRIDAY)] = true;
			if (!prevDayFound) {
				firstDay = DateTimeUtil.normalizeDay(DayOfWeek.FRIDAY);
				// Avoid resetting the last day if there was a gap. Ex:
				// Wednesday through Monday
				if (!gapFound) {
					lastDay = DateTimeUtil.normalizeDay(DayOfWeek.FRIDAY);
				}

				prevDayFound = true;
			} else {
				if (!gapFound) {
					lastDay = DateTimeUtil.normalizeDay(DayOfWeek.FRIDAY);
				}

			}
			prevDayFound = true;
		} else {
			if (prevDayFound) {
				gapFound = true;
			}

			prevDayFound = false;
		}
		if (isSaturday) {
			daysOfWeek[DateTimeUtil.normalizeDay(DayOfWeek.SATURDAY)] = true;
			if (!prevDayFound) {
				firstDay = DateTimeUtil.normalizeDay(DayOfWeek.SATURDAY);
				// Avoid resetting the last day if there was a gap. Ex:
				// Wednesday through Monday
				if (!gapFound) {
					lastDay = DateTimeUtil.normalizeDay(DayOfWeek.SATURDAY);
				}

				prevDayFound = true;
			} else {
				if (!gapFound) {
					lastDay = DateTimeUtil.normalizeDay(DayOfWeek.SATURDAY);
				}

			}
			prevDayFound = true;
		} else {
			if (prevDayFound) {
				gapFound = true;
			}

			prevDayFound = false;
		}
		if (isSunday) {
			daysOfWeek[DateTimeUtil.normalizeDay(DayOfWeek.SUNDAY)] = true;
			if (!prevDayFound) {
				firstDay = DateTimeUtil.normalizeDay(DayOfWeek.SUNDAY);
				// Avoid resetting the last day if there was a gap. Ex:
				// Wednesday through Monday
				if (!gapFound) {
					lastDay = DateTimeUtil.normalizeDay(DayOfWeek.SUNDAY);
				}

			} else {
				if (!gapFound) {
					lastDay = DateTimeUtil.normalizeDay(DayOfWeek.SUNDAY);
				}

			}
		}

		/*
		 * Theoretically dayFound HAS to be true by this point.... if
		 * (!dayFound) { throw new Exception? }
		 */
		startDay = firstDay;
		endDay = lastDay;
	}

	/**
	 * Generates the user-friend prettyString (ie. "M-F 5:00P-10:30P (C))" This
	 * assumes that analyzeDays() was already called, since it relies on
	 * daysOfWeek being properly populated, and startDay/endDay already being
	 * found in the case of continuous time periods
	 */
	private void createPrettyString() {
		StringBuilder tempPrettyString = new StringBuilder();
		if (startDay == endDay) {
			tempPrettyString.append(DateTimeUtil.abbreviateNormalizedDay(startDay));
		} else {
			tempPrettyString.append(DateTimeUtil.abbreviateNormalizedDay(startDay) + "-" + DateTimeUtil.abbreviateNormalizedDay(endDay));
		}
		switch (timePeriodType) {
		case CONTINUOUS:
			tempPrettyString.append(" ").append(DateTimeUtil.formatTime(startTime)).append("-").append(DateTimeUtil.formatTime(endTime)).append(" (C)");
			break;
		case DAILY:
			tempPrettyString.append(" ").append(DateTimeUtil.formatTime(startTime)).append("-").append(DateTimeUtil.formatTime(endTime)).append(" (D)");
			break;

		}
		prettyString = tempPrettyString.toString();
	}

	/**
	 * Generates a calculation-friendly TimePeriod structure that describes the
	 * same time periods as defined by this TimePeriodPart structure. This
	 * assumes analyzeDays() was called, since it relies on daysOfWeek being
	 * properly populated, and startDay/endDay already being found in the case
	 * of continuous time periods
	 * 
	 * @return
	 */
	public TimePeriod convertToTimePeriod() {
		BitSet[] days = new BitSet[7];
		switch (timePeriodType) {
		case CONTINUOUS:
			if (startDay == endDay) {
				for (int i = 0; i < 7; i++) {
					if (i == startDay) {
						days[i] = TimePeriod.createDay(startTime, endTime);
					} else {
						days[i] = TimePeriod.createEmptyDay();
					}
				}
			} else if (startDay < endDay) {
				for (int i = 0; i < 7; i++) {
					if (i == startDay) {
						days[i] = TimePeriod.createDay(startTime, Duration.ofMinutes(23 * 60 + 59));
					} else if (i == endDay) {
						days[i] = TimePeriod.createDay(Duration.ofMinutes(0), endTime);
					} else if (i > startDay && i < endDay) {
						days[i] = TimePeriod.createFullDay();
					} else {
						days[i] = TimePeriod.createEmptyDay();
					}
				}
			} else {
				for (int i = 0; i < 7; i++) {
					if (i == startDay) {
						days[i] = TimePeriod.createDay(startTime, Duration.ofMinutes(23 * 60 + 59));
					} else if (i == endDay) {
						days[i] = TimePeriod.createDay(Duration.ofMinutes(0), endTime);
					} else if (i < startDay && i > endDay) {
						days[i] = TimePeriod.createEmptyDay();
					} else {
						days[i] = TimePeriod.createFullDay();
					}
				}
			}
			break;
		case DAILY:
			for (int i = 0; i < 7; i++) {
				if (daysOfWeek[i]) {
					days[i] = TimePeriod.createDay(startTime, endTime);
				} else {
					days[i] = TimePeriod.createEmptyDay();
				}
			}
			break;

		}
		return new TimePeriod(days);
	}

	public String getPrettyString() {
		return prettyString;
	}

}