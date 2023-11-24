package hellojpa;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
//@Table("USER") -- 엔티티와 테이블 이름이 다르면 이렇게 매핑할 수 있다.
public class Member {
    @Id
    private Long id;
    //@Column("user_name") -- 원하는 컬럼 이름에 값을 매핑하도록 할 수 있다.
    private String name;
    private Integer age; // Integer에 맞는 데이터 타입으로 자동 매핑
    @Enumerated(EnumType.STRING) //enum를 사용하면 사용 - String으로만 사용 enum에 무언가 추가되면 Original로 하면 숫자 혼동...
    private RoleType roleType;
    @Temporal(value = TemporalType.TIMESTAMP) //DB는 Date(날짜), Time(시간), TimeStamp(날짜 + 시간)을 구분
    private Date createdDate;
    @Temporal(value = TemporalType.TIMESTAMP) //시간 작성 시 Temporal 사용
    private Date lastModifiedDate;
    
    //사실 최근의 하이버네이트는 Temporal 사용 X 가능
    private LocalDate testCreatedDate; //Date로 작성
    private LocalDateTime testlastModifiedDate; //TimeStamp로 작성
    
    @Lob //데이터 제한 없는 대형 데이터 넣을 때 사용 - String으로 하면 기본적으로 clab으로 해준다. (문자열 X면 blab으로 매핑)
    private String description;
    
    @Transient //DB 반영 없이 서버에서만 사용
    private int temp;
    
    //기본적으로 JPA는 기본 생성자가 필요 - 내부적으로 reflection이나, proxy 등의 작업에 사용...

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
}
