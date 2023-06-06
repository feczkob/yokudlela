package hu.soft4d.yokudlela3.client.persistence.repository;

import hu.soft4d.yokudlela3.client.persistence.entity.MenuEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, UUID> {

    MenuEntity findByDay(LocalDate day);

    List<MenuEntity> findByDayBetween(LocalDate from, LocalDate to, Pageable pageable);
}
