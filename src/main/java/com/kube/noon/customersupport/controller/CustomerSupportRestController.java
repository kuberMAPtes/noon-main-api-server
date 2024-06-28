package com.kube.noon.customersupport.controller;
import com.kube.noon.customersupport.domain.ChatbotConversation;
import com.kube.noon.customersupport.dto.notice.NoticeDto;
import com.kube.noon.customersupport.dto.report.ReportDto;
import com.kube.noon.customersupport.dto.report.ReportProcessingDto;
import com.kube.noon.customersupport.service.ChatbotService;
import com.kube.noon.customersupport.service.CustomerSupportService;
import com.kube.noon.feed.dto.FeedAttachmentDto;
import com.kube.noon.feed.dto.FeedDto;
import com.kube.noon.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     * @param reportProcessingDto 관리자가 지정한 (다정수치감소량, 계정잠금일수, 신고상태) 및 신고 아이디가 반영된 신고 처리 정보
     * @return 신고 처리한 신고 정보
     */
    @PostMapping("/updateReport")
    public ReportProcessingDto updateReport(@RequestBody ReportProcessingDto reportProcessingDto) {

        log.info("reportProcessingDto={}",reportProcessingDto);

        return customerSupportService.updateReport(reportProcessingDto);
    }

    /**
     * 텍스트 에디터에 이미지 업로드
     * 글 작성 시 중간에 이미지를 삽입하면 즉시 오브젝트 스토리지 저장, 클라이언트에서 활용할 수 있게 URL제공
     * @param attachment 사용자가 추가한 이미지
     * @return 오브젝트 스토리지에 저장한 이미지의 URL
     */
    @PostMapping("/uploadAttachment")
    public String uploadImage(@RequestParam("attachment") MultipartFile attachment) {

        return customerSupportService.addFile(attachment);
        
    }

    /**
     * 공지사항 작성
     * @param writerId 작성자 아이디
     * @param title 공지 제목
     * @param feedText 공지 내용 (첨부파일 Object Storage url이 포함되어있다.)
     * @return
     * @throws IOException
     */
    @PostMapping("/addNotice")
    public FeedDto addNotice(@RequestParam("writerId") String writerId,
                                       @RequestParam("title") String title,
                                       @RequestParam("feedText") String feedText) throws IOException {

        return customerSupportService.addNotice(writerId, title, feedText);

    }

    /**
     * 공지 삭제하기
     * @param feedDto 삭제할 공지 아이디 등이 담긴 Dto
     * @return 삭제된 공지 정보(activated=false, 기존 정보는 그대로 보관)
     */
    @PostMapping("/deleteNotice")
    public FeedDto deleteNotice(@RequestBody FeedDto feedDto ) {

        log.info("feedDto={}",feedDto);

        int noticeId = feedService.deleteFeed(feedDto.getFeedId());
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
    public FeedDto getNoticeByNoticeId(@RequestParam int noticeId) {

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
     * 이미지 목록 보기
     */
    @GetMapping("/getImageList")
    public List<FeedAttachmentDto> getImageList(@RequestParam(required = false) Integer pageNumber) {

        log.info("pageNumber={}",pageNumber);

        if(pageNumber==null){
            log.info("이미지목록={}",customerSupportService.getImageList());
            return customerSupportService.getImageList();
        }
        return customerSupportService.getImageListByPageable(pageNumber);
    }


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
    public FeedAttachmentDto addBlurFile(@RequestBody FeedAttachmentDto feedAttachmentDto,  @RequestParam int blurIntensity) {

        log.info("blurIntensity={}",blurIntensity);
        log.info("feedAttachmentDto={}",feedAttachmentDto);

        try {
            return customerSupportService.addBluredImage(customerSupportService.getImageByAttatchmentId(feedAttachmentDto.getAttachmentId()), blurIntensity );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 블러 취소하기
     * @return 블러가 취소되어 블러 URL이 NULL인 첨부파일 정보
     */
    @PostMapping("/deleteBlurFile")
    public FeedAttachmentDto deleteBlurFile(@RequestBody FeedAttachmentDto feedAttachmentDto) {

        log.info("feedAttachmentDto={}",feedAttachmentDto);

        try {
            return customerSupportService.deleteBluredImage(customerSupportService.getImageByAttatchmentId(feedAttachmentDto.getAttachmentId()) );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 유해피드 삭제 및 작성자 계정잠금일수 연장
     *
     * @param feedDto 유해 첨부파일이 포함된 피드 아이디가 담긴 Dto
     * @return 삭제(activated=false) 처리된 피드 정보
     */
    @PostMapping("/deleteBadFeed")
    public FeedDto deleteBadFeed(@RequestBody FeedDto feedDto, @RequestParam String reqUnlockDuration) {

        log.info("feedDto={}",feedDto);

        return customerSupportService.deleteBadFeed(feedDto, reqUnlockDuration);
    }

}
