## Intro
웹 애플리케이션의 health-check를 확인하는 애플리케이션입니다.<br/>
AWS의 Target-group + CloudWatch 기능의 간단한 버전입니다.<br/>
health-check API 호출 주기를 설정할 수 있으며, 4xx, 5xx 응답이 연속으로 threshold 이상 누적되면 관리자의 이메일로 즉시 알립니다.<br/>
0시 0분에 전날의 health-check 결과를 정리한 Daily report를 이메일로 받을 수 있습니다.

## 주요 기능 화면 예시
<img width="400" alt="Summary" src="https://github.com/playkuround/server-health-check/assets/51076814/59ba7b17-23f7-4b52-9f2f-d0a9e69128b8"> <br/>
관리하는 도메인들의 상태 정보를 확인할 수 있습니다.

<br/>

<img width="400" alt="스크린샷 2024-06-21 오후 2 03 38" src="https://github.com/playkuround/server-health-check/assets/51076814/ed611b21-48be-41da-9dcc-04c58b800823">
<img width="400" alt="스크린샷 2024-06-21 오후 2 03 51" src="https://github.com/playkuround/server-health-check/assets/51076814/67473f0e-54b9-4ec4-971d-a6fbf589082c">  <br/>
특정 도메인의 정보를 상세히 확인할 수 있습니다.

<br/><br/>

<img width="400" alt="스크린샷 2024-06-21 오후 2 04 48" src="https://github.com/playkuround/server-health-check/assets/51076814/86106f7d-d588-4bd9-b71a-afcf2d52653a">
<img width="400" alt="스크린샷 2024-06-21 오후 2 04 43" src="https://github.com/playkuround/server-health-check/assets/51076814/93c4b8bd-20d5-44d7-80b0-d77c92594a93"> <br/>
새로운 Target을 등록하거나, 관리자 이메일을 추가할 수 있습니다.

<br/><br/>

<img width="400" alt="스크린샷 2024-06-21 오후 2 04 57" src="https://github.com/playkuround/server-health-check/assets/51076814/98cf4153-4372-4b52-bd97-49913707781c"> <br/>
이메일 알림이 오는 연속된 에러 응답의 threshold와 health-check 주기를 변경할 수 있습니다.

<br/><br/>

<img width="600" alt="스크린샷 2024-06-21 오후 1 47 08" src="https://github.com/playkuround/server-health-check/assets/51076814/84910782-76a7-4a30-975f-4ee88f287ed3"> <br/>
Daily Report 이메일 예시입니다.
