package app.vinhomes.security;

import app.vinhomes.entity.Account;
import app.vinhomes.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class SecurityService {
    @Autowired
    private AccountService accountService;
    public boolean checkIfEnabledFromAuthentication(Authentication authentication){
        System.out.println("inside authentication extractor");
        Account getAccount = accountService.getCurrentlyLogginAccount(authentication);
        if(getAccount != null){
            boolean isEnable = getAccount.isEnabled();
            if(isEnable){
                System.out.println(getAccount.getAccountName()+ "     "+getAccount.getEmail());
                return true;
            }
        }
        return false;

    }

    public boolean checkIsBlock(Authentication authentication){
        Account getAccount = accountService.getCurrentlyLogginAccount(authentication);
        if(getAccount != null){
            boolean isBlock = getAccount.getIsBlock();
            if(isBlock == false){
                return true;
            }
        }
        return false;
    }
    public Account getUserThroughAuth(Authentication authentication){
        Account getAccount = accountService.getCurrentlyLogginAccount(authentication);
        if(getAccount != null){
            return getAccount;
        }
        return null;
    }
}
