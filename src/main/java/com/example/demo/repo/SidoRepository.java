package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.vo.SidoVo;

@Repository
public interface SidoRepository extends JpaRepository<SidoVo, String>{

}
