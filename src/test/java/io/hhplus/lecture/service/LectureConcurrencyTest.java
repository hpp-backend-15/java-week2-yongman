package io.hhplus.lecture.service;

import io.hhplus.lecture.common.exception.AlreadyAppliedException;
import io.hhplus.lecture.domain.entity.Lecture;
import io.hhplus.lecture.domain.entity.LectureHistory;
import io.hhplus.lecture.domain.entity.User;
import io.hhplus.lecture.domain.repository.LectureHistoryRepository;
import io.hhplus.lecture.domain.repository.LectureRepository;
import io.hhplus.lecture.domain.repository.UserRepository;
import io.hhplus.lecture.domain.service.LectureService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

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

    // 실패하는 코드
    @Test
    //@Transactional
    @DisplayName("40명이 동시에 수강신청을 할 때 - 낙관적 락")
    void applyLecture_ConcurrencyTest() throws InterruptedException {
        //given
        Lecture lecture = new Lecture("항해플러스",30,0);
        lectureRepository.saveLecture(lecture);

        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (long i = 0; i < threadCount; i++){
            long finalI = i;
            long finalI1 = i;
            executor.execute(() -> {
                try {
                    User user = new User(finalI, "유저"+finalI1);
                    // generate Value는 객체가 저장될 때 Id를 하나 하나 올려준다. -> Spring? Java Application? DB?
                    User saveUser = userRepository.saveUser(user);
                    lectureService.apply(saveUser.getId(), lecture.getId());
                    successCount.incrementAndGet();
                }catch (ObjectOptimisticLockingFailureException e){
                    failCount.incrementAndGet();
                }catch (Exception e){
                    e.printStackTrace();
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

    @Test
    @DisplayName("40명이 동시에 수강신청을 할 때 - 비관적 락")
    void applyLecture_PessimisticTest() throws InterruptedException {
        //given
        int threadNum = 40;

        Lecture lecture = new Lecture("항해플러스",30,0);
        Lecture saveLecture = lectureRepository.saveLecture(lecture);

        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        CountDownLatch latch = new CountDownLatch(threadNum);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadNum; i++){
            long finalI = i;
            executor.execute(() -> {
                try {
                    User user = new User(finalI,"유저"+finalI);
                    User saveUser = userRepository.saveUser(user);
                    lectureService.apply(saveUser.getId(),saveLecture.getId());
                    successCount.incrementAndGet();
                }catch(Exception e) {
                    e.printStackTrace();
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

    @Test
    @DisplayName("같은 유저 1회 성공, 4회 실패") // <- 실패하는 테스트입니다. 성공 : 2 , 실패 : 3
    void applyOnceAndFailOthers() throws InterruptedException {
        //given
        Lecture lecture = new Lecture("항해플러스", 30, 0);
        Lecture saveLecture = lectureRepository.saveLecture(lecture);

        User user = new User("이용만");
        User saveUser = userRepository.saveUser(user);

        int numThread = 5;

        ExecutorService executor = Executors.newFixedThreadPool(numThread);
        CountDownLatch latch = new CountDownLatch(numThread);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < numThread; i++){
            executor.execute(() -> {
                try {
                    LectureHistory lectureHistory = lectureService.apply(saveUser.getId(), saveLecture.getId());
                    System.out.println("신청이 완료 되엇는가? : " + lectureHistory.getIsApplied() );
                    successCount.incrementAndGet();
                }catch (AlreadyAppliedException e){
                    System.out.println("이미 지원한 경력이 있습니다.");
                    e.printStackTrace();
                    failCount.incrementAndGet();
                }
                latch.countDown();
            });
        }
        latch.await();

        System.out.println("성공한 수는 1번 : " + successCount);
        System.out.println("실패한 수는 4번 : " + failCount);

        assertEquals(1,successCount);
        assertEquals(4,failCount);
    }

}



