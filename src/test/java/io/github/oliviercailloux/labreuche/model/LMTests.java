package io.github.oliviercailloux.labreuche.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;
import io.github.oliviercailloux.decision.arguer.labreuche.Tools;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;
import io.github.oliviercailloux.uta_calculator.view.MainBuyingNewCar; 


public class LMTests {
	
	@Test
	public void labreucheModelTests(){
		
		MainLabreucheModel mlm = new MainLabreucheModel();		
		
		/*List<Criterion> c_list = mlm.lm5.phi_noa.C;
		
		List<Criterion> list = 	mlm.lm5.criteria;
		Criterion c = list.get(1);
		list.clear();
		list.add(c);
		*/
		// suivi l'exemple UTA pour les tests mais erreurs compilation pour le premier test
		
		//assertEquals("is all permutations ok?", c_list,list);
	}
}
