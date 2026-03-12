package com.example.ConnectDB.service;

import com.example.ConnectDB.model.Address;
import com.example.ConnectDB.model.UserDeliveryAddress;
import com.example.ConnectDB.repository.AddressRepository;
import com.example.ConnectDB.repository.UserDeliveryAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <--- Добавьте эту аннотацию

import java.util.List;

@Service
@Transactional // <--- Либо на весь класс
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserDeliveryAddressRepository userDeliveryAddressRepository;

    // --- CRUD для Admin ---
    @Transactional // <--- Или на каждый метод, который меняет данные
    public Address createPickupPoint(Address address) {
        address.setAddressType(Address.AddressType.PICKUP);
        return addressRepository.save(address);
    }

    @Transactional
    public Address updatePickupPoint(Long id, Address addressDetails) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Адрес не найден"));
        if (!address.getAddressType().equals(Address.AddressType.PICKUP)) {
            throw new RuntimeException("Можно обновлять только точки самовывоза");
        }
        address.setStreet(addressDetails.getStreet());
        address.setCity(addressDetails.getCity());
        return addressRepository.save(address);
    }

    @Transactional
    public void deletePickupPoint(Long id) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Адрес не найден"));
        if (address.getAddressType().equals(Address.AddressType.PICKUP)) {
            System.out.println("Deactivating address ID: " + id); // <--- Для отладки
            address.setActive(false); // Деактивируем
            addressRepository.save(address); // Сохраняем изменения
        } else {
            throw new RuntimeException("Можно удалять только точки самовывоза");
        }
    }

    // --- Функции для User ---
    public List<Address> getAvailablePickupPoints() {
        return addressRepository.findByAddressTypeAndIsActiveTrue(Address.AddressType.PICKUP);
    }

    public List<UserDeliveryAddress> getUserDeliveryAddresses(Long userId) {
        return userDeliveryAddressRepository.findByUserId(userId);
    }

    public UserDeliveryAddress addUserDeliveryAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .filter(a -> a.getAddressType() == Address.AddressType.DELIVERY && a.isActive()) // <--- isActive(), не getIsActive()
                .orElseThrow(() -> new RuntimeException("Неверный адрес доставки"));

        UserDeliveryAddress userAddr = new UserDeliveryAddress();
        userAddr.setUserId(userId);
        userAddr.setAddress(address);
        return userDeliveryAddressRepository.save(userAddr);
    }

    @Transactional
    public void removeUserDeliveryAddress(Long id, Long userId) {
        UserDeliveryAddress addr = userDeliveryAddressRepository.findById(id).orElseThrow(() -> new RuntimeException("Адрес не найден"));
        if (!addr.getUserId().equals(userId)) {
            throw new RuntimeException("Нет прав на удаление этого адреса");
        }
        userDeliveryAddressRepository.delete(addr);
    }
}