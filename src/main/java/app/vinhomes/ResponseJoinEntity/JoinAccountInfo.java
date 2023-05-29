package app.vinhomes.ResponseJoinEntity;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Address;
import app.vinhomes.entity.Phone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JoinAccountInfo {
    private Account account;
    private List<Phone> phoneList;
    //private List<Address> addressList;
    private Address address;
}
