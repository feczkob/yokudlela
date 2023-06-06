package hu.soft4d.yokudlela3.client.persistence.repository;

import hu.soft4d.yokudlela3.client.persistence.entity.DishEntity;

import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DishRepository extends JpaRepository<DishEntity, UUID> {

    List<DishEntity> findByActive(boolean active, Pageable pageable);

    List<DishEntity> findByNameLikeIgnoreCase(@NonNull String name, Pageable pageable);

    List<DishEntity> findByNameLikeIgnoreCaseAndActive(@NonNull String name, boolean active, Pageable pageable);

}
