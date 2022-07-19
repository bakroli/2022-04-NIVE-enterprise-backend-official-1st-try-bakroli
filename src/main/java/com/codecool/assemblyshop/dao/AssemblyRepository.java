package com.codecool.assemblyshop.dao;

import com.codecool.assemblyshop.model.Assembly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssemblyRepository extends JpaRepository<Assembly, Long> {
}
