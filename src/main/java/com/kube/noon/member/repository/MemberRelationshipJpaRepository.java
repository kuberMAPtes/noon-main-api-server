package com.kube.noon.member.repository;

import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.enums.RelationshipType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRelationshipJpaRepository extends JpaRepository<MemberRelationship, Integer>,MemberRelationshipJpaRepositoryQuery{

    void deleteByToMember_MemberIdAndFromMember_MemberIdAndRelationshipType(String toId, String fromId, RelationshipType relationshipType);
    Optional<MemberRelationship> findByToMember_MemberIdAndFromMember_MemberIdAndRelationshipType(String toId, String fromId, RelationshipType relationshipType);
}
