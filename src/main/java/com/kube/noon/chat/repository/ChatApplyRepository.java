package com.kube.noon.chat.repository;

import com.kube.noon.chat.domain.ChatApply;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatApplyRepository extends JpaRepository<ChatApply,Integer> {
    List<ChatApply> findByRespondentAndActivatedFalse(String respondent);
    ChatApply findByChatApplyId(@Param("chatApplyId") Integer chatApplyId);

    /**
     * ChatApplyDto 의 chatApplyId 를 기반으로 레코드를 찾아 해당 레코드의 accepted 를 true 로 변경
     * @param chatApplyId
     * @return chatApply
     */
    @Modifying
    @Transactional
    @Query("UPDATE ChatApply c SET c.activated = true WHERE c.chatApplyId = :chatApplyId")
    int acceptChatApply(@Param("chatApplyId") Integer chatApplyId);

}
