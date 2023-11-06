package com.alexandervov.repository;

import com.alexandervov.entity.Application;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ApplicationRepository extends CrudRepository<Application, Integer> {

    Optional<Application> findApplicationByName(String name);

    List<Application> findApplicationsByCategoryId(int id);
}
