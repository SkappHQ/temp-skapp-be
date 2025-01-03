package com.skapp.community.peopleplanner.payload.request;

import com.skapp.community.common.type.ModuleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Data transfer object for restricting role assignments in a specific module.")
public class ModuleRoleRestrictionRequestDto {

	@Schema(description = "The module to which the role restriction is applied.", example = "ATTENDANCE")
	private ModuleType module;

	@Schema(description = "Flag indicating whether the Admin role is restricted for the specified module.",
			example = "true")
	private Boolean isAdmin;

	@Schema(description = "Flag indicating whether the Manager role is restricted for the specified module.",
			example = "false")
	private Boolean isManager;

}
