package robi.api.auth.grant;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrantService {

    private final GrantRepository grantRepository;

    public GrantService(GrantRepository grantRepository) {
        this.grantRepository = grantRepository;
    }

    public List<Grant> getSystemGrants() {
        return grantRepository.getAll();
    }

}
