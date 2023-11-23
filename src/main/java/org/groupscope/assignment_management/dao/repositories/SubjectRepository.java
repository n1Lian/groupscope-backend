package org.groupscope.assignment_management.dao.repositories;

import org.groupscope.assignment_management.entity.Subject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends CrudRepository<Subject, Long> {

    Subject getSubjectByName(String name);

    Subject getSubjectByNameAndGroup_Id(String name, Long group_id);

    List<Subject> findAllByGroup_Name(String groupName);

    void deleteSubjectByName(String name);

    void deleteSubjectByNameAndGroup_Id(String name, Long group_id);
}
