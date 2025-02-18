package com.wrkr.tickety.domains.ticket.application.usecase.comment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.wrkr.tickety.domains.attachment.domain.service.CommentAttachmentUploadService;
import com.wrkr.tickety.domains.attachment.domain.service.S3ApiService;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.CommentRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.domain.event.CommentCreateEvent;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.comment.CommentSaveService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.exception.CommentErrorCode;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("CommentCreateUseCase 테스트")
class CommentCreateUseCaseTest {

    @Mock
    private TicketGetService ticketGetService;
    @Mock
    private CommentSaveService commentSaveService;
    @Mock
    private CommentAttachmentUploadService commentAttachmentUploadService;
    @Mock
    private S3ApiService s3ApiService;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private CommentCreateUseCase commentCreateUseCase;

    private Member mockMember;
    private Ticket mockTicket;
    private Comment mockComment;
    private CommentRequest mockRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMember = mock(Member.class);
        mockTicket = mock(Ticket.class);
        mockComment = mock(Comment.class);
        mockRequest = new CommentRequest("이슈 다시 확인해주세요.");
    }

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }


    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        @Test
        @DisplayName("코멘트 작성 성공")
        void createComment_Success() {
            // Given
            given(ticketGetService.getTicketByTicketId(any())).willReturn(mockTicket);
            given(mockTicket.isRelatedWith(any())).willReturn(true);
            given(mockTicket.isCommentable()).willReturn(true);
            given(commentSaveService.saveComment(any())).willReturn(mockComment);
            given(mockComment.getCommentId()).willReturn(1L);

            // When
            PkResponse response = commentCreateUseCase.createComment(mockMember, 1L, mockRequest, null);

            // Then
            verify(commentSaveService).saveComment(any(Comment.class));
            verify(applicationEventPublisher).publishEvent(any(CommentCreateEvent.class));
            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("첨부파일과 함께 코멘트 작성 성공")
        void createComment_WithAttachments_Success() {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            given(file.isEmpty()).willReturn(false);
            given(file.getOriginalFilename()).willReturn("test.png");
            given(file.getSize()).willReturn(1024L);
            given(ticketGetService.getTicketByTicketId(any())).willReturn(mockTicket);
            given(mockTicket.isRelatedWith(any())).willReturn(true);
            given(mockTicket.isCommentable()).willReturn(true);
            given(commentSaveService.saveComment(any())).willReturn(mockComment);
            given(mockComment.getCommentId()).willReturn(1L);
            given(s3ApiService.uploadCommentFile(any())).willReturn("https://s3-url.com/test.png");

            // When
            PkResponse response = commentCreateUseCase.createComment(mockMember, 1L, mockRequest, List.of(file));

            // Then
            verify(commentSaveService).saveComment(any(Comment.class));
            verify(commentAttachmentUploadService).saveAll(anyList());
            verify(applicationEventPublisher).publishEvent(any(CommentCreateEvent.class));
            assertThat(response).isNotNull();
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureCases {

        @Test
        @DisplayName("존재하지 않는 티켓 ID로 코멘트 작성 시 예외 발생")
        void createComment_Fail_TicketNotFound() {
            // Given
            given(ticketGetService.getTicketByTicketId(any())).willThrow(ApplicationException.from(TicketErrorCode.TICKET_NOT_FOUND));

            // When & Then
            assertThatThrownBy(() -> commentCreateUseCase.createComment(mockMember, 999L, mockRequest, null))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining(TicketErrorCode.TICKET_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("해당 티켓에 대한 접근 권한이 없는 경우 예외 발생")
        void createComment_Fail_UnauthorizedAccess() {
            // Given
            given(ticketGetService.getTicketByTicketId(any())).willReturn(mockTicket);
            given(mockTicket.isRelatedWith(any())).willReturn(false);

            // When & Then
            assertThatThrownBy(() -> commentCreateUseCase.createComment(mockMember, 1L, mockRequest, null))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining(TicketErrorCode.UNAUTHORIZED_ACCESS.getMessage());
        }

        @Test
        @DisplayName("코멘트가 불가능한 상태의 티켓인 경우 예외 발생")
        void createComment_Fail_CommentConflict() {
            // Given
            given(ticketGetService.getTicketByTicketId(any())).willReturn(mockTicket);
            given(mockTicket.isRelatedWith(any())).willReturn(true);
            given(mockTicket.isCommentable()).willReturn(false);

            // When & Then
            assertThatThrownBy(() -> commentCreateUseCase.createComment(mockMember, 1L, mockRequest, null))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining(CommentErrorCode.COMMENT_CONFLICT.getMessage());
        }

        @Test
        @DisplayName("잘못된 파일 형식 업로드 시 예외 발생")
        void createComment_Fail_InvalidFileFormat() {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            given(file.isEmpty()).willReturn(false);
            given(file.getOriginalFilename()).willReturn("test.exe");
            given(file.getSize()).willReturn(1024L);
            given(ticketGetService.getTicketByTicketId(any())).willReturn(mockTicket);

            given(ticketGetService.getTicketByTicketId(any())).willReturn(mockTicket);
            given(mockTicket.isRelatedWith(any())).willReturn(true);
            given(mockTicket.isCommentable()).willReturn(true);
            given(commentSaveService.saveComment(any())).willReturn(mockComment);

            // When & Then
            assertThatThrownBy(() -> commentCreateUseCase.createComment(mockMember, 1L, mockRequest, List.of(file)))
                .isInstanceOf(IllegalArgumentException.class);

        }
    }

    @Nested
    @DisplayName("첨부파일 관련 실패 케이스")
    class AttachmentFailureCases {

        @Test
        @DisplayName("첨부파일 개수 초과 시 예외 발생")
        void createComment_Fail_MaxFileCountExceeded() {
            // Given
            List<MultipartFile> files = List.of(
                mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class),
                mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class)
            );

            given(ticketGetService.getTicketByTicketId(any())).willReturn(mockTicket);
            given(mockTicket.isRelatedWith(any())).willReturn(true);
            given(mockTicket.isCommentable()).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> commentCreateUseCase.createComment(mockMember, 1L, mockRequest, files))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("첨부파일 개수는 최대 5개까지 가능합니다.");
        }

        @Test
        @DisplayName("첨부파일 크기 초과 시 예외 발생")
        void createComment_Fail_MaxFileSizeExceeded() {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            given(file.getSize()).willReturn(15 * 1024 * 1024L);
            given(file.getOriginalFilename()).willReturn("large-file.jpg");

            given(ticketGetService.getTicketByTicketId(any())).willReturn(mockTicket);
            given(mockTicket.isRelatedWith(any())).willReturn(true);
            given(mockTicket.isCommentable()).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> commentCreateUseCase.createComment(mockMember, 1L, mockRequest, List.of(file)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("파일 크기는 최대 10MB까지 가능합니다.");
        }

        @Test
        @DisplayName("잘못된 파일명 (빈 파일명) 시 예외 발생")
        void createComment_Fail_InvalidFileName() {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            given(file.getOriginalFilename()).willReturn("");
            given(file.getSize()).willReturn(1024L);

            given(ticketGetService.getTicketByTicketId(any())).willReturn(mockTicket);
            given(mockTicket.isRelatedWith(any())).willReturn(true);
            given(mockTicket.isCommentable()).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> commentCreateUseCase.createComment(mockMember, 1L, mockRequest, List.of(file)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("파일 이름이 유효하지 않습니다.");
        }

        @Test
        @DisplayName("잘못된 파일 확장자 시 예외 발생")
        void createComment_Fail_InvalidFileExtension() {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            given(file.getOriginalFilename()).willReturn("malware.exe");
            given(file.getSize()).willReturn(1024L);

            given(ticketGetService.getTicketByTicketId(any())).willReturn(mockTicket);
            given(mockTicket.isRelatedWith(any())).willReturn(true);
            given(mockTicket.isCommentable()).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> commentCreateUseCase.createComment(mockMember, 1L, mockRequest, List.of(file)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("허용되지 않은 파일 확장자입니다.");
        }
    }
}
