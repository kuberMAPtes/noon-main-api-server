package com.kube.noon.customersupport.controller;
import com.kube.noon.building.dto.BuildingDto;
import com.kube.noon.building.dto.BuildingZzimDto;
import com.kube.noon.building.service.BuildingProfileService;
import com.kube.noon.chat.service.ChatroomService;
import com.kube.noon.customersupport.domain.ChatbotConversation;
import com.kube.noon.customersupport.domain.Report;
import com.kube.noon.customersupport.dto.notice.NoticeDto;
import com.kube.noon.customersupport.dto.report.ReportDto;
import com.kube.noon.customersupport.dto.report.ReportProcessingDto;
import com.kube.noon.customersupport.service.ChatbotService;
import com.kube.noon.customersupport.service.CustomerSupportService;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.dto.FeedAttachmentDto;
import com.kube.noon.feed.dto.FeedDto;
import com.kube.noon.feed.dto.FeedSummaryDto;
import com.kube.noon.feed.service.FeedService;
import com.kube.noon.member.dto.MemberRelationshipDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/customersupport")
@RequiredArgsConstructor
public class CustomerSupportRestController {

    ///Field
    private final CustomerSupportService customerSupportService;
    private final ChatbotService chatbotService;
    private final FeedService feedService;


    /**
     * 신고하기
     * @return 작성한 신고 정보
     */
    @PostMapping("/addReport")
    public ReportDto addReport(@RequestBody ReportDto reportDto) {

        log.info("reportDto={}",reportDto);

        reportDto.setReportedTime(LocalDateTime.now());
        return customerSupportService.addReport(reportDto);
    }

    /**
     * 신고 처리하기
     * @return 신고 처리한 신고 정보
     */
    @PostMapping("/updateReport")
    public ReportProcessingDto updateReport(@RequestBody ReportProcessingDto reportProcessingDto) {

        log.info("reportProcessingDto={}",reportProcessingDto);

        ///////////////////////////////////////////처리되었다고 문자 알림 추가.

        reportProcessingDto.setReportedTime(LocalDateTime.now());
        return customerSupportService.updateReport(reportProcessingDto);
    }

    /**
     * 공지(피드) 작성하기
     * @param feedDto 공지 타이틀, 내용 등이 담긴 Dto
     * @return 작성한 공지 정보
     */
    @PostMapping("/addNotice")
    public FeedDto addNotice(@RequestBody FeedDto feedDto ) {

        log.info("feedDto={}",feedDto);

        feedDto.setWrittenTime(LocalDateTime.now());
        log.info("feedDto={}",feedDto.toString());
        int noticeId = feedService.addFeed(feedDto);

        return feedService.getFeedById(noticeId);
    }

    /**
     * 공지 삭제하기
     * @param feedDto 삭제할 공지 아이디 등이 담긴 Dto
     * @return 삭제된 공지 정보(activated=false, 기존 정보는 그대로 보관)
     */
    @PostMapping("/deleteNotice")
    public FeedDto deleteNotice(@RequestBody FeedDto feedDto ) {

        log.info("feedDto={}",feedDto);

        int noticeId = feedService.deleteFeed(feedDto);
        return feedService.getFeedById(noticeId);
    }


    /**
     * 신고 상세보기
     */
    @GetMapping("/getReportByReportId")
    public ReportDto getReportByReportId(@RequestParam("reportId") int reportId) {

        log.info("reportId={}",reportId);

        return customerSupportService.getReportByReportId(reportId);
    }

    /**
     * 신고 목록 보기
     */
    @GetMapping("/getReportList")
    public List<ReportDto> getReportList(@RequestParam(required = false) Integer pageNumber) {

        log.info("pageNumber={}",pageNumber);

        if(pageNumber==null){
            return customerSupportService.getReportList();
        }
        return customerSupportService.getReportListByPageable(pageNumber);

    }

    /**
     * 공지 상세보기
     */
    @GetMapping("/getNoticeByNoticeId")
    public FeedDto getNoticeByNoticeId(int noticeId) {

        log.info("noticeId={}",noticeId);

        return feedService.getFeedById(noticeId);
    }
    

    /**
     * 공지 목록 보기
     */
    @GetMapping("/getNoticeList")
    public List<NoticeDto> getNoticeList(@RequestParam(required = false) Integer pageNumber) {

        log.info("pageNumber={}",pageNumber);

        if(pageNumber==null){
            return customerSupportService.getNoticeList();
        }
        return customerSupportService.getNoticeListByPageable(pageNumber);

    }


    /**
     * 챗봇 질문하기
     */
    @PostMapping("/getChatbotConversation")
    public ChatbotConversation getChatbotConversation(@RequestBody ChatbotConversation conversation) {

        log.info("conversation={}",conversation);

        return chatbotService.ask(conversation.getUserQuestion());

    }



    /**
     * 이미지 목록 보기 ==> 도엽님의 서비스 완성되면 적용
     */
    /*
    @GetMapping("/getFeedImageList")
    public List<ReportDto> getFeedImageList(@RequestParam(required = false) Integer pageNumber) {

        log.info("pageNumber={}",pageNumber);


        if(pageNumber==null){
            return feedService.getFeedImageList();
        }
        return feedService.getFeedImageListByPageable(pageNumber);

    }
    */


    /**
     * 유해 사진 목록 보기
     */
    @GetMapping("/getFilteredListByAI")
    public List<FeedAttachmentDto> getFilteredListByAI(@RequestParam(required = false) Integer pageNumber) {

        log.info("pageNumber={}",pageNumber);

        if(pageNumber==null){
            return customerSupportService.getFilteredListByAI();
        }
        return customerSupportService.getFilteredListByAIAndPageable(pageNumber);

    }


    /**
     * 이미지 상세보기
     */
    @GetMapping("/getImageByAttatchmentId")
    public FeedAttachmentDto getImageByAttatchmentId(int attchmentId) {

        log.info("attchmentId={}",attchmentId);

        return customerSupportService.getImageByAttatchmentId(attchmentId);
    }


    /**
     * 블러 처리하기
     * @return 블러된 이미지 URL이 포함된 피드 첨부파일 정보
     */
    @PostMapping("/addBlurFile")
    public FeedAttachmentDto addBlurFile(@RequestBody FeedAttachmentDto feedAttachmentDto) {

        log.info("feedAttachmentDto={}",feedAttachmentDto);

        try {
            return customerSupportService.addBluredImage(customerSupportService.getImageByAttatchmentId(feedAttachmentDto.getAttachmentId()) );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 피드 삭제하기
     * @param feedDto 유해 첨부파일이 포함된 피드 아이디가 담긴 Dto
     * @return 삭제(activated=false) 처리된 피드 정보
     */
    @Deprecated //==> 피드 컨트롤러로 대체될 수 있음.
    @PostMapping("/deleteBadFeed")
    public FeedDto deleteBadFeed(@RequestBody FeedDto feedDto) {

        log.info("feedDto={}",feedDto);
        
        int feedId = feedService.deleteFeed(feedDto);
        return feedService.getFeedById(feedId);
    }

}
