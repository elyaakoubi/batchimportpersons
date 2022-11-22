package ma.ensa.batch;


import ma.ensa.entities.Personne;
import ma.ensa.repositories.PersonRepo;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Service("personWriter")
@Import(PersonRepo.class)
public class PersonWriter implements ItemWriter<Personne>{
	
	@Autowired
	private PersonRepo personRepo;

	@Transactional
	@Override
	public void write(List<? extends Personne> personnes) throws Exception {
		for(Personne pr : personnes){
			personRepo.save(pr);
		}
	}
}
