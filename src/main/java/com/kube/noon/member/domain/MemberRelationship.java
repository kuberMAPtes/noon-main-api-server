package com.kube.noon.member.domain;


import com.kube.noon.member.enums.RelationshipType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * //    내가 내 프로필 볼 때 : fromId = 현준 으로 객체를 찾으면 팔로잉리스트
 * //    내가 내 프로필 볼 때 : toId = 현준 으로 객체를 찾으면 팔로워리스트
 * //    내가 타인의 프로필 볼 때 : fromId = 철수로 객체를 찾으면 철수의 팔로잉 리스트
 */
@Entity
@Table(name = "member_relationship", indexes = {
        @Index(name = "idx_from_id", columnList = "from_id"),
        @Index(name = "idx_to_id", columnList = "to_id")
})
@Data // Lombok 어노테이션으로 getter, setter, toString, equals, hashCode 자동 생성
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 포함하는 생성자 생성
public class MemberRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_relationship_id")
    private int memberRelationshipId;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false)
    private RelationshipType relationshipType;

    @Column(name = "activated", nullable = false)
    private boolean activated = true;

    @ManyToOne
    @JoinColumn(name = "from_id", referencedColumnName = "member_id")
    private Member fromMember;

    @ManyToOne
    @JoinColumn(name = "to_id", referencedColumnName = "member_id")
    private Member toMember;


}
