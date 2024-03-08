package com.example.demo.vo;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SJVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	private int 언제;
	private String 선거;
	private String 시도;
	private String sggName;
	private int sunsu;
	private String 일차;

	@ElementCollection
	private List<String> dpSu;

}
