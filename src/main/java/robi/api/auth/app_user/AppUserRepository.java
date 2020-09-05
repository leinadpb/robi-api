package robi.api.auth.app_user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import robi.api.common.BaseRepository;

import javax.sql.DataSource;
import java.sql.Types;

@Repository
public class AppUserRepository extends BaseRepository<AppUser> {

    public AppUserRepository(@Qualifier("ROBIDataSource") DataSource dataSource) {
        super(dataSource);
    }


    public AppUser getById(Long userId) {
        return (AppUser) execute(AppUser.class,"get_user_by_id", userId, "userId");
    }

    public AppUser getByUsername(String username) {
        return (AppUser) execute(AppUser.class, "get_user_by_username", username, "username");
    }

    public Long add(AppUser user) {
        return (Long) execute("ins_user", user, Types.BIGINT);
    }

    public Boolean usernameAvailable(String username) {
        return (Boolean) execute("check_username_available", username, "username", Types.BOOLEAN);
    }

    public Boolean emailAvailable(String email) {
        return (Boolean) execute("check_email_available", email, "email", Types.BOOLEAN);
    }

}
