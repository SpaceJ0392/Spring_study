package spring_and_jpa_api_study.springboot_and_jpa2.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum OrderStatus {
    ORDER, CANCEL
}
