package com.example.vo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface MemberRepository extends CrudRepository<MemberVo, String> {
	List<MemberVo> findByOrignid(String originid);
}
