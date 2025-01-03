package com.skapp.community.peopleplanner.repository;

import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.Team;
import com.skapp.community.timeplanner.type.ClockInType;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeTeamRepository {

	Long countAvailableEmployeesByTeamIdsAndDate(List<Long> teamsFilter, LocalDate currentDate, Long currentUserId);

	List<Employee> getEmployeesByTeamIds(String searchKeyword, List<Long> teams, List<ClockInType> clockInType,
			LocalDate date, Long currentUserId);

	List<Team> findTeamsByEmployeeId(Long employeeId);

	List<Employee> getEmployeesByTeamIds(List<Long> teams, Long currentUserId, boolean isAdmin);

	void deleteEmployeeTeamByTeamId(Long teamId);

	void deleteEmployeeTeamByTeamIdAndEmployeeIds(Long teamId, List<Long> employeeIds);

}
