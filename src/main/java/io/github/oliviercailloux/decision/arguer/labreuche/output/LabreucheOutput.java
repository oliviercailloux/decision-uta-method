package io.github.oliviercailloux.decision.arguer.labreuche.output;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;

public interface LabreucheOutput {

	public AlternativesComparison<LabreucheModel> getAlternativesComparison();

	public Anchor getAnchor();
}
