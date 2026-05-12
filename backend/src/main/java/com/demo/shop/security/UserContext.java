package com.demo.shop.security;

public class UserContext {
    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> usernameHolder = new ThreadLocal<>();
    private static final ThreadLocal<Integer> roleHolder = new ThreadLocal<>();

    public static void set(Long userId, String username, Integer role) {
        userIdHolder.set(userId);
        usernameHolder.set(username);
        roleHolder.set(role);
    }

    public static Long getCurrentUserId() {
        return userIdHolder.get();
    }

    public static String getCurrentUsername() {
        return usernameHolder.get();
    }

    public static Integer getCurrentUserRole() {
        return roleHolder.get();
    }

    public static boolean isAdmin() {
        return Integer.valueOf(0).equals(roleHolder.get());
    }

    public static void clear() {
        userIdHolder.remove();
        usernameHolder.remove();
        roleHolder.remove();
    }
}
