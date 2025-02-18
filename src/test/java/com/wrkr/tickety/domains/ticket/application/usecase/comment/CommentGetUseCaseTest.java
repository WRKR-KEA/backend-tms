package com.wrkr.tickety.domains.ticket.application.usecase.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.wrkr.tickety.domains.attachment.domain.service.CommentAttachmentGetService;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.response.CommentResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.CommentMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.comment.CommentGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("CommentGetUseCase 테스트")
class CommentGetUseCaseTest {

    @Mock
    private TicketGetService ticketGetService;
    @Mock
    private CommentGetService commentGetService;
    @Mock
    private CommentAttachmentGetService commentAttachmentGetService;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentGetUseCase commentGetUseCase;

    private Member mockMember;
    private Ticket mockTicket;
    private Comment mockComment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMember = mock(Member.class);
        mockTicket = mock(Ticket.class);
        mockComment = mock(Comment.class);
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
        @DisplayName("댓글 조회 성공")
        void getComment_Success() {
            // Given
            given(ticketGetService.getTicketByTicketId(any())).willReturn(mockTicket);
            given(mockTicket.isAccessibleBy(any())).willReturn(true);
            given(commentGetService.getCommentsByTicket(mockTicket)).willReturn(List.of(mockComment));
            given(mockComment.getCreatedAt()).willReturn(LocalDateTime.of(2025, 1, 21, 14, 50, 30));
            // When
            CommentResponse response = commentGetUseCase.getComment(mockMember, 1L);

            // Then
            verify(ticketGetService).getTicketByTicketId(any());
            verify(commentGetService).getCommentsByTicket(mockTicket);
            assertThat(response).isNotNull();
            assertThat(response.comments()).isNotEmpty();
        }

        @Test
        @DisplayName("댓글이 없는 경우 빈 리스트 반환")
        void getComment_EmptyList() {
            // Given
            given(ticketGetService.getTicketByTicketId(any())).willReturn(mockTicket);
            given(mockTicket.isAccessibleBy(any())).willReturn(true);
            given(commentGetService.getCommentsByTicket(mockTicket)).willReturn(List.of());

            // When
            CommentResponse response = commentGetUseCase.getComment(mockMember, 1L);

            // Then
            verify(ticketGetService).getTicketByTicketId(any());
            verify(commentGetService).getCommentsByTicket(mockTicket);
            assertThat(response).isNotNull();
            assertThat(response.comments()).isEmpty();
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureCases {

        @Test
        @DisplayName("존재하지 않는 티켓 ID로 조회 시 예외 발생")
        void getComment_Fail_TicketNotFound() {
            // Given
            given(ticketGetService.getTicketByTicketId(any()))
                .willThrow(ApplicationException.from(TicketErrorCode.TICKET_NOT_FOUND));

            // When & Then
            assertThatThrownBy(() -> commentGetUseCase.getComment(mockMember, 999L))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining(TicketErrorCode.TICKET_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("접근 권한이 없는 티켓 조회 시 예외 발생")
        void getComment_Fail_UnauthorizedAccess() {
            // Given
            given(ticketGetService.getTicketByTicketId(any())).willReturn(mockTicket);
            given(mockTicket.isAccessibleBy(any())).willReturn(false);

            // When & Then
            assertThatThrownBy(() -> commentGetUseCase.getComment(mockMember, 1L))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining(TicketErrorCode.UNAUTHORIZED_ACCESS.getMessage());
        }
    }
}
