package com.wrkr.tickety.domains.ticket.presentation;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.docs.RestDocsSupport;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.CommentRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.CommentResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.comment.CommentCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.comment.CommentGetUseCase;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.exception.CommentErrorCode;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CommentController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class CommentControllerTest extends RestDocsSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentCreateUseCase commentCreateUseCase;

    @MockitoBean
    private CommentGetUseCase commentGetUseCase;

    @MockitoBean
    private JwtUtils jwtUtils;

    private CommentRequest commentRequest;
    private PkResponse pkResponse;
    private CommentResponse commentResponse;
    private String encryptedTicketId;
    private String invalidTicketId;
    private Member member;
    private Ticket ticket;

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @BeforeEach
    void setUp() {
        encryptedTicketId = PkCrypto.encrypt(1L);
        invalidTicketId = PkCrypto.encrypt(100L);
        commentRequest = new CommentRequest("이슈 다시 확인해주세요.");
        pkResponse = new PkResponse(encryptedTicketId);
        commentResponse = new CommentResponse(PkCrypto.encrypt(1L), List.of());
        member = new Member(1L, "nickname", "password", "name", "01012345678", "email", "department", "position", "profileImage", Role.USER, "agitUrl", true,
            true, true, true, false, false);
    }

    @Override
    protected Object initController() {
        return new CommentController(commentCreateUseCase, commentGetUseCase);
    }

    @Nested
    @DisplayName("댓글 작성 API 테스트")
    class CreateCommentTests {

        @Test
        @DisplayName("성공: 텍스트 댓글 작성")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "user.psw")
        void createComment_Success_TextOnly() throws Exception {
            given(commentCreateUseCase.createComment(any(), any(), any(), anyList())).willReturn(pkResponse);

            MockMultipartFile commentRequestFile = new MockMultipartFile(
                "CommentRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(commentRequest)
            );

            mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/user/tickets/{ticketId}/comments", encryptedTicketId)
                    .file(commentRequestFile)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("Comment/CreateComment/Request/Success1",
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("댓글 작성 API")
                        .summary("댓글 작성 성공 (텍스트)").build())));
        }

        @Test
        @DisplayName("성공: 텍스트 댓글 작성 (첨부파일 포함)")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "user.psw")
        void createComment_Success_TextWithAttachments() throws Exception {
            given(commentCreateUseCase.createComment(any(), any(), any(), anyList())).willReturn(pkResponse);

            MockMultipartFile commentRequestFile = new MockMultipartFile(
                "CommentRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(commentRequest)
            );

            MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.jpg",
                "text/plain", "file1 content".getBytes());
            MockMultipartFile file2 = new MockMultipartFile("attachments", "file2.jpg",
                "text/plain", "file2 content".getBytes());

            mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/user/tickets/{ticketId}/comments", encryptedTicketId)
                    .file(commentRequestFile)
                    .file(file1)
                    .file(file2)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("Comment/CreateComment/Request/Success2",
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("댓글 작성 API")
                        .summary("댓글 작성 성공 (텍스트 + 첨부파일)").build())));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 티켓 ID")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "user.psw")
        void createComment_Fail_TicketNotFound() throws Exception {
            willThrow(ApplicationException.from(TicketErrorCode.TICKET_NOT_FOUND))
                .given(commentCreateUseCase).createComment(any(), any(), any(), anyList());

            MockMultipartFile commentRequestFile = new MockMultipartFile(
                "CommentRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(commentRequest)
            );

            MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.jpg",
                "text/plain", "file1 content".getBytes());
            MockMultipartFile file2 = new MockMultipartFile("attachments", "file2.jpg",
                "text/plain", "file2 content".getBytes());

            mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/user/tickets/{ticketId}/comments", encryptedTicketId)
                    .file(commentRequestFile)
                    .file(file1)
                    .file(file2)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(csrf()))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document("Comment/CreateComment/Request/Failure1",
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("댓글 작성 API")
                        .summary("댓글 작성 실패 - 존재하지 않는 티켓").build())));
        }

        @Test
        @DisplayName("실패: 권한 없음")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "user.psw")
        void createComment_Fail_UnauthorizedAccess() throws Exception {
            willThrow(ApplicationException.from(TicketErrorCode.UNAUTHORIZED_ACCESS))
                .given(commentCreateUseCase).createComment(any(), any(), any(), anyList());

            MockMultipartFile commentRequestFile = new MockMultipartFile(
                "CommentRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(commentRequest)
            );

            MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.jpg",
                "text/plain", "file1 content".getBytes());
            MockMultipartFile file2 = new MockMultipartFile("attachments", "file2.jpg",
                "text/plain", "file2 content".getBytes());

            mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/user/tickets/{ticketId}/comments", encryptedTicketId)
                    .file(commentRequestFile)
                    .file(file1)
                    .file(file2)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(csrf()))
                .andExpect(status().isForbidden())
                .andDo(print())
                .andDo(document("Comment/CreateComment/Request/Failure2",
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("댓글 작성 API")
                        .summary("댓글 작성 실패 - 권한 없음").build())));
        }

        @Test
        @DisplayName("실패: 코멘트가 불가능한 상태의 티켓인 경우 예외 발생")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "user.psw")
        void createComment_Fail_CommentConflict() throws Exception {
            willThrow(ApplicationException.from(CommentErrorCode.COMMENT_CONFLICT))
                .given(commentCreateUseCase).createComment(any(), any(), any(), anyList());

            MockMultipartFile commentRequestFile = new MockMultipartFile(
                "CommentRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(commentRequest)
            );

            MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.jpg",
                "text/plain", "file1 content".getBytes());
            MockMultipartFile file2 = new MockMultipartFile("attachments", "file2.jpg",
                "text/plain", "file2 content".getBytes());

            mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/user/tickets/{ticketId}/comments", encryptedTicketId)
                    .file(commentRequestFile)
                    .file(file1)
                    .file(file2)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(csrf()))
                .andExpect(status().isConflict())
                .andDo(print())
                .andDo(document("Comment/CreateComment/Request/Failure3",
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("댓글 작성 API")
                        .summary("댓글 작성 실패 - 코멘트 불가능한 상태의 티켓").build())));
        }


        @Test
        @DisplayName("실패: 잘못된 파일 형식 업로드 시 예외 발생")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "user.psw")
        void createComment_Fail_InvalidFileFormat() throws Exception {
            willThrow(new IllegalArgumentException("허용되지 않은 파일 확장자입니다. [txt] (허용 확장자: [png, pdf, xlsx, xls, jpeg, jpg])"))
                .given(commentCreateUseCase).createComment(any(), any(), any(), anyList());

            MockMultipartFile commentRequestFile = new MockMultipartFile(
                "CommentRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(commentRequest)
            );

            MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.txt",
                "text/plain", "file1 content".getBytes());
            MockMultipartFile file2 = new MockMultipartFile("attachments", "file2.txt",
                "text/plain", "file2 content".getBytes());

            mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/user/tickets/{ticketId}/comments", encryptedTicketId)
                    .file(commentRequestFile)
                    .file(file1)
                    .file(file2)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print())
                .andDo(document("Comment/CreateComment/Request/Failure4",
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("댓글 작성 API")
                        .summary("댓글 작성 실패 - 잘못된 파일 형식").build())));
        }

        @Test
        @DisplayName("실패: 파일 업로드 용량 초과 시 예외 발생")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "user.psw")
        void createComment_Fail_FileSizeExceeded() throws Exception {
            willThrow(new IllegalArgumentException("파일 크기는 최대 10MB까지 가능합니다."))
                .given(commentCreateUseCase).createComment(any(), any(), any(), anyList());

            MockMultipartFile commentRequestFile = new MockMultipartFile(
                "CommentRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(commentRequest)
            );

            MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.jpg",
                "text/plain", new byte[11 * 1024 * 1024]);
            MockMultipartFile file2 = new MockMultipartFile("attachments", "file2.jpg",
                "text/plain", new byte[11 * 1024 * 1024]);

            mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/user/tickets/{ticketId}/comments", encryptedTicketId)
                    .file(commentRequestFile)
                    .file(file1)
                    .file(file2)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print())
                .andDo(document("Comment/CreateComment/Request/Failure5",
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("댓글 작성 API")
                        .summary("댓글 작성 실패 - 파일 업로드 용량 초과").build())));
        }

        @Test
        @DisplayName("실패: 첨부파일 개수 초과 시 예외 발생")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "user.psw")
        void createComment_Fail_AttachmentCountExceeded() throws Exception {
            willThrow(new IllegalArgumentException("첨부파일 개수는 최대 5개까지 가능합니다."))
                .given(commentCreateUseCase).createComment(any(), any(), any(), anyList());

            MockMultipartFile commentRequestFile = new MockMultipartFile(
                "CommentRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(commentRequest)
            );

            MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.jpg",
                "text/plain", "file1 content".getBytes());
            MockMultipartFile file2 = new MockMultipartFile("attachments", "file2.jpg",
                "text/plain", "file2 content".getBytes());
            MockMultipartFile file3 = new MockMultipartFile("attachments", "file3.jpg",
                "text/plain", "file3 content".getBytes());
            MockMultipartFile file4 = new MockMultipartFile("attachments", "file4.jpg",
                "text/plain", "file4 content".getBytes());
            MockMultipartFile file5 = new MockMultipartFile("attachments", "file5.jpg",
                "text/plain", "file5 content".getBytes());
            MockMultipartFile file6 = new MockMultipartFile("attachments", "file6.jpg",
                "text/plain", "file6 content".getBytes());

            mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/user/tickets/{ticketId}/comments", encryptedTicketId)
                    .file(commentRequestFile)
                    .file(file1)
                    .file(file2)
                    .file(file3)
                    .file(file4)
                    .file(file5)
                    .file(file6)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print())
                .andDo(document("Comment/CreateComment/Request/Failure6",
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("댓글 작성 API")
                        .summary("댓글 작성 실패 - 첨부파일 개수 초과").build())));
        }

        @Test
        @DisplayName("실패: 잘못된 파일명 (빈 파일명) 시 예외 발생")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "user.psw")
        void createComment_Fail_InvalidFileName() throws Exception {
            willThrow(new IllegalArgumentException("파일 이름이 유효하지 않습니다."))
                .given(commentCreateUseCase).createComment(any(), any(), any(), anyList());

            MockMultipartFile commentRequestFile = new MockMultipartFile(
                "CommentRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(commentRequest)
            );

            MockMultipartFile file1 = new MockMultipartFile("attachments", "",
                "text/plain", new byte[0]);

            mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/user/tickets/{ticketId}/comments", encryptedTicketId)
                    .file(commentRequestFile)
                    .file(file1)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print())
                .andDo(document("Comment/CreateComment/Request/Failure7",
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("댓글 작성 API")
                        .summary("댓글 작성 실패 - 잘못된 파일명").build())));
        }
    }

    @Nested
    @DisplayName("댓글 조회 API 테스트")
    class GetCommentTests {

        @Test
        @DisplayName("성공: 댓글 조회")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "user.psw")
        void getComment_Success() throws Exception {
            given(commentGetUseCase.getComment(any(), any())).willReturn(commentResponse);

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/user/tickets/{ticketId}/comments", encryptedTicketId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("Comment/GetComment/Request/Success1",
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("댓글 조회 API")
                        .summary("댓글 조회 성공").build())));
        }

        @Test
        @DisplayName("성공: 댓글이 없는 경우 빈 리스트 반환")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "user.psw")
        void getComment_Success_EmptyList() throws Exception {
            given(commentGetUseCase.getComment(any(), any())).willReturn(new CommentResponse(PkCrypto.encrypt(1L), List.of()));

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/user/tickets/{ticketId}/comments", encryptedTicketId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("Comment/GetComment/Request/Success2",
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("댓글 조회 API")
                        .summary("댓글 조회 성공 - 빈 리스트").build())));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 티켓 ID")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "user.psw")
        void getComment_Fail_TicketNotFound() throws Exception {
            willThrow(ApplicationException.from(TicketErrorCode.TICKET_NOT_FOUND))
                .given(commentGetUseCase).getComment(any(), any());

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/user/tickets/{ticketId}/comments", invalidTicketId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document("Comment/GetComment/Request/Failure1",
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("댓글 조회 API")
                        .summary("댓글 조회 실패 - 존재하지 않는 티켓").build())));
        }

        @Test
        @DisplayName("실패: 권한 없음")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "user.psw")
        void getComment_Fail_UnauthorizedAccess() throws Exception {
            willThrow(ApplicationException.from(TicketErrorCode.UNAUTHORIZED_ACCESS))
                .given(commentGetUseCase).getComment(any(), any());

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/user/tickets/{ticketId}/comments", encryptedTicketId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print())
                .andDo(document("Comment/GetComment/Request/Failure2",
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("댓글 조회 API")
                        .summary("댓글 조회 실패 - 권한 없음").build())));
        }
    }
}
