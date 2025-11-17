package lab2.bank.model;

import java.util.List;
import java.util.UUID;

public class User {
    private final UUID id;
    private String nickname;
    private List<UUID> accountIds;

    public User(UUID id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public User(String nickname) {
        this(UUID.randomUUID(), nickname);
    }

    public UUID getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<UUID> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(List<UUID> accountIds) {
        this.accountIds = accountIds;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", accountIds=" + accountIds +
                '}';
    }
}
