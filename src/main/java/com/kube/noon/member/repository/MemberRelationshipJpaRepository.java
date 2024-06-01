package com.kube.noon.member.repository;

import com.kube.noon.member.domain.MemberRelationship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRelationshipJpaRepository extends JpaRepository<MemberRelationship, Integer> {
}
