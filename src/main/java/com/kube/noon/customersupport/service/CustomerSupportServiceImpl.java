package com.kube.noon.customersupport.service;

import com.kube.noon.building.repository.BuildingProfileRepository;
import com.kube.noon.common.FeedCategory;
import com.kube.noon.common.FileType;
import com.kube.noon.common.ObjectStorageAWS3S;
import com.kube.noon.common.PublicRange;
import com.kube.noon.customersupport.domain.Report;
import com.kube.noon.customersupport.dto.notice.NoticeDto;
import com.kube.noon.customersupport.dto.report.ReportDto;
import com.kube.noon.customersupport.dto.report.ReportProcessingDto;
import com.kube.noon.customersupport.enums.UnlockDuration;
import com.kube.noon.customersupport.repository.AttachmentFilteringRepository;
import com.kube.noon.customersupport.repository.NoticeRepository;
import com.kube.noon.customersupport.repository.ReportRepository;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.FeedAttachment;
import com.kube.noon.feed.dto.FeedAttachmentDto;
import com.kube.noon.feed.dto.FeedDto;
import com.kube.noon.feed.repository.FeedAttachmentRepository;
import com.kube.noon.feed.repository.FeedRepository;
import com.kube.noon.feed.service.FeedService;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.member.UpdateMemberDajungScoreDto;
import com.kube.noon.member.dto.member.UpdateMemberDto;
import com.kube.noon.member.repository.impl.MemberRepositoryImpl;
import com.kube.noon.member.service.MemberService;
import com.kube.noon.notification.domain.NotificationType;
import com.kube.noon.notification.dto.NotificationDto;
import com.kube.noon.notification.service.NotificationServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 신고, 공지사항, 유해사진필터링 서비스를 제공하는 구현체이다.
 *
 * @author 허예지
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerSupportServiceImpl implements CustomerSupportService{


    ///Field
    private final ReportRepository reportRepository;
    private final MemberService memberService;
    private final AttachmentFilteringRepository attachmentFilteringRepository;
    private final FeedAttachmentRepository feedAttachmentRepository;
    private final NoticeRepository noticeRepository;
    private final NotificationServiceImpl notificationService;
    private final FeedService feedService;
    private final ObjectStorageAWS3S objectStorageAWS3S;
    private final FeedRepository feedRepository;
    private final MemberRepositoryImpl memberRepositoryImpl;
    private final BuildingProfileRepository buildingProfileRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;


    /**
     * 공지사항에 딸린 멀티파트 파일 저장, 저장된 url반환(위지윅 구현을 위함)
     */
    private Map<String , String> tmpImageStorage = new HashMap<>(); // 임시 이미지 저장소<uploadedUrl,FileType>
    public String addFile(MultipartFile attachment){

        if (attachment != null) {
                if (!attachment.isEmpty()) {
                    try {

                        String fileName = attachment.getOriginalFilename();
                        File dest = new File(uploadDir + File.separator + fileName); // TODO: 일단 로컬에 저장. 추후 저장 하지 않고 Object Storage에 바로 저장할 계획

                        // 필요한 디렉토리가 없으면 생성
                        dest.getParentFile().mkdirs();
                        // 파일 저장
                        attachment.transferTo(dest);

                        // ObjectStorage업로드
                        String uploadedFileUrl = objectStorageAWS3S.uploadNoticeFile(dest.getAbsolutePath());
                        log.info("Object Storage uploaded URL={}", uploadedFileUrl);
                        
                        //공지 최종 등록 전까지 첨부파일 임시 저장
                        tmpImageStorage.put(uploadedFileUrl, attachment.getContentType());
                        
                        return uploadedFileUrl;

                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException("Failed to store file", e);
                    }
                }///end of if
        }///end of if

        return null;

    }/// addFile

    @Override
    public FeedDto addNotice(String writerId, String title, String text) throws IOException {

        log.info("writerId: {}, Title: {}, FeedText: {}", writerId, title, text);

        //1. 저장할 공지사항 setting
        Feed feed = new Feed();

        Integer maxFeedId = feedRepository.findMaxId();
        if (maxFeedId == null) maxFeedId = 0;
        Integer newFeedId = maxFeedId + 1;
        feed.setFeedId(newFeedId);

        feed.setWriter(memberRepositoryImpl.findMemberById(writerId).orElseThrow());
        feed.setTitle(title);
        feed.setFeedText(text);
        feed.setWrittenTime(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime());
        feed.setFeedCategory(FeedCategory.NOTICE);
        feed.setPublicRange(PublicRange.PUBLIC);
        feed.setViewCnt(0L);
        feed.setActivated(true);
        feed.setBuilding(buildingProfileRepository.findById(10000).orElseThrow());

        Feed savedFeed = feedRepository.save(feed);

        //2. 저장할 공지사항 첨부파일들 setting
        for (Map.Entry<String, String> entry : tmpImageStorage.entrySet()) {

            String uploadedFileUrl = entry.getKey();
            String contentType = entry.getValue();
            FeedAttachment feedAttachment = new FeedAttachment();

            Integer maxId = feedAttachmentRepository.findMaxId();
            if (maxId == null) maxId = 0;
            Integer newId = ++maxId;
            feedAttachment.setAttachmentId(newId);
            feedAttachment.setFeed(savedFeed);
            feedAttachment.setFileUrl(uploadedFileUrl);
            feedAttachment.setActivated(true);

            if (contentType.startsWith("image/")) {
                feedAttachment.setFileType(FileType.PHOTO);
            } else if (contentType.startsWith("video/")) {
                feedAttachment.setFileType(FileType.VIDEO);
            }
            log.info("Multipart type={}", feedAttachment.getFileType());

            //첨부파일 DB저장
            if (savedFeed.getAttachments() == null) {
                savedFeed.setAttachments(new ArrayList<>());
            }
            savedFeed.getAttachments().add(feedAttachment);
            log.info("feedAttachment={}",feedAttachment);
        }

        //3. 공지사항(공지+첨부파일) DB저장
        feedRepository.save(feed);
        log.info("Saved Notice={}",feed);

        tmpImageStorage.clear();


        return FeedDto.toDto(feed);

    }///end of addNotice


    /**
     * 공지사항 목록을 조회
     *
     * @return
     */
    @Override
    public List<NoticeDto> getNoticeList() {

        List<Feed> noticeList = noticeRepository.findByFeedCategoryAndActivated(FeedCategory.NOTICE, true);

        return noticeList.stream()
                .map(NoticeDto::fromEntity)
                .collect(Collectors.toList());

    }


    /**
     * 공지사항 목록을 특정 페이지만 조회
     * 페이지 사이즈(한 페이지의 목록 개수)는 메타데이터로 정의됨.
     *
     * @param pageNumber 목록을 얻으려는 페이지
     * @return
     */
    @Override
    public List<NoticeDto> getNoticeListByPageable(int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "writtenTime"));

        Page<Feed> noticePage = noticeRepository.findByFeedCategoryAndActivated(FeedCategory.NOTICE, true, pageable);

        return noticePage.getContent().stream()
                .map(NoticeDto::fromEntity)
                .collect(Collectors.toList());

    }

    @Override
    public ReportDto getLatestReport() {

        return ReportDto.fromEntity(reportRepository.findLatestReport());

    }


    /**
     * 신고 목록을 조회
     *
     * @return 신고 목록 Dto
     */
    @Override
    public List<ReportDto> getReportList() {

        List<Report> reports = reportRepository.findAll();

        return reports.stream()
                .map(ReportDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 신고 목록을 목록을 특정 페이지만 조회
     *
     * @return 신고 목록 Dto
     */
    @Override
    public List<ReportDto> getReportListByPageable(int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, 5);
        //Pageable pageable = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "writtenTime"));
        Page<Report> reportPage = reportRepository.findAll(pageable);

        return reportPage.getContent().stream()
                .map(ReportDto::fromEntity)
                .collect(Collectors.toList());
    }




    /**
     * 신고 아이디를 통해 특정 신고의 내용을 조회한다.
     *
     * @param reportId 신고아이디
     * @return 조회한 신고 Dto
     */
    @Override
    public ReportDto getReportByReportId(int reportId) {
        return ReportDto.fromEntity(reportRepository.findReportByReportId(reportId));
    }




    /**
     * ReportDto로 입력받은 신고를 신고테이블에 추가한다.
     *
     * @param reportDto 신고 내용을 담은 신고 Dto
     * @return 신고테이블에서 가장 최근에 추가된 신고(내가 추가한 신고) Dto
     */
    @Transactional
    @Override
    public ReportDto addReport(ReportDto reportDto) {

        reportRepository.save(reportDto.toEntity());

        return ReportDto.fromEntity(reportRepository.findLatestReport());
    }




    /**
     * reportId로 조회한 신고를 '처리'한다.
     * 신고 처리에 관한 설명은 NOON 참조 문서 [참조 25]
     *
     * @param reportProcessingDto 조회했던 report에서 신고상태, 신고처리텍스트가 변경되고 unlockDuration(계정잠금연장일수)이 추가된 Dto.
     *                           unlockDuration은 Enum UnlockDuration.java 참고
     *
     * @return 신고 처리되어 신고처리텍스트와 변경된 신고 상태를 포함한 Dto
     */
    @Transactional
    @Override
    public ReportProcessingDto updateReport(ReportProcessingDto reportProcessingDto) {

        Report report = reportRepository.findReportByReportId(reportProcessingDto.getReportId());
        report.setReportStatus(reportProcessingDto.getReportStatus());
        report.setProcessingText(reportProcessingDto.getProcessingText());
        log.info("처리된 신고정보={}",report);

        //신고 상태 변경, 신고처리 텍스트 추가
        reportRepository.save(report);

        //피신고자 계정 잠금 일수 연장
        updateUnlockTime(report.getReporteeId(), reportProcessingDto.getUnlockDuration());

        //피신고자 다정수치 감소
        UpdateMemberDajungScoreDto updateMemberDajungScoreDto = new UpdateMemberDajungScoreDto();
        updateMemberDajungScoreDto.setDajungScore(reportProcessingDto.getDajungScoreReduction());
        updateMemberDajungScoreDto.setMemberId(report.getReporteeId());
        log.info((updateMemberDajungScoreDto.toString()));
        memberService.updateDajungScore(updateMemberDajungScoreDto);

        //신고 처리 알림
        this.sendReportNotification(ReportProcessingDto.fromEntity(report));

        return ReportProcessingDto.fromEntity(reportRepository.findReportByReportId(report.getReportId()));

    }

    /**
     * 계정 잠금 일수 연장
     * @param memberId 잠금 연장 대상자 아이디
     * @param reqUnlockDuration 관리자가 선택한 연장 일수
     */
    public void updateUnlockTime(String memberId, String reqUnlockDuration){

        Optional<Member> reportee = memberService.findMemberById(memberId);
        log.info("계정잠금연장 대상자 정보={}", reportee.toString());
        log.info("잠금 추가 일수={}",reqUnlockDuration);

        UpdateMemberDto updateMemberDto = new UpdateMemberDto();
        BeanUtils.copyProperties(reportee.orElseThrow(), updateMemberDto);

        LocalDateTime memberUnlockTime = reportee.orElseThrow().getUnlockTime();
        int unlockDuration = UnlockDuration.valueOf(reqUnlockDuration).getDays();

        if(memberUnlockTime.isBefore(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime())){
            updateMemberDto.setUnlockTime(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime().plusDays(unlockDuration));
        }else{
            updateMemberDto.setUnlockTime(memberUnlockTime.plusDays(unlockDuration));
        }

        memberService.updateMember(updateMemberDto);

    }


    /**
     * 모든 이미지 리스트 가져오기 (페이지 X, 삭제여부구분 X)
     * @return 이미지 리스트
     */
    @Override
    public List<FeedAttachmentDto> getAllImageList() {

        return feedAttachmentRepository.findByFileType(FileType.PHOTO).stream()
                .map(FeedAttachmentDto::toDto)
                .collect(Collectors.toList());

    }

    /**
     * 활성화된 모든 이미지 리스트 가져오기 (페이지 X, 삭제여부구분 O)
     * @return 이미지 리스트
     */
    @Override
    public List<FeedAttachmentDto> getImageList() {

        log.info("임플에서 이미지목록={}", feedAttachmentRepository.findByFileTypeAndActivated(FileType.PHOTO, true).stream()
                .map(FeedAttachmentDto::toDto)
                .collect(Collectors.toList()));

        return feedAttachmentRepository.findByFileTypeAndActivated(FileType.PHOTO, true).stream()
                .map(FeedAttachmentDto::toDto)
                .collect(Collectors.toList());

    }

    /**
     * 모든 이미지 목록 페이지별로 가져오기 (페이지 O, 삭제여부구분 X)
     */
    @Override
    public List<FeedAttachmentDto> getAllImageListByPageable(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 2);
        return feedAttachmentRepository.findByFileType(FileType.PHOTO, pageable).stream()
                .map(FeedAttachmentDto::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 활성화된 모든 이미지(삭제되지 않은) 목록 페이지별로 가져오기 - (페이지 O, 삭제여부구분 O) Default
     */
    @Override
    public List<FeedAttachmentDto> getImageListByPageable(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 2);
        return feedAttachmentRepository.findByFileTypeAndActivated(FileType.PHOTO, true, pageable).stream()
                .map(FeedAttachmentDto::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 이미지 상세보기
     */
    @Override
    public FeedAttachmentDto getImageByAttatchmentId(int attachmentId) {

        return FeedAttachmentDto.toDto(feedAttachmentRepository.findByAttachmentId(attachmentId));
    }

    /**
     *  블러파일 생성, Object Storage업로드, FeedAttachment의 blurredFileUrl저장을 한다.
     *
     * @param attachmentDto 블러 파일을 생성하려는 첨부파일 Dto
     * @return Object Storage에 업로드된 블러 파일의 url이 저장된 첨부파일 Dto
     */
    @Override
    public FeedAttachmentDto addBluredImage(FeedAttachmentDto attachmentDto, int blurIntensity) throws IOException {

        log.info("블러 세기={}", blurIntensity);

        // 블러 파일 생성 및 Object Storage 저장, 저장 url 요청
        String blurredFileUrl = attachmentFilteringRepository.addBlurredFile(attachmentDto.getFileUrl(), attachmentDto.getAttachmentId(), blurIntensity);

        attachmentDto.setBlurredFileUrl(blurredFileUrl);
        feedAttachmentRepository.save(FeedAttachmentDto.toEntity(attachmentDto));

        return attachmentDto;
    }

    /**
     *  블러 url삭제
     *
     * @param attachmentDto 블러 파일을 생성하려는 첨부파일 Dto
     * @return Object Storage에 업로드된 블러 파일의 url이 저장된 첨부파일 Dto
     */
    @Override
    public FeedAttachmentDto deleteBluredImage(FeedAttachmentDto attachmentDto) throws IOException {

        attachmentDto.setBlurredFileUrl(null);
        feedAttachmentRepository.save(FeedAttachmentDto.toEntity(attachmentDto));

        return attachmentDto;
    }



    /**
     * 유해 이미지 포함된 피드 삭제 & 작성자 계정 잠금일수 연장
     * @param feedDto 유해 이미지가 포함된 feed의 정보
     * @param reqUnlockDuration 관리자가 설정한 계정 잠금 연장 일수
     * @return 삭제 처리된 피드 정보
     */
    @Override
    public FeedDto deleteBadFeed(FeedDto feedDto, String reqUnlockDuration) {
        int feedId = feedService.deleteFeed(feedDto.getFeedId());
        feedDto = feedService.getFeedById(feedId);

        //작성자 계정 잠금일수 연장
        this.updateUnlockTime(feedDto.getWriterId(), reqUnlockDuration);

        return feedDto;
    }

    /**
     * 활성화된 모든 유해 이미지(삭제되지 않은) 목록 가져오기 - (페이지 X, 삭제여부구분 O) Default
     */
    @Override
    public List<FeedAttachmentDto> getFilteredListByAI() {

        List<FeedAttachment> feedAttachmentList = feedAttachmentRepository.findByFileTypeAndActivated(FileType.PHOTO, true);

        List<FeedAttachmentDto> filteredList = attachmentFilteringRepository.findBadImageListByAI(feedAttachmentList).stream()
                .map(FeedAttachmentDto::toDto)
                .collect(Collectors.toList());

        return filteredList;
    }

    /**
     * 활성화된 모든 유해 이미지(삭제되지 않은) 목록 페이지별로 가져오기 - (페이지 O, 삭제여부구분 O) Default
     */
    @Override
    public List<FeedAttachmentDto> getFilteredListByAIAndPageable(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 2);

        List<FeedAttachment> feedAttachmentList = feedAttachmentRepository.findByFileTypeAndActivated(FileType.PHOTO, true, pageable);
        Page<FeedAttachment> feedAttachmentPage = attachmentFilteringRepository.findBadImageListByAI(feedAttachmentList, pageable);

        List<FeedAttachmentDto> filteredList = feedAttachmentPage.getContent().stream()
                .map(FeedAttachmentDto::toDto)
                .collect(Collectors.toList());


        return filteredList;
    }

    @Override
    public void sendReportNotification(ReportProcessingDto reportProcessingDto) {

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setReceiverId(reportProcessingDto.getReporterId());
        notificationDto.setNotificationType(NotificationType.REPORT);
        notificationDto.setNotificationText(reportProcessingDto.getProcessingText());

        notificationService.sendNotification(notificationDto);
    }


}
