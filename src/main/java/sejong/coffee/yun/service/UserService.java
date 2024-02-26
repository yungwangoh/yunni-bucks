package sejong.coffee.yun.service;

import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Member;

import java.util.List;

public interface UserService {

    default Member signUp(String name, String email, String password, Address address) {
        return null;
    }
    default Member updateName(Long memberId, String updateName) {
        return null;
    }
    default Member updateEmail(Long memberId, String updateEmail) {
        return null;
    }
    default Member updatePassword(Long memberId, String updatePassword) {
        return null;
    }
    default void deleteMember(Long memberId) {}
    default String signIn(String email, String password) {return null;}
    default String signOut(String accessToken, Long memberId) {return null;}
    default List<Member> updateAllUserRank() {return null;}
    default Member findMember(Long memberId) {
        return null;
    }
    default List<Member> findAll() {
        return null;
    }
    default String duplicateName(String name) {return null;}
    default String duplicateEmail(String email) { return null; }
}
