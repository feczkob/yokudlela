package com.yokudlela.dailymenu.client.persistence.repository;

import com.yokudlela.dailymenu.client.persistence.entity.MenuItemEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, UUID> {

    List<MenuItemEntity> findByMenu_Day(LocalDate day, Pageable pageable);

    List<MenuItemEntity> findByMenu_DayBetween(LocalDate from, LocalDate to, Pageable pageable);
}
