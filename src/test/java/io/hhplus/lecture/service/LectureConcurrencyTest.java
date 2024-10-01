package io.hhplus.lecture.service;

import io.hhplus.lecture.common.exception.LectureNotFoundException;
import io.hhplus.lecture.domain.entity.Lecture;
import io.hhplus.lecture.domain.entity.LectureHistory;
import io.hhplus.lecture.domain.entity.User;
import io.hhplus.lecture.domain.repository.LectureHistoryRepository;
import io.hhplus.lecture.domain.repository.LectureRepository;
import io.hhplus.lecture.domain.repository.UserRepository;
import io.hhplus.lecture.domain.service.LectureService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LectureConcurrencyTest {

    private final LectureService lectureService;
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final LectureHistoryRepository lectureHistoryRepository;

    @Autowired
    public LectureConcurrencyTest(LectureService lectureService, UserRepository userRepository, LectureRepository lectureRepository, LectureHistoryRepository lectureHistoryRepository) {
        this.lectureService = lectureService;
        this.userRepository = userRepository;
        this.lectureRepository = lectureRepository;
        this.lectureHistoryRepository = lectureHistoryRepository;
    }
    @Test
    @Transactional()
    @DisplayName("40명이 동시에 수강신청을 할 때 - 낙관적 락")
    void applyLecture_ConcurrencyTest() throws InterruptedException {
        //given
        Lecture lecture = new Lecture("항해플러스",30,0);
        lectureRepository.saveLecture(lecture);


        int threadCount = 40;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (long i = 0; i < threadCount; i++){
            long finalI = i;
            long finalI1 = i;
            executor.execute(() -> {
                try {
                    User user = new User(finalI,"유저"+ finalI1);
                    userRepository.saveUser(user);
                    lectureService.apply(user.getId(),lecture.getId());
                    successCount.incrementAndGet();
                }catch (ObjectOptimisticLockingFailureException e){
                    System.out.println("낙관적 락 충돌 발생");
                    failCount.incrementAndGet();
                }catch (Exception e){
                    failCount.incrementAndGet();
                }
                latch.countDown();
            });
        }

        latch.await();

        System.out.println("성공한 수  =  " + successCount.get());
        System.out.println("실패한 수  =  " + failCount.get());

        assertEquals(30, successCount.get());
        assertEquals(10, failCount.get());

    }
}
