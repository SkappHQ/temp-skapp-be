package com.skapp.community.peopleplanner.service;

import com.skapp.community.common.payload.response.ResponseEntityDto;
import com.skapp.community.common.util.MessageUtil;
import com.skapp.community.peopleplanner.mapper.PeopleMapper;
import com.skapp.community.peopleplanner.model.JobFamily;
import com.skapp.community.peopleplanner.model.JobTitle;
import com.skapp.community.peopleplanner.repository.EmployeeDao;
import com.skapp.community.peopleplanner.repository.JobFamilyDao;
import com.skapp.community.peopleplanner.repository.JobFamilyTitleDao;
import com.skapp.community.peopleplanner.repository.JobTitleDao;
import com.skapp.community.peopleplanner.service.impl.JobServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class JobServiceImplUnitTest {

	@Mock
	private JobFamilyDao jobFamilyDao;

	@Mock
	private JobTitleDao jobTitleDao;

	@Mock
	private JobFamilyTitleDao jobFamilyTitleDao;

	@Mock
	private EmployeeDao employeeDao;

	@Mock
	private PeopleMapper peopleMapper;

	@Mock
	private MessageUtil messageUtil;

	@Mock
	private EmployeeTimelineService employeeTimelineService;

	JobServiceImpl jobService;

	@BeforeEach
	void setup() {
		jobService = Mockito.spy(new JobServiceImpl(jobFamilyDao, jobTitleDao, jobFamilyTitleDao, employeeDao,
				peopleMapper, messageUtil, employeeTimelineService));
	}

	@Test
	void updateTeam_returnsHttpOk() {
		JobFamily jobFamily = getJobFamily();

		when(jobFamilyDao.findById(1L)).thenReturn(Optional.of(jobFamily));

		ResponseEntityDto response = jobService.getJobFamilyById(1L);
		Assertions.assertEquals("successful", response.getStatus());
	}

	private JobFamily getJobFamily() {
		JobFamily jobFamily = new JobFamily();
		jobFamily.setJobFamilyId(1L);
		jobFamily.setActive(true);
		jobFamily.setName("Engineering");
		JobTitle jobTitle = new JobTitle();
		jobTitle.setJobTitleId(1L);
		jobTitle.setName("Senior");
		Set<JobTitle> jobTitleList = new HashSet<>();
		jobTitleList.add(jobTitle);
		jobFamily.setJobTitles(jobTitleList);

		return jobFamily;
	}

}
