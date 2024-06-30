package com.kube.noon.member.repository;

import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.enums.RelationshipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberRelationshipJpaRepository extends JpaRepository<MemberRelationship, Integer>, MemberRelationshipJpaRepositoryQuery {

    Optional<MemberRelationship> findByFromMember_MemberIdAndToMember_MemberIdAndRelationshipType(String fromId, String toId, RelationshipType relationshipType);
    Optional<MemberRelationship> findByFromMember_MemberIdAndToMember_MemberIdAndRelationshipTypeAndActivated(String fromId, String toId, RelationshipType relationshipType, boolean activated);

    void deleteByFromMember_MemberIdAndToMember_MemberId(String fromId, String toId);
}
