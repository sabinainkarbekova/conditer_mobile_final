// com.example.ConnectDB.service.AdminAddressService.java
package com.example.ConnectDB.service;

import com.example.ConnectDB.model.PickupPoint; // Убедитесь, что модель PickupPoint создана
import com.example.ConnectDB.repository.PickupPointRepository; // Убедитесь, что репозиторий создан
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminAddressService {

    @Autowired
    private PickupPointRepository pickupPointRepository; // Убедитесь, что репозиторий инжектится

    public List<PickupPoint> getAllPickupPoints() {
        return pickupPointRepository.findAll();
    }

    public PickupPoint updatePickupPoint(Long id, PickupPoint updatedPointData) {
        PickupPoint existingPoint = pickupPointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Точка самовывоза не найдена"));

        // Обновляем поля из полученных данных
        existingPoint.setStreet(updatedPointData.getStreet());
        existingPoint.setCity(updatedPointData.getCity());
        existingPoint.setPostalCode(updatedPointData.getPostalCode());
        existingPoint.setCountry(updatedPointData.getCountry());
        // Обновляем статус активности
        existingPoint.setActive(updatedPointData.isActive());

        return pickupPointRepository.save(existingPoint);
    }
}