#### ERD


![image](https://github.com/user-attachments/assets/334b428b-b008-4a19-abf5-0c5f4751e1df)

## 1. Lecture 테이블
- lecture_id (PK): 강의의 고유 식별자로, 각 강의를 식별하는 데 사용됩니다.
- title: 강의의 제목을 저장하는 필드입니다.
- max_student: 강의의 최대 수강 인원을 나타내며, 강의 신청자가 이 인원을 초과할 수 없습니다.
- current_student: 현재까지 강의에 신청한 수강 인원의 수를 나타냅니다.
- lecturer: 강의를 진행하는 강사의 이름을 저장하는 필드입니다.
- open_date: 강의가 시작되는 날짜를 저장하는 필드입니다. 신청 기간이나 신청 가능 여부를 판단하는 데 사용될 수 있습니다.
 
## 2. User 테이블
- user_id (PK): 사용자(수강자)의 고유 식별자로, 각 사용자를 식별하는 데 사용됩니다.
- name: 사용자의 이름을 저장하는 필드입니다.
  
## 3. LectureHistory 테이블
- lecture_history_id (PK): 강의 신청 내역의 고유 식별자입니다. 각 강의 신청 내역을 식별하는 데 사용됩니다.
- apply_date: 사용자가 강의에 신청한 날짜를 저장합니다.
- is_applied: 사용자가 해당 강의에 신청했는지 여부를 나타내는 Boolean 값입니다. 신청 여부를 추적하는 데 사용됩니다.
- lecture_id (FK): 해당 강의를 가리키는 외래 키입니다. Lecture 테이블과의 N:1 관계를 나타내며, 하나의 강의에는 여러 사용자가 신청할 수 있습니다.
- user_id (FK): 해당 신청 내역의 사용자를 가리키는 외래 키입니다. User 테이블과의 N:1 관계를 나타내며, 하나의 사용자는 여러 강의에 신청할 수 있습니다.

### 설계 이유
- Lecture 테이블: 강의의 기본 정보를 저장하고, 수강 인원 관리를 위해 max_student와 current_student 필드를 둠으로써 수강 정원을 제한할 수 있도록 설계되었습니다. 또한, 강의 시작일(open_date)을 통해 강의 신청 기간이나 신청 가능 여부를 제어할 수 있습니다.

- User 테이블: 사용자(수강자)의 정보를 저장하며, LectureHistory와 연관되어 사용자가 어떤 강의에 신청했는지 관리할 수 있도록 설계되었습니다.

- LectureHistory 테이블: 사용자가 강의에 신청한 내역을 기록하며, is_applied 필드를 통해 중복 신청을 방지하거나 신청 여부를 관리할 수 있습니다.

### 설계 포인트
Lecture와 LectureHistory의 관계: 하나의 강의는 여러 사용자에 의해 신청될 수 있지만, 한 사용자는 여러 강의에 신청할 수 있는 N관계를 LectureHistory를 통해 관리합니다.
User와 LectureHistory의 관계: 하나의 사용자는 여러 강의에 신청할 수 있으며, LectureHistory를 통해 신청 내역을 관리할 수 있습니다.
