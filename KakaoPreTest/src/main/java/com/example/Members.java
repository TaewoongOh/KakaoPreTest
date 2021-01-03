package com.example;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "members")
public class Members implements Serializable {
	private static final long serialVersionUID = 8124227299932339862L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "m_num")
	private long mNum;
	@Column(name = "name")
	private String name;
	@Column(name = "age")
	private int age;
}
