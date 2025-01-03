package com.skapp.community.peopleplanner.repository.impl;

import com.skapp.community.common.model.User;
import com.skapp.community.common.model.User_;
import com.skapp.community.peopleplanner.model.Employee;
import com.skapp.community.peopleplanner.model.EmployeeTeam;
import com.skapp.community.peopleplanner.model.EmployeeTeam_;
import com.skapp.community.peopleplanner.model.Employee_;
import com.skapp.community.peopleplanner.model.JobFamily;
import com.skapp.community.peopleplanner.model.JobFamilyTitle;
import com.skapp.community.peopleplanner.model.JobFamilyTitle_;
import com.skapp.community.peopleplanner.model.JobFamily_;
import com.skapp.community.peopleplanner.model.JobTitle;
import com.skapp.community.peopleplanner.model.JobTitle_;
import com.skapp.community.peopleplanner.model.Team_;
import com.skapp.community.peopleplanner.payload.response.JobFamilyOverviewDto;
import com.skapp.community.peopleplanner.payload.response.JobTitleOverviewDto;
import com.skapp.community.peopleplanner.repository.JobFamilyRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JobFamilyRepositoryImpl implements JobFamilyRepository {

	@NonNull
	private final EntityManager entityManager;

	@Override
	public List<JobFamily> getJobFamiliesByEmployeeCount() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<JobFamily> criteriaQuery = criteriaBuilder.createQuery(JobFamily.class);
		Root<JobFamily> root = criteriaQuery.from(JobFamily.class);
		Join<JobFamily, Employee> employeeJoin = root.join(JobFamily_.EMPLOYEES, JoinType.LEFT);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(criteriaBuilder.equal(root.get(JobFamily_.isActive), true));

		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		criteriaQuery.where(predArray);
		criteriaQuery.groupBy(root.get(JobFamily_.JOB_FAMILY_ID));
		criteriaQuery.orderBy(criteriaBuilder
			.desc(criteriaBuilder.coalesce(criteriaBuilder.count(employeeJoin.get(Employee_.EMPLOYEE_ID)), 0)));
		TypedQuery<JobFamily> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public List<JobFamilyOverviewDto> getJobFamilyOverview(List<Long> teamIds) {
		if (teamIds != null && teamIds.isEmpty()) {
			return Collections.emptyList();
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<JobFamilyOverviewDto> criteriaQuery = criteriaBuilder.createQuery(JobFamilyOverviewDto.class);
		Root<JobFamily> jobFamilyRoot = criteriaQuery.from(JobFamily.class);

		Join<JobFamily, Employee> employeeJoin = jobFamilyRoot.join(JobFamily_.employees, JoinType.LEFT);
		Join<Employee, User> userJoin = employeeJoin.join(Employee_.user, JoinType.LEFT);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(criteriaBuilder.equal(jobFamilyRoot.get(JobFamily_.isActive), true));

		if (teamIds != null && !teamIds.contains(-1L)) {
			Join<Employee, EmployeeTeam> employeeTeamJoin = employeeJoin.join(Employee_.teams, JoinType.LEFT);
			predicates.add(employeeTeamJoin.get(EmployeeTeam_.team).get(Team_.teamId).in(teamIds));
		}

		predicates.add(criteriaBuilder.or(criteriaBuilder.isNull(userJoin.get(User_.isActive)),
				criteriaBuilder.equal(userJoin.get(User_.isActive), true)));

		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.groupBy(jobFamilyRoot.get(JobFamily_.jobFamilyId), jobFamilyRoot.get(JobFamily_.name));
		criteriaQuery.multiselect(jobFamilyRoot.get(JobFamily_.jobFamilyId), jobFamilyRoot.get(JobFamily_.name),
				criteriaBuilder.count(employeeJoin));

		TypedQuery<JobFamilyOverviewDto> query = entityManager.createQuery(criteriaQuery);
		return query.getResultList();
	}

	public List<JobTitleOverviewDto> getJobTitlesByJobFamily(Long jobFamilyId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<JobTitleOverviewDto> criteriaQuery = criteriaBuilder.createQuery(JobTitleOverviewDto.class);

		Root<JobFamilyTitle> jobFamilyTitleRoot = criteriaQuery.from(JobFamilyTitle.class);
		Join<JobFamilyTitle, JobTitle> jobTitleJoin = jobFamilyTitleRoot.join(JobFamilyTitle_.jobTitle);
		Join<JobTitle, Employee> employeeJoin = jobTitleJoin.join(JobTitle_.employees, JoinType.LEFT);

		criteriaQuery
			.select(criteriaBuilder.construct(JobTitleOverviewDto.class, jobTitleJoin.get(JobTitle_.jobTitleId),
					jobTitleJoin.get(JobTitle_.name), criteriaBuilder.count(employeeJoin)));

		criteriaQuery.where(criteriaBuilder
			.equal(jobFamilyTitleRoot.get(JobFamilyTitle_.jobFamily).get(JobFamily_.jobFamilyId), jobFamilyId));
		criteriaQuery.groupBy(jobTitleJoin.get(JobTitle_.jobTitleId), jobTitleJoin.get(JobTitle_.name));

		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	@Override
	public JobFamily getJobFamilyById(Long jobFamilyId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<JobFamily> criteriaQuery = criteriaBuilder.createQuery(JobFamily.class);
		Root<JobFamily> root = criteriaQuery.from(JobFamily.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(criteriaBuilder.equal(root.get(JobFamily_.jobFamilyId), jobFamilyId));
		predicates.add(criteriaBuilder.equal(root.get(JobFamily_.isActive), true));

		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
	}

	@Override
	public JobFamily getJobFamilyByName(String jobFamilyName) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<JobFamily> criteriaQuery = criteriaBuilder.createQuery(JobFamily.class);
		Root<JobFamily> root = criteriaQuery.from(JobFamily.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(criteriaBuilder.equal(root.get(JobFamily_.name), jobFamilyName));
		predicates.add(criteriaBuilder.equal(root.get(JobFamily_.isActive), true));

		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
	}

	@Override
	public JobTitle getJobTitleById(Long jobTitleId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<JobTitle> criteriaQuery = criteriaBuilder.createQuery(JobTitle.class);
		Root<JobTitle> root = criteriaQuery.from(JobTitle.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(criteriaBuilder.equal(root.get(JobTitle_.jobTitleId), jobTitleId));
		predicates.add(criteriaBuilder.equal(root.get(JobTitle_.isActive), true));

		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
	}

	@Override
	public JobTitle getJobTitleByName(String jobTitleName) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<JobTitle> criteriaQuery = criteriaBuilder.createQuery(JobTitle.class);
		Root<JobTitle> root = criteriaQuery.from(JobTitle.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(criteriaBuilder.equal(root.get(JobTitle_.name), jobTitleName));
		predicates.add(criteriaBuilder.equal(root.get(JobTitle_.isActive), true));

		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
	}

}
