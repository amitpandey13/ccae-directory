package com.pdgc.general.util.status;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

/**
 * This class is going to be used as a way to log statuses on the offsets for each respective processor that is actively consuming.
 * It will be consumed by the OffsetStatusProcessor and will be sent from all the processors in existence that actively consume from
 * topics.
 * 
 * This will allow us to identify what threads are doing what work and where messages came from.
 * 
 * @author THOMAS LOH
 *
 */
@Data
@Builder
public class MessageOffset implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long requestId;
	private String topic;
	private Integer partition;
	private Long offset;
	private String threadName; // Thread who consumed this
	private LocalDateTime startedAt; // Represents the time the message is first consumed and processed
	private LocalDateTime finishedAt; // Represents the time we finished the message and sent it to the OffsetProcessor
}
