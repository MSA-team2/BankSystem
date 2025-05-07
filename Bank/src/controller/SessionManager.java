package controller;

import model.domain.Member;

public class SessionManager {
    private static Member currentUser = null;  // 현재 로그인한 사용자
    private static boolean isAdmin = false;  // 관리자 여부

    // 로그인 처리
    public static void login(Member member) {
        currentUser = member;
        isAdmin = (member.getRole() == 1);  // role이 1이면 관리자
    }

    // 로그아웃 처리
    public static void logout() {
        currentUser = null;
        isAdmin = false;
    }

    // 로그인 여부 확인
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    // 관리자 여부 확인
    public static boolean isAdmin() {
        return isAdmin;
    }

    // 현재 사용자 정보 반환
    public static Member getCurrentUser() {
        return currentUser;
    }
}
