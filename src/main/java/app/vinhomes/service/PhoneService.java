package app.vinhomes.service;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.customer.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneService {
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private AccountRepository accountRepository;
    public List<Phone> getAllPhonenumberByAccount(Account account) {
        List<Phone>getPhoneList = phoneRepository.findByAccount(account);
        if(getPhoneList == null || getPhoneList.isEmpty()){
            return null;
        }
        return getPhoneList ;
    }
    public List<Phone> getAllPhonenumberByAccountId(Long accountid){
        try{
            Account getAccount = accountRepository.findByAccountId(accountid);
            return getAllPhonenumberByAccount(getAccount);
        }catch (Exception e){
            return null;
        }
    }
}
