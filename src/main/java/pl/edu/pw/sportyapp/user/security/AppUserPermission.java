package pl.edu.pw.sportyapp.user.security;

public enum AppUserPermission {
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    GAME_READ("game:read"),
    GAME_WRITE("game:write");

    private final String permission;

    AppUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
