package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.vo.SJVO;

@Repository
public interface SJRepository extends JpaRepository<SJVO, Long>{

}
