package com.kube.noon.member.repository;

import com.kube.noon.member.domain.MemberRelationship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRelationshipJpaRepository extends JpaRepository<MemberRelationship, Integer>,MemberRelationshipJpaRepositoryQuery{

    Optional<MemberRelationship> findByFromMember_MemberIdAndToMember_MemberId(String fromId, String toId);
    void deleteByFromMember_MemberIdAndToMember_MemberId(String fromId, String toId);
}
