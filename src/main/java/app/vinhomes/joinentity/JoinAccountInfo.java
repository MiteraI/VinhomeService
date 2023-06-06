package app.vinhomes.joinentity;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.customer.Address;
import app.vinhomes.entity.customer.Phone;
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
