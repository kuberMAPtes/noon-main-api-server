package com.kube.noon.member.repository;

import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.enums.RelationshipType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRelationshipJpaRepository extends JpaRepository<MemberRelationship, Integer>,MemberRelationshipJpaRepositoryQuery{

    void deleteByToMember_MemberIdAndFromMember_MemberIdAndRelationshipType(String toId, String fromId, RelationshipType relationshipType);

}
