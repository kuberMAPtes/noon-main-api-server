package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Integer> {
}
