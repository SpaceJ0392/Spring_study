package com.springstudy.hello.repository;

import com.springstudy.hello.domain.Member;

import java.util.*;

public class MemoryMemberRepository implements MemberRepository{

    private static final Map<Long, Member> store = new HashMap<>();
    private static Long sequence = 0L;
    @Override
    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
        //Optional 은 돌아오는 값이 Null 이어도 감쌀 수 있음
        // 예전에는 그냥 Null을 보내기도 했었음 (그런데 Null을 감싸서 보내는 이유는 감싸서 보내면 클라가 처리가 가능)
    }

    @Override
    public Optional<Member> findByName(String name) {
        return store.values().stream() //stream() : 말그대로 values를 stream 형식으로 쭉 읽음.
                .filter(member -> member.getName().equals(name))
                .findAny(); //findAny() : 하나가 반환되면 끝남.
        //값이 없으면 Null 이 Optional로 반환될 것...
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore(){
        store.clear(); // 공용 공간 초기화
    }
}
