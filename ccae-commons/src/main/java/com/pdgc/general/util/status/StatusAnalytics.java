package com.pdgc.general.util.status;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.pdgc.general.util.StopWatch;

/**
 * This class will store 
 * @author THOMAS LOH
 *
 */
public abstract class StatusAnalytics implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Long requestId;
	protected String worstStepName;
	protected Long worstStepDuration;
	protected String ip;
	protected String hostName;
	protected Long heapSize;
	protected Long heapMaxSize;
	protected Long heapSizeFree;
	protected String allSteps;
	
	public StatusAnalytics(Long requestId) {
		try {
			InetAddress address = InetAddress.getLocalHost();
        	this.ip = address.getHostAddress();
        	this.hostName = address.getCanonicalHostName();
		} catch(UnknownHostException e) {
			this.ip = "unknown host!";
			this.hostName = "unknown host name!";
		}
		this.worstStepDuration = 0L;
		this.worstStepName = "Dummy start";
	}
	
	/**
	 * StepDuration is in milliseconds. Usually taken from StopWatch
	 * 
	 * @param stepDuration
	 * @param stepName
	 */
	public void setWorstStep(String stepName, Long stepDuration) {
		allSteps = allSteps + " \r\n " + stepName + " : " + stepDuration + " | ";  
		if(this.worstStepDuration < stepDuration) {
			this.worstStepDuration = stepDuration;
			this.worstStepName = stepName;
		}
	}
	
	public void setStopWatchVals(StopWatch timer) {
		this.heapSize = timer.getHeapSize();
		this.heapMaxSize = timer.getHeapMaxSize();
		this.heapSizeFree = timer.getHeapFreeSize();
	}
	
	public Long getWorstStepDuration() {
		return worstStepDuration;
	}
	public String getWorstStepName() {
		return worstStepName;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public Long getHeapSize() {
		return heapSize;
	}
	public Long getHeapMaxSize() {
		return heapMaxSize;
	}
	public Long getHeapSizeFree() {
		return heapSizeFree;
	}
	public String getAllSteps() {
		return allSteps;
	}
}
