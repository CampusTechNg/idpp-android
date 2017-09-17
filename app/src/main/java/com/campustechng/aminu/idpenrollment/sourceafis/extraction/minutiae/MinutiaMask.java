/**
 * @author Veaceslav Dubenco
 * @since 19.10.2012
 */
package com.campustechng.aminu.idpenrollment.sourceafis.extraction.minutiae;

import com.campustechng.aminu.idpenrollment.sourceafis.general.Angle;
import com.campustechng.aminu.idpenrollment.sourceafis.general.BinaryMap;
import com.campustechng.aminu.idpenrollment.sourceafis.general.Calc;
import com.campustechng.aminu.idpenrollment.sourceafis.general.DetailLogger;
import com.campustechng.aminu.idpenrollment.sourceafis.general.Point;
import com.campustechng.aminu.idpenrollment.sourceafis.general.PointF;
import com.campustechng.aminu.idpenrollment.sourceafis.general.Size;
import com.campustechng.aminu.idpenrollment.sourceafis.meta.DpiAdjusted;
import com.campustechng.aminu.idpenrollment.sourceafis.meta.Parameter;
import com.campustechng.aminu.idpenrollment.sourceafis.templates.Minutia;
import com.campustechng.aminu.idpenrollment.sourceafis.templates.TemplateBuilder;

import java.util.Iterator;

/**
 * 
 */
public final class MinutiaMask {
	@DpiAdjusted(min = 0)
	@Parameter(lower = 0, upper = 50)
	public float DirectedExtension = 10.06f;

	public DetailLogger.Hook Logger = DetailLogger.off;

	public void Filter(TemplateBuilder template, BinaryMap mask) {
		Iterator<Minutia> iter = template.minutiae.iterator();
		while (iter.hasNext()) {
			Minutia minutia = iter.next();
			Point arrow = Calc.Round(PointF.multiply(-DirectedExtension,
					Angle.ToVector(minutia.Direction)));
			if (!mask.GetBitSafe(Point.add(minutia.Position, new Size(arrow)),
					false)) {
				iter.remove();
			}
		}
		Logger.log(template);
	}
}
