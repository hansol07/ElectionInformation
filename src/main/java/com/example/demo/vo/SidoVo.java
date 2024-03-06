package com.example.demo.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "sido")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SidoVo {

	 @Column(name = "sido_code")
	 @Id
	private String sidoCode;
	 @Column(name = "sido_name")
	private String sidoName;
}
