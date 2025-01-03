package com.skapp.community.timeplanner.payload.request;

import com.skapp.community.timeplanner.type.TimeRecordSort;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class ManagerTimeRecordFilterDto {

	@Min(0)
	private int page = 0;

	@Min(1)
	private int size = 7;

	@NotNull
	private LocalDate startDate;

	@NotNull
	private LocalDate endDate;

	private List<Long> teamIds;

	private Boolean isExport = false;

	private Sort.Direction sortOrder = Sort.Direction.ASC;

	private TimeRecordSort sortKey = TimeRecordSort.NAME;

}
