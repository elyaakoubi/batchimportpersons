package ma.ensa.batch;


import ma.ensa.entities.Personne;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

//@Service("personProcessor")
public class PersonProcessor implements ItemProcessor<Personne, Personne>{
	@Override
	public Personne process(Personne personne) throws Exception {
		if("M".equals(personne.getCivilite()))
			{
			return personne;
			}
		return null;
	}
}
