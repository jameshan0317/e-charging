package echarging;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="echargers", path="echargers")
public interface EchargerRepository extends PagingAndSortingRepository<Echarger, Long>{


}
