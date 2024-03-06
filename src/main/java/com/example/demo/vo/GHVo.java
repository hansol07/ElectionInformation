package com.example.demo.vo;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GHVo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	private int 언제;
	private String 선거;
	private String 시도;
	private String sggName;
	private int sunsu;
	private int tusu;
	@ElementCollection
	private List<String> jungdang;
	@ElementCollection
	private List<String> hubo;
	@ElementCollection
	private List<String> dpSu;
	private int mutusu;
	private int gigwonsu;
	
	
	
	
	
}
