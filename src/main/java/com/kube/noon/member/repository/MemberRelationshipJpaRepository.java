package com.kube.noon.member.repository;

import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.enums.RelationshipType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRelationshipJpaRepository extends JpaRepository<MemberRelationship, Integer> {

    //#member_1이 차단한 사람,member_1의 팔로잉 리스트
    // SELECT * FROM member_relationship WHERE from_id='member_1' AND relationship_type='BLOCK';
    List<MemberRelationship> findByFromIdAndRelationshipTypeAndActivated(String fromId, RelationshipType relationshipType, boolean activated);
    //#member_1을  차단한 사람,member_1의 팔로워 리스트
    // SELECT * FROM member_relationship WHERE to_id='member_1' AND relationship_type='FOLLOW';
    List<MemberRelationship> findByToIdAndRelationshipTypeAndActivated(String toId, RelationshipType relationshipType, boolean activated);

}
