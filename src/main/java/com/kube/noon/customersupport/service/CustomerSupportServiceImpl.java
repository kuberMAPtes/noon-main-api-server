package com.kube.noon.customersupport.service;

import com.kube.noon.common.FileType;
import com.kube.noon.common.ObjectStorageAWS3S;
import com.kube.noon.customersupport.domain.Report;
import com.kube.noon.customersupport.dto.report.ReportDto;
import com.kube.noon.customersupport.dto.report.ReportProcessingDto;
import com.kube.noon.customersupport.enums.UnlockDuration;
import com.kube.noon.customersupport.repository.AttachmentFilteringRepository;
import com.kube.noon.customersupport.repository.ReportRepository;
import com.kube.noon.feed.domain.FeedAttachment;
import com.kube.noon.feed.dto.FeedAttachmentDto;
import com.kube.noon.feed.repository.FeedAttachmentRepository;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.UpdateMemberDto;
import com.kube.noon.member.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 신고, 공지사항, 유해사진필터링 서비스를 제공하는 구현체이다.
 *
 * @author 허예지
 */
@Slf4j
@Service
public class CustomerSupportServiceImpl implements CustomerSupportService{


    ///Field
    private final ReportRepository reportRepository;
    private final MemberService memberService;
    private final AttachmentFilteringRepository attachmentFilteringRepository;
    private final FeedAttachmentRepository feedAttachmentRepository;
    private final String bucketName;
    private final ObjectStorageAWS3S objectStorageAWS3S;


    public CustomerSupportServiceImpl(
            ReportRepository reportRepository,
            MemberService memberService, AttachmentFilteringRepository attachmentFilteringRepository, FeedAttachmentRepository feedAttachmentRepository,
            @Value("${bucket.name}") String bucketName,
            ObjectStorageAWS3S objectStorageAWS3S) {
        this.reportRepository = reportRepository;
        this.memberService = memberService;
        this.attachmentFilteringRepository = attachmentFilteringRepository;
        this.feedAttachmentRepository = feedAttachmentRepository;
        this.bucketName = bucketName;
        this.objectStorageAWS3S = objectStorageAWS3S;
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
     * @param reportProcessingDto 조회했던 report에서 신고상태, 신고처리텍스트가 변경된 reportDto
     * @param unlockDuration 계정잠금연장일수. Enum UnlockDuration.java 참고
     * @return 신고 처리되어 신고처리텍스트와 변경된 신고 상태를 포함한 Dto
     */
    @Transactional
    @Override
    public ReportProcessingDto updateReport(ReportProcessingDto reportProcessingDto, String unlockDuration) {

        //신고 상태 변경, 신고처리 텍스트 추가
        reportRepository.save(reportProcessingDto.toEntity());


        //피신고자 계정 잠금 일수 연장
        Optional<Member> reportee = memberService.findMemberById(reportProcessingDto.getReporteeId());
        log.info("피신고자 정보={}", reportee.toString());

        UpdateMemberDto updateMemberDto = new UpdateMemberDto();
        BeanUtils.copyProperties(reportee.orElseThrow(), updateMemberDto);
        updateMemberDto.setUnlockTime(reportee.orElseThrow().getUnlockTime().plusDays(UnlockDuration.valueOf(unlockDuration).getDays()));

        memberService.updateMember(updateMemberDto);

        return ReportProcessingDto.fromEntity(reportRepository.findReportByReportId(reportProcessingDto.getReportId()));
        
    }

    /**
     *  블러파일 생성, Object Storage업로드, FeedAttachment의 blurredFileUrl저장을 한다.
     *
     * @param attachmentDto 블러 파일을 생성하려는 첨부파일 Dto
     * @return Object Storage에 업로드된 블러 파일의 url이 저장된 첨부파일 Dto
     */
    @Override
    public FeedAttachmentDto addBluredImage(FeedAttachmentDto attachmentDto) throws IOException {

        // 블러 파일 생성 및 Object Storage 저장, 저장 url 요청
        String blurredFileUrl = attachmentFilteringRepository.addBluredFile(attachmentDto.getFileUrl());

        attachmentDto.setBlurredFileUrl(blurredFileUrl);
        feedAttachmentRepository.save(FeedAttachmentDto.toEntity(attachmentDto));

        return attachmentDto;
    }

    @Override
    public List<FeedAttachmentDto> getFilteredListByAI() {

        List<FeedAttachment> feedAttachmentList = feedAttachmentRepository.findByFileType(FileType.PHOTO);

        List<FeedAttachmentDto> filteredList = attachmentFilteringRepository.findBadImageListByAI(feedAttachmentList).stream()
                .map(FeedAttachmentDto::toDto)
                .collect(Collectors.toList());

        return filteredList;
    }


}
