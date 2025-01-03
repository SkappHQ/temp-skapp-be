package com.skapp.community.timeplanner.payload.request;

import com.skapp.community.peopleplanner.type.RequestType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TimeRequestDto {

	@NotNull
	@Schema(description = "Work started time.")
	private LocalDateTime startTime;

	@NotNull
	@Schema(description = "Work ended time.")
	private LocalDateTime endTime;

	@Schema(description = "Time request type", example = "MANUAL_ENTRY_REQUEST")
	private RequestType requestType;

	@Schema(description = "Time request record id")
	private Long recordId;

	@NotNull
	@Schema(description = "Time zone id")
	private String zoneId;

}
