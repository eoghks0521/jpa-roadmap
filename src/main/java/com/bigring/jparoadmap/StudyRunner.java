package com.bigring.jparoadmap;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bigring.jparoadmap.relation.RelationMember;
import com.bigring.jparoadmap.relation.Team;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StudyRunner implements ApplicationRunner {

    @PersistenceContext
    EntityManager em;

    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //create();
        // update();
        // findByJPQL();
        //managePersistenceContext();
        relate();
    }

    private void relate() {
        Team team = new Team();
        team.setName("TeamA");
        em.persist(team);

        RelationMember member = new RelationMember();
        member.setUsername("MemberA");
        member.setTeam(team);
        em.persist(member);

        // 영속성 컨텍스트의 1차캐시를 타지 않게 하고 디비에서 가져오는 쿼리를 보고 싶다하는 경우
        // em.flush();
        // em.clear();

        RelationMember findMember = em.find(RelationMember.class, member.getId());
        Team findTeam = findMember.getTeam();
        log.info("findTeam = {}",findTeam.getName());

        // 맴버 외래키 변경
        Team newTema = em.find(Team.class, 100L);
        findMember.setTeam(newTema);
    }

    private void create(){
        Member member = new Member();
        member.setId(1L);
        member.setName("hello");
        em.persist(member);
    }

    private void update(){
        Member findMember = em.find(Member.class, 1L);
        log.info("Member id: {}, name: {}",findMember.getId(),findMember.getName());
        findMember.setName("updateHello");
    }

    private void findByJPQL(){
        List<Member> memberList = em.createQuery("select m from Member as m", Member.class)
            .setFirstResult(0)
            .setMaxResults(10)
            .getResultList();
        for (Member member : memberList) {
            log.info("member name: {}", member.getName());
        }
    }

    private void managePersistenceContext(){
        //비영속
        Member member = new Member();
        member.setId(3L);
        member.setName("hi");

        //영속
        log.info("before");
        em.persist(member);
        log.info("after");
        // before after 와 상관없이 뒤에 쿼리가 날아간다. -> 트랜잭션 커밋을 하는 순간 날아감

        // 1차 캐시에서 값을 가져오기 때문에 쿼리가 날아가지 않는다.
        Member findMember = em.find(Member.class,"3L");
        log.info("member name: {}", findMember.getName());
    }

}
