package com.kube.noon.chat.serviceImpl;

import com.kube.noon.chat.domain.ChatApply;
import com.kube.noon.chat.dto.ChatApplyDto;
import com.kube.noon.chat.repository.ChatApplyRepository;
import com.kube.noon.chat.service.ChatMatchingService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("chatMatchingService")
public class ChatMatchingServiceImpl implements ChatMatchingService {

    @Autowired
    private ChatApplyRepository chatApplyRepository;

    @Override
    public String applyChatting(ChatApplyDto chatApplyDto) throws Exception {

        //entitiy 생성
        ChatApply chatApply = new ChatApply();
        chatApply.setApplicant(chatApplyDto.getFromId());
        chatApply.setRespondent(chatApplyDto.getToId());
        chatApply.setApplyMessage(chatApplyDto.getApplyMessage());
        chatApply.setActivated(false);
        System.out.println(chatApplyRepository.save(chatApply));

        return "";
    }

    @Override
    public ChatApplyDto getChatApply(int chatApplyId) throws Exception {

        ChatApply chatApplyResponse = chatApplyRepository.findByChatApplyId(chatApplyId);;
        ChatApplyDto chatApplyDto = new ChatApplyDto();
        chatApplyDto.setChatApplyId(chatApplyResponse.getChatApplyId());
        chatApplyDto.setFromId(chatApplyResponse.getApplicant());
        chatApplyDto.setToId(chatApplyResponse.getRespondent());
        chatApplyDto.setApplyMessage(chatApplyResponse.getApplyMessage());

        return chatApplyDto;
    }

    @Override
    public ChatApplyDto acceptChatting(ChatApplyDto chatApplyDto) throws Exception {

        // chatApplyId로 레코드를 찾아 accepted = true 로 세팅후 chatApplyDto 반환
        Optional<ChatApply> optionalChatApply = chatApplyRepository.findById(chatApplyDto.getChatApplyId());
        if (optionalChatApply.isPresent()) {
            ChatApply chatApply = optionalChatApply.get();
            chatApply.setActivated(true);
            
            ChatApply resultChatApply = chatApplyRepository.save(chatApply);
            return convertToChatApplyDto(resultChatApply);
        }else{
            throw new RuntimeException("ChatApply not found with id: " + chatApplyDto.getChatApplyId());
        }
    }

    @Override
    public ChatApplyDto rejectChatting(ChatApplyDto chatApplyDto) throws Exception {

        // chatApplyId로 레코드를 찾아 accepted 로 세팅
        Optional<ChatApply> optionalChatApply = chatApplyRepository.findById(chatApplyDto.getChatApplyId());
        if (optionalChatApply.isPresent()) {
            ChatApply chatApply = optionalChatApply.get();
            chatApply.setActivated(true);
            chatApply.setRejectMessage(chatApplyDto.getRejectMessage());

            ChatApply resultChatApply = chatApplyRepository.save(chatApply);
            System.out.println("        🦐[ServiceImpl rejectChatting] 처리한 resultChatApply DTO => " + chatApplyDto);
            return convertToChatApplyDto(resultChatApply);
        }else{
            throw new RuntimeException("ChatApply not found with id: " + chatApplyDto.getChatApplyId());
        }
    }

    @Override
    public List<ChatApplyDto> newChatApplyList(String memberId) throws Exception {

        // respondent(=memberId) 에 해당하는 chatApply 목록을 가져옴
        List<ChatApply> chatApplies = chatApplyRepository.findByRespondentAndActivatedFalse(memberId);

        System.out.println("        🦐[ServiceImpl] 새 대화신청목록  조회한 것 chatApplies => " + chatApplies);

        List<ChatApplyDto> chatApplyDtos = new ArrayList<>();

        if (!chatApplies.isEmpty()) {

            for (ChatApply chatApply : chatApplies) {
                ChatApplyDto chatApplyDto = new ChatApplyDto();

                // 채팅 신청자와 채팅 메세지를 가져와 뿌려줄 것임
                chatApplyDto.setFromId(chatApply.getApplicant());
                chatApplyDto.setApplyMessage(chatApply.getApplyMessage());
                chatApplyDto.setChatApplyId(chatApply.getChatApplyId());
                chatApplyDto.setActivated(chatApply.getActivated());
                chatApplyDtos.add(chatApplyDto);

            }
        }

        System.out.println("        🦐[ServiceImpl] 새 대화 신청목록 dto => " + chatApplyDtos);

        return chatApplyDtos;
    }



    @NotNull
    private ChatApplyDto convertToChatApplyDto(ChatApply resultChatApply) {
        ChatApplyDto resultChatApplyDto = new ChatApplyDto();
        resultChatApplyDto.setChatApplyId(resultChatApply.getChatApplyId());
        resultChatApplyDto.setFromId(resultChatApply.getApplicant());
        resultChatApplyDto.setToId(resultChatApply.getRespondent());
        resultChatApplyDto.setApplyMessage(resultChatApply.getApplyMessage());
        resultChatApplyDto.setRejectMessage(resultChatApply.getRejectMessage());
        resultChatApplyDto.setActivated(resultChatApply.getActivated());

        return resultChatApplyDto;
    }
}



