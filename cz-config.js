module.exports = {
    types: [
        { value: ':sparkles: Feature', name: '✨ Feature :\t새로운 기능 추가' },
        { value: ':bug: Fix', name: '🐛 Fix :\t버그 수정' },
        { value: ':memo: Docs', name: '📝 Docs :\t문서 수정' },
        { value: ':art: Style', name: '🎨 Style :\t코드 포맷팅 (비기능 수정)' },
        { value: ':recycle: Refactor', name: '♻️ Refactor :\t코드 리팩토링' },
        { value: ':white_check_mark: Test', name: '✅ Test :\t테스트 코드 추가' },
        { value: ':wrench: Chore', name: '🔧 Chore :\t기타 작업' },
        { value: ':construction: Wip', name: '🚧 Wip :\t작업 중인 임시 커밋 (지양)' },
    ],
    messages: {
        type: '커밋의 변경 사항 유형을 선택하세요 :',
        scope: '변경된 범위를 입력하세요 (예: 파일명, 폴더명) :',
        subject: '커밋 메시지를 간단히 설명하세요 (50자 이내) :',
        body: '변경된 내용 및 작업 내용을 작성하세요 (예: 파일 이름과 작업 내역) |를 사용하여 줄을 넘길 수 있습니다.(선택사항) :',
        footer: '해결된 이슈 번호, 관련된 이슈번호, 참고한 이슈 번호를 작성하세요 |를 사용하여 줄을 넘길 수 있습니다.(선택사항) :',
        confirmCommit: '이 커밋 메시지로 커밋하시겠습니까?',
    },
    subjectLimit: 100,
    skipQuestions: ['scope'],
    allowTicketNumber: true,
    isTicketNumberRequired: true,
    ticketNumberPrefix: 'WRKR-',
    ticketNumberSuffix: '',
    breaklineChar: '|',

};