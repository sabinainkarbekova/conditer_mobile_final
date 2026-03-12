// AddressRepository.java
package com.example.ConnectDB.repository;

import com.example.ConnectDB.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    // Только активные
    List<Address> findByAddressTypeAndIsActiveTrue(Address.AddressType addressType);

    // 🔥 Все (активные и неактивные)
    List<Address> findByAddressType(Address.AddressType addressType);
}