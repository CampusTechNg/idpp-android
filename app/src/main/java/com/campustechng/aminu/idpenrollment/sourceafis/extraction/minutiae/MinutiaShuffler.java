/**
 * @author Veaceslav Dubenco
 * @since 19.10.2012
 */
package com.campustechng.aminu.idpenrollment.sourceafis.extraction.minutiae;

import com.campustechng.aminu.idpenrollment.sourceafis.general.Calc;
import com.campustechng.aminu.idpenrollment.sourceafis.templates.Minutia;
import com.campustechng.aminu.idpenrollment.sourceafis.templates.TemplateBuilder;

import java.util.Random;

/**
 * 
 */
public final class MinutiaShuffler {
	public void Shuffle(TemplateBuilder template) {
		int seed = 0;
		for (Minutia minutia : template.minutiae)
			seed += minutia.Direction + minutia.Position.X + minutia.Position.Y
					+ minutia.Type.getValue();
		template.minutiae = Calc.Shuffle(template.minutiae, new Random(seed));
	}

}
