package ma.ensa.repositories;


import ma.ensa.entities.Personne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepo extends JpaRepository<Personne,Integer> {

}
