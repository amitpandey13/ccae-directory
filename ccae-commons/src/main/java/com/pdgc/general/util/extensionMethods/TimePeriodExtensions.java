package com.pdgc.general.util.extensionMethods;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class TimePeriodExtensions {

	public List<String> getWindowsStringForDay(TimePeriod timePeriod, DayOfWeek dayOfWeek) {
		List<String> dayParts = new ArrayList<>();
		
		BitSet day = timePeriod.getDay(DateTimeUtil.normalizeDay(dayOfWeek));
		
		int startBlock = -1;
		int endBlock = -1;
		for (int i = 0; i < 96; i++) {
			if (day.get(i)) {
				if (startBlock == -1) {
					startBlock = i;
				}

				endBlock = i;
			} else {
				if (startBlock != -1) {
					Duration startTime = Duration.ofHours(startBlock / 4).plusMinutes((startBlock % 4) * 15);
					Duration endTime = Duration.ofHours(endBlock / 4).plusMinutes((endBlock % 4) * 15);
					dayParts.add(DateTimeUtil.formatTime(startTime));
					dayParts.add("-");
					dayParts.add(DateTimeUtil.formatTime(endTime));
					startBlock = -1;
					endBlock = -1;
				}

			}
		}
		if (startBlock != -1) {
			// Handle a full day
			if (startBlock == 0 && endBlock == 95) {
				dayParts.add("All Day");
			} else {
				Duration startTime = Duration.ofHours(startBlock / 4).plusMinutes((startBlock % 4) * 15);
				Duration endTime = Duration.ofHours(24);
				dayParts.add(DateTimeUtil.formatTime(startTime));
				dayParts.add("-");
				dayParts.add(DateTimeUtil.formatTime(endTime));
				startBlock = -1;
				endBlock = -1;
			}
		}

		return dayParts;
	}
}
