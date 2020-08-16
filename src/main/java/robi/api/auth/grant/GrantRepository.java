package robi.api.auth.grant;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import robi.api.common.BaseRepository;
import robi.api.common.ObjectWithOwner;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;

@Repository
public class GrantRepository extends BaseRepository<Grant> {

    public GrantRepository(@Qualifier("ROBIDataSource") DataSource source) {
        super(source);
    }

    public Long addGrant(Grant grant) {
        return (Long) execute("ins_role", grant);
    }

    public Grant getById(String grantId) {
        return (Grant) execute(Grant.class, "get_grant_by_id", grantId, "grantId");
    }

    public List<Grant> getUserRoles(Long userId) {
        return all("get_user_roles", userId, "userId");
    }

    public List<Grant> getAll() {
        return all("get_all_grants");
    }

    public Boolean addGrantToUser(String grantId, Long userId) {
        return (Boolean) execute("add_grant_to_user", new ObjectWithOwner(grantId, userId), Types.BOOLEAN);
    }

}
