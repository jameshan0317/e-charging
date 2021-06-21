package echarging;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="echargings", path="echargings")
public interface EchargingRepository extends PagingAndSortingRepository<Echarging, Long>{

    Optional<Echarging> findByReserveId(Long reserveId);

}
