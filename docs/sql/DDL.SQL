-- 사용자 테이블
CREATE TABLE member (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(100) NOT NULL
);

-- 콘서트 테이블
CREATE TABLE concert (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         title VARCHAR(100) NOT NULL
);

-- 콘서트 일정 테이블
CREATE TABLE concert_schedule (
                                  id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  concert_id BIGINT NOT NULL,
                                  concert_date TIMESTAMP NOT NULL,
                                  FOREIGN KEY (concert_id) REFERENCES concert(id)
);

-- 좌석 테이블
CREATE TABLE seat (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      concert_schedule_id BIGINT NOT NULL,
                      seat_number INT NOT NULL,
                      status VARCHAR(100) NOT NULL, -- 'AVAILABLE', 'RESERVED', 'PAID'
                      price BIGINT NOT NULL,
                      version BIGINT NOT NULL,
                      FOREIGN KEY (concert_schedule_id) REFERENCES concert_schedule(id)
);

-- 예약 테이블 (사용자와 콘서트 연관)
CREATE TABLE reservation (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             member_id BIGINT NOT NULL,
                             seat_id BIGINT NOT NULL,
                             status VARCHAR(100) NOT NULL, -- 'RESERVED', 'PAID', 'CANCELED'
                             expiry_at TIMESTAMP NOT NULL,
                             FOREIGN KEY (member_id) REFERENCES member(id),
                             FOREIGN KEY (seat_id) REFERENCES seat(id)
);

-- 포인트 테이블 (유저의 현재 포인트 잔액 관리)
CREATE TABLE account (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         member_id BIGINT NOT NULL,
                         balance BIGINT NOT NULL,
                         FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 포인트 히스토리 테이블 (충전/사용 내역 관리)
CREATE TABLE account_history (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 account_id BIGINT NOT NULL,
                                 amount BIGINT NOT NULL, -- 포인트의 증감 (양수는 충전, 음수는 사용)
                                 type VARCHAR(100) NOT NULL, -- 'CHARGE', 'USE'
                                 create_at TIMESTAMP NOT NULL,
                                 FOREIGN KEY (account_id) REFERENCES account(id)
);

-- 대기열 큐 테이블 (세션별로 대기열 관리, 사용자 정보와 무관하게 처리)
CREATE TABLE queue (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       member_id BIGINT NOT NULL,
                       token VARCHAR(100) NOT NULL,
                       status VARCHAR(100) NOT NULL, -- 'WAITING', 'PASSED', 'EXPIRED'
                       expiry_at TIMESTAMP NOT NULL,
                       FOREIGN KEY (member_id) REFERENCES member(id)
);