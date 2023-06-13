package app.vinhomes.repository.customer;

import app.vinhomes.entity.customer.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByBuildingBlockAndBuildingRoom(String builingBlock, String buildingRoom );

    @Query
            (value = "select * from tbl_address where address_id = (select address_id from tbl_account where account_id = ?1)", nativeQuery=true)
    Address findByCustomerId(Long accountId);

}