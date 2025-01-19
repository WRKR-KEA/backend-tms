module.exports = {
    extends: ['@commitlint/config-conventional'],
    rules: {
        // 커밋 메시지 유형 강제
        'type-enum': [
            2,
            'always',
            [
                ':sparkles: Feature', // 새로운 기능 추가
                ':bug: Fix',  // 버그 수정
                ':memo: Docs', // 문서 수정
                ':art: Style', // 코드 포맷팅 (비기능 수정)
                ':recycle: Refactor', // 코드 리팩토링
                ':white_check_mark: Test', // 테스트 코드 추가
                ':wrench: Chore', // 기타 작업
                ':construction: Wip',  // 작업 중인 임시 커밋
            ],
        ],
        "type-case": [2, "always", "sentence-case"],
        "type-empty": [2, "never"],
        // 커밋 메시지 유형과 제목에 대한 정규식 검증
        'header-case': [2, 'always', 'sentence-case'],
        // 제목이 반드시 있어야 함
        'subject-empty': [2, 'never'],
        // 제목 최대 길이 제한
        'subject-max-length': [2, 'always', 100],
    },
};