package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.vo.TPVO;
@Repository
public interface TPRepository extends JpaRepository<TPVO,Long>{

}
