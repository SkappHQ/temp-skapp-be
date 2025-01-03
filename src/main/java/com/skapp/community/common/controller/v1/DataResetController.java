package com.skapp.community.common.controller.v1;

import com.skapp.community.common.payload.response.ResponseEntityDto;
import com.skapp.community.common.service.DataResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/reset-database")
@Tag(name = "Data Reset Controller", description = "Operations related to resetting the database")
public class DataResetController {

	@NonNull
	private final DataResetService dataResetService;

	@Operation(summary = "Reset the Database",
			description = "This endpoint resets the entire database to its initial state. "
					+ "API key is required if the user is not authenticated.",
			parameters = { @Parameter(name = "X-Reset-Database-Key",
					description = "API key for accessing this endpoint (required if unauthenticated)",
					in = ParameterIn.HEADER) })
	@GetMapping
	public ResponseEntity<ResponseEntityDto> resetDatabase() {
		ResponseEntityDto response = dataResetService.resetDatabase();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
