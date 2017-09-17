/**
 * @author Veaceslav Dubenco
 * @since 19.10.2012
 */
package com.campustechng.aminu.idpenrollment.sourceafis.extraction.minutiae;

import com.campustechng.aminu.idpenrollment.sourceafis.general.Calc;
import com.campustechng.aminu.idpenrollment.sourceafis.general.DetailLogger;
import com.campustechng.aminu.idpenrollment.sourceafis.meta.DpiAdjusted;
import com.campustechng.aminu.idpenrollment.sourceafis.meta.Parameter;
import com.campustechng.aminu.idpenrollment.sourceafis.templates.Minutia;
import com.campustechng.aminu.idpenrollment.sourceafis.templates.TemplateBuilder;

/**
 * 
 */
public final class StandardDpiScaling {
	@DpiAdjusted
	@Parameter(lower = 500, upper = 500)
	public int DpiScaling = 500;

	public DetailLogger.Hook Logger = DetailLogger.off;

	public void Scale(TemplateBuilder template) {
		float dpiFactor = 500 / (float) DpiScaling;
		for (Minutia minutia : template.minutiae)
			minutia.Position = Calc.Multiply(dpiFactor, minutia.Position);
		Logger.log(template);
	}
}
