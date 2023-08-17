package sejong.coffee.yun.mock.repository;

import org.springframework.stereotype.Repository;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static sejong.coffee.yun.domain.exception.ExceptionControl.*;

@Repository
public class FakeUserRepository implements UserRepository {

    private final List<Member> members = new ArrayList<>();
    private Long id = 0L;

    @Override
    public Member save(Member member) {
        Member newMember = Member.from(++id, member);
        members.add(newMember);

        return newMember;
    }

    @Override
    public Member findById(Long id) {
        return members.stream()
                .filter(user -> Objects.equals(user.getId(), id))
                .findAny()
                .orElseThrow(NOT_FOUND_USER::notFoundException);
    }

    @Override
    public List<Member> findAll() {
        return members;
    }

    @Override
    public boolean exist(Long id) {
        try {
            findById(id);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public void delete(Long id) {
        Member member = findById(id);
        members.remove(member);
    }

    @Override
    public void updateName(Long id, String name) {
        Member member = findById(id);
        member.updateName(name);
    }

    @Override
    public void updatePassword(Long id, String password) {
        Member member = findById(id);
        member.updatePassword(password);
    }

    @Override
    public void updateEmail(Long id, String email) {
        Member member = findById(id);
        member.updateEmail(email);
    }

    @Override
    public void duplicateEmail(String email) {
        boolean match = members.stream()
                .anyMatch(member -> Objects.equals(member.getEmail(), email));

        if(match) throw DUPLICATE_USER_EMAIL.duplicatedEmailException();
    }

    @Override
    public void duplicateName(String name) {
        boolean match = members.stream()
                .anyMatch(member -> Objects.equals(member.getName(), name));

        if(match) throw DUPLICATE_USER_NAME.duplicatedNameException();
    }

    @Override
    public Member findByEmail(String email) {
        return members.stream()
                .filter(member -> Objects.equals(member.getEmail(), email))
                .findAny()
                .orElseThrow(NOT_FOUND_USER::notFoundException);
    }

}
