package app.vinhomes.repository.customer;

import app.vinhomes.entity.customer.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByBuildingBlockAndBuildingRoom(String builingBlock, String buildingRoom );

}
