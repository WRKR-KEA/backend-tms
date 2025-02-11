package com.wrkr.tickety.common;

import static org.mockito.Mockito.mock;

import com.wrkr.tickety.common.utils.ExecuteParallel;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberSaveService;
import com.wrkr.tickety.domains.member.domain.service.MemberUpdateService;
import com.wrkr.tickety.domains.member.persistence.mapper.MemberPersistenceMapper;
import com.wrkr.tickety.domains.member.persistence.repository.MemberRepository;
import com.wrkr.tickety.domains.notification.domain.service.kakaowork.KakaoworkMessageService;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryCreateService;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryDeleteService;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryUpdateService;
import com.wrkr.tickety.domains.ticket.domain.service.comment.CommentGetService;
import com.wrkr.tickety.domains.ticket.domain.service.comment.CommentSaveService;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideCreateService;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideDeleteService;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideGetService;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideUpdateService;
import com.wrkr.tickety.domains.ticket.domain.service.statistics.StatisticsGetService;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketDeleteService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketSaveService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketUpdateService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistorySaveService;
import com.wrkr.tickety.domains.ticket.persistence.mapper.CategoryPersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.mapper.CommentPersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.mapper.GuidePersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.mapper.TemplatePersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.mapper.TicketHistoryPersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.mapper.TicketPersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.repository.CategoryRepository;
import com.wrkr.tickety.domains.ticket.persistence.repository.CommentRepository;
import com.wrkr.tickety.domains.ticket.persistence.repository.GuideRepository;
import com.wrkr.tickety.domains.ticket.persistence.repository.TemplateRepository;
import com.wrkr.tickety.domains.ticket.persistence.repository.TicketHistoryQueryDslRepository;
import com.wrkr.tickety.domains.ticket.persistence.repository.TicketHistoryRepository;
import com.wrkr.tickety.domains.ticket.persistence.repository.TicketQueryDslRepository;
import com.wrkr.tickety.domains.ticket.persistence.repository.TicketRepository;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Tag("UseCase")
@Transactional
@ExecuteParallel
@RecordApplicationEvents
public abstract class UnitTest {

//    @Autowired
//    private DatabaseCleaner databaseCleaner;

    /**
     * Service
     **/
    // Member
    protected final MemberSaveService memberSaveService = mock(MemberSaveService.class);
    protected final MemberGetService memberGetService = mock(MemberGetService.class);
    protected final MemberUpdateService memberUpdateService = mock(MemberUpdateService.class);

    // Category
    protected final CategoryCreateService categoryCreateService = mock(CategoryCreateService.class);
    protected final CategoryGetService categoryGetService = mock(CategoryGetService.class);
    protected final CategoryUpdateService categoryUpdateService = mock(CategoryUpdateService.class);
    protected final CategoryDeleteService categoryDeleteService = mock(CategoryDeleteService.class);

    // Comment
    protected final CommentSaveService commentSaveService = mock(CommentSaveService.class);
    protected final CommentGetService commentGetService = mock(CommentGetService.class);

    // Guide
    protected final GuideCreateService guideCreateService = mock(GuideCreateService.class);
    protected final GuideGetService guideGetService = mock(GuideGetService.class);
    protected final GuideUpdateService guideUpdateService = mock(GuideUpdateService.class);
    protected final GuideDeleteService guideDeleteService = mock(GuideDeleteService.class);

    // Statistics
    protected final StatisticsGetService statisticsGetService = mock(StatisticsGetService.class);

    // Template
    protected final TemplateGetService templateGetService = mock(TemplateGetService.class);

    // Ticket
    protected final TicketSaveService ticketSaveService = mock(TicketSaveService.class);
    protected final TicketGetService ticketGetService = mock(TicketGetService.class);
    protected final TicketUpdateService ticketUpdateService = mock(TicketUpdateService.class);
    protected final TicketDeleteService ticketDeleteService = mock(TicketDeleteService.class);

    // TicketHistory
    protected final TicketHistorySaveService ticketHistorySaveService = mock(TicketHistorySaveService.class);
    protected final TicketHistoryGetService ticketHistoryGetService = mock(TicketHistoryGetService.class);

    /**
     * Persistence
     **/
    // Member
    protected final MemberRepository memberRepository = mock(MemberRepository.class);
    protected final MemberPersistenceMapper memberPersistenceMapper = mock(MemberPersistenceMapper.class);

    // Category
    protected final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    protected final CategoryPersistenceMapper categoryPersistenceMapper = mock(CategoryPersistenceMapper.class);

    // Comment
    protected final CommentRepository commentRepository = mock(CommentRepository.class);
    protected final CommentPersistenceMapper commentPersistenceMapper = mock(CommentPersistenceMapper.class);

    // Guide
    protected final GuideRepository guideRepository = mock(GuideRepository.class);
    protected final GuidePersistenceMapper guidePersistenceMapper = mock(GuidePersistenceMapper.class);

    // Template
    protected final TemplateRepository templateRepository = mock(TemplateRepository.class);
    protected final TemplatePersistenceMapper templatePersistenceMapper = mock(TemplatePersistenceMapper.class);

    // Ticket
    protected final TicketRepository ticketRepository = mock(TicketRepository.class);
    protected final TicketQueryDslRepository ticketQueryDslRepository = mock(TicketQueryDslRepository.class);
    protected final TicketPersistenceMapper ticketPersistenceMapper = mock(TicketPersistenceMapper.class);

    // TicketHistory
    protected final TicketHistoryRepository ticketHistoryRepository = mock(TicketHistoryRepository.class);
    protected final TicketHistoryQueryDslRepository ticketHistoryQueryDslRepository = mock(TicketHistoryQueryDslRepository.class);
    protected final TicketHistoryPersistenceMapper ticketHistoryPersistenceMapper = mock(TicketHistoryPersistenceMapper.class);

    /**
     * Utils
     */
    // ApplicationEventPublisher
    protected final ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);
    protected final KakaoworkMessageService kakaoworkMessageService = mock(KakaoworkMessageService.class);
}
