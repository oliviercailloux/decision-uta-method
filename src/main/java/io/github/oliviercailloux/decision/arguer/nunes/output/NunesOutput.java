package io.github.oliviercailloux.decision.arguer.nunes.output;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.nunes.NunesModel;

public interface NunesOutput {

	public Pattern getPattern();

	public AlternativesComparison<NunesModel> getAlternativesComparison();
}
